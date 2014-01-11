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


class Npc_bill_dauterive(igo_id: Int, enqueue: (Callable[Unit]) => Future[Unit])
  extends Igo(1, enqueue) {

  val box = new Box(Vector3f.ZERO, 4, 4, 4);
  geo = Igo_repo.createGeometryFromMesh(box, "box", new Vector3f(0, 450, 0), ColorRGBA.Yellow)
  geo.addControl(new RigidBodyControl(1.0f))

  override def handle(event: Event): Boolean = {

    if (event.data == 'move) {

      if (worker_process_running) {
        println("W already running")
      } else {
        println("spawning W")
        worker_process_running = true
        future {
          while (worker_process_running) {
            Thread sleep ((fw.tpf * 1000) toLong)
            Thread `yield`

            //concurrency error from LWGL
            //geometry.move(fw.tpf * 10.0f, 0, 0)

            val callable = new Callable[Unit]() {
              def call() {
                geo.move(10 * fw.tpf, 0, 0)
              }
            }
            enqueue(callable)
          }
        }

      }

    }

    if (event.data == 'stop) {

      if (worker_process_running) {
        println("terminating W")
        worker_process_running = false
      } else {
        println("nonsense")
      }

    }

    true

  }
}