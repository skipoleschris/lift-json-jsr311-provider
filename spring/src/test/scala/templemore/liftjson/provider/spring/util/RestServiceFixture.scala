package templemore.liftjson.provider.spring.util

import com.sun.jersey.api.client.{Client, ClientResponse, WebResource}

trait RestServiceFixture {

  protected def port = 8081
  protected def LocalServer = "http://localhost:" + port
  private val NoContent = 204

  protected def invokeService[T](path: String, expectedStatus: Int)
                                (withAction: (WebResource) => ClientResponse)
                                (implicit manifest: Manifest[T]): Option[T] = {
    val client = Client.create()
    val response = withAction(client.resource(LocalServer + path))
    response.getStatus match {
      case code if (code == expectedStatus) =>
        if (code == NoContent ) None
        else Option(response.getEntity(manifest.erasure).asInstanceOf[T])
      case code => throw new IllegalStateException("Error invoking REST service. Status code: %d, body: %s".format(
                                                   code, response.getEntity(classOf[String])))
    }
  }
}
