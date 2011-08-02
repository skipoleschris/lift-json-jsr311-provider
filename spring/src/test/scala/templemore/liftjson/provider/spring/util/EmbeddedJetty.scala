package templemore.liftjson.provider.spring.util

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.webapp.WebAppContext


trait EmbeddedJetty {

  private val server = new Server()
  protected def port = 8081
  protected def webAppPath: String

  protected def startJetty(): Unit = {
    val connector = new SelectChannelConnector()
    connector.setPort(8081)
    connector.setHost("127.0.0.1")
    server.addConnector(connector)

    val wac = new WebAppContext()
    wac.setContextPath("/")
    wac.setWar(webAppPath)
    server.setHandler(wac)
    server.setStopAtShutdown(true)

    server.start()
  }

  protected def stopJetty(): Unit = server.stop()
}