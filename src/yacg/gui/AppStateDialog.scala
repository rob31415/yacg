package yacg.gui

import de.lessvoid.nifty.screen.ScreenController
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.app.SimpleApplication

class AppStateDialog extends AbstractAppState with ScreenController {
  def bind(x$1: de.lessvoid.nifty.Nifty, x$2: de.lessvoid.nifty.screen.Screen): Unit = {}
  def onEndScreen(): Unit = {}
  def onStartScreen(): Unit = {}

  def bla { println("YEA") }

  def initialize(statemanager: AppStateManager, app: SimpleApplication) {

  }
  
  override def update(tpf: Float) {
    
  }

  override def cleanup() {
  }
}

