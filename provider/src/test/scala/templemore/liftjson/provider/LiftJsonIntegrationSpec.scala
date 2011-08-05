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
    "Convert json into a case class instance"                        ! readCaseClassFromJson^
    "Convert a case class instance into json"                        ! writeJsonFromCaseClass^
    "Convert transformed json into a case class"                     ! readCaseClassFromTransformedJson^
    "Convert a case class instance into transformed json"            ! writeTransformedJsonFromCaseClass^
    "Convert json into optional case class fields"                   ! readCaseClassWithOptionalsFromJson^
    "Convert a optional case class fields into json"                 ! writeJsonFromCaseClassWithOptionals^
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
}

case class FooBar(foo: Option[String], bar: Option[String])
