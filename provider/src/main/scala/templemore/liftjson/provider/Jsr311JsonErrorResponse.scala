package templemore.liftjson.provider

import javax.ws.rs.core.Response


class Jsr311JsonErrorResponse extends Response {
  def getEntity = null

  def getStatus = 0

  def getMetadata = null
}