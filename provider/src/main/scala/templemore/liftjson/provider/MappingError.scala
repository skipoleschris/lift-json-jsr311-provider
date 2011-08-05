package templemore.liftjson.provider

import net.liftweb.json.MappingException


private[provider] case class MappingError(msg: String) {
  def cause = "MappingError"
  def message = "Unable to process supplied Json body. " + msg
}

private[provider] object MappingError {
  def apply(cause: MappingException): MappingError = MappingError(cause.getMessage.replaceAll("\\n", ". "))
}