package yacg.lifescipt

import yacg.events._
import scala.collection.mutable
import yacg.time.Time
import yacg.util.Logger

/*
 * build schedule from stringdata containing npc-id, time, and events/commands
 *
 * parser recognizes:
 * a) a context/collection/section when it sees "id="
 * b) a sub-context/collection/section of (a) when it sees "@"
 * 
 * (a) collects Schedule.data_type items (q.v.)
 *     maintains the id of an igo because (b) needs that
 * (b) collects Schedule.npc_events_type items
 *     maintains a time(-stamp) and a list of events that are supposed to happen sequentially at that time 
 */
object Lifescript_parser {

  private class Context(id: String) extends Logger {
    private val schedule_fragment = new Schedule.data_type()
    val subcontext = new Subcontext()
    
    log_debug("created new Lifescript_parser context for " + id)

    def out = schedule_fragment
    def commit {
      schedule_fragment.put(subcontext.data.time, subcontext.out(id))
      subcontext.reset
    }

    class Subcontext {
      object data {
        var time = new Time
        var events: List[Event] = Nil
      }
      def reset = { data.time = new Time; data.events = Nil }
      def put(event: Event) = this.data.events = this.data.events :+ event
      def out(id: String): Schedule.npc_events_type = {
        var events = new Schedule.npc_events_type()
        events.put(id, data.events)
        events
      }
    }

  }

  def convert(data: List[String], id: String): Schedule.data_type = {
    var context = new Context(id)

    for (line <- data) {
      if (line.length() > 0 && !line.startsWith("#")) {
        identify_tokens(line, context)
      }
    }

    context.out
  }

  private def identify_tokens(line: String, context: Context) {

    if (line.startsWith("@")) {
      context.commit
      context.subcontext.data.time = new Time(line.substring(1))
    } else if (line.startsWith("that's a life")) {
      context.commit
    } else {
      context.subcontext.put(
        new Event_with_stringdata(false,
          Event.get_symbol_from_string(line.split(' ')(0)),
          line.split(' ')(1)))
    }

  }

}