package yacg.util

import yacg.igo._
import java.io.PrintWriter
import java.io.File
import yacg.jme.Terrain
import yacg.igog.Igog
import javax.imageio.ImageIO
import yacg.persistence.Db

// @TODO: exctract the transformations between 3D to image-space to some other place and document it for a 10 year old to understand
object Svg extends Logger {

  private val figure_size = 10

  private def header(width: Integer, height: Integer): String = {
    "<?xml version=\"1.0\"?>" +
      "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">" +
      "<svg viewBox = \"0 0 " + width + " " + height + "\" version = \"1.1\">"
  }

  private def trailer: String = "</svg>"

  // read from neo4j and write to svg-xml
  def export_igog(output_file_name: String, heightfield_master_filename: String) {

    val image = ImageIO.read(new File(heightfield_master_filename))
    val width = image.getWidth
    val height = image.getHeight
    var svg: String = header(width, height)
    svg += image_tag(heightfield_master_filename, width, height)

    Scene_graph_interface.getIgosByType().foreach(igo => svg += igo2svg(igo))

    Igog.get_locations.foreach(location => {
      log_debug("xy=" + location._2._1 + "/" + location._2._2)
      val xy = coordConversion.threeDToImage(location._2._1,location._2._2)
      svg += location2svg(location._1, xy._1, xy._2)
    })

    svg += trailer

    val writer = new PrintWriter(new File(output_file_name))
    writer.write(svg)
    writer.close()

    log_debug("svg export to file " + output_file_name + ": " + svg)
  }

  private def igo2svg(igo: Igo): String = {
    var name = ""
    var color = ""

    val xy = coordConversion.threeDToImage(igo.geo.getLocalTranslation)

    igo.typeName match {
      case "npc" => name = igo.asInstanceOf[Npc].name; color = "yellow"
      case "static" => name = igo.asInstanceOf[Static].file_name; color = "blue"
      case _ =>
    }

    get_svg_figure(name, xy._1, xy._2, color) + "<desc>" + igo.typeName + "</desc></rect>\n"
  }

  private def location2svg(id: String, x: Float, y: Float): String = {
    val desc = "location"
    get_svg_figure(id, x, y, "green") + "<desc>" + desc + "</desc></rect>\n"
  }

  private def get_svg_figure(id: String, x: Float, y: Float, color: String): String = {
    "<rect id=\"" + id + "\" x = \"" + x + "\" y = \"" + y + "\" width = \"" + figure_size + "\" height = \"" + figure_size + "\" fill = \"" + color + "\" stroke = \"black\">"
  }

  private def image_tag(file: String, width: Integer, height: Integer): String = {
    "<image xlink:href=\"" + file + "\" width=\"" + width + "\" height=\"" + height + "\" />"
  }

  // read from svg-xml and update neo4j
  def update_igog(file_name: String) {
    val xml = scala.xml.XML.loadFile(file_name)
    //log_debug(xml.toString)

    val staticIds = new scala.collection.mutable.ListBuffer[Long]
    Db.execute("start result=node(*) where has(result.type) and result.type='static' return result", (node: org.neo4j.graphdb.Node) => {
      staticIds += (node.getId())
    })
    
    val backgroundImage = (xml \ "image")
    val width = (backgroundImage \ "@width").text.toInt
    val height = (backgroundImage \ "@height").text.toInt

    (xml \ "rect").foreach(node => {
      val id = (node \ "@id").text
      //val x = (node \ "@x").text.toFloat
      //val y = (node \ "@y").text.toFloat
      val xy = coordConversion.ImageToThreeD((node \ "@x").text.toFloat, (node \ "@y").text.toFloat)
      node.foreach(node => {
        val igog_type = (node \ "desc").text
        igog_type match {
          case "npc" => Igog.update_npc(id, xy.x , xy.z)
          case "location" => Igog.update_location(id, xy.x , xy.z)
          case "static" => Igog.update_static(staticIds.remove(0), xy.x , xy.z)
        }
      })
    })

    log_debug("finished svg 2 neo4j")
  }

}