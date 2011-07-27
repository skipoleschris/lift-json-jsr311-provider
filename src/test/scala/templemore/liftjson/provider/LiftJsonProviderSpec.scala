package templemore.liftjson.provider

import fixture.{Address, Person}
import org.specs2.Specification
import javax.ws.rs.core.MediaType
import util.JsonUtilities
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class LiftJsonProviderSpec extends Specification with JsonUtilities { def is =

  "Specification for the lift-json provider class"                   ^
                                                                     endp^
  "A writer lift-json provider should"                               ^
    "Indicate it can write for json into a case class"               ! canWriteJsonCaseClass^
    "Indicate it CAN NOT write for text into a case class"           ! canNotWriteTextCaseClass^
    "Indicate it CAN NOT write for json into a non-case class"       ! canNotWriteJsonNormalClass^
    "Fail to provide a size"                                         ! noSizeProvided^
    "Convert a case class instance into json"                        ! writeJsonFromCaseClass^
                                                                     endp^
  "A reader lift-json provider should"                               ^
    "Indicate it can read for json into a case class"                ! canReadJsonCaseClass^
    "Indicate it CAN NOT read for text into a case class"            ! canNotReadTextCaseClass^
    "Indicate it CAN NOT read for json into a non-case class"        ! canNotReadJsonNormalClass^
    "Convert json into a case class instance"                        ! readCaseClassFromJson^
                                                                     end


  def canWriteJsonCaseClass = {
    val provider = new LiftJsonProvider()
    provider.isWriteable(classOf[Person],
                         classOf[Person].getGenericSuperclass,
                         Array[java.lang.annotation.Annotation](),
                         MediaType.APPLICATION_JSON_TYPE) must_== true
  }

  def canNotWriteTextCaseClass = {
    val provider = new LiftJsonProvider()
    provider.isWriteable(classOf[Person],
                         classOf[Person].getGenericSuperclass,
                         Array[java.lang.annotation.Annotation](),
                         MediaType.TEXT_PLAIN_TYPE) must_== false
  }

  def canNotWriteJsonNormalClass = {
    val provider = new LiftJsonProvider()
    provider.isWriteable(classOf[NonCaseClass],
                         classOf[NonCaseClass].getGenericSuperclass,
                         Array[java.lang.annotation.Annotation](),
                         MediaType.APPLICATION_JSON_TYPE) must_== false
  }

  def noSizeProvided = {
    val provider = new LiftJsonProvider()
    provider.getSize(Address(List("Line 1"), "Town", "POSTCODE", "UK"),
                     classOf[Address],
                     classOf[Address].getGenericSuperclass,
                     Array[java.lang.annotation.Annotation](),
                     MediaType.APPLICATION_JSON_TYPE) must_== -1
  }

  def writeJsonFromCaseClass = {
    val provider = new LiftJsonProvider()
    val entityStream = new ByteArrayOutputStream()
    provider.writeTo(Address(List("Line 1"), "Town", "POSTCODE", "UK"),
                     classOf[Address],
                     classOf[Address].getGenericSuperclass,
                     Array[java.lang.annotation.Annotation](),
                     MediaType.APPLICATION_JSON_TYPE,
                     null,
                     entityStream)
      entityStream.toString must_== compact("""{
          "lines": [ "Line 1" ],
          "town" : "Town",
          "postcode" : "POSTCODE",
          "country" : "UK"
        }""")
  }

  def canReadJsonCaseClass = {
    val provider = new LiftJsonProvider()
    provider.isReadable(classOf[Person],
                        classOf[Person].getGenericSuperclass,
                        Array[java.lang.annotation.Annotation](),
                        MediaType.APPLICATION_JSON_TYPE) must_== true
  }

  def canNotReadTextCaseClass = {
    val provider = new LiftJsonProvider()
    provider.isReadable(classOf[Person],
                        classOf[Person].getGenericSuperclass,
                        Array[java.lang.annotation.Annotation](),
                        MediaType.TEXT_PLAIN_TYPE) must_== false
  }

  def canNotReadJsonNormalClass = {
    val provider = new LiftJsonProvider()
    provider.isReadable(classOf[NonCaseClass],
                        classOf[NonCaseClass].getGenericSuperclass,
                        Array[java.lang.annotation.Annotation](),
                        MediaType.APPLICATION_JSON_TYPE) must_== false
  }

  def readCaseClassFromJson = {
    val provider = new LiftJsonProvider()
    val json = """{
          "lines": [ "Line 1" ],
          "town" : "Town",
          "postcode" : "POSTCODE",
          "country" : "UK"
        }"""

    val entityStream = new ByteArrayInputStream(json.getBytes)
    provider.readFrom(classOf[Address].asInstanceOf[Class[AnyRef]],
                      classOf[Address].getGenericSuperclass,
                      Array[java.lang.annotation.Annotation](),
                      MediaType.APPLICATION_JSON_TYPE,
                      null,
                      entityStream) must_== Address(List("Line 1"), "Town", "POSTCODE", "UK")
  }

  class NonCaseClass(val content: String)
}