package templemore.liftjson.provider

import org.specs2.Specification
import java.lang.IllegalStateException
import util.JsonUtilities


class JsonDocumentErrorResponseGeneratorSpec extends Specification with JsonUtilities { def is =

  "Specification for the Json Document Error Response Generator"     ^
                                                                     endp^
  "A Json document error response generator should"                  ^
    "create an internal server error status code for an exception"   ! statusOnException^
    "generate a json entity for an exception"                        ! entityOnException^
    "create a bad request status code for a mapping error"           ! statusOnMappingError^
    "generate a json entity for a mapping error"                     ! entityOnMappingError^
                                                                     endp

  def statusOnException = {
    val errorResponse = JsonDocumentErrorResponseGenerator.generate(new IllegalStateException("Bang"))
    errorResponse.httpStatusCode must_== 500
  }

  def entityOnException = {
    val errorResponse = JsonDocumentErrorResponseGenerator.generate(new IllegalStateException("Bang"))
    compact(errorResponse.responseBody) must_== compact(ExceptionJsonEntity)
  }

  def statusOnMappingError = {
    val errorResponse = JsonDocumentErrorResponseGenerator.generate(MappingError("Bang"))
    errorResponse.httpStatusCode must_== 400
  }

  def entityOnMappingError = {
    val errorResponse = JsonDocumentErrorResponseGenerator.generate(MappingError("Bang"))
    compact(errorResponse.responseBody) must_== compact(MappingErrorJsonEntity)
  }

  private lazy val ExceptionJsonEntity = """{
    "httpStatusCode" : "500",
    "httpReasonPhrase" : "Internal Server Error",
    "cause" : "java.lang.IllegalStateException",
    "message" : "Bang"
  }"""

  private lazy val MappingErrorJsonEntity = """{
    "httpStatusCode" : "400",
    "httpReasonPhrase" : "Bad Request",
    "cause" : "MappingError",
    "message" : "Unable to process supplied Json body. Bang"
  }"""
}