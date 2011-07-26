package templemore.liftjson.provider

import org.specs2.Specification
import com.sun.jersey.api.client.ClientResponse
import java.text.SimpleDateFormat
import java.util.{TimeZone, Date, Calendar}

class ProviderAcceptanceTest extends Specification with RestServiceFixture { def is =

  "Acceptance test for the Lift-Json JSR 311 provider"               ^
                                                                     endp^
  "The JSR 311 provider should"                                      ^
    "Support automated conversion from JSON into a domain object"    ! simpleInRestCall^
                                                                     end

  def simpleInRestCall = {
    val resource = new SimpleRestService()
    invokeService[String](resource, "/simple", 204) { res =>
      res.header("Content-Type", "application/json").put(classOf[ClientResponse], """{
        "firstName" : "Chris",
        "surname" : "Turner",
        "dob" : "%s",
        "address" : {
          "lines": [ "4 Some Building", "Some Road" ],
          "town": "Some Town",
          "postcode" : "ST1 1ST",
          "country" : "UK"
        }
      }""".format(asIsoString(makeDate(2, 7, 1973))))
    }

    val expectedAddress = Address(Seq("4 Some Building", "Some Road"), "Some Town", "ST1 1ST", "UK")
    val expectedPerson = Person("Chris", "Turner", makeDate(2, 7, 1973, TimeZone.getTimeZone("UTC")), expectedAddress)
    resource.lastPerson must_== expectedPerson
  }

  private def makeDate(day: Int, month: Int, year: Int, timezone: TimeZone = TimeZone.getDefault) = {
    val cal = Calendar.getInstance()
    cal.setTimeZone(timezone)
    cal.set(year, month - 1, day, 0, 0, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

  private def asIsoString(date: Date) = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    sdf.format(date)
  }
}