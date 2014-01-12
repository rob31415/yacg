package yacg.igo
import java.util.concurrent.Callable
import java.util.concurrent.Future
import yacg.lifescipt.Lifescript_interpreter
import com.jme3.scene.Geometry
import com.jme3.asset.AssetManager
import com.jme3.scene.Spatial

class float_wrap(var tpf: Float) // wrap byval with a byref

abstract class Igo(igo_id: Int, assetManager: AssetManager, enqueue: (Callable[Unit]) => Future[Unit]) extends Lifescript_interpreter {

  var fw = new float_wrap(0.016f)	//experience value on core i3, HD-graphics
  var geo: Spatial = _				//Geometry extends Spatial

}