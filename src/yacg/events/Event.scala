package yacg.events

import com.jme3.math.Vector3f
import yacg.util.Logger

object Event {

  def get_symbol_from_string(strg: String): Symbol =
    strg match {
      case "moveto" => 'moveto
      case "do" => 'do
      case "transfer" => 'transfer
      case _ => 'undefined
    }

}

class Event(val elevated_prio: Boolean = false, val event_type: Symbol = 'undefined) extends Comparable[Event] with Logger {

  //0 indicates == , 1 indicates > and -1 indicates <
  override def compareTo(other: Event): Int = {
    if (elevated_prio && other.elevated_prio) { log_debug("both low prio"); return -1 } //lifo amongst elevated prio events
    if (elevated_prio && !other.elevated_prio) { log_debug("left hi prio"); return -1 }
    if (!elevated_prio && other.elevated_prio) { log_debug("right hi prio"); return 1 }
    log_debug("both hi prio")
    0
  }
  override def toString: String = "Event type=" + event_type
}

class Event_with_locationdata(override val elevated_prio: Boolean = false,
  override val event_type: Symbol = 'undefined, var location: Vector3f = null) extends Event(elevated_prio, event_type) {
}

class Event_with_stringdata(override val elevated_prio: Boolean = false,
  override val event_type: Symbol = 'undefined, val data: String = "") extends Event(elevated_prio, event_type) {
  override def toString: String = super.toString + " data=" + data
}
