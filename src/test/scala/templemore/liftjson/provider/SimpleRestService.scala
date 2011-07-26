package templemore.liftjson.provider

import javax.ws.rs.{Consumes, PUT, Path}
import javax.ws.rs.core.MediaType
import java.util.Date

@Path("simple")
class SimpleRestService {

  var lastPerson: Person = _

  @PUT
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def simplePut(person: Person): Unit = {
    println("Person: " + person)
    lastPerson = person
  }
}

case class Person(firstName: String, surname: String, dob: Date, address: Address)
case class Address(lines: Seq[String], town: String, postcode: String, country: String)