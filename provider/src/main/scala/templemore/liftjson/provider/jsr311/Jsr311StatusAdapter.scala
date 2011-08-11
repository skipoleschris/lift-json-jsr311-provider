package templemore.liftjson.provider.jsr311

import javax.ws.rs.core.Response.Status

trait HttpStatus {
  def statusCode: Int
  def reasonPhrase: String
}

private[jsr311] abstract class Jsr311HttpStatus(status: Status) extends HttpStatus {
  def statusCode = status.getStatusCode
  def reasonPhrase = status.getReasonPhrase
}

private[jsr311] object BadRequestHttpStatus extends Jsr311HttpStatus(Status.BAD_REQUEST)
private[jsr311] object InternalServerErrorHttpStatus extends Jsr311HttpStatus(Status.INTERNAL_SERVER_ERROR)

object Jsr311StatusAdapter {
  def badRequest = BadRequestHttpStatus
  def internalServerError = InternalServerErrorHttpStatus
}