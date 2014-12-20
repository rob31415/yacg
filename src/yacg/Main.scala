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
import yacg.igo.Scene_graph_interface
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
import scala.util.Random
import com.jme3.post.filters.FogFilter
import yacg.time.In_game_clock
import time.In_game_clock
import yacg.igog.Igog
import yacg.igo.Npc
import yacg.jme.Terrain
import yacg.jme.Player
import yacg.jme.Jme_interface
import yacg.gui.Gui
import yacg.jme.Jme_interface
import java.util.concurrent.Future
import java.util.concurrent.Callable
import com.jme3.app.Application
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import yacg.util.Logger
import com.jme3.niftygui.NiftyJmeDisplay
import de.lessvoid.nifty.screen._
import de.lessvoid.nifty._
import com.jme3.post._
import filter.OldFilmFilter
import yacg.gui.AppStateDialog
import yacg.gui.AppStateRunning

object Main extends Logger {
  val basepath_assets = System.getProperty("user.dir") + "/assets/"
  val basepath_igog_db = System.getProperty("user.dir") + "/igogdb/"
  val width = 800
  val height = 600
  val app = new Main

  def main(args: Array[String]): Unit = {

    import java.util.logging.{ Logger, Level }
    Logger.getLogger("").setLevel(Level.WARNING);

    val settings: AppSettings = new AppSettings(true);

    if (Configurator.fullscreen) {
      val device: GraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      val modes = device.getDisplayModes()

      GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().foreach(el => log_debug("screendevice " + el.getIDstring()))
      modes.foreach(el => log_debug("displaymode " + el.getBitDepth() + " " + el.getRefreshRate() + " " + el.getWidth() + " " + el.getHeight()))

      val i = 0
      settings.setResolution(modes(i).getWidth(), modes(i).getHeight());
      settings.setFrequency(modes(i).getRefreshRate());
      settings.setDepthBits(modes(i).getBitDepth())
      //settings.setBitsPerPixel(16);
      settings.setFullscreen(device.isFullScreenSupported());
    } else {
      settings.setResolution(width, height);
      settings.setBitsPerPixel(32);
      settings.setFullscreen(false)
    }

    app.setShowSettings(false);
    app.setSettings(settings);
    app.setPauseOnLostFocus(false)

    //MouseInput.get().setCursorVisible(true);    
    //org.lwjgl.input.Mouse.setGrabbed(false)

    app.start
  }
}

class Main extends SimpleApplication with Logger with ActionListener with Jme_interface {

  var fw = new float_wrap(1.0f)

  override def enqueue[Unit](callable: Callable[Unit]): Future[Unit] = super[SimpleApplication].enqueue(callable)

  override def simpleInitApp: Unit = {
    assetManager.registerLocator(Main.basepath_assets, classOf[FileLocator])

    init_jme_interface()
    setupAppStates
    Igog.init(Main.basepath_igog_db)

    Terrain.init(this)
    Scene_graph_interface init (this)
    Player.init(this)
    yacg.jme.Light.init(rootNode)
    Lifescript_scheduler.init
    yacg.jme.Sky.init(this)
    Gui.init(this, this.audioRenderer, guiViewPort, settings.getWidth, settings.getHeight)
    audio.Audio.init(assetManager, rootNode)
    //yacg.jme.Fog.init(this.asset_mgr, this.viewport, rootNode)

    bla
    blubb
    //yacg.dialogSystem.DialogTest.__start__
    
  }

  
  def setupAppStates {
    this.stateManager.attach(new AppStateRunning)
    this.stateManager.attach(new AppStateDialog)
    //TODO invenStory
    bullet_app_state = new BulletAppState()
    log_debug("attach bulletappstate to statemgr")
    stateManager.attach(bullet_app_state)
  }
  
  
  def bla {
    var material = new Material(assetManager, "Materials/tksGrass.j3md") //
    //this.asset_mgr.loadMaterial("Materials/tksGrass.j3md")
    //Scene_graph_interface.get_igo_by_id("bill").get.geo.setMaterial(material)
    Scene_graph_interface.get_igo_by_id("frank").get.geo.setMaterial(material)

  }

  def blubb {
    val processor = new FilterPostProcessor(assetManager)
    val filter = new OldFilmFilter(new ColorRGBA(112f / 255f, 66f / 255f, 20f / 255f, 1f), 0.75f, 0.08f, 0.4f, 1.1f)
/*    filter.setNoiseDensity(0.01f)
    filter.setScratchDensity(0.02f)
    filter.setVignettingValue(0.02f)
    filter.setColorDensity(0.03f)
    *
    */
    filter.setFilterColor(new com.jme3.math.ColorRGBA(112f / 255f, 66f / 255f, 20f / 255f, 1f))
    processor.addFilter(filter)
    viewport.addProcessor(processor)
  }

  override def destroy {
    Igog.shutdown
    Scene_graph_interface.shutdown
    super.destroy
  }

  override def simpleUpdate(tpf: Float) {
    Player.update(camera, tpf)
  }

  override def onAction(binding: String, isPressed: Boolean, tpf: Float) = Player.onAction(binding, isPressed, tpf)

  def init_jme_interface() {
    root_node = rootNode
    asset_mgr = assetManager
    camera = getCamera()
    viewport = viewPort
    input_mgr = inputManager
    input_listener = this
    gui_node = guiNode
  }

}


