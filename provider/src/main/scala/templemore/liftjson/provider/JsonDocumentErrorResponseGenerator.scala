package templemore.liftjson.provider

import javax.ws.rs.core.Response.Status


private[provider] object JsonDocumentErrorResponseGenerator extends ErrorResponseGenerator {

  def generate(cause: Throwable) =
    ErrorResponse(Status.INTERNAL_SERVER_ERROR.getStatusCode,
                  makeJson(Status.INTERNAL_SERVER_ERROR, cause.getClass.getName, cause.getMessage))

  def generate(error: MappingError) =
    ErrorResponse(Status.BAD_REQUEST.getStatusCode,
                  makeJson(Status.BAD_REQUEST, error.cause, error.message))

  private def makeJson(status: Status, cause: String, message: String) = """{
    "httpStatusCode" : "%d",
    "httpReasonPhrase" : "%s",
    "cause" : "%s",
    "message" : "%s"
  }""".format(status.getStatusCode,
              status.getReasonPhrase,
              cause,
              message)
}