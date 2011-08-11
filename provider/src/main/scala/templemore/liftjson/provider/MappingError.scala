package templemore.liftjson.provider

import net.liftweb.json.MappingException


case class MappingError(msg: String) {
  def cause = "MappingError"
  def message = "Unable to process supplied Json body. " + msg
}

object MappingError {
  def apply(cause: MappingException): MappingError = MappingError(cause.getMessage.replaceAll("\\n", ". "))
}