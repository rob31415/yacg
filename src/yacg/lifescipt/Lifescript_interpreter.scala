package yacg.lifescipt

import yacg.events._
import java.util.concurrent.CountDownLatch
import java.util.concurrent.PriorityBlockingQueue
import scala.concurrent._
import ExecutionContext.Implicits.global
//import scala.util.{ Success, Failure }

// lifescript interpreter (per NPC)
trait Lifescript_interpreter {
  //@TODO: no new operator for every event (maybe use java.util.concurrent.locks.Condition?)
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
    running = false
  }

  private def loop() {
    while (running) {
      {
        println("LSI sleeping", q.size())
        signal.await()
        println("LSI waking up", q.size())
        val event = q.peek()

        if (event.elevated_prio) {
          handle_elevated_prio_event(event)
        } else {
        	println("normal-prio event")
          if (handle(event)) q.take
        }
        signal = new CountDownLatch(1)
      }
    }
  }

  private def event_comes_from_worker_thread(event: Event): Boolean = event.data == 'thread_ended

  private def handle_elevated_prio_event(event: Event) {
    if (event_comes_from_worker_thread(event)) {
      println("event from W")
      q.take()
      if (q.size > 0) if (handle(q.peek)) q.take
    } else {
      if (worker_process_running) {
        println("terminating currently running W")
        worker_process_running = false
      }
      println("clearing q")
      q.clear()
      //@TODO: process event
    }
  }

}
