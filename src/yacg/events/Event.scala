package yacg.events

import com.jme3.math.Vector2f

class Event(val elevated_prio: Boolean = false, val data: Symbol = 'undefined, val location: Vector2f = null) extends Comparable[Event] {

  //0 indicates == , 1 indicates > and -1 indicates <
  //@TODO: may not be quite right - check it...
  override def compareTo(other: Event): Int = {
    if(elevated_prio && other.elevated_prio) {println("1a"); return -1}		//lifo amongst elevated prio events
    if(elevated_prio && !other.elevated_prio) {println("1b"); return -1}
    if(!elevated_prio && other.elevated_prio) {println("-1a"); return 1}
    println("-1b")
    1
  }
  override def toString: String = "Event data=" + data
}
