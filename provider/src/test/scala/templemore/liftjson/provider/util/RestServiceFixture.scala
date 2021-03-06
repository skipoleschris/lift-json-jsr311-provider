package templemore.liftjson.provider.util

import com.sun.jersey.api.core.DefaultResourceConfig
import com.sun.jersey.simple.container.SimpleServerFactory
import com.sun.jersey.api.client.{Client, ClientResponse, WebResource}
import templemore.liftjson.provider.LiftJsonProvider

trait RestServiceFixture {

  protected def LocalServer = "http://localhost:32434"
  private val NoContent = 204

  protected def invokeService[T](resource: AnyRef, path: String, expectedStatus: Int,
                                 provider: LiftJsonProvider = new LiftJsonProvider())
                                (withAction: (WebResource) => ClientResponse)
                                (implicit manifest: Manifest[T]): Option[T] = {
    val resourceConfig = new DefaultResourceConfig()
    resourceConfig.getSingletons.add(resource)
    resourceConfig.getSingletons.add(provider)
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
