package yacg

import com.jme3.scene.shape.Box
import com.jme3.math.Vector3f
import com.jme3.math.ColorRGBA
import com.jme3.bullet.control.RigidBodyControl
import java.util.concurrent.Future
import java.util.concurrent.Callable
import com.jme3.scene.Geometry
import scala.concurrent._
import ExecutionContext.Implicits.global
import java.util.concurrent.Callable
import java.util.concurrent.Future
import yacg.igo.Igo
import yacg.events._
import yacg.igo.Igo_repo
import com.jme3.math.Vector2f
import com.jme3.terrain.geomipmap.TerrainGrid
import com.jme3.asset.AssetManager

class Npc_bill_dauterive(igo_id: Int,
  terrain: TerrainGrid,
  assetManager: AssetManager,
  enqueue: (Callable[Unit]) => Future[Unit])
  extends Igo(1, assetManager, enqueue) {

  var target: Vector2f = _

  //val box = new Box(Vector3f.ZERO, 10, 10, 10)
  //geo = Igo_repo.createGeometryFromMesh(box, "box", new Vector3f(0, 150, 0), ColorRGBA.Yellow)
  geo = assetManager.loadModel("Models/bill_cube.j3o")
  geo.setLocalScale(new Vector3f(4,4,4))
  geo.addControl(new RigidBodyControl(1.0f))



  override def handle(event: Event): Boolean = {

    if (event.data == 'moveto) {
      if (worker_process_running) {
        println("W already running")
        return false
      } else {
        println("spawning W")
        spawn(event)
      }
    }

    if (event.data == 'stop) {
      if (worker_process_running) {
        println("terminating W")
        worker_process_running = false
        target = null
      } else {
        println("nonsense")
      }
    }

    //ignore all other event-types

    true

  }

  def spawn(event: Event) {
    worker_process_running = true

    future {
      while (worker_process_running) {
        Thread sleep ((fw.tpf * 1000) toLong)
        Thread `yield`

        if (target == null) {
          target = event.location
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
        val y = 4 + (3.0f * terrain.getHeightmapHeight(new Vector2f(geo.getLocalTranslation().x, geo.getLocalTranslation().z)))
        val z = geo.getLocalTranslation().z
        geo.setLocalTranslation(x, y, z)

        //println(x, y, z)
      }
    }
    enqueue(callable)
  }

  def end_worker_thread {
    println("end_worker_thread")
    target = null;
    worker_process_running = false;
    put(new Event(true, 'thread_ended))
  }

}