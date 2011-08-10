package templemore.liftjson.provider.jsr311

import javax.ws.rs.core.Response
import templemore.liftjson.provider.ErrorResponse

object Jsr311ResponseAdapter {

  def apply(error: ErrorResponse): Response = Response.status(error.httpStatusCode)
                                                      .`type`(error.contentType)
                                                      .entity(error.responseBody).build
}
