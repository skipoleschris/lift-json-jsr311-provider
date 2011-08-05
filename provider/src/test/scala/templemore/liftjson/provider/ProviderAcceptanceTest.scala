package templemore.liftjson.provider

import fixture.{Person, Address, TestRestService}
import org.specs2.Specification
import com.sun.jersey.api.client.ClientResponse
import java.util.TimeZone
import util.{JsonUtilities, DateUtilities, RestServiceFixture}
import java.lang.IllegalStateException
import javax.ws.rs.core.Response.Status

class ProviderAcceptanceTest extends Specification
                             with RestServiceFixture
                             with DateUtilities
                             with JsonUtilities { def is =

  sequential^
  "Acceptance test for the Lift-Json JSR 311 provider"               ^
                                                                     endp^
  "The JSR 311 provider should"                                      ^
    "support automated conversion from JSON into a domain object"    ! simpleInRestCall^
    "support automated conversion from a domain object into JSON"    ! simpleOutRestCall^
    "support transformation of incoming JSON"                        ! transformingInRestCall^
    "support transformation of outgoing JSON"                        ! transformingOutRestCall^
    "allow use of an alternative transformer factory"                ! alternativeTransformerFactory^
    "cleanly handle json that cannot be converted to a case class"   ! unconvertableInput^
    "return 500 code error message on any unhandled exception in"    ! unhandledExceptionIn^
    "return 500 code error message on any unhandled exception out"   ! unhandledExceptionOut^
                                                                     end

  def simpleInRestCall = {
    val resource = new TestRestService()
    invokeService[String](resource, "/ws/simple", 204) { res =>
      res.header("Content-Type", "application/json").put(classOf[ClientResponse], simpleJsonDocument)
    }

    val expectedAddress = Address(Seq("4 Some Building", "Some Road"), "Some Town", "ST1 1ST", "UK")
    val expectedPerson = Person("Chris", "Turner", makeDate(2, 7, 1973, TimeZone.getTimeZone("UTC")), expectedAddress)
    resource.lastPerson must_== expectedPerson
  }

  def simpleOutRestCall = {
    val resource = new TestRestService()
    val response = invokeService[String](resource, "/ws/simple", 200) { res =>
      res.get(classOf[ClientResponse])
    }

    val json = response.getOrElse(throw new IllegalStateException())
    json must_== compact(simpleJsonDocument)
  }

  def transformingInRestCall = {
    val resource = new TestRestService()
    invokeService[String](resource, "/ws/transforming", 204) { res =>
      res.header("Content-Type", "application/json").put(classOf[ClientResponse], transformJsonDocument)
    }

    val expectedAddress = Address(Seq("4 Some Building", "Some Road"), "Some Town", "ST1 1ST", "UK")
    val expectedPerson = Person("Chris", "Turner", makeDate(2, 7, 1973, TimeZone.getTimeZone("UTC")), expectedAddress)
    resource.lastPerson must_== expectedPerson
  }

  def transformingOutRestCall = {
    val resource = new TestRestService()
    val response = invokeService[String](resource, "/ws/transforming", 200) { res =>
      res.get(classOf[ClientResponse])
    }

    val json = response.getOrElse(throw new IllegalStateException())
    json must_== compact(transformJsonDocument)
  }

  def alternativeTransformerFactory = {
    val factory = new TestTransformerFactory()
    val config = ProviderConfig(factory)
    val resource = new TestRestService()
    invokeService[String](resource, "/ws/transforming", 200, new LiftJsonProvider(config)) { res =>
      res.get(classOf[ClientResponse])
    }

    factory.invoked must_==  true
  }

  class TestTransformerFactory(var invoked: Boolean = false) extends TransformerFactory {
    override def transformer[T <: JsonASTTransformer](transformerClass: Class[T]) = {
      invoked = true
      NewInstanceTransformerFactory.transformer(transformerClass)
    }
  }

  def unconvertableInput = {
    val resource = new TestRestService()
    val response = invokeService[String](resource, "/ws/simple", 400) { res =>
      res.header("Content-Type", "application/json").put(classOf[ClientResponse], invalidJsonDocument)
    }

    val error = response.getOrElse(throw new IllegalStateException())
    compact(error) must_== compact(unconvertableInputJson)
  }

  def unhandledExceptionIn = {
    val resource = new TestRestService()
    val response = invokeService[String](resource, "/ws/exception", 500) { res =>
      res.header("Content-Type", "application/json").put(classOf[ClientResponse], simpleJsonDocument)
    }

    val error = response.getOrElse(throw new IllegalStateException())
    compact(error) must_== compact(error500Json)
  }

  def unhandledExceptionOut = {
    val resource = new TestRestService()
    val response = invokeService[String](resource, "/ws/exception", 500) { res =>
      res.get(classOf[ClientResponse])
    }

    val error = response.getOrElse(throw new IllegalStateException())
    compact(error) must_== compact(error500Json)
  }

  protected lazy val simpleJsonDocument = """{
        "firstName" : "Chris",
        "surname" : "Turner",
        "dob" : "%s",
        "address" : {
          "lines": [ "4 Some Building", "Some Road" ],
          "town" : "Some Town",
          "postcode" : "ST1 1ST",
          "country" : "UK"
        }
      }""".format(asIsoString(makeDate(2, 7, 1973)))

  protected lazy val transformJsonDocument = """{
        "fullName" : "Chris Turner",
        "dob" : "%s",
        "address" : {
          "lines": [ "4 Some Building", "Some Road" ],
          "town" : "Some Town",
          "postcode" : "ST1 1ST",
          "country" : "UK"
        }
      }""".format(asIsoString(makeDate(2, 7, 1973)))

  protected lazy val invalidJsonDocument = """{
        "fullName" : "Chris Turner",
        "age" : "38",
      }""".format(asIsoString(makeDate(2, 7, 1973)))

  protected lazy val unconvertableInputJson = """{
        "applicationCode" : "1",
        "httpStatusCode" : "%d",
        "httpReasonPhrase" : "%s",
        "message" : "Unable to process supplied Json body"
      }""".format(Status.BAD_REQUEST.getStatusCode,
                  Status.BAD_REQUEST.getReasonPhrase)

  protected lazy val error500Json = """{
        "applicationCode" : "0",
        "httpStatusCode" : "%d",
        "httpReasonPhrase" : "%s",
        "cause" : "java.lang.IllegalStateException",
        "message" : "Expected error message"
      }""".format(Status.INTERNAL_SERVER_ERROR.getStatusCode,
                  Status.INTERNAL_SERVER_ERROR.getReasonPhrase)
}