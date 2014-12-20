package yacg.jme

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape
import com.jme3.bullet.control.CharacterControl
import com.jme3.math.Vector3f
import yacg.util.Logger
import com.jme3.renderer.Camera
import com.jme3.input.InputManager
import com.jme3.input.controls.KeyTrigger
import com.jme3.input.KeyInput
import com.jme3.input.controls.MouseButtonTrigger
import yacg.igo.Scene_graph_interface
import yacg.igo.Npc
import yacg.events.Event_with_locationdata
import yacg.time.In_game_clock
import com.jme3.collision.CollisionResults
import com.jme3.math.Ray
import com.jme3.math.Vector2f
import com.jme3.scene.Node
import com.jme3.input.controls.InputListener
import yacg.util.Svg
import yacg.Main
import yacg.dialogSystem.Dialog
import yacg.gui.Gui

object Player extends Logger {
  private var left: Boolean = false;
  private var right: Boolean = false;
  private var up: Boolean = false;
  private var down: Boolean = false;
  private var upup: Boolean = false;
  private var player: CharacterControl = _
  var camDir: Vector3f = new Vector3f()
  var camLeft: Vector3f = new Vector3f()
  var camUp: Vector3f = new Vector3f()
  var walkDirection: Vector3f = new Vector3f()
  var jme_interface: Jme_interface = _

  def init(jme_interface: Jme_interface) {
    this.jme_interface = jme_interface
    val capsuleShape = new CapsuleCollisionShape(2f, 2f, 2)

    //val capsuleShape = new BoxCollisionShape()
    //player = new BetterCharacterControl(1.0f,1.0f,1.0f) //capsuleShape, 0.05f)
    //player.setGravity(new Vector3f(0,-10,0))
    player = new CharacterControl(capsuleShape, 0.01f)
    player.setGravity(3.4f)

    player.setJumpSpeed(20)
    player.setFallSpeed(30)
    player.setGravity(10)
    player.setPhysicsLocation(new Vector3f(-2561, 30, -831.6f))

    log_debug("bulletAppState ps.add player")
    jme_interface.bullet_app_state.getPhysicsSpace().add(player);

    initCamera(jme_interface.camera)
    setup_keys(jme_interface.input_mgr, jme_interface.input_listener)
  }

  def initCamera(cam: Camera) {
    cam.setLocation(new Vector3f(0, 200, 0))
    cam.setFrustumFar(8000.0f)
    //cam.
    //this.getCamera().lookAt(boxie.geometry.getLocalTranslation(), Vector3f.UNIT_Y)
    //this.getCamera().lookAt(new Vector3f(-370, 200, 500), Vector3f.UNIT_Y)
    //flyCam.setEnabled(false)
    //flyCam.setMoveSpeed(500f)
  }

  def setup_keys(input_mgr: InputManager, input_listener: InputListener) {
    val keys = Map("Left" -> new KeyTrigger(KeyInput.KEY_A),
      "Right" -> new KeyTrigger(KeyInput.KEY_D),
      "Up" -> new KeyTrigger(KeyInput.KEY_W),
      "Down" -> new KeyTrigger(KeyInput.KEY_S),
      "Jump" -> new KeyTrigger(KeyInput.KEY_SPACE),
      "g" -> new KeyTrigger(KeyInput.KEY_G),
      "h" -> new KeyTrigger(KeyInput.KEY_H),
      "t" -> new KeyTrigger(KeyInput.KEY_T),
      "q" -> new KeyTrigger(KeyInput.KEY_Q),
      "i" -> new KeyTrigger(KeyInput.KEY_I),
      "p" -> new KeyTrigger(KeyInput.KEY_P),
      "y" -> new KeyTrigger(KeyInput.KEY_Y),
      "r" -> new KeyTrigger(KeyInput.KEY_R),
      "m" -> new KeyTrigger(KeyInput.KEY_M),
      "c" -> new KeyTrigger(KeyInput.KEY_C),
      "return" -> new KeyTrigger(KeyInput.KEY_RETURN),
      "o" -> new KeyTrigger(KeyInput.KEY_O))

    keys.foreach(keyval => { input_mgr.addMapping(keyval._1, keyval._2); input_mgr.addListener(input_listener, keyval._1) })

    input_mgr.addMapping("mouse-l", new MouseButtonTrigger(0))
    input_mgr.addListener(input_listener, "mouse-l")
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
    } else if (binding.equals("q")) {
      upup = isPressed;
    } else if (binding.equals("Jump")) {
      if (isPressed) { player.jump(); }
    }

