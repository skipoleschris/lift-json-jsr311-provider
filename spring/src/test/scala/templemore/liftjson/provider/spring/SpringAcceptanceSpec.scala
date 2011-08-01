package templemore.liftjson.provider.spring

import org.specs2.Specification
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.specs2.specification.Around
import org.specs2.execute.Result
import org.eclipse.jetty.webapp.WebAppContext

class SpringAcceptanceSpec extends Specification { def is =

  sequential^
  "Acceptance specification for the spring support component"        ^
                                                                     endp^
  "The spring support module should"                                 ^
    "allow the provider to be used in a spring application"          ! JettyServer(springSupport)^
                                                                     end

  def springSupport = {
    pending
  }



  object JettyServer extends Around {
    val server = new Server()

    def around[T](t: => T)(implicit evidence: ( T ) => Result) = {
      try {
        startJetty()
        t
      } finally stopJetty()
    }

    private def startJetty(): Unit = {
      val connector = new SelectChannelConnector()
      connector.setPort(8081)
      connector.setHost("127.0.0.1")
      server.addConnector(connector)

      val wac = new WebAppContext()
      wac.setContextPath("/")
      wac.setWar("spring/src/test/webapp")
      server.setHandler(wac)
      server.setStopAtShutdown(true)

      server.start()
    }

    private def stopJetty(): Unit = server.stop()
  }
}