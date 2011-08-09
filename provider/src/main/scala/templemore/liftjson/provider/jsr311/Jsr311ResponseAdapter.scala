package templemore.liftjson.provider.jsr311

import javax.ws.rs.core.Response
import templemore.liftjson.provider.ErrorResponse

object Jsr311ResponseAdapter {

  //TODO: send a type
  def apply(error: ErrorResponse): Response = Response.status(error.httpStatusCode)
                                                      .entity(error.responseBody).build
}
