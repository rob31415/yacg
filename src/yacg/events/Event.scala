package yacg.events

class Event(val elevated_prio: Boolean = false, val data: Symbol = 'undefined) extends Comparable[Event] {
  override def compareTo(other: Event): Int = 0
  override def toString: String = "Event data=" + data
}
