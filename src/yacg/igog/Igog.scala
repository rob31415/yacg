package yacg.igog

import yacg.util.Logger
import scala.collection.mutable
import yacg.igo.Npc
import yacg.igo.Scene_graph_interface
import com.jme3.math.Vector3f
import com.jme3.math.Vector2f
import yacg.jme.Jme_interface
import yacg.jme.Terrain
import scala.collection.mutable._
import com.jme3.bullet.control.RigidBodyControl
import yacg.igo.Igo
import yacg.persistence.Db
import com.jme3.scene.Node


object Igog extends Logger {
  type location_type = HashMap[String, (Float, Float)]
  val SCALE: Float = 4.0f;
  //val npcs: Node = new Node("npcs")
  //val statics: Node = new Node("statics")

  def init(db_path: String) {
    Db.init(db_path)
  }

  def shutdown {
    Db shutdown
  }

  def get_all_npcs_names: List[String] = {
    var ret_val: List[String] = Nil
    val query = "start result=node(*) where result.type = 'npc' return result"

    Db.execute(query, (node: org.neo4j.graphdb.Node) => {
      ret_val = ret_val :+ node.getProperty("name").asInstanceOf[String]
      log_debug("npc=" + node.getProperty("name"))
    })

    ret_val
  }

  def get_location(id: String, location_name: String): (Float, Float) = {
    var ret_val = (0f, 0f)

    val query = "start npc=node(*) match (npc)-[:" + location_name + "]->(location) where npc.type='npc' and npc.name='" + id + "' and location.type='location' return location as result"

    Db.execute(query, (node: org.neo4j.graphdb.Node) => {
      ret_val = (node.getProperty("x").toString().toFloat, node.getProperty("y").toString().toFloat)
      log_debug("xy=" + ret_val._1 + " " + ret_val._2)
    })

    ret_val
  }

  def create_igos(jme_interface: Jme_interface) {

    //jme_interface.root_node.attachChild(npcs)
    //jme_interface.root_node.attachChild(statics)

    List(("npc", "start_x", "start_y", true), if(yacg.Configurator.loadStatics)("static", "x", "y", false) else ("","","",false)).foreach(element => {

      val query = "start result=node(*) where result.type = '" + element._1 + "' return result"

      Db.execute(query, (node: org.neo4j.graphdb.Node) => {
        val name = if (element._4) node.getProperty("name").asInstanceOf[String] else ""
        val file_name = node.getProperty("modelfile").asInstanceOf[String]
        val x = node.getProperty(element._2).toString().toFloat
        val y = node.getProperty(element._3).toString().toFloat

        log_debug("xyz=" + x + " " + Terrain.get_mesh_height(x, y) + " " + y + " - " + file_name)

        var igo: Igo = Igo.createInstance(element._1, name, file_name, jme_interface)
        igo.geo.setLocalTranslation(x, Terrain.get_mesh_height(x, y), y)
        igo.geo.setLocalScale(SCALE)
        //npc.geo.addControl(new RigidBodyControl(1.0f))
        //jme_interface.bullet_app_state.getPhysicsSpace().add(npc.geo)
        jme_interface.root_node.attachChild(igo.geo)
        //if(igo.typeName == "npc") npcs.attachChild(igo.geo)
        //if(igo.typeName == "static") statics.attachChild(igo.geo)
        igo run
      })
    })

  }

  def get_locations: location_type = {
    var ret_val = new location_type()
    val query = "start result=node(*) where result.type = 'location' return result"

    Db.execute(query, (node: org.neo4j.graphdb.Node) => {
      val name = node.getProperty("name").asInstanceOf[String]
      val x = node.getProperty("x").toString().toFloat
      val y = node.getProperty("y").toString().toFloat

      ret_val.put(name, (x, y))
    })

    ret_val
  }

  def update_location(id: String, x: Float, y: Float) {
    Db.execute("start result=node(*) where has(result.name) and has(result.type) and result.name = '" + id.replace("'", "\\'") + "' and result.type='location' set result.x='" + x + "', result.y='" + y + "' return result".toString())
    //Db.update_node(id, "location", "x", x.toString, "y", y.toString)
  }

  def update_npc(id: String, x: Float, y: Float) {
    Db.execute("start result=node(*) where has(result.name) and has(result.type) and result.name = '" + id.replace("'", "\\'") + "' and result.type='npc' set result.start_x='" + x + "', result.start_y='" + y + "' return result".toString())
    //Db.update_node(id, "npc", "start_x", x.toString, "start_y", y.toString)
  }

  def update_static(id: Long, x: Float, y: Float) {
    Db.execute("start result=node(" + id.toString() + ") where has(result.type) and result.type='static' set result.x='" + x + "', result.y='" + y + "' return result".toString())
    //Db.update_node(id, "npc", "start_x", x.toString, "start_y", y.toString)
  }

  def setPropertyOnPlayer(propName: String) {
    Db.execute("start result=node(*) where has(result.type) and result.type = 'player' set result." + propName + "=true return result")
  }

  def removePropertyFromPlayer(propName: String) {
    Db.execute("start result=node(*) where has(result.type) and result.type = 'player' set result." + propName + "=null return result")
  }

  def playerHasProperty(propName: String): Boolean = hasProperty("start result=node(*) where has(result.type) and result.type = 'player' and has(result." + propName + ") return count(*) as result")

  def hasProperty(query: String): Boolean = {
    var retval = false
    Db.execute(query, (value: Long) => { retval = value > 0 })
    retval
  }

  def setPropertyOnIgo(id: String, propName: String) {
    Db.execute("start result=node(*) where has(result.name) and has(result.type) and result.name = '" + id.replace("'", "\\'") + "' and result.type='npc' set result." + propName + "=true return result")
  }

  def removePropertyFromIgo(id: String, propName: String) {
    Db.execute("start result=node(*) where has(result.name) and has(result.type) and result.name = '" + id.replace("'", "\\'") + "' and result.type='npc' set result." + propName + "=null")
  }

  //java.lang.Long cannot be cast to org.neo4j.graphdb.Node
  def igoHasProperty(id: String, propName: String): Boolean =  hasProperty("start result=node(*) where has(result.name) and has(result.type) and has(result." + propName + ") and result.name ='" + id.replace("'", "\\'") + "' and result.type='npc' return count(*) as result")


}