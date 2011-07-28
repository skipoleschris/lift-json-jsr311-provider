package templemore.liftjson.provider

import fixture.{AddressInputTransformer, Address}
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
                                                                     end

  //TODO: Tests covering converting json into a case class with a transformer present

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
                    None) must_== Address(List("Line 1"), "Town", "POSTCODE", "UK")
  }

  def writeJsonFromCaseClass = {
    val entityStream = new ByteArrayOutputStream()
    convertToJson(Address(List("Line 1"), "Town", "POSTCODE", "UK"),
                  entityStream)
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
                    Some(classOf[AddressInputTransformer])) must_== Address(List("Line 1", "Line 2"), "Town", "POSTCODE", "UK")
  }
}
