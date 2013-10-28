package yacg


import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.math.{Vector3f,ColorRGBA}
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box
import com.jme3.terrain.geomipmap._
import com.jme3.texture.Texture
import com.jme3.texture.Texture.WrapMode
import com.jme3.light.DirectionalLight
import com.jme3.terrain.geomipmap.grid.ImageTileLoader
import com.jme3.terrain.heightmap.Namer
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator
import com.jme3.asset.plugins._


object Main {
  def main(args:Array[String]): Unit = {

    import java.util.logging.{Logger,Level}
    Logger.getLogger("").setLevel(Level.WARNING);

    val app = new Main
    app.start
  }
}


class Main extends SimpleApplication {
 
    private var mat_terrain: Material = _
    private var terrain: TerrainGrid = _
    private var grassScale: Float = 64
    private var dirtScale: Float = 16
    


  override def simpleInitApp: Unit = {
    //@TODO obviously use relative path 
    assetManager.registerLocator("/home/rob/development/eclipse/workspace/yacg_2/assets/", classOf[FileLocator])

    //@TODO terrain collision detection for walking on it
    flyCam.setMoveSpeed(100f)
    
    initMaterial
    initTerrain
    initCamera
    initLight
  }

    
  def initCamera: Unit = {
    this.getCamera().setLocation(new Vector3f(0, 200, 0))
    this.getCamera().lookAt(new Vector3f(0,0,0), Vector3f.UNIT_Y)
    this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f))
  }

  
  def initMaterial: Unit = {
    //@TODO explain, where this j3md is and what it does
    this.mat_terrain = new Material(this.assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md") //

    /*
    val grass = this.assetManager.loadTexture("Textures/grass.jpg")
    grass.setWrap(WrapMode.Repeat)
    this.mat_terrain.setTexture("region1ColorMap", grass)
    this.mat_terrain.setVector3("region1", new Vector3f(-10, 0, this.grassScale))

    val dirt = this.assetManager.loadTexture("Textures/dirt.jpg")
    dirt.setWrap(WrapMode.Repeat)
    this.mat_terrain.setTexture("region2ColorMap", dirt)
    this.mat_terrain.setVector3("region2", new Vector3f(0, 900, this.dirtScale))
     */
    
    //@TODO: use a intentionally designed texture for the whole terrain
    val rock = this.assetManager.loadTexture("Textures/stone.jpg")
    rock.setWrap(WrapMode.Repeat)

    this.mat_terrain.setTexture("slopeColorMap", rock)
    this.mat_terrain.setFloat("slopeTileFactor", 32)
    this.mat_terrain.setFloat("terrainSize", 512)
  }

  def initLight: Unit = {
    val light = new DirectionalLight()
    light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize())
    rootNode.addLight(light);
  }


  def initTerrain: Unit = {
    //@TODO explain, why not every patch looks similar, although it's always the same png
    this.terrain = new TerrainGrid("terrain", 65, 257, new ImageTileLoader(assetManager, 
      new Namer() {
        def getName(x: Int, y: Int): String = "Textures/heightmap.png"
        	//@TODO don't always the same heightfield 
            //return "Interface/Scenes/TerrainMountains/terrain_" + x + "_" + y + ".png";
    }));

    this.terrain.setMaterial(mat_terrain)
    this.terrain.setLocalTranslation(0, 0, 0)
    this.terrain.setLocalScale(3f, 1.5f, 3f)
    this.rootNode.attachChild(this.terrain)

    val control = new TerrainGridLodControl(this.terrain, getCamera())
    control.setLodCalculator( new DistanceLodCalculator(64, 2.7f) )  // patch size, and a multiplier
    this.terrain.addControl(control)
  }


}




