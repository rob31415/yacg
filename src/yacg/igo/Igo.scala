package yacg.igo
import java.util.concurrent.Callable
import java.util.concurrent.Future
import yacg.lifescipt.Lifescript_interpreter
import com.jme3.scene.Geometry
import com.jme3.asset.AssetManager
import com.jme3.scene.Spatial
import com.jme3.export.Savable
import com.jme3.math.Vector3f
import yacg.jme.Jme_interface

class float_wrap(var tpf: Float) // wrap byval with a byref

object Igo {
  def createInstance(typeName: String, name: String, file_name: String, jme_interface: Jme_interface): Igo = {
    typeName match {
      case "npc" => return new Npc(name, file_name, jme_interface)
      case "static" => return new Static(name, file_name, jme_interface.asset_mgr)
      case _ => throw new RuntimeException("there is no igo with typename " + typeName)
    }
  }
}

abstract class Igo(val name: String, val file_name: String, assetManager: AssetManager, enqueue: yacg.jme.Jme_interface.enqueueLamba = null)
  extends Lifescript_interpreter with Savable {

  var fw = new float_wrap(0.016f) 	//experience value on core i3 laptop, HD-graphics
  var geo: Spatial = _ 				//Geometry extends Spatial
  var typeName: String = "igo"

  log_debug("creating new igo with id=" + name + " from file " + file_name)

  geo = assetManager.loadModel("Models/" + file_name) //.j3o
  geo.setName(name)
  geo.setUserData("id", name)
  geo.setUserData("igo_ref", this)
  geo.setLocalScale(new Vector3f(4, 4, 4))
  //geo.addControl(new RigidBodyControl(1.0f))
  //geo.setLocalTranslation(100, 100,100)

  // just some dummy stuff from Savable, which is needed to be able to do setUserData(igo)
  def read(x$1: com.jme3.export.JmeImporter): Unit = {}
  def write(x$1: com.jme3.export.JmeExporter): Unit = {}

  def stop
}