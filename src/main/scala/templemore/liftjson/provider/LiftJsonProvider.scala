package templemore.liftjson.provider

import javax.ws.rs.{Consumes, Produces}
import java.lang.reflect.Type
import java.lang.annotation.Annotation
import javax.ws.rs.core.{MultivaluedMap, MediaType}
import javax.ws.rs.ext.{Provider, MessageBodyReader, MessageBodyWriter}
import io.Source
import net.liftweb.json._
import java.io.{OutputStreamWriter, OutputStream, InputStream}

@Provider
@Consumes(Array(MediaType.APPLICATION_JSON, "text/json"))
@Produces(Array(MediaType.APPLICATION_JSON, "text/json"))
class LiftJsonProvider extends MessageBodyReader[AnyRef] with MessageBodyWriter[AnyRef] {

  def isWriteable(classType: Class[_],
                  genericType: Type,
                  annotations: Array[Annotation],
                  mediaType: MediaType) =
    isJsonConversionForCaseClass(mediaType, classType)

  def getSize(value: AnyRef,
              classType: Class[_],
              genericType: Type,
              annotations: Array[Annotation],
              mediaType: MediaType) = -1L

  def writeTo(value: AnyRef,
              classType: Class[_],
              genericType: Type,
              annotations: Array[Annotation],
              mediaType: MediaType,
              httpHeaders: MultivaluedMap[String, AnyRef],
              entityStream: OutputStream): Unit = {
    val jsonAST = Extraction.decompose(value)(DefaultFormats)
    Printer.compact(render(jsonAST), new OutputStreamWriter(entityStream))
  }

  def isReadable(classType: Class[_],
                 genericType: Type,
                 annotations: Array[Annotation],
                 mediaType: MediaType) = {
    isJsonConversionForCaseClass(mediaType, classType)
  }

  def readFrom(classType: Class[AnyRef],
               genericType: Type,
               annotations: Array[Annotation],
               mediaType: MediaType,
               httpHeaders: MultivaluedMap[String, String],
               entityStream: InputStream) = {
    def extract(jsonAST: JValue, classType: Class[_]): AnyRef =
      jsonAST.extract(DefaultFormats, Manifest.classType(classType))

    val buf = new scala.collection.mutable.StringBuilder()
    Source.createBufferedSource(entityStream).getLines().foreach(buf.append)

    val jsonAST = parse(buf.toString)

    val transformer = annotations.find(_.isInstanceOf[Transformer]).asInstanceOf[Option[Transformer]].map(createTransformer)
    val transformedJsonAST = transformer.map(_.transform(jsonAST)).getOrElse(jsonAST)

    classType.cast(extract(transformedJsonAST, classType))
  }

  def createTransformer(annotation: Transformer) = {
    annotation.value().newInstance().asInstanceOf[JsonASTTransformer]
  }

  private def isJsonConversionForCaseClass(mediaType: MediaType, classType: Class[_]): Boolean = {
    isJsonType(Option(mediaType)) &&
    classType.getInterfaces.contains(classOf[Product])
  }

  private def isJsonType(mediaType: Option[MediaType]) = {
    def checkSubType(m: MediaType) = {
      val subtype = m.getSubtype
      "json".equalsIgnoreCase (subtype) || subtype.endsWith ("+json")
    }
    mediaType.map(checkSubType).getOrElse(false)
  }
}