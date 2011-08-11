package templemore.liftjson.provider.fixture

import javax.ws.rs.core.MediaType
import javax.ws.rs._
import java.util.{TimeZone, Date}
import templemore.liftjson.provider.util.DateUtilities
import templemore.liftjson.provider.Transformer

@Path("ws")
class TestRestService extends DateUtilities {

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
  def transformingPut(@Transformer(classOf[PersonInputTransformer]) person: Person): Unit = {
    lastPerson = person
  }

  @GET
  @Path("transforming")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Transformer(classOf[PersonOutputTransformer])
  def transformingGet: Person = {
    val address = Address(Seq("4 Some Building", "Some Road"), "Some Town", "ST1 1ST", "UK")
    Person("Chris", "Turner", makeDate(2, 7, 1973, TimeZone.getTimeZone("UTC")), address)
  }

  @PUT
  @Path("exception")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def exceptionPut(@Transformer(classOf[ExceptionThrowingTransformer]) person: Person): Unit = {
  }

  @GET
  @Path("exception")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Transformer(classOf[ExceptionThrowingTransformer])
  def exceptionGet: Person = {
    val address = Address(Seq("4 Some Building", "Some Road"), "Some Town", "ST1 1ST", "UK")
    Person("Chris", "Turner", makeDate(2, 7, 1973, TimeZone.getTimeZone("UTC")), address)
  }
}

case class Person(firstName: String, surname: String, dob: Date, address: Address)
case class Address(lines: Seq[String], town: String, postcode: String, country: String)