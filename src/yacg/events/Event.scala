package yacg.events

import com.jme3.math.Vector2f

class Event(val elevated_prio: Boolean = false, val data: Symbol = 'undefined, val location: Vector2f = null) extends Comparable[Event] {

  //0 indicates == , 1 indicates > and -1 indicates <
  override def compareTo(other: Event): Int = {
    if(elevated_prio && other.elevated_prio) {println("a"); return -1}		//lifo amongst elevated prio events
    if(elevated_prio && !other.elevated_prio) {println("b"); return -1}
    if(!elevated_prio && other.elevated_prio) {println("c"); return 1}
    println("d")
    0
  }
  override def toString: String = "Event data=" + data
}
