package com.ldg.utilities

import com.typesafe.config.Config

import scala.util.{Failure, Success, Try}

object ConfigUtilities extends ScalaLogger {

  def getConfig[T <: Config,R](config:T)(block: T=>R): Either[FailureTrait,R] = {

    Try(block(config)) match {

      case Success(expectedValue) => Right(expectedValue)

      case  Failure(e) => 	Left(ConfigurationError(
        config.toString,s"Error reading cronexpression configuration file, exception: ${e.getMessage}")
      )
    }
  }
}