package yacg.lifescipt

class Time(var day: Int = 0, var hour: Int = 0, var minute: Int = 0) {
  override def toString(): String = {
    "" + day + "/" + hour + "h" + minute
  }

  override def equals(that: Any): Boolean = {
    if (that.isInstanceOf[Time]) {
      val that_casted = that.asInstanceOf[Time]
      this.hashCode() == that.asInstanceOf[Time].hashCode() &&
        that_casted.day == day &&
        that_casted.hour == hour &&
        that_casted.minute == minute
    } else {
      false
    }
  }

  override def hashCode = day + hour + minute
}

// keep track of in-game time
object In_game_clock {

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
    game_time.day += 1
    if (game_time.day > 6) game_time.day = 0
  }

}
