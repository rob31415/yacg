package yacg.igo

import yacg.events.Event
import com.jme3.asset.AssetManager
import yacg.igog.Igog
import yacg.events.Event_with_stringdata
import com.jme3.math._
import yacg.events.Event_with_locationdata
import scala.concurrent._
import ExecutionContext.Implicits.global
import java.util.concurrent.Callable
import java.util.concurrent.Future
import yacg.jme.Terrain

class Mobile(name: String, file_name: String, assetManager: AssetManager, enqueue: yacg.jme.Jme_interface.enqueueLamba)
  extends Igo(name, file_name, assetManager, enqueue) {

  typeName = "mobile"
  var target: Vector2f = _

  override def handle(event: Event): Boolean = {

    log_debug(this.name + " received event of type" + event.event_type)

    if (event.event_type == 'moveto) {
      if (worker_process_running) {
        log_debug("W already running")
        return false
      } else {
        val xy = Igog.get_location(name, event.asInstanceOf[Event_with_stringdata].data)
        val event_location = new Event_with_locationdata(false, 'moveto, new Vector3f(xy._1, 0, xy._2))
        spawn(event_location)
      }
    }

    if (event.event_type == 'stop) {
      if (worker_process_running) {
        log_debug("terminating W")
        worker_process_running = false
        target = null
      } else {
        log_debug("nonsense")
      }
    }

    //ignore all other event-types

    true

  }

  def stop = end_worker_thread

  def spawn(event: Event_with_locationdata) {
    worker_process_running = true

    log_debug("starting thread - worker for Igo")

    try {
      future {
        while (worker_process_running) {
          Thread sleep ((fw.tpf * 1000) toLong)
          Thread `yield`

          if (target == null) {
            target = new Vector2f(event.location.x, event.location.z)
          } else {
            val direction = calc_move_vector
            if (direction.isDefined) {
              modify_scenegraph(direction.get)
            } else {
              end_worker_thread
            }
          }
        }
      }
    } catch {
      case e: Throwable => this.log_error("", e)
    }
  }

  def calc_move_vector: Option[Vector3f] = {
    var current_location = new Vector2f(geo.getLocalTranslation().x, geo.getLocalTranslation().z)
    var direction = target subtract current_location

    if (direction.length > 1f) {
      direction = direction.normalize
      direction.mult(0.5f)
      //println(direction.toString())

      var x = direction.x
      var y = 0f
      var z = direction.y

      return Some(new Vector3f(x, y, z))
    }

    None
  }

  def modify_scenegraph(direction: Vector3f) {
    val callable = new Callable[Unit]() {
      def call() {
        //geo.move(10 * fw.tpf, 0, 0)

        geo.move(direction)
        //@TODO: rotate in movement-direction
        //geo.lookAt(geo.getLocalTranslation, direction)

        val x = geo.getLocalTranslation().x
        val y = Terrain.get_mesh_height(geo.getLocalTranslation().x, geo.getLocalTranslation().z)
        val z = geo.getLocalTranslation().z
        geo.setLocalTranslation(x, y, z)

        //log_debug("xyz=" + x + " " + y + " " + z)
      }
    }
    enqueue(callable)
  }

  def end_worker_thread {
    if (worker_process_running) {
      log_debug("end_worker_thread")
      target = null;
      worker_process_running = false;
      put(new Event(true, 'thread_ended))
    }
  }

}