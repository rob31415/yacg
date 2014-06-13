package yacg.lifescipt

import yacg.events.Event
import scala.collection.mutable.HashMap
import yacg.time.Time

// { time, {npc-id, [events]} }  ({}=map, []=list)
object Schedule {
  type data_type = HashMap[Time, npc_events_type]
  type npc_events_type = HashMap[String, List[Event]]
  private var schedule: data_type = new data_type()
    
  def get: data_type = schedule

  // we can't just merge the given map with the schedule, because
  // the last added entry would overwrite previous entry having equal key.
  // if an entry with a given key already exists, we need to concat their values.
  //
  // so, for example if both bill and frank where to to something at 12 oclock, the 
  // key (12 oclock) will have a value (of npc_events_type),
  // consisting of both, bill and franks events.
  def add(map: data_type) {	
    // map foreach {item => println(item._1 + " -> " + item._2 + "\n")}
    schedule ++= map.map {case (key,value) => key -> (value ++= schedule.get(key).getOrElse(Nil)) }
  }

  override def toString: String = {
    var retval = "number of entries=" + schedule.size + "\n"
    schedule foreach {item => retval += item._1 + " -> " + item._2 + "\n"}
    retval
  }
}

