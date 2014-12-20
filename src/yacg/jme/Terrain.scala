package yacg.jme

import com.jme3.terrain.geomipmap.TerrainGrid
import com.jme3.terrain.geomipmap.grid.ImageTileLoader
import com.jme3.asset.AssetManager
import com.jme3.terrain.heightmap.Namer
import com.jme3.terrain.geomipmap.TerrainGridLodControl
import com.jme3.renderer.Camera
import com.jme3.material.Material
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator
import com.jme3.terrain.geomipmap.TerrainGridListener
import com.jme3.math.Vector3f
import com.jme3.terrain.geomipmap.TerrainQuad
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape
import com.jme3.bullet.BulletAppState
import com.jme3.texture.Texture.WrapMode
import com.jme3.math.Vector2f
import com.jme3.bullet.control.RigidBodyControl
import yacg.util.Logger
import yacg.igog.Igog
import yacg.Main
import yacg.igo.Scene_graph_interface
import com.jme3.texture.Image
import com.jme3.texture.image.ImageRaster
import com.jme3.texture.Texture
import com.jme3.terrain.heightmap.ImageBasedHeightMap
import jme3tools.converters.ImageToAwt
import com.jme3.terrain.heightmap.HillHeightMap
import com.jme3.terrain.heightmap.AbstractHeightMap
import scala.sys.process._
import com.jme3.math.FastMath

class Counter {
  private var layer_counter_diff = -1
  private var layer_counter_normal = -1

  def diffuse: String =
    {
      layer_counter_diff += 1
      if (layer_counter_diff > 0) {
        return "_" + layer_counter_diff.toString
      }
      ""
    }

  def normal: String =
    {
      layer_counter_normal += 1
      if (layer_counter_normal > 0) {
        return "_" + layer_counter_normal.toString
      }
      ""
    }
}

/*
 * 
 * terraingrid:
 * tile format: png, 129x129, 16bpp greyscale, 77ppi, ca 27kb size
 * 
 */
object Terrain extends Logger {
  val scale_x = 14f
  val scale_y = 8f
  val scale_z = 14f
  val patch_size = 512
  val patch_number_of = 16

  private var terrain: TerrainGrid = _ //TerrainQuad
  private var mat_terrain: Material = _
  private var heightmap_image: Image = _

  val pathPrefix = "Textures/terrain/resources/"

  //r0
  val redStone = "red stone/rock-texture71.jpg"
  //g0
  val darkRock = "dark rock/stone.jpg"
  //b0
  val crackedDirt = "brown cracked dirt/cracked-dirt-texture-01.jpg"
  //a0
  val dryGrass = "yellowbrown grass/drygrass.jpg"

  //r1
  val redbrownRockyDirt = "redbrown rocky dirt/dirt.jpg"
  //g1
  val brownDirt = "brown dirt/Dirt-Texture-05_small.png"
  //b1
  val lightgreySand = "lightgrey sand/sandwhite.jpg"
  //a1
  val greenGrass = "green grass/grass.jpg"

