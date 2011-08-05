package templemore.liftjson.provider

import fixture.{AddressOutputTransformer, AddressInputTransformer, Address}
import org.specs2.Specification
import util.JsonUtilities
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class LiftJsonIntegrationSpec extends Specification
                              with LiftJsonIntegration
                              with JsonUtilities { def is =

  "Specification for the trait integrating lift json support"        ^
                                                                     endp^
  "The lift json integration should"                                 ^
    "convert json into a case class instance"                        ! readCaseClassFromJson^
    "convert a case class instance into json"                        ! writeJsonFromCaseClass^
    "convert transformed json into a case class"                     ! readCaseClassFromTransformedJson^
    "convert a case class instance into transformed json"            ! writeTransformedJsonFromCaseClass^
    "convert json into optional case class fields"                   ! readCaseClassWithOptionalsFromJson^
    "convert a optional case class fields into json"                 ! writeJsonFromCaseClassWithOptionals^
    "deal with not being able to map json to a case class"           ! failureToMapJson^
                                                                     end

  protected def config = ProviderConfig()

  def readCaseClassFromJson = {
    val json = """{
          "lines": [ "Line 1" ],
          "town" : "Town",
          "postcode" : "POSTCODE",
          "country" : "UK"
        }"""

    val entityStream = new ByteArrayInputStream(json.getBytes)
    convertFromJson(classOf[Address].asInstanceOf[Class[AnyRef]],
                    entityStream,
                    None) must_== Right(Address(List("Line 1"), "Town", "POSTCODE", "UK"))
  }

  def writeJsonFromCaseClass = {
    val entityStream = new ByteArrayOutputStream()
    convertToJson(Address(List("Line 1"), "Town", "POSTCODE", "UK"),
                  entityStream, None)
      entityStream.toString must_== compact("""{
          "lines": [ "Line 1" ],
          "town" : "Town",
          "postcode" : "POSTCODE",
          "country" : "UK"
        }""")
  }

  def readCaseClassFromTransformedJson = {
    val json = """{
          "line1" : "Line 1",
          "line2" : "Line 2",
          "town" : "Town",
          "postcode" : "POSTCODE",
          "country" : "UK"
        }"""

    val entityStream = new ByteArrayInputStream(json.getBytes)
    convertFromJson(classOf[Address].asInstanceOf[Class[AnyRef]],
                    entityStream,
                    Some(classOf[AddressInputTransformer])) must_== Right(Address(List("Line 1", "Line 2"), "Town", "POSTCODE", "UK"))
  }

  def writeTransformedJsonFromCaseClass = {
    val entityStream = new ByteArrayOutputStream()
    convertToJson(Address(List("Line 1", "Line 2"), "Town", "POSTCODE", "UK"),
                  entityStream, Some(classOf[AddressOutputTransformer]))
      entityStream.toString must_== compact("""{
          "line1" : "Line 1",
          "line2" : "Line 2",
          "town" : "Town",
          "postcode" : "POSTCODE",
          "country" : "UK"
        }""")
  }

  def readCaseClassWithOptionalsFromJson = {
    val json = """{
          "foo" : "Line 1"
        }"""

    val entityStream = new ByteArrayInputStream(json.getBytes)
    convertFromJson(classOf[FooBar].asInstanceOf[Class[AnyRef]],
                    entityStream,
                    None) must_== Right(FooBar(Some("Line 1"), None))
  }

  def writeJsonFromCaseClassWithOptionals = {
    val entityStream = new ByteArrayOutputStream()
    convertToJson(FooBar(Some("Line 1"), None),
                  entityStream, None)
      entityStream.toString must_== compact("""{
          "foo" : "Line 1"
        }""")
  }

  def failureToMapJson = {
    val json = """{
          "invalid" : "Line 1"
        }"""

    val entityStream = new ByteArrayInputStream(json.getBytes)
    convertFromJson(classOf[BarFoo].asInstanceOf[Class[AnyRef]],
                    entityStream,
                    None) must_== Left(new MappingError("No usable value for foo. Did not find value which can be converted into java.lang.String"))
  }
}


case class FooBar(foo: Option[String], bar: Option[String])
case class BarFoo(foo: String)
