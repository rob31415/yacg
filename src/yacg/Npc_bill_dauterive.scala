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

class Npc_bill_dauterive(igo_id: Int, terrain: TerrainGrid, enqueue: (Callable[Unit]) => Future[Unit])
  extends Igo(1, enqueue) {

  val box = new Box(Vector3f.ZERO, 4, 4, 4)
  geo = Igo_repo.createGeometryFromMesh(box, "box", new Vector3f(0, 150, 0), ColorRGBA.Yellow)
  geo.addControl(new RigidBodyControl(1.0f))

  var target: Vector2f = _

  override def handle(event: Event): Boolean = {

    if (event.data == 'moveto) {

      if (worker_process_running) {
        println("W already running")
      } else {
        println("spawning W")
        worker_process_running = true
        future {
          while (worker_process_running) {
            Thread sleep ((fw.tpf * 1000) toLong)
            Thread `yield`

            if (target == null) {
              target = event.location
            } else {
              var current_location = new Vector2f(geo.getLocalTranslation().x, geo.getLocalTranslation().z)
              var direction = target subtract current_location

              if (direction.length < 1f) {
                target = null;
                worker_process_running = false;
              } else {
                direction = direction.normalize
                direction.mult(0.5f)
                println(direction.toString())

                var x = direction.x
                var y = 0f
                var z = direction.y

                var move = new Vector3f(x, y, z)

                val callable = new Callable[Unit]() {
                  def call() {
                    //geo.move(10 * fw.tpf, 0, 0)

                    geo.move(move)

                    x = geo.getLocalTranslation().x
                    y = 6.0f + terrain.getHeightmapHeight(new Vector2f(geo.getLocalTranslation().x, geo.getLocalTranslation().z))
                    z = geo.getLocalTranslation().z
                    geo.setLocalTranslation(x, y, z)

                    //println(x, y, z)
                  }
                }
                enqueue(callable)
              }

            }

          }

        }

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

}