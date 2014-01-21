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
import java.util.concurrent.Callable
import yacg.igo.float_wrap
import yacg.igo.Igo
import yacg.events._
import yacg.igo.Igo_repo
import yacg.lifescipt.Lifescript_scheduler
import com.jme3.math.Vector2f
import com.jme3.collision.CollisionResults
import com.jme3.math.Ray
import com.jme3.input.controls.MouseButtonTrigger
import com.jme3.light.AmbientLight
import com.jme3.asset.TextureKey
import com.jme3.util.SkyFactory
import com.jme3.renderer.queue.RenderQueue.Bucket
import com.jme3.scene.Spatial
import com.jme3.renderer.queue.RenderQueue.ShadowMode
import com.jme3.shadow.DirectionalLightShadowRenderer
import com.jme3.shadow.DirectionalLightShadowFilter
import com.jme3.post.FilterPostProcessor
import com.jme3.shadow.PssmShadowRenderer
import com.jme3.shadow.PssmShadowRenderer.FilterMode
import com.jme3.system.AppSettings

object Main {
  def main(args: Array[String]): Unit = {

    import java.util.logging.{ Logger, Level }
    Logger.getLogger("").setLevel(Level.WARNING);

    val settings = new AppSettings(true);
    settings.setResolution(800, 600);
    settings.setBitsPerPixel(32);
    settings.setFullscreen(false)

    val app = new Main
    app.setShowSettings(false);
    app.setSettings(settings);
    app.start

  }
}

class Main extends SimpleApplication with ActionListener {

  private var mat_terrain: Material = _
  private var terrain: TerrainGrid = _
  private var bulletAppState: BulletAppState = _
  private var left: Boolean = false;
  private var right: Boolean = false;
  private var up: Boolean = false;
  private var down: Boolean = false;
  private var player: CharacterControl = _
  var camDir: Vector3f = new Vector3f()
  var camLeft: Vector3f = new Vector3f()
  var walkDirection: Vector3f = new Vector3f()
  var npc_bill: Igo = _
  var fw = new float_wrap(1.0f)

  override def simpleInitApp: Unit = {
    assetManager.registerLocator(System.getProperty("user.dir") + "/assets/", classOf[FileLocator])

    //flyCam.setMoveSpeed(500f)

    bulletAppState = new BulletAppState()
    System.out.println("attach bulletappstate to statemgr")
    stateManager.attach(bulletAppState)

    initMaterial
    initTerrain
    initGeo
    initCamera
    initLight
    setUpKeys
    Lifescript_scheduler.init
    createPickMark
    createSky
    createStaticModels
  }

  def createStaticModels {
    val geo = assetManager.loadModel("Models/house1.j3o")
    geo.setLocalScale(new Vector3f(24, 24, 24))
    geo.setLocalTranslation(new Vector3f(-350, 200, 470))
    geo.addControl(new RigidBodyControl(1.0f))
    bulletAppState.getPhysicsSpace().add(geo)
    rootNode.attachChild(geo)
  }

  def createSky {
    val sky = SkyFactory.createSky(assetManager, "Textures/misc/skydome2.bmp", true)
    rootNode.attachChild(sky)
    sky.setQueueBucket(Bucket.Sky)
    sky.setCullHint(Spatial.CullHint.Never)
  }

  def createPickMark {
    val box = new Box(Vector3f.ZERO, 2, 2, 2)
    val geo = Igo_repo.createGeometryFromMesh(box, "pick_mark", new Vector3f(0, 0, 0), ColorRGBA.Red)
    geo.setLocalTranslation(settings.getWidth() / 2, settings.getHeight() / 2, 0);
    guiNode.attachChild(geo);
  }

  def initGeo {
    Igo_repo init (rootNode, bulletAppState, assetManager, this.enqueue, terrain)
    npc_bill = Igo_repo get_igo_by_id (1)
    npc_bill run
  }

