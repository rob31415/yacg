package yacg.jme

import java.util.concurrent.Future
import java.util.concurrent.Callable
import com.jme3.scene.Node
import com.jme3.asset.AssetManager
import com.jme3.bullet.BulletAppState
import com.jme3.renderer.ViewPort
import com.jme3.input.InputManager
import com.jme3.input.controls.InputListener
import de.lessvoid.nifty.screen.ScreenController

object Jme_interface {
  type enqueueLamba = (Callable[Unit]) => Future[Unit]
}

trait Jme_interface {

  var root_node: Node = _
  var bullet_app_state: BulletAppState = _
  var asset_mgr: AssetManager = _
  def enqueue[Unit](dummy: Callable[Unit]): Future[Unit]
  var camera: com.jme3.renderer.Camera = _
  var viewport: ViewPort = _
  var input_mgr: InputManager = _
  var input_listener: InputListener = _
  var screen_controller: ScreenController = _
  var gui_node: Node = _
}
