package yacg

import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.math.{ Vector3f, ColorRGBA }
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
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape
import com.jme3.bullet.control.CharacterControl
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.input.controls.KeyTrigger
import com.jme3.input.KeyInput
import com.jme3.input.controls.ActionListener
import com.jme3.bullet.collision.shapes.BoxCollisionShape
import com.jme3.bullet.control.CharacterControl
import com.jme3.bullet.control.CharacterControl
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape
import com.jme3.terrain.heightmap.AbstractHeightMap
import com.jme3.terrain.heightmap.ImageBasedHeightMap
import com.jme3.math.FastMath
import com.jme3.scene.Mesh
import akka.actor._



object Main {
  def main(args: Array[String]): Unit = {

    import java.util.logging.{ Logger, Level }
    Logger.getLogger("").setLevel(Level.WARNING);

    val app = new Main
    app.start
  }
}

class Main extends SimpleApplication with ActionListener {

  private var mat_terrain: Material = _
  private var terrain: TerrainGrid = _
  //    private var terrain: TerrainQuad = _
  private var bulletAppState: BulletAppState = _
  private var left: Boolean = false;
  private var right: Boolean = false;
  private var up: Boolean = false;
  private var down: Boolean = false;
  private var player: CharacterControl = _
  var camDir: Vector3f = new Vector3f()
  var camLeft: Vector3f = new Vector3f()
  var walkDirection: Vector3f = new Vector3f()
  var boxie: Geometry = _
  private var boxie_location = new Vector3f(0, 450, 0)

  override def simpleInitApp: Unit = {
    assetManager.registerLocator(System.getProperty("user.dir") + "/assets/", classOf[FileLocator])

    //@TODO terrain collision detection for walking on it
    //flyCam.setMoveSpeed(500f)

    initMaterial
    initTerrain
    initCamera
    //initLight
    setUpKeys
    createCube
  }