  def initCamera {
    this.getCamera().setLocation(new Vector3f(0, 1000, 0))
    //this.getCamera().lookAt(boxie.geometry.getLocalTranslation(), Vector3f.UNIT_Y)
    //this.getCamera().lookAt(new Vector3f(-370, 200, 500), Vector3f.UNIT_Y)
    this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f))
    this.getCamera().setFrustumFar(10000.0f)
    //flyCam.setEnabled(false)

    val capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1)

    //val capsuleShape = new BoxCollisionShape()
    //player = new BetterCharacterControl(1.0f,1.0f,1.0f) //capsuleShape, 0.05f)
    //player.setGravity(new Vector3f(0,-10,0))
    player = new CharacterControl(capsuleShape, 0.05f)

    player.setJumpSpeed(20)
    player.setFallSpeed(30)
    player.setGravity(30)
    player.setPhysicsLocation(new Vector3f(-300, 200, 400))

    System.out.println("bulletAppState ps.add player")
    bulletAppState.getPhysicsSpace().add(player);

  }

  def initMaterial {
    // http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:materials_overview

    /*
    this.mat_terrain = new Material(this.assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md") //
    map_diffuse.setWrap(WrapMode.Repeat)
    this.mat_terrain.setTexture("slopeColorMap", rock)
    this.mat_terrain.setFloat("slopeTileFactor", 32)
    this.mat_terrain.setFloat("terrainSize", 512)
     */

    mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
    mat_terrain.setBoolean("isTerrainGrid", false)
    mat_terrain.setBoolean("useTriPlanarMapping", false)
    mat_terrain.setFloat("Shininess", 0.0f)

    val map_diffuse = assetManager.loadTexture("Textures/misc/cracked-dirt/cracked-dirt-texture-01.jpg")
    map_diffuse.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("DiffuseMap", map_diffuse)
    mat_terrain.setFloat("DiffuseMap_0_scale", 64)

    val map_normal = assetManager.loadTexture("Textures/misc/cracked-dirt/cracked-dirt-texture-01_hn.jpg")
    map_diffuse.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("NormalMap", map_normal)

    mat_terrain.setTexture("AlphaMap", assetManager.loadTexture("Textures/misc/cracked-dirt/cracked-dirt-texture-01_mn.jpg"))

  }

  def initLight {
    val light = new DirectionalLight()
    //light.setDirection((new Vector3f(0.3f, -0.5f, 0.4f)).normalize())
    light.setDirection((new Vector3f(-0.1f, -0.4f, -1)))
    light.setColor(ColorRGBA.Yellow) //new ColorRGBA(200 / 255, 150 / 255, 50 / 255, 1))
    rootNode.addLight(light)

    val light2 = new AmbientLight()
    light2.setColor(ColorRGBA.Red)
    rootNode.addLight(light2)

    //initShadow(light)
  }

  //@TODO: shadow, y u no work !?
  def initShadow(sun: DirectionalLight) {
    val shadowmap_size = 256
    /*
    val dlsr = new DirectionalLightShadowRenderer(assetManager, shadowmap_size, 3);
    dlsr.setLight(sun);
    viewPort.addProcessor(dlsr);

    val dlsf = new DirectionalLightShadowFilter(assetManager, shadowmap_size, 3);
    dlsf.setLight(sun);
    dlsf.setEnabled(true);
    
    val fpp = new FilterPostProcessor(assetManager);
    fpp.addFilter(dlsf);
    viewPort.addProcessor(fpp);
*/

    val pssm = new PssmShadowRenderer(assetManager, 4096, 3);
    pssm.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
    pssm.setFilterMode(FilterMode.PCF4);
    viewPort.addProcessor(pssm);

    terrain.setShadowMode(ShadowMode.CastAndReceive)
    rootNode.setShadowMode(ShadowMode.Receive)
  }

  // @TODO: get the scale right, design a huge heightfield
  // @TODO: do good-looking texture-splatting
  // see http://www.gamasutra.com/blogs/AndreyMishkinis/20130716/196339/Advanced_Terrain_Texture_Splatting.php
  // and http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:materials_overview
  // section TerrainLighting.j3md

  //space time causation human-intent
  def initTerrain {

    this.terrain = new TerrainGrid("terrain", 512 + 1, 1024 + 1, new ImageTileLoader(assetManager,
      new Namer() {
        def getName(x: Int, y: Int): String = {
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
    this.terrain.setLocalScale(10f, 3f, 10f)
    System.out.println("add terrain to rootnode")
    this.rootNode.attachChild(this.terrain)

    //System.out.println("add lodctrlr to terrain")
    this.terrain.addControl(control)

    System.out.println("terrain.addctrl riidbocy")
    terrain.addControl(new RigidBodyControl(0))

    //val terrainCollisionShape = new HeightfieldCollisionShape(terrain.getHeightMap(), terrain.getLocalScale())
    //val terrain_phy = new RigidBodyControl(terrainCollisionShape, 0f)
    //this.terrain.addControl(terrain_phy);
    System.out.println("bulletAppState phyicspace.add terrain")
    bulletAppState.getPhysicsSpace().add(terrain);
  }

  def setUpKeys {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("g", new KeyTrigger(KeyInput.KEY_G));
    inputManager.addMapping("h", new KeyTrigger(KeyInput.KEY_H));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
    inputManager.addListener(this, "g");
    inputManager.addListener(this, "h");
    inputManager.addMapping("mouse-l", new MouseButtonTrigger(0))
    inputManager.addListener(this, "mouse-l")

  }

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

    if (binding == "mouse-l" && !isPressed) {
      npc_bill put new Event(false, 'moveto, pick)
    }

    if (binding == "g" && !isPressed) {
      npc_bill put new Event(false, 'moveto, new Vector2f(player.getPhysicsLocation().x, player.getPhysicsLocation().z))
    }

    if (binding == "h" && !isPressed) {
      npc_bill put new Event(true, 'stop)
    }

  }

  def pick: Vector2f = {
    val results = new CollisionResults()
    val ray = new Ray(cam.getLocation(), cam.getDirection())
    rootNode.collideWith(ray, results)

    var x = player.getPhysicsLocation().x
    var y = player.getPhysicsLocation().z
    println("****", x, y)

    if (results.size() > 0) {
      val target = results.getClosestCollision().getGeometry()
      x = results.getClosestCollision().getContactPoint().x
      y = results.getClosestCollision().getContactPoint().z
      println("****" + target.getName(), x, y)
    }

    new Vector2f(x, y)
  }

  override def simpleUpdate(tpf: Float) {
    camDir.set(cam.getDirection()).multLocal(6.6f);
    camLeft.set(cam.getLeft()).multLocal(2.4f);
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

    npc_bill.fw.tpf = tpf
  }

}



