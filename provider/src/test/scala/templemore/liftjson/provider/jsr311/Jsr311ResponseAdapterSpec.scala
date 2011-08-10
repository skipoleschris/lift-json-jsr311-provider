package templemore.liftjson.provider.jsr311

import org.specs2.Specification
import templemore.liftjson.provider.ErrorResponse
import javax.ws.rs.core.MediaType

class Jsr311ResponseAdapterSpec extends Specification { def is =

  "Specification for the Jsr-311 Response Adapter"                   ^
                                                                     endp^
  "A jsr-311 response adapter should"                                ^
    "convert a 500 status code into a jsr-311 Response instance"     ! convert500Response^
    "add a content type into the jsr-311 Response metadata"          ! includeContentType^
    "include the given body in the jsr-311 Response entity"          ! includeEntity^
                                                                     end

  def convert500Response = {
    val error = ErrorResponse(Error500Code, JsonContentType, ExpectedEntity)
    Jsr311ResponseAdapter(error).getStatus must_== Error500Code
  }

  def includeContentType = {
    val error = ErrorResponse(Error500Code, JsonContentType, ExpectedEntity)
    Jsr311ResponseAdapter(error).getMetadata.getFirst("Content-Type")
                                .asInstanceOf[MediaType].toString must_== JsonContentType
  }

  def includeEntity = {
    val error = ErrorResponse(Error500Code, JsonContentType, ExpectedEntity)
    Jsr311ResponseAdapter(error).getEntity must_== ExpectedEntity
  }

  private val Error500Code = 500
  private val JsonContentType = "application/json"
  private val ExpectedEntity = """{ "message" : "Body Message" }"""
}