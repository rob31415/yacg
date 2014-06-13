package yacg.lifescipt

import yacg.events._
import java.util.concurrent.CountDownLatch
import java.util.concurrent.PriorityBlockingQueue
import scala.concurrent._
import ExecutionContext.Implicits.global
import yacg.util.Logger
//import scala.util.{ Success, Failure }

// lifescript interpreter (per NPC)
trait Lifescript_interpreter extends Logger {
  //@TODO: no new operator for every event (maybe use java.util.concurrent.locks.Condition?)
  private var signal = new CountDownLatch(1)
  private var running = false
  private val q = new PriorityBlockingQueue[Event]
  protected var worker_process_running = false

  def put(event: Event) {
    log_debug("fixin to put on q " + event.toString())
    q.put(event)
    log_debug("well, i done put event on q right there")
    signal countDown
  }

  // returns true if the event should be popped
  def handle(event: Event): Boolean

  def run {
    running = true
    log_debug("starting thread for Lifescript_interpreter")
    try {
      future {
        loop
      }
    } catch {
      case e => this.log_error("", e)
    }
  }

  def terminate {
    running = false
  }

  private def loop() {
    while (running) {
      {
        log_debug("LSI sleeping, qsize=" + q.size())
        signal.await()
        log_debug("LSI waking up, qsize=" + q.size())
        val event = q.peek()

        if (event.elevated_prio) {
          handle_elevated_prio_event(event)
        } else {
          log_debug("normal-prio event")
          if (handle(event)) q.take
        }
        signal = new CountDownLatch(1)
      }
    }
  }

  private def event_comes_from_worker_thread(event: Event): Boolean = event.event_type == 'thread_ended

  private def handle_elevated_prio_event(event: Event) {
    if (event_comes_from_worker_thread(event)) {
      log_debug("event from W")
      q.take()
      if (q.size > 0) if (handle(q.peek)) q.take
    } else {
      if (worker_process_running) {
        log_debug("terminating currently running W")
        worker_process_running = false
      }
      log_debug("clearing q")
      q.clear()
      //@TODO: process event
    }
  }

}
