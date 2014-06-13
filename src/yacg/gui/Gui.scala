package yacg.gui

import com.jme3.niftygui.NiftyJmeDisplay
import yacg.jme.Jme_interface
import com.jme3.audio.AudioRenderer
import com.jme3.renderer.ViewPort
import yacg.igo.Scene_graph_interface
import com.jme3.scene.shape.Box
import com.jme3.math.Vector3f
import com.jme3.scene.Node
import de.lessvoid.nifty.Nifty
import de.lessvoid.nifty.screen.Screen
import com.jme3.math.ColorRGBA
import yacg.util.Logger

trait Gui extends Logger {
  var nifty: Nifty = _
  
  def init_gui(jme_interface: Jme_interface, audio_renderer: AudioRenderer, viewport: ViewPort, width: Float, height: Float) {
    /*
    val niftyDisplay = new NiftyJmeDisplay(jme_interface.asset_mgr, jme_interface.input_mgr, audio_renderer, viewport)
    nifty = niftyDisplay.getNifty()
    nifty.fromXml("Interface/gui.xml", "start", jme_interface.screen_controller)
    viewport.addProcessor(niftyDisplay)
    */
    createPickMark(jme_interface.gui_node, width, height)
  }

  def bind(nifty: Nifty, screen: Screen) {
    log_debug("bind( " + screen.getScreenId() + ")");
  }

  def onStartScreen() {
    log_debug("onStartScreen");
  }

  def onEndScreen() {
    log_debug("onEndScreen");
  }

  def quit() {
    nifty.gotoScreen("end");
  }

  def createPickMark(gui_node: Node, width: Float, height: Float) {
    val box = new Box(Vector3f.ZERO, 2, 2, 2)
    val geo = Scene_graph_interface.createGeometryFromMesh(box, "pick_mark", new Vector3f(0, 0, 0), ColorRGBA.Red)
    geo.setLocalTranslation(width / 2, height / 2, 0);
    gui_node.attachChild(geo);
  }

}