  // @TODO: get the scale right, design a huge heightfield
  // @TODO: do good-looking texture-splatting
  // see http://www.gamasutra.com/blogs/AndreyMishkinis/20130716/196339/Advanced_Terrain_Texture_Splatting.php
  // and http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:materials_overview
  // section TerrainLighting.j3md
  def init(jme_interface: Jme_interface) {

    terrain = new TerrainGrid("terrain", patch_size + 1, (patch_size * 2) + 1,
      new ImageTileLoada(jme_interface.asset_mgr,
        new Namer() {
          def getName(x: Int, z: Int): String = {

            val path = "Textures/terrain/heightmap/"
            val filename = path + "tile_" + "%03d".format(x + (z * patch_number_of)) + ".png"

            if (x >= 0 && x < patch_number_of && z >= 0 && z < patch_number_of) {
              log_debug("terrain x/z " + x + " " + z + " -> " + filename)
              filename
            } else {
              log_debug("terrain x/z " + x + " " + z + " -> ignored")
              path + "tile_000.png"
            }
          }
        }));

    /*
    val heightMapImage = jme_interface.asset_mgr.loadTexture("Textures/heightmap/heightmap_master.png");
    val heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
    heightmap.load();
    terrain = new TerrainQuad("my terrain", 65, 4096+1, heightmap.getHeightMap());
    */

    val control = new TerrainGridLodControl(this.terrain, jme_interface.camera)
    control.setLodCalculator(new DistanceLodCalculator(patch_size + 1, 1.7f)) // patch size, and a multiplier 2.7

    terrain.addListener(new TerrainGridListener() {

      def gridMoved(newCenter: Vector3f) {
      }

      def tileAttached(cell: Vector3f, quad: TerrainQuad) {
        log_debug("attaching terrain tile" + cell.toString())

        while (quad.getControl(classOf[RigidBodyControl]) != null) {
          quad.removeControl(classOf[RigidBodyControl]);
        }

        quad.addControl(new RigidBodyControl(new HeightfieldCollisionShape(quad.getHeightMap(), terrain.getLocalScale()), 0));
        jme_interface.bullet_app_state.getPhysicsSpace().add(quad)
        quad.setMaterial(getTerrainMaterial(cell.x.toInt, cell.z.toInt, jme_interface.asset_mgr))

        //Terrain.createVegetation(jme_interface, cell.x.toInt, cell.z.toInt)

        Scene_graph_interface.refresh()
      }

      def tileDetached(cell: Vector3f, quad: TerrainQuad) {
        log_debug("detaching terrain tile" + cell.toString())
        if (quad.getControl(classOf[RigidBodyControl]) != null) {
          jme_interface.bullet_app_state.getPhysicsSpace().remove(quad);
          quad.removeControl(classOf[RigidBodyControl]);
        }
      }

    });

    //@TODO: why does commenting this crash? do we need minimal-mat here initially?
    this.terrain.setMaterial(getTerrainMaterial(0, 0, jme_interface.asset_mgr))
    this.terrain.setLocalTranslation(0, 0, 0)
    this.terrain.setLocalScale(scale_x, scale_y, scale_z)
    log_debug("add terrain to rootnode")

    //System.out.println("add lodctrlr to terrain")
    this.terrain.addControl(control)

    log_debug("terrain.addctrl rigidbody")
    terrain.addControl(new RigidBodyControl(0))

    //val terrainCollisionShape = new HeightfieldCollisionShape(terrain.getHeightMap(), terrain.getLocalScale())
    //val terrain_phy = new RigidBodyControl(terrainCollisionShape, 0f)
    //this.terrain.addControl(terrain_phy);
    log_debug("bulletAppState phyicspace.add terrain")
    jme_interface.bullet_app_state.getPhysicsSpace().add(terrain);

    jme_interface.root_node.attachChild(terrain)

    //heightmap_image = jme_interface.asset_mgr.loadTexture("Textures/heightmap/heightmap_master.png").getImage()

  }

  def get_mesh_height(x: Float, y: Float): Float = {
    val y_h = terrain.getHeight(new Vector2f(x, y))
    //val y_hm = terrain.getHeightmapHeight(new Vector2f(x, y))

    //log_debug("terrain.getHeight=" + y_h + " terrain.getHeightmapHeight=" + y_hm)

    //may be NaN if the tile underlying the giving coordinates isn't attached
    if (y_h.isNaN()) 0 else y_h
  }

  // that's wrong
  def convert_3d_to_imagespace(x: Float, z: Float): (Int, Int) = {
    val xm = (patch_size + z / scale_z) % (patch_size * patch_number_of)
    val ym = (patch_size + x / scale_x) % (patch_size * patch_number_of)
    log_debug("3d xz " + x + " " + z + " imgspace xy " + xm + " " + ym)
    (xm.toInt, ym.toInt)
  }

  // doesn't work properly
  def get_height_at(x: Float, z: Float): Float = {
    if (heightmap_image.getFormat() == Image.Format.Luminance8) {

      val ir = ImageRaster.create(heightmap_image)
      val xy = convert_3d_to_imagespace(x, z)
      var texel = ir.getPixel(xy._1, ((ir.getHeight() / 2) + xy._2) % ir.getHeight())

      val height = (0.299 * texel.r + 0.587 * texel.g + 0.114 * texel.b).toFloat
      val height_scaled = map(height) * 255 * scale_y

      log_debug("height is " + height_scaled + " without scale " + height * 255)

      return height_scaled.toFloat
    } else {
      log_error("heightmap master image hasn't Image.Format.Luminance8")
    }

    0.0f
  }

