package templemore.liftjson.provider

import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

class Jsr311JsonErrorResponse(applicationCode: Int, status: Status, cause: String, message: String) extends Response {

  def getMetadata = Jsr311MultiValuedMap()

  def getStatus = status.getStatusCode

  def getEntity = """{
    "applicationCode" : "%d",
    "httpStatusCode" : "%d",
    "httpReasonPhrase" : "%s",
    "cause" : "%s",
    "message" : "%s"
    }""".format(applicationCode,
                status.getStatusCode,
                status.getReasonPhrase,
                cause,
                message)
}

object Jsr311JsonErrorResponse {
  private val INTERNAL_ERROR = 0
  private val MAPPING_ERROR = 1

  def apply(status: Status, cause: Throwable) =
    new Jsr311JsonErrorResponse(INTERNAL_ERROR, status, cause.getClass.getName, cause.getMessage)

  def apply(error: MappingError) =
    new Jsr311JsonErrorResponse(MAPPING_ERROR, Status.BAD_REQUEST, error.cause, error.message)
}