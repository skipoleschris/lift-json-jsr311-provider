package templemore.liftjson.provider.fixture

import javax.ws.rs.core.MediaType
import javax.ws.rs._
import java.util.{TimeZone, Date}
import templemore.liftjson.provider.util.DateUtilities

@Path("ws")
class SimpleRestService extends DateUtilities {

  var lastPerson: Person = _

  @PUT
  @Path("simple")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def simplePut(person: Person): Unit = {
    lastPerson = person
  }

  @GET
  @Path("simple")
  @Produces(Array(MediaType.APPLICATION_JSON))
  def simpleGet: Person = {
    val address = Address(Seq("4 Some Building", "Some Road"), "Some Town", "ST1 1ST", "UK")
    Person("Chris", "Turner", makeDate(2, 7, 1973, TimeZone.getTimeZone("UTC")), address)
  }

  @PUT
  @Path("transforming")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def transformingPut(person: Person): Unit = {
    lastPerson = person
  }
}

case class Person(firstName: String, surname: String, dob: Date, address: Address)
case class Address(lines: Seq[String], town: String, postcode: String, country: String)