  // unknown, why a linear gradient in image space looks like a sqrt slope in 3d y 
  // it might as well be something other then sqrt
  // maybe one of erode, flatten and smooth makes it just look like sqrt
  // https://android.googlesource.com/platform/external/jmonkeyengine/+/59b2e6871c65f58fdad78cd7229c292f6a177578/engine/src/terrain/com/jme3/terrain/heightmap/AbstractHeightMap.java
  def map(x: Float): Float = {
    val start = 0f
    val end = math.Pi / 2
    val y = math.sqrt(x) //math.sin(x * end)
    log_debug("map " + x + " -> " + y)
    y.toFloat
  }

  // http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:materials_overview
  def getTerrainMaterial(x: Int, y: Int, assetManager: AssetManager): Material = {

    def addTexture(mapId: String, filename: String, mapScaleId: String = "", scale: Int = 0) {
      val texture = assetManager.loadTexture(filename)
      texture.setWrap(WrapMode.Repeat)
      mat_terrain.setTexture(mapId, texture)
      if (mapScaleId != "") mat_terrain.setFloat(mapScaleId, scale)
    }

    mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
    mat_terrain.setBoolean("isTerrainGrid", false) //true doesn't scale textures up
    mat_terrain.setBoolean("useTriPlanarMapping", false)
    mat_terrain.setFloat("Shininess", 0.0f)
    mat_terrain.setBoolean("WardIso", true);
    if (yacg.Configurator.terrainWireframe) {
      mat_terrain.getAdditionalRenderState().setWireframe(true);
    }

    val filenameAlpha1 = "Textures/terrain/heightmap/blend1/tile_blend_" + "%03d".format((y * Terrain.patch_number_of) + x) + ".png"
    mat_terrain.setTexture("AlphaMap", assetManager.loadTexture(filenameAlpha1))

    addTexture("DiffuseMap", pathPrefix + redStone, "DiffuseMap_0_scale", 32)
    addTexture("DiffuseMap_1", pathPrefix + darkRock, "DiffuseMap_1_scale", 32)
    addTexture("DiffuseMap_2", pathPrefix + crackedDirt, "DiffuseMap_2_scale", 128)
    addTexture("DiffuseMap_3", pathPrefix + dryGrass, "DiffuseMap_3_scale", 32)

    val filenameAlpha2 = "Textures/terrain/heightmap/blend2/tile_blend_" + "%03d".format((y * Terrain.patch_number_of) + x) + ".png"
    mat_terrain.setTexture("AlphaMap_1", assetManager.loadTexture(filenameAlpha2))

    addTexture("DiffuseMap_4", pathPrefix + redbrownRockyDirt, "DiffuseMap_4_scale", 32)
    addTexture("DiffuseMap_5", pathPrefix + brownDirt, "DiffuseMap_5_scale", 32)
    addTexture("DiffuseMap_6", pathPrefix + lightgreySand, "DiffuseMap_6_scale", 32)
    addTexture("DiffuseMap_7", pathPrefix + greenGrass, "DiffuseMap_7_scale", 32)

    /*
    addTexture("NormalMap", "Textures/misc/stone/stone_ln.jpg")
    addTexture("NormalMap_1", "Textures/misc/stone/stone_ln.jpg")
    addTexture("NormalMap_2", "Textures/misc/stone/stone_ln.jpg")
    addTexture("NormalMap_3", "Textures/misc/stone/stone_ln.jpg")
    addTexture("NormalMap_4", "Textures/misc/stone/stone_ln.jpg")
    addTexture("NormalMap_5", "Textures/misc/stone/stone_ln.jpg")
*/
    mat_terrain
  }

