package yacg.lifescipt

import scala.concurrent._
import ExecutionContext.Implicits.global
import collection.mutable.Map
import yacg.events.Event
import yacg.igo.Scene_graph_interface
import java.io.File
import yacg.Main
import scala.io.Source
import yacg.time.In_game_clock
import yacg.util.Logger

// read lifescript for every npc into { time, {npc-id, [events]} }  ({}=map, []=list)
// dispatch events to npcs in their due time
object Lifescript_scheduler extends Logger {

  val basepath = Main.basepath_assets + "/Lifescript/"

  def init = iterate_scriptfile_dir

  future {
    log_debug("starting thread for Lifescript_scheduler")

    try {
      while (yacg.Configurator.dispatchEvents) {
        Thread sleep In_game_clock.thread_sleep_in_milli_seconds
        In_game_clock.tick
        //@TODO: make log4j shut up if msg is from inside callbacks... ??
        //log_debug("ingame time: " + In_game_clock.game_time.toString)
        Schedule.get.get(In_game_clock game_time) foreach { value => log_debug("found schedule entry at " + In_game_clock.game_time.toString); value.foreach { element => dispatch(element) } }
      }
    } catch {
      case e => this.log_error("", e)
    }
  }

  //@TODO: use schedule.npc_events_type
  def dispatch(id_event_list: Tuple2[String, List[Event]]) {
    log_debug("dispatch to id '" + id_event_list._1 + "'")
    id_event_list._2.foreach { event =>
      log_debug("event=" + event);
      val igo = Scene_graph_interface.get_igo_by_id(id_event_list._1)
      if (igo.isDefined) {
        igo.get.put(event)
      } else {
        log_warn("matching igo not found")
      }
    }
  }

  def iterate_scriptfile_dir {
    for (file <- new File(basepath).listFiles) {
      if (file.isFile) {
        val id = file.getName.substring(0, file.getName.lastIndexOf('.'))
        Schedule.add(Lifescript_parser.convert(get_file_contents(file), id))
      }
    }
    log_debug("here's the whole damn schedule of everything:\n" + Schedule.toString)
  }

  // thanks to thomas
  def get_file_contents(file: File): List[String] = {
    if (file.isFile) {
      Source.fromFile(file.getPath).getLines.toList.flatMap(line =>
        if (line.startsWith("include")) {
          get_file_contents(new File(basepath + line.substring(line.indexOf(" ") + 1)))
        } else {
          List(line)
        })
    } else {
      List()
    }
  }

}
