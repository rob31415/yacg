package yacg.time

// keep track of in-game time
// this determines how fast in game time is relative to realworld time
object In_game_clock {

  //how much faster ingame time relative to realworld
  val factor = 180 // 1 minute real time = 30 minutes game time
  val thread_sleep_in_milli_seconds = (1000 * 60) / factor //100

  var game_time = new Time

  def tick: Time = {
    game_time.minute += 1
    if (game_time.minute > 59) { game_time.minute = 0; inc_h }
    game_time
  }

  private def inc_h {
    game_time.hour += 1
    if (game_time.hour > 24) { game_time.hour = 0; inc_day }
  }

  private def inc_day {
    game_time.day = Some(game_time.day.get + 1)
    if (game_time.day.get > 6) game_time.day = Some(0)
  }

}
