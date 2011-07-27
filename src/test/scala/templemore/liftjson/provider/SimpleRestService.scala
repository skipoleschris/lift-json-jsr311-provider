package templemore.liftjson.provider

import javax.ws.rs.core.MediaType
import javax.ws.rs._
import java.util.{TimeZone, Date}
import net.liftweb.json.JsonAST.{JField, JValue}
import com.sun.tools.hat.internal.model.JavaValue
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing.Validation
import ch.epfl.lamp.fjbg.JField

@Path("ws")
class SimpleRestService extends DateUtilities {

  var lastPerson: Person = _

  @PUT
  @Path("simple")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def simplePut(person: Person): Unit = {
    println("Person: " + person)
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
  def transformingPut(@Transformer(classOf[PersonInTransformer]) person: Person): Unit = {
    println("Person: " + person)
    lastPerson = person
  }
}

case class Person(firstName: String, surname: String, dob: Date, address: Address)
case class Address(lines: Seq[String], town: String, postcode: String, country: String)

class PersonInTransformer extends JsonASTTransformer {

  def transform(json: JValue) = {
    def isFullName(v: JValue) = v match {
      case JField("fullName", x) => true
      case _ => false
    }

    val fullName = json.find(isFullName)
    json.remove(isFullName).
  }
}