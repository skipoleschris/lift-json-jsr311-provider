package templemore.liftjson.provider.spring.util

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.webapp.WebAppContext


class EmbeddedJetty(webAppPath: String, port: Int = 8081) {

  private val server: Server = new Server()

  def start(): Unit = {
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

  def stop(): Unit = server.stop()
}