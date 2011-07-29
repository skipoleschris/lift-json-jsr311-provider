package templemore.liftjson.provider

import fixture.{Person, Address, TestRestService}
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
    "support transformation of incoming JSON"                        ! transformingInRestCall^
    "support transformation of outgoing JSON"                        ! transformingOutRestCall^
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