package templemore.liftjson.provider


trait ErrorResponseGenerator {

  def generate(cause: Throwable): ErrorResponse

  def generate(error: MappingError): ErrorResponse
}