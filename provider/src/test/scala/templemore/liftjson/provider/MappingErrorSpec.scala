package templemore.liftjson.provider

import org.specs2.Specification
import net.liftweb.json.MappingException
import java.lang.IllegalStateException


class MappingErrorSpec extends Specification { def is =

  "Specificatin for the Mapping Error"                               ^
                                                                     endp^
  "A mapping error should"                                           ^
    "convert a mapping exception to a mapping error cause"           ! causeFromException^
    "convert a mapping exception to a mapping error message"         ! messageFromException^
                                                                     endp

  def causeFromException = {
    val mappingError = MappingError(mappingException)
    mappingError.cause must_== "MappingError"
  }

  def messageFromException = {
    val mappingError = MappingError(mappingException)
    mappingError.message must_== "Unable to process supplied Json body. The message"
  }

  def mappingException: MappingException = {
    new MappingException("The message", new IllegalStateException())
  }
}