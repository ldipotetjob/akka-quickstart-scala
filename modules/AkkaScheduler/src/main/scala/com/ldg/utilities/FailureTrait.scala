package com.ldg.utilities

sealed trait FailureTrait {
  val failuresTags: String
  val message: String
}
case class AnyFailure(failuresTags: String,message:String) extends FailureTrait
case class JsoupFailure(failuresTags: String,message:String) extends FailureTrait
case class ConfigurationError(failuresTags: String,message:String) extends FailureTrait