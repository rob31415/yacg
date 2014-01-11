package yacg.lifescipt

import yacg.events._
import java.util.concurrent.CountDownLatch
import java.util.concurrent.PriorityBlockingQueue
import scala.concurrent._
import ExecutionContext.Implicits.global
//import scala.util.{ Success, Failure }


// lifescript interpreter (per NPC)
trait Lifescript_interpreter {
  private var signal = new CountDownLatch(1)
  private var running = false
  private val q = new PriorityBlockingQueue[Event]
  protected var worker_process_running = false

  def put(event: Event) {
    q.put(event)
    println("q put " + event.toString())
    signal countDown
  }

  def handle(event: Event): Boolean

  def run {
    running = true
    future {
      loop
    }
  }

  def terminate {
    worker_process_running
  }

  private def loop() {
    while (running) {
    {
      println("LSI sleeping")
      signal.await()
      println("LSI waking up")
      val event = q.peek()

      if (event.elevated_prio) {
        q.clear()
        if (worker_process_running) {
          println("terminating currently running W")
          worker_process_running = false
          //@TODO handleevent
        } else {
          //@TODO handleevent
        }
      } else {
        if(handle(event)) q.take()
      }
      signal = new CountDownLatch(1)
    }
    }
  }
  
  
}
