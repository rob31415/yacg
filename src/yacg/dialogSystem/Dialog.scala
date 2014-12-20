package yacg.dialogSystem

import yacg.igo.Igo
import org.jruby.embed.ScriptingContainer
import yacg.gui.Gui
import yacg.util.Logger
import yacg.igog.Igog
import java.nio.file.Files
import java.nio.file.Paths

object ContextGlobal {
  def knows(name: String): Boolean = false
  def ?(variable: String): Boolean = true
  def ?-(variable: String): Boolean = true
  def !(variable: String) {}
  def !-(variable: String) {}
  //todo: how to do choice and goto?
}

class Player(var methodNames: java.util.ArrayList[String]) {
  def says(text: String) = Gui.displayText(text)
  def choice(texts: java.util.ArrayList[String], methodNames: java.util.ArrayList[String]) {
    this.methodNames = methodNames
    Gui.displayChoice(texts.toArray().toList)
  }
  def isset(variable: String): Boolean = Igog.playerHasProperty(variable)
  def set(variable: String) = Igog.setPropertyOnPlayer(variable)
  def unset(variable: String) = Igog.removePropertyFromPlayer(variable)
  def end() {
    Dialog.end()
  }
}

class Partner(name: String) {
  def says(text: String) = Gui.displayText(text)
  def isset(variable: String): Boolean = Igog.igoHasProperty(name, variable)
  def set(variable: String) = Igog.setPropertyOnIgo(name, variable)
  def unset(variable: String) = Igog.removePropertyFromIgo(name, variable)
}

class Dialog(partner: String) extends Logger {
  log_debug("about to create new Dialog for id=" + partner)
  val fileContent = scala.io.Source.fromFile(Dialog.getDialogScriptFilename(partner)).mkString
  val container = new ScriptingContainer()
  val player = new Player(null)
  container.put("Player", player)
  container.put("Partner", new Partner(partner))

  val shellCode = """
	def choice(choices)
	  texts = choices.keys
	  methodNames = choices.values.collect{|element| element.to_s}
	  Player.choice java.util.ArrayList.new(texts), java.util.ArrayList.new(methodNames) 
	end
    
    def exit
    	Player.end
    end
    
    def wait(seconds = 3)
    	sleep seconds
    end
    
    start
    """

  val receiver = container.runScriptlet(fileContent + "\n" + shellCode)

  def callMethod(choiceNumber: Int) {
    container.callMethod(receiver, player.methodNames.get(choiceNumber))
  }

  def end() {
    container.clear()
  }
}

object Dialog extends Logger {
  private var dialog: Dialog = null

  def getDialogScriptFilename(id: String): String = {
    yacg.Main.basepath_assets + "/Dialog/" + id + ".dialogScript"
  }

  def initiate(id: String): Dialog = {
    if (id.isEmpty()) {
      log_debug("no id given, can't initiate dialog")
    } else {
      if (dialog == null) {
        if (Files.exists(Paths.get(getDialogScriptFilename(id)))) {
          Gui.showDialog()
          dialog = new Dialog(id)
        } else
          log_warn("no dialogScript for '" + id + "' - can't show dialog gui")
      }
    }
    dialog
  }

  def pickChoice(choiceNumber: Int) = dialog.callMethod(choiceNumber)

  def end() {
    Gui.exit()
    if (dialog != null) {
      dialog.end
    }
    dialog = null
  }

}
