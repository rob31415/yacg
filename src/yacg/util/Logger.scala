package yacg.util

import org.slf4j.LoggerFactory
import java.util.Properties
import org.apache.log4j.PropertyConfigurator
import java.io.FileInputStream

trait Logger {
  // to debug log4j use this as vm arg: -Dlog4j.debug=true

  val logger = LoggerFactory.getLogger(getClass)
  val log4JPropertyFile = "./log/log4j.properties";
  val p = new Properties();

  p.load(new FileInputStream(log4JPropertyFile));
  PropertyConfigurator.configure(p);

  def log_debug(msg: => String) = if (logger.isDebugEnabled) logger.debug(msg)
  def log_warn(msg: => String) = if (logger.isWarnEnabled) logger.warn(msg)
  def log_error(msg: => String, e: Throwable) = if (logger.isErrorEnabled) logger.error(msg, e)
  def log_error(msg: => String) = if (logger.isErrorEnabled) logger.error(msg)
}