    if (!isPressed) {
      binding match {
        case "mouse-l" => Scene_graph_interface.get_igo_by_id("frank").get.asInstanceOf[Npc].put(new Event_with_locationdata(true, 'moveto, pick(jme_interface.camera, jme_interface.root_node)))
        case "p" => pick_output()
        case "t" => log_debug(In_game_clock.game_time.toString)
        case "o" => Svg.export_igog(Main.basepath_assets + "/yacg_igos.svg", Main.basepath_assets + "/Textures/heightmap2/heightmap_master_merica.png")
        case "r" =>  Scene_graph_interface.refresh()
        // case "h" => Terrain.get_height_at(101, -302)
        case "i" => {Svg.update_igog(Main.basepath_assets + "/yacg_igos.svg"); Scene_graph_interface.reinit() } 
        case "y" => {log_debug(yacg.persistence.Db.dump); log_debug(Scene_graph_interface.dump_current_locations); Scene_graph_interface.getIgosByType()}
        case "m" => Terrain.createMosaic(Main.basepath_assets + "/terrainTextureMosaic.png")
        case "c" => Dialog.initiate(findDialogPartnerId())
        case "return" => Dialog.pickChoice(Gui.getCurrentChoiceNumber())
        case _ =>  //log_debug("ignored key " + binding)
      }
    }

  }
  
  def findDialogPartnerId(): String = {
    // new Dialog(Scene_graph_interface.get_igo_by_id("bill").get)
    "bill"
  }

  def pick_output() {
    val from_img = pick(jme_interface.camera, jme_interface.root_node)
    val from_mesh = Terrain.get_mesh_height(from_img.x, from_img.z)
    log_debug("pick from img : " + from_img.x + ", " + from_img.y + ", " + from_img.z)
    log_debug("pick from mesh: " + from_img.x + ", " + from_mesh + ", " + from_img.z)
    log_debug("player xyz: " + player.getPhysicsLocation().x + " " + player.getPhysicsLocation().y + " " + player.getPhysicsLocation().z + "; " + player.getPhysicsLocation())
  }

  def update(camera: Camera, tpf: Float) {
    camDir.set(camera.getDirection()).multLocal(1.0f);	//6.6f	56
    camLeft.set(camera.getLeft()).multLocal(1.0f);	//2.4f	50
    camUp.set(camera.getUp()).multLocal(1.0f);	//52
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
    if (upup) {
      walkDirection.addLocal(camUp);
    }
    player.setWalkDirection(walkDirection);
    camera.setLocation(player.getPhysicsLocation());

    Scene_graph_interface.set_tpf_on_all_npcs(tpf)
  }

  def pick(camera: Camera, root_node: Node): Vector3f = {
    val results = new CollisionResults()
    val ray = new Ray(camera.getLocation(), camera.getDirection())
    root_node.collideWith(ray, results)

    var x = player.getPhysicsLocation().x
    var z = player.getPhysicsLocation().z

    if (results.size() > 0) {
      val target = results.getClosestCollision().getGeometry()
      x = results.getClosestCollision().getContactPoint().x
      z = results.getClosestCollision().getContactPoint().z
    } else {
      log_warn("pick didn't collide with anything, using player position as default instead")
    }

    var y = Terrain.get_mesh_height(x, z)

    new Vector3f(x, y, z)
  }

}