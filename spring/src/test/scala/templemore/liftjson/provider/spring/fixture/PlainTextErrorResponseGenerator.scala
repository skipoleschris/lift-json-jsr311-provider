package templemore.liftjson.provider.spring.fixture

import templemore.liftjson.provider.{ErrorResponse, MappingError, ErrorResponseGenerator}
import templemore.liftjson.provider.jsr311.Jsr311StatusAdapter._

class PlainTextErrorResponseGenerator extends ErrorResponseGenerator {

  def generate(cause: Throwable) =
    ErrorResponse(internalServerError.statusCode, cause.getMessage)

  def generate(error: MappingError) =
    ErrorResponse(badRequest.statusCode, error.message)
}