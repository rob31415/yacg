package yacg.events

import com.jme3.math.Vector2f

class Event(val elevated_prio: Boolean = false, val data: Symbol = 'undefined, val location: Vector2f = null) extends Comparable[Event] {
  override def compareTo(other: Event): Int = 0
  override def toString: String = "Event data=" + data
}
