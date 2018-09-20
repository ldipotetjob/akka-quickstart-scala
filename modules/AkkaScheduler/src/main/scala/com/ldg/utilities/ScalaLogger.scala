package com.ldg.utilities

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

trait ScalaLogger {
  def log: Logger = {
    Logger(LoggerFactory.getLogger(this.getClass))
  }
}