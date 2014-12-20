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
import com.jme3.niftygui.NiftyJmeDisplay
import de.lessvoid.nifty.screen._
import de.lessvoid.nifty._
import de.lessvoid.nifty.elements.render.TextRenderer
import de.lessvoid.nifty.controls.ListBox
import de.lessvoid.nifty.controls.Label

object Gui extends Logger with ScreenController {
  var nifty: Nifty = _
  var textRenderer: TextRenderer = _
  var label: Label = _

  def init(jme_interface: Jme_interface, audio_renderer: AudioRenderer, viewport: ViewPort, width: Float, height: Float) {
    val niftyDisplay = new NiftyJmeDisplay(jme_interface.asset_mgr, jme_interface.input_mgr, audio_renderer, viewport)
    nifty = niftyDisplay.getNifty()
    viewport.addProcessor(niftyDisplay)

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

  def showDialog() {
    nifty.fromXml("Interface/gui2.xml", "start", this)
    label = nifty.getCurrentScreen().findNiftyControl("text", classOf[Label])
  }
  
  def exit() {
    getChoiceDisplay().clear()
    nifty.exit()
  }

  def displayText(text: String) {
    //val x = label.getElement().getRenderer(classOf[TextRenderer]).setText(text)
    label.setText(text)
    log_debug("displayText: " + text)
  }

  private def getChoiceDisplay(): ListBox[String] = {
    nifty.getCurrentScreen().findNiftyControl("listo", classOf[ListBox[String]])
  }

  def displayChoice(lines: List[Object]) {
    getChoiceDisplay().clear()
    lines.foreach(element => getChoiceDisplay().addItem(element.toString()))
  }

  def getCurrentChoiceNumber(): Int = {
    getChoiceDisplay().getFocusItemIndex()
  }

}