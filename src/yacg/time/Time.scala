package yacg.time

import yacg.util.Logger

class Time(var day: Option[Int] = Some(0), var hour: Int = 0, var minute: Int = 0) extends Logger {

  // e.g. "0/8h45"
  def this(formatted: String) {
    this(None, 0, 0)

    var day = "^[0-9]/".r.findFirstIn(formatted)
    if (day.isDefined) this.day = Some(day.get.dropRight(1).toInt)
    this.hour = "\\d{1,2}h".r.findFirstIn(formatted).get.dropRight(1).toInt
    this.minute = "h\\d{1,2}$".r.findFirstIn(formatted).get.substring(1).toInt

    //log_debug("converted time: ", this.day, this.hour, this.minute)
  }

  override def toString(): String = {
    "" + day + "/" + hour + "h" + minute
  }

  override def equals(that: Any): Boolean = {
    if (that.isInstanceOf[Time]) {
      val that_casted = that.asInstanceOf[Time]
      val ret_val = this.hashCode() == that.asInstanceOf[Time].hashCode() &&
        (this.day.isEmpty || that_casted.day.isEmpty || day.get == that_casted.day.get) &&
        that_casted.hour == hour &&
        that_casted.minute == minute
        //@TODO: make log4j shut up if msg is from inside callbacks... ??
        //log_debug("is " + this.toString + " ==  " + that_casted.toString + "? " + b)
        ret_val
    } else {
      log_warn("can't compare something incompatible")
      false
    }
  }

  // we can't include day, because e.g.
  // "@0h45" (None/0h45) must match "@x/0h45" where x is any day (1 for ex. becomes Some(1)/0h45)
  // e.g. we want: "is None/0h45 ==  Some(0)/0h45? true"
  override def hashCode = hour + minute
}
