package templemore.liftjson.provider.spring

import org.specs2.Specification
import org.specs2.specification.Around
import org.specs2.execute.Result
import util.{JsonUtilities, RestServiceFixture, EmbeddedJetty}
import com.sun.jersey.api.client.ClientResponse

class SpringAcceptanceSpec extends Specification
                           with RestServiceFixture
                           with JsonUtilities { def is =

  sequential^
  "Acceptance specification for the spring support component"        ^
                                                                     endp^
  "The spring support module should"                                 ^
    "allow the provider to be used in a spring application"          ! JettyServer(springSupport)^
                                                                     end

  def springSupport = {
    val response = invokeService[String]("/user", 200) { res =>
      res.get(classOf[ClientResponse])
    }

    val json = response.getOrElse(throw new IllegalStateException())
    json must_== compact(usersJsonDocument)
  }

  private val usersJsonDocument = """[
      { "username" : "root", "fullName" : "Administrator" },
      { "username" : "chris", "fullName" : "Chris Turner" },
      { "username" : "foo", "fullName" : "Foo Bar" }
  ]"""

  object JettyServer extends Around with EmbeddedJetty {

    protected def webAppPath = "spring/src/test/webapp"

    def around[T](t: => T)(implicit evidence: ( T ) => Result) = {
      try {
        startJetty()
        t
      } finally stopJetty()
    }
  }
}
