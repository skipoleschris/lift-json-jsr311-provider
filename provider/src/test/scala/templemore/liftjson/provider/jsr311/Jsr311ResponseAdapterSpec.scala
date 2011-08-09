package templemore.liftjson.provider.jsr311

import org.specs2.Specification
import templemore.liftjson.provider.ErrorResponse

class Jsr311ResponseAdapterSpec extends Specification { def is =

  "Specification for the Jsr-311 Response Adapter"                   ^
                                                                     endp^
  "A jsr-311 response adapter should"                                ^
    "Convert a 500 status code into a jsr-311 Response instance"     ! convert500Response^
    "Include the given body in the jsr-311 Response entity"          ! includeEntity^
                                                                     end

  def convert500Response = {
    val error = ErrorResponse(Error500Code, ExpectedEntity)
    Jsr311ResponseAdapter(error).getStatus must_== Error500Code
  }

  def includeEntity = {
    val error = ErrorResponse(Error500Code, ExpectedEntity)
    Jsr311ResponseAdapter(error).getEntity must_== ExpectedEntity
  }

  private val Error500Code = 500
  val ExpectedEntity = """{ "message" : "Body Message" }"""
}