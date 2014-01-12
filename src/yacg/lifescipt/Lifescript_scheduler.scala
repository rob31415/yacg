package yacg.lifescipt

import scala.concurrent._
import ExecutionContext.Implicits.global
import collection.mutable.Map
import yacg.events.Event
import yacg.igo.Igo_repo

// read lifescript for every npc into { time, {npc-id, [events]} }  ({}=map, []=list)
// dispatch events to npcs in their due time
object Lifescript_scheduler {

  // this determines how fast in game time is relative to realworld time
  val thread_sleep_in_milli_seconds = 250
  //  var npc_events = Map[Int, List[Event]]()
  //  var times = Map[Time, Map[Int, List[Event]]()

  def init {}

  val test_event_list = Nil //new Event(false, 'some_event_from_lss) :: new Event(false, 'another_event_from_lss) :: Nil
  val test_npc_events = Map(1 -> test_event_list)
  val times = Map(new Time(0, 1, 34) -> test_npc_events)

  future {
    while (true) {
      Thread sleep thread_sleep_in_milli_seconds
      times.get(In_game_clock tick) foreach { key_val => key_val.foreach { key_val => dispatch(key_val) } }
    }
  }

  def dispatch(id_event_list: Tuple2[Int, List[Event]]) {
    id_event_list._2.foreach { event => Igo_repo.get_igo_by_id(id_event_list._1).put(event) }
  }

}
