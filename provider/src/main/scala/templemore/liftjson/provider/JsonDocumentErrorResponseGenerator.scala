package templemore.liftjson.provider

import jsr311.HttpStatus
import jsr311.Jsr311StatusAdapter._

private[provider] object JsonDocumentErrorResponseGenerator extends ErrorResponseGenerator {

  def generate(cause: Throwable) =
    ErrorResponse(internalServerError.statusCode,
                  makeJson(internalServerError, cause.getClass.getName, cause.getMessage))

  def generate(error: MappingError) =
    ErrorResponse(badRequest.statusCode,
                  makeJson(badRequest, error.cause, error.message))

  private def makeJson(status: HttpStatus, cause: String, message: String) = """{
    "httpStatusCode" : "%d",
    "httpReasonPhrase" : "%s",
    "cause" : "%s",
    "message" : "%s"
  }""".format(status.statusCode,
              status.reasonPhrase,
              cause,
              message)
}