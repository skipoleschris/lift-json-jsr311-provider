package templemore.liftjson.provider

import fixture.{PersonInputTransformer, Address, Person}
import org.specs2.Specification
import javax.ws.rs.core.MediaType
import util.{DateUtilities, JsonUtilities}
import java.io.{InputStream, OutputStream, ByteArrayInputStream, ByteArrayOutputStream}
import io.Source
import collection.mutable.StringBuilder
import javax.ws.rs.WebApplicationException

class LiftJsonProviderSpec extends Specification with JsonUtilities with DateUtilities { def is =

  "Specification for the lift-json provider class"                   ^
                                                                     endp^
  "A writer lift-json provider should"                               ^
    "indicate it can write for json into a case class"               ! canWriteJsonCaseClass^
    "indicate it CAN NOT write for text into a case class"           ! canNotWriteTextCaseClass^
    "indicate it CAN NOT write for json into a non-case class"       ! canNotWriteJsonNormalClass^
    "fail to provide a size"                                         ! noSizeProvided^
    "convert the expected case class instance"                       ! convertFromCaseClass^
    "write json to the supplied entity stream"                       ! writeToEntityStream^
    "not utilise and transformer when no annotation"                 ! notUtiliseWriteTransformer^
    "recognise a transformer annotation"                             ! utiliseWriteTransformer^
                                                                     endp^
  "A reader lift-json provider should"                               ^
    "indicate it can read for json into a case class"                ! canReadJsonCaseClass^
    "indicate it CAN NOT read for text into a case class"            ! canNotReadTextCaseClass^
    "indicate it CAN NOT read for json into a non-case class"        ! canNotReadJsonNormalClass^
    "convert json from the supplied entity stream"                   ! readFromEntityStream^
    "convert into the expected case class"                           ! convertToCaseClass^
    "not utilise any transformer when no annotation"                 ! notUtiliseReadTransformer^
    "recognise a transformer annotation"                             ! utiliseReadTransformer^
    "generate a 400 exception for invalid json"                      ! exceptionForInvalidJson^
                                                                     end

  def canWriteJsonCaseClass = {
    val provider = new TestableLiftJsonProvider()
    provider.isWriteable(classOf[Person],
                         classOf[Person].getGenericSuperclass,
                         Array[java.lang.annotation.Annotation](),
                         MediaType.APPLICATION_JSON_TYPE) must_== true
  }

  def canNotWriteTextCaseClass = {
    val provider = new TestableLiftJsonProvider()
    provider.isWriteable(classOf[Person],
                         classOf[Person].getGenericSuperclass,
                         Array[java.lang.annotation.Annotation](),
                         MediaType.TEXT_PLAIN_TYPE) must_== false
  }

  def canNotWriteJsonNormalClass = {
    val provider = new TestableLiftJsonProvider()
    provider.isWriteable(classOf[NonCaseClass],
                         classOf[NonCaseClass].getGenericSuperclass,
                         Array[java.lang.annotation.Annotation](),
                         MediaType.APPLICATION_JSON_TYPE) must_== false
  }

  def noSizeProvided = {
    val provider = new TestableLiftJsonProvider()
    provider.getSize(Address(List("Line 1"), "Town", "POSTCODE", "UK"),
                     classOf[Address],
                     classOf[Address].getGenericSuperclass,
                     Array[java.lang.annotation.Annotation](),
                     MediaType.APPLICATION_JSON_TYPE) must_== -1
  }

  def convertFromCaseClass = {
    val entityStream = new ByteArrayOutputStream()
    val provider = writeTo(entityStream)
    provider.capturedValue must_== Address(List("Line 1"), "Town", "POSTCODE", "UK")
  }

  def writeToEntityStream = {
    val entityStream = new ByteArrayOutputStream()
    writeTo(entityStream)
    compact(entityStream.toString) must_== compact("""{
          "lines": [ "Line 1" ],
          "town" : "Town",
          "postcode" : "POSTCODE",
          "country" : "UK"
        }""")
  }

  def notUtiliseWriteTransformer = {
    val entityStream = new ByteArrayOutputStream()
    val transformer = writeTo(entityStream)
    transformer.capturedTransformerClass must_== None
  }

  def utiliseWriteTransformer = {
    val entityStream = new ByteArrayOutputStream()
    val transformer = writeTo(entityStream, Some(transformerAnnotation))
    transformer.capturedTransformerClass must_== Some(classOf[PersonInputTransformer])
  }

  private def writeTo(entityStream: OutputStream, annotation: Option[java.lang.annotation.Annotation] = None) = {
    val json = """{
          "lines": [ "Line 1" ],
          "town" : "Town",
          "postcode" : "POSTCODE",
          "country" : "UK"
        }"""
    val provider = new TestableLiftJsonProvider(outputJson = json)
    val annotations = annotation.map(Array(_)).getOrElse(Array[java.lang.annotation.Annotation]())
    provider.writeTo(Address(List("Line 1"), "Town", "POSTCODE", "UK"),
                     classOf[Address],
                     classOf[Address].getGenericSuperclass,
                     annotations,
                     MediaType.APPLICATION_JSON_TYPE,
                     null,
                     entityStream)
    provider
  }

