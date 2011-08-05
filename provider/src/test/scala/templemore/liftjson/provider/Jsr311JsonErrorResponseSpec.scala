package templemore.liftjson.provider

import org.specs2.Specification
import util.JsonUtilities
import net.liftweb.json.MappingException


class Jsr311JsonErrorResponseSpec extends Specification with JsonUtilities { def is =

  "Specification for the Jsr-311 Json Error Response"                ^
                                                                     endp^
  "The Jsr-311 json error response should"                           ^
    "have the status code populated for an exception"                ! statusCodeForException^
    "have a json message populated for an exception"                 ! jsonMessageForException^
    "have the status code populated for a mapping error"             ! statusCodeForMappingError^
    "have a json message populated for a mapping error"              ! jsonMessageForMappingError^
    "provide a metadata map"                                         ! metadataMap^
                                                                     end

  def statusCodeForException = {
    val response = Jsr311JsonErrorResponse(new IllegalStateException("The Message"))
    response.getStatus must_== 500
  }

  def jsonMessageForException = {
    val response = Jsr311JsonErrorResponse(new IllegalStateException("The Message"))
    compact(response.getEntity) must_== compact(exceptionJson)
  }

  def statusCodeForMappingError = {
    val response = Jsr311JsonErrorResponse(MappingError(mappingException))
    response.getStatus must_== 400
  }

  def jsonMessageForMappingError = {
    val response = Jsr311JsonErrorResponse(MappingError(mappingException))
    compact(response.getEntity) must_== compact(mappingErrorJson)
  }

  def metadataMap = {
    val response = Jsr311JsonErrorResponse(new IllegalStateException("The Message"))
    response.getMetadata must_!= null
  }

  private def mappingException: MappingException = {
    new MappingException("The message", new IllegalStateException())
  }

  private lazy val exceptionJson = """{
      "applicationCode" : "0",
      "httpStatusCode" : "500",
      "httpReasonPhrase" : "Internal Server Error",
      "cause" : "java.lang.IllegalStateException",
      "message" : "The Message"
    }"""

  private lazy val mappingErrorJson = """{
      "applicationCode" : "1",
      "httpStatusCode" : "400",
      "httpReasonPhrase" : "Bad Request",
      "cause" : "MappingError",
      "message" : "Unable to process supplied Json body. The message"
    }"""
}