package templemore.liftjson.provider

import jsr311.HttpStatus
import jsr311.Jsr311StatusAdapter._

private[provider] object JsonDocumentErrorResponseGenerator extends ErrorResponseGenerator {

  private val JsonContentType = "application/json"

  def generate(cause: Throwable) =
    {
      ErrorResponse(internalServerError.statusCode,
                    JsonContentType,
                    makeJson(internalServerError, cause.getClass.getName, cause.getMessage))
    }

  def generate(error: MappingError) =
    ErrorResponse(badRequest.statusCode,
                  JsonContentType,
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