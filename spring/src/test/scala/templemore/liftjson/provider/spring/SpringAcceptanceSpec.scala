package templemore.liftjson.provider.spring

import org.specs2.Specification
import org.specs2.execute.Result
import util.{JsonUtilities, RestServiceFixture, EmbeddedJetty}
import com.sun.jersey.api.client.ClientResponse
import org.specs2.specification.{Before, Around}

class SpringAcceptanceSpec extends Specification
                           with RestServiceFixture
                           with JsonUtilities
                           with Before { def is =

  sequential^
  "Acceptance specification for the spring support component"        ^
                                                                     endp^
  "The spring support module should"                                 ^
    "allow the provider to be used in a spring application"          ! JettyServer(springSupport)^
    "support spring injection of a transformer"                      ! JettyServer(injectedTransformer)^
    "support spring injection of custom error response generator"    ! JettyServer(injectedErrorResponseGenerator)^
                                                                     end

  def springSupport = {
    val response = invokeService[String]("/user", 200) { res =>
      res.get(classOf[ClientResponse])
    }

    val json = response.getOrElse(throw new IllegalStateException())
    json must_== compact(usersJsonDocument)
  }

  def injectedTransformer = {
    invokeService[String]("/user", 204) { res =>
      res.header("Content-Type", "application/json").put(classOf[ClientResponse], UserJsonDocument)
    } must_==  None
  }

  def injectedErrorResponseGenerator = {
    val response = invokeService[String]("/user", 400) { res =>
      res.header("Content-Type", "application/json").put(classOf[ClientResponse], InvalidUserJsonDocument)
    }

    val message = response.getOrElse(throw new IllegalStateException())
    message must_== "Unable to process supplied Json body. Unable to find user field in json document."
  }

  private val usersJsonDocument = """[
      { "username" : "root", "fullName" : "Administrator" },
      { "username" : "chris", "fullName" : "Chris Turner" },
      { "username" : "foo", "fullName" : "Foo Bar" }
  ]"""

  private val UserJsonDocument = """{
    "user" : "Fred Bloggs <fred>"
  }"""

  private val InvalidUserJsonDocument = """{
    "invalid" : "I'm not valid"
  }"""

  def before = throw new IllegalStateException()

  object JettyServer extends Around {

    def around[T](t: => T)(implicit evidence: ( T ) => Result) = {
      val jetty = new EmbeddedJetty("spring/src/test/webapp")
      try {
        jetty.start()
        t
      } finally jetty.stop()
    }
  }
}
