package templemore.liftjson.provider

import com.sun.jersey.api.core.DefaultResourceConfig
import com.sun.jersey.simple.container.SimpleServerFactory
import com.sun.jersey.api.client.{Client, ClientResponse, WebResource}

trait RestServiceFixture {

  protected def LocalServer = "http://localhost:32434"
  private val NoContent = 204

  protected def invokeService[T](resource: AnyRef, path: String, expectedStatus: Int)
                                (withAction: (WebResource) => ClientResponse)
                                (implicit manifest: Manifest[T]): Option[T] = {
    val resourceConfig = new DefaultResourceConfig(classOf[LiftJsonProvider])
    resourceConfig.getSingletons.add(resource)
    val serverHandle = SimpleServerFactory.create(LocalServer, resourceConfig)

    try {
      val client = Client.create()
      val response = withAction(client.resource(LocalServer + path))
      response.getStatus match {
        case code if (code == expectedStatus) =>
          if (code == NoContent ) None
          else Option(response.getEntity(manifest.erasure).asInstanceOf[T])
        case code => throw new IllegalStateException("Error invoking REST service. Status code: %d, body: %s".format(
                                                     code, response.getEntity(classOf[String])))
      }
    } finally {
      serverHandle.close()
    }
  }
}
