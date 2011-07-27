package templemore.liftjson.provider

import fixture.{Person, Address, SimpleRestService}
import org.specs2.Specification
import com.sun.jersey.api.client.ClientResponse
import java.util.TimeZone
import java.lang.IllegalStateException
import util.{JsonUtilities, DateUtilities, RestServiceFixture}

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
    "support transformation of JSON on an in call"                   ! transformingInRestCall^
                                                                     end

  def simpleInRestCall = {
    val resource = new SimpleRestService()
    invokeService[String](resource, "/ws/simple", 204) { res =>
      res.header("Content-Type", "application/json").put(classOf[ClientResponse], simpleJsonDocument)
    }

    val expectedAddress = Address(Seq("4 Some Building", "Some Road"), "Some Town", "ST1 1ST", "UK")
    val expectedPerson = Person("Chris", "Turner", makeDate(2, 7, 1973, TimeZone.getTimeZone("UTC")), expectedAddress)
    resource.lastPerson must_== expectedPerson
  }

  def simpleOutRestCall = {
    val resource = new SimpleRestService()
    val response = invokeService[String](resource, "/ws/simple", 200) { res =>
      res.get(classOf[ClientResponse])
    }

    val json = response.getOrElse(throw new IllegalStateException())
    json must_== compact(simpleJsonDocument)
  }

  def transformingInRestCall = {
    val resource = new SimpleRestService()
    invokeService[String](resource, "/ws/transforming", 204) { res =>
      res.header("Content-Type", "application/json").put(classOf[ClientResponse], transformJsonDocument)
    }

    val expectedAddress = Address(Seq("4 Some Building", "Some Road"), "Some Town", "ST1 1ST", "UK")
    val expectedPerson = Person("Chris", "Turner", makeDate(2, 7, 1973, TimeZone.getTimeZone("UTC")), expectedAddress)
    resource.lastPerson must_== expectedPerson
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
}