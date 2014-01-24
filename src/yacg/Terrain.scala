package yacg

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
//import com.jme3.terrain.{Terrain => JmeTerrain}
import com.jme3.texture.Texture.WrapMode

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

object Terrain {

  var terrain: TerrainGrid = _
  var assetManager: AssetManager = _
  var camera: Camera = _
  var bulletAppState: BulletAppState = _

  private var mat_terrain: Material = _

  // @TODO: get the scale right, design a huge heightfield
  // @TODO: do good-looking texture-splatting
  // see http://www.gamasutra.com/blogs/AndreyMishkinis/20130716/196339/Advanced_Terrain_Texture_Splatting.php
  // and http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:materials_overview
  // section TerrainLighting.j3md
  def init: TerrainGrid = {

    this.terrain = new TerrainGrid("terrain", 512 + 1, 1024 + 1, new ImageTileLoader(assetManager,
      new Namer() {
        def getName(x: Int, y: Int): String = {
          val x_patch = Math.abs(x) % 2;
          val y_patch = Math.abs(y) % 2;

          // System.out.println("" + x + ", " +y + " -> " + x_patch + ", " + y_patch)
          "Textures/heightmap/heightmap_" + x_patch + "_" + y_patch + ".png"
        }
      }));
    val control = new TerrainGridLodControl(this.terrain, camera)
    control.setLodCalculator(new DistanceLodCalculator(512, 2.7f)) // patch size, and a multiplier

    terrain.addListener(new TerrainGridListener() {

      def gridMoved(newCenter: Vector3f) {
      }

      def tileAttached(cell: Vector3f, quad: TerrainQuad) {
        while (quad.getControl(classOf[RigidBodyControl]) != null) {
          quad.removeControl(classOf[RigidBodyControl]);
        }
        quad.addControl(new RigidBodyControl(new HeightfieldCollisionShape(quad.getHeightMap(), terrain.getLocalScale()), 0));
        bulletAppState.getPhysicsSpace().add(quad)
        println("**" + cell.toString())
        quad.setMaterial(getTerrainMaterial(cell.x.toInt, cell.z.toInt))
      }

      def tileDetached(cell: Vector3f, quad: TerrainQuad) {
        if (quad.getControl(classOf[RigidBodyControl]) != null) {
          bulletAppState.getPhysicsSpace().remove(quad);
          quad.removeControl(classOf[RigidBodyControl]);
        }
      }

    });

    //@TODO: why does commenting this crash? do we need minimal-mat here initially?
    this.terrain.setMaterial(getTerrainMaterial(0, 0))
    this.terrain.setLocalTranslation(0, 0, 0)
    this.terrain.setLocalScale(10f, 3f, 10f)
    System.out.println("add terrain to rootnode")

    //System.out.println("add lodctrlr to terrain")
    this.terrain.addControl(control)

    System.out.println("terrain.addctrl riidbocy")
    terrain.addControl(new RigidBodyControl(0))

    //val terrainCollisionShape = new HeightfieldCollisionShape(terrain.getHeightMap(), terrain.getLocalScale())
    //val terrain_phy = new RigidBodyControl(terrainCollisionShape, 0f)
    //this.terrain.addControl(terrain_phy);
    System.out.println("bulletAppState phyicspace.add terrain")
    bulletAppState.getPhysicsSpace().add(terrain);

    terrain
  }

  def addLayer(diffuse: String, normal: String, size: Int) {
    var counter = new Counter
    val bla1 = counter.diffuse

    val map_diffuse = assetManager.loadTexture(diffuse)
    map_diffuse.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("DiffuseMap" + bla1, map_diffuse)
    mat_terrain.setFloat("DiffuseMap" + (if (bla1.isEmpty) "_0" else bla1) + "_scale", size)

    val map_normal = assetManager.loadTexture(normal)
    map_normal.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("NormalMap" + counter.normal, map_normal)
  }

  def getTerrainMaterial(x: Int, y: Int): Material = {
    // http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:materials_overview

    /*
    this.mat_terrain = new Material(this.assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md") //
    map_diffuse.setWrap(WrapMode.Repeat)
    this.mat_terrain.setTexture("slopeColorMap", rock)
    this.mat_terrain.setFloat("slopeTileFactor", 32)
    this.mat_terrain.setFloat("terrainSize", 512)
     */

    // 
    // Textures/misc/test_alpha.png
    // Textures/misc/cracked-dirt/cracked-dirt-texture-01_mn.jpg
    // Textures/misc/cracked-dirt/cracked-dirt-texture-01_a.jpg

    mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
    mat_terrain.setBoolean("isTerrainGrid", false)
    mat_terrain.setBoolean("useTriPlanarMapping", false)
    mat_terrain.setFloat("Shininess", 0.0f)
    mat_terrain.setBoolean("WardIso", true);

    mat_terrain.setTexture("AlphaMap", assetManager.loadTexture("Textures/heightmap/heightmap_alpha_" + x + "_" + y + ".png"))

    /*
    addLayer("Textures/misc/cracked-dirt/cracked-dirt-texture-01.jpg", "Textures/misc/cracked-dirt/cracked-dirt-texture-01_mn.jpg", 16)
    addLayer("Textures/misc/dirt/dirt.jpg", "Textures/misc/dirt/dirt_mn.jpg", 32)
    addLayer("Textures/misc/stone/stone.jpg", "Textures/misc/stone/stone_ln.jpg", 16)
*/

    val map_diffuse2 = assetManager.loadTexture("Textures/misc/dirt/dirt.jpg")
    map_diffuse2.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("DiffuseMap_1", map_diffuse2)
    mat_terrain.setFloat("DiffuseMap_1_scale", 128)

    val map_normal2 = assetManager.loadTexture("Textures/misc/dirt/dirt_ln.jpg")
    map_normal2.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("NormalMap_1", map_normal2)

    
    val map_diffuse = assetManager.loadTexture("Textures/misc/cracked-dirt/cracked-dirt-texture-01.jpg")
    map_diffuse.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("DiffuseMap", map_diffuse)
    mat_terrain.setFloat("DiffuseMap_0_scale", 64)

    val map_normal = assetManager.loadTexture("Textures/misc/cracked-dirt/cracked-dirt-texture-01_ln.jpg")
    map_normal.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("NormalMap", map_normal)

    
    val map_diffuse3 = assetManager.loadTexture("Textures/misc/stone/stone.jpg")
    map_diffuse3.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("DiffuseMap_2", map_diffuse3)
    mat_terrain.setFloat("DiffuseMap_2_scale", 32)

    val map_normal3 = assetManager.loadTexture("Textures/misc/stone/stone_ln.jpg")
    map_normal3.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("NormalMap_2", map_normal3)

    /*
    mat_terrain.setTexture("AlphaMap_1", assetManager.loadTexture("Textures/misc/stone/stone_h.jpg"))

    
    val map_diffuse4 = assetManager.loadTexture("Textures/misc/stone/stone_s.jpg")
    map_diffuse4.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("DiffuseMap_3", map_diffuse4)
    mat_terrain.setFloat("DiffuseMap_3_scale", 2)

    val map_normal4 = assetManager.loadTexture("Textures/misc/stone/stone_s.jpg")
    map_normal4.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("NormalMap_3", map_normal4)
     */
    
    mat_terrain
  }

}
