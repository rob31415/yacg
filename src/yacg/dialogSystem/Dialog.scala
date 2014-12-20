package yacg.dialogSystem

import yacg.igo.Igo
import org.jruby.embed.ScriptingContainer
import yacg.gui.Gui
import yacg.util.Logger
import yacg.igog.Igog

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

  val fileContent = scala.io.Source.fromFile(yacg.Main.basepath_assets + "/Dialog/" + partner + ".dialogScript").mkString
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

object Dialog {
  private var dialog: Dialog = null

  def initiate(id: String): Dialog = {
    if (!id.isEmpty()) {
      Gui.showDialog()
      if (dialog == null) {
        dialog = new Dialog(id)
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