  def createMosaic(filename: String) = {
    val prefix = "\"" + Main.basepath_assets + pathPrefix
    val cmd = "montage " +
      prefix + redStone + "\" " +
      prefix + darkRock + "\" " +
      prefix + crackedDirt + "\" " +
      prefix + dryGrass + "\" " +
      prefix + redbrownRockyDirt + "\" " +
      prefix + brownDirt + "\" " +
      prefix + lightgreySand + "\" " +
      prefix + greenGrass + "\" -thumbnail 300x300 -set caption %t -background grey40 -pointsize 9 -density 144x144 +polaroid -resize 50%  -background white -geometry +1+1 -tile 4x4 -title \"YACG Terrain Textures\" " + filename

    log_debug("use this to create mosaic image:\n " + cmd + "\n")

    //@TODO: why doesnt that work? leerzeichen problem.
    Process(cmd)!
    //val output = cmd.!!
  }

  def createVegetation(jme_interface: Jme_interface, terrainCellX: Integer, terrainCellY: Integer) = {
  
    log_debug("createVegetation")

    val offsetX = patch_size * terrainCellX * Terrain.scale_z;
    val offsetY = patch_size * terrainCellY * Terrain.scale_x;
    val offsetXEnd = (offsetX + (patch_size * Terrain.scale_z))
    val offsetYEnd = (offsetY + (patch_size * Terrain.scale_x))

    //400-1700x  946-1200y  is red

    val image = jme_interface.asset_mgr.loadTexture("Textures/terrain/vegetation1.bmp").getImage()
    val ir = ImageRaster.create(image)
    var rnd = new scala.util.Random
    rnd.setSeed(851974)
    val step = 100
    var counter = 0
    var tempVector = new com.jme3.math.Vector3f()
    var r: Integer = 0
    var red: Integer = 0

    log_debug("createVegetation " + offsetX + " " + offsetY + " " + offsetXEnd + " " + offsetYEnd)

    var x = offsetX
    var y = offsetY

    while (x < offsetXEnd) {
      y = offsetY
      while (y < offsetXEnd) {

        tempVector.x = y;
        tempVector.z = x;
        var bla = yacg.util.coordConversion.threeDToImage(tempVector)

        r = rnd.nextInt(255)
        red = 250 //((ir.getPixel(bla._1, bla._2).r) * 255).toInt

        // log_debug("random=" + r + " red=" + red + " x=" + bla._1 + " y=" + bla._2 + " 3dX=" + x + " 3dY=" + y)

        if (r < red) {
          createShrub(tempVector, jme_interface)
          counter += 1
        }

        y += step
      }
      x += step
    }

    /*
    offsetX to offsetXEnd by step map { x =>
      {
        offsetY to offsetYEnd by step map { y =>
          {

            tempVector.x = y;
            tempVector.z = x;
            var bla = yacg.util.coordConversion.threeDToImage(tempVector)

            //log_debug("rrrr " + bla._1 + " " + bla._2 + " ; " + x + " " + y)

            if (r < ir.getPixel(bla._1, bla._2).r * 255) {
              log_debug("rrrr " + r + " " + ir.getPixel(bla._1, bla._2).r * 255)
              createShrub(bla._1, bla._2, jme_interface)
              counter += 1
            }
          }
        }
      }
    }
    * 
    */

    log_debug("created " + counter + "shrubs")
  }

  def createShrub(location: com.jme3.math.Vector3f, jme_interface: Jme_interface) {
    val geo = jme_interface.asset_mgr.loadModel("Models/shrub1.blend")
    geo.setLocalScale(new Vector3f(40, 20, 40))

    //val xyz = yacg.util.coordConversion.ImageToThreeD(x, y)
    //xyz.y = this.get_mesh_height(xyz.z, xyz.x)

    location.y = this.get_mesh_height(location.z, location.x)

    log_debug("createShrub " + location.x + " " + location.y + " " + location.z)

    geo.setLocalTranslation(location)
    geo.rotate(180f * FastMath.DEG_TO_RAD, util.Random.nextInt(130) * FastMath.DEG_TO_RAD, 0f) //.setLocalRotation(new Quarternion())
    //geo.addControl(new RigidBodyControl(1.0f))
    //bulletAppState.getPhysicsSpace().add(geo)
    jme_interface.root_node.attachChild(geo)
  }

}