  def initCamera: Unit = {
    this.getCamera().setLocation(new Vector3f(0, 10, 0))
    this.getCamera().lookAt(boxie_location, Vector3f.UNIT_Y)
    this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f))
    this.getCamera().setFrustumFar(10000.0f)
    //flyCam.setEnabled(false)

    bulletAppState = new BulletAppState()
    stateManager.attach(bulletAppState)
    System.out.println("attach bulletappstate to statemgr")

    val capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1)

    //val capsuleShape = new BoxCollisionShape()
    //player = new BetterCharacterControl(1.0f,1.0f,1.0f) //capsuleShape, 0.05f)
    //player.setGravity(new Vector3f(0,-10,0))
    player = new CharacterControl(capsuleShape, 0.05f)
    player.setPhysicsSpace(bulletAppState.getPhysicsSpace())
    //player.

    player.setJumpSpeed(20)
    player.setFallSpeed(30)
    player.setGravity(30)
    player.setPhysicsLocation(new Vector3f(0, 100, 100))

    System.out.println("bulletAppState phyicspace.add terrain")
    bulletAppState.getPhysicsSpace().add(terrain);
    System.out.println("bulletAppState ps.add player")
    bulletAppState.getPhysicsSpace().add(player);

  }

  def initMaterial: Unit = {
    //@TODO explain, where this j3md is and what it does
    this.mat_terrain = new Material(this.assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md") //

    //@TODO: use a intentionally designed texture for the whole terrain
    val rock = this.assetManager.loadTexture("Textures/misc/stone.jpg")
    rock.setWrap(WrapMode.Repeat)

    this.mat_terrain.setTexture("slopeColorMap", rock)
    this.mat_terrain.setFloat("slopeTileFactor", 32)
    this.mat_terrain.setFloat("terrainSize", 512)
  }

  def initLight: Unit = {
    val light = new DirectionalLight()
    light.setDirection((new Vector3f(0.3f, -0.5f, 0.4f)).normalize())
    // @TODO why doesnt this do anything?
    //rootNode.addLight(light);
  }

  def initTerrain: Unit = {

    this.terrain = new TerrainGrid("terrain", 512 + 1, 1024 + 1, new ImageTileLoader(assetManager,
      new Namer() {
        def getName(x: Int, y: Int): String = {
          //@TODO don't always the same heightfield 
          //return "Interface/Scenes/TerrainMountains/terrain_" + x + "_" + y + ".png";
          val x_patch = Math.abs(x) % 2;
          val y_patch = Math.abs(y) % 2;
          // System.out.println("" + x + ", " +y + " -> " + x_patch + ", " + y_patch)
          "Textures/heightmap/heightmap_" + x_patch + "_" + y_patch + ".png"
        }
      }));
    val control = new TerrainGridLodControl(this.terrain, getCamera())
    control.setLodCalculator(new DistanceLodCalculator(512, 2.7f)) // patch size, and a multiplier

    terrain.addListener(new TerrainGridListener() {

      def gridMoved(newCenter: Vector3f) {
      }

      def tileAttached(cell: Vector3f, quad: TerrainQuad) {
        while (quad.getControl(classOf[RigidBodyControl]) != null) {
          quad.removeControl(classOf[RigidBodyControl]);
        }
        quad.addControl(new RigidBodyControl(new HeightfieldCollisionShape(quad.getHeightMap(), terrain.getLocalScale()), 0));
        bulletAppState.getPhysicsSpace().add(quad);
      }

      def tileDetached(cell: Vector3f, quad: TerrainQuad) {
        if (quad.getControl(classOf[RigidBodyControl]) != null) {
          bulletAppState.getPhysicsSpace().remove(quad);
          quad.removeControl(classOf[RigidBodyControl]);
        }
      }

    });

    /*
    var heightMapImage = assetManager.loadTexture("Textures/heightmap/heightmap_0_0.png")
    var heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
    heightmap.load();    
    terrain = new TerrainQuad("Golfcourse", 65, 1025, heightmap.getHeightMap());    
    val control = new TerrainLodControl(this.terrain, getCamera())
    control.setLodCalculator( new DistanceLodCalculator(512, 2.7f) )  // patch size, and a multiplier
* */

    this.terrain.setMaterial(mat_terrain)
    this.terrain.setLocalTranslation(0, 0, 0)
    //this.terrain.setLocalScale(10f, 3f, 10f)
    System.out.println("add terrain to rootnode")
    this.rootNode.attachChild(this.terrain)

    //System.out.println("add lodctrlr to terrain")
    this.terrain.addControl(control)

    System.out.println("terrain.addctrl riidbocy")
    terrain.addControl(new RigidBodyControl(0))

    //val terrainCollisionShape = new HeightfieldCollisionShape(terrain.getHeightMap(), terrain.getLocalScale())
    //val terrain_phy = new RigidBodyControl(terrainCollisionShape, 0f)
    //this.terrain.addControl(terrain_phy);

  }

  def setUpKeys: Unit = {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
  }

  /**
   * These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed.
   */
  def onAction(binding: String, isPressed: Boolean, tpf: Float) {
    if (binding.equals("Left")) {
      left = isPressed;
    } else if (binding.equals("Right")) {
      right = isPressed;
    } else if (binding.equals("Up")) {
      up = isPressed;
    } else if (binding.equals("Down")) {
      down = isPressed;
    } else if (binding.equals("Jump")) {
      if (isPressed) { player.jump(); }
    }
  }

  /**
   * This is the main event loop--walking happens here.
   * We check in which direction the player is walking by interpreting
   * the camera direction forward (camDir) and to the side (camLeft).
   * The setWalkDirection() command is what lets a physics-controlled player walk.
   * We also make sure here that the camera moves with player.
   */
  //@Override
  override def simpleUpdate(tpf: Float): Unit = {
    camDir.set(cam.getDirection()).multLocal(2.6f);
    camLeft.set(cam.getLeft()).multLocal(0.4f);
    walkDirection.set(0, 0, 0);
    if (left) {
      walkDirection.addLocal(camLeft);
    }
    if (right) {
      walkDirection.addLocal(camLeft.negate());
    }
    if (up) {
      walkDirection.addLocal(camDir);
    }
    if (down) {
      walkDirection.addLocal(camDir.negate());
    }
    player.setWalkDirection(walkDirection);
    cam.setLocation(player.getPhysicsLocation());

    doCubeStuff(tpf);
  }

  def doCubeStuff(tpf: Float) {
//    boxie.move(0.016f*10.0f, 0, 0)
//    boxie.move(10*tpf, 0, 0)
  }

  def createCube() {
    var b = new Box(Vector3f.ZERO, 4, 4, 4);
    boxie = createGeometryFromMesh(b, "box", boxie_location, ColorRGBA.Yellow)
    boxie.addControl(new RigidBodyControl(1.0f))
    bulletAppState.getPhysicsSpace().add(boxie)
    rootNode.attachChild(boxie)

    val cubemover = ActorSystem("System").actorOf(Props(new CubeMover(boxie)), "CubeMover")
    cubemover ! Go
  }

  def createGeometryFromMesh(mesh: Mesh, name: String, loc: Vector3f, color: ColorRGBA): Geometry =
    {
      var geom = new Geometry(name, mesh)
      var mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
      mat.setColor("Color", color)
      geom.setMaterial(mat)
      geom.setLocalTranslation(loc)
      geom;
    }

}

case object Go

class CubeMover(boxie: Geometry) extends Actor {
  
    def receive = {
      case Go =>
        println("Hello World!")
        while(true)
        {
        	Thread.sleep(160)
        	boxie.move(0.016f*10.0f, 0, 0)          
        }
    }
    /*
     * uncaught exception thrown in thread LWJGL Renderer Thread,5,main
     * illegalstateexception: scene graph is not properly updated for rendering.
     * state was changed after rottnode.updategeometricstate() call.
     * make shure you do not modify the scene from another thread!
     * problem spatial name: root node
     * 
     * uncomment "cubemover ! Go" to make it work
     */
  }