  def canReadJsonCaseClass = {
    val provider = new TestableLiftJsonProvider()
    provider.isReadable(classOf[Person],
                        classOf[Person].getGenericSuperclass,
                        Array[java.lang.annotation.Annotation](),
                        MediaType.APPLICATION_JSON_TYPE) must_== true
  }

  def canNotReadTextCaseClass = {
    val provider = new TestableLiftJsonProvider()
    provider.isReadable(classOf[Person],
                        classOf[Person].getGenericSuperclass,
                        Array[java.lang.annotation.Annotation](),
                        MediaType.TEXT_PLAIN_TYPE) must_== false
  }

  def canNotReadJsonNormalClass = {
    val provider = new TestableLiftJsonProvider()
    provider.isReadable(classOf[NonCaseClass],
                        classOf[NonCaseClass].getGenericSuperclass,
                        Array[java.lang.annotation.Annotation](),
                        MediaType.APPLICATION_JSON_TYPE) must_== false
  }

  def readFromEntityStream = {
    val json = """{
          "lines": [ "Line 1" ],
          "town" : "Town",
          "postcode" : "POSTCODE",
          "country" : "UK"
        }"""
    val entityStream = new ByteArrayInputStream(json.getBytes)
    val transformer = readFrom(entityStream)
    compact(transformer.capturedJson) must_== compact(json)
  }

  def convertToCaseClass = {
    val entityStream = new ByteArrayInputStream("".getBytes)
    val transformer = readFrom(entityStream)
    transformer.capturedClassType must_==  classOf[Address]
  }

  def notUtiliseReadTransformer = {
    val entityStream = new ByteArrayInputStream("".getBytes)
    val transformer = readFrom(entityStream)
    transformer.capturedTransformerClass must_== None
  }

  def utiliseReadTransformer = {
    val entityStream = new ByteArrayInputStream("".getBytes)
    val transformer = readFrom(entityStream, Some(transformerAnnotation))
    transformer.capturedTransformerClass must_== Some(classOf[PersonInputTransformer])
  }

  def exceptionForInvalidJson = {
    val entityStream = new ByteArrayInputStream("".getBytes)
    readFrom(entityStream,
             Some(transformerAnnotation),
             None,
             Some(new MappingError("The message"))) must throwA[WebApplicationException].like {
      case e: WebApplicationException => e.getResponse.getStatus must_== 400
    }
  }

  private def readFrom(entityStream: InputStream,
                       annotation: Option[java.lang.annotation.Annotation] = None,
                       outputObject: Option[Address] = Some(Address(Seq("Line 1"), "Town", "Postcode", "Country")),
                       mappingError: Option[MappingError] = None) = {
    val provider = new TestableLiftJsonProvider(outputObject = outputObject, mappingError = mappingError)
    val annotations = annotation.map(Array(_)).getOrElse(Array[java.lang.annotation.Annotation]())
    provider.readFrom(classOf[Address].asInstanceOf[Class[AnyRef]],
                      classOf[Address].getGenericSuperclass,
                      annotations,
                      MediaType.APPLICATION_JSON_TYPE,
                      null,
                      entityStream)
    provider
  }

  private def transformerAnnotation =
    classOf[NonCaseClass].getMethod("transformerPlaceholder").getAnnotation(classOf[Transformer])

  class NonCaseClass(val content: String) {
    @Transformer(classOf[PersonInputTransformer]) def transformerPlaceholder(): Unit = {}
  }

  // Testable version of the provider that overrides the conversion methods to capture
  // the parameter values rather than doing the actual conversions
  class TestableLiftJsonProvider(outputJson: String = "",
                                 outputObject: Option[AnyRef] = None,
                                 mappingError: Option[MappingError] = None) extends LiftJsonProvider {

    var capturedValue: AnyRef = _
    var capturedClassType: Class[AnyRef] = _
    var capturedTransformerClass: Option[Class[_ <: JsonASTTransformer]] = _
    var capturedJson: String = _

    override protected def convertToJson(value: AnyRef,
                                         entityStream: OutputStream,
                                         transformerClass: Option[Class[_ <: JsonASTTransformer]]): Unit = {
      capturedValue = value
      capturedTransformerClass = transformerClass
      entityStream.write(outputJson.getBytes)
    }

    override protected def convertFromJson(classType: Class[AnyRef],
                                           entityStream: InputStream,
                                           transformerClass: Option[Class[_ <: JsonASTTransformer]]): Either[MappingError, AnyRef] = {
      capturedClassType = classType
      capturedTransformerClass = transformerClass

      val buf = new StringBuilder()
      Source.fromInputStream(entityStream).getLines().foreach { l => buf.append(l); buf.append('\n') }
      capturedJson = buf.toString()

      outputObject.map(Right(_)).getOrElse(Left(mappingError.get))
    }
  }
}