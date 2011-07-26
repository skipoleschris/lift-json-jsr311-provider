package templemore.liftjson.provider

import javax.ws.rs.{Consumes, Produces}
import java.lang.reflect.Type
import java.lang.annotation.Annotation
import javax.ws.rs.core.{MultivaluedMap, MediaType}
import java.io.{OutputStream, InputStream}
import javax.ws.rs.ext.{Provider, MessageBodyReader, MessageBodyWriter}
import net.liftweb.json._
import io.Source

@Provider
@Consumes(Array(MediaType.APPLICATION_JSON, "text/json"))
@Produces(Array(MediaType.APPLICATION_JSON, "text/json"))
class LiftJsonProvider extends MessageBodyReader[AnyRef] with MessageBodyWriter[AnyRef] {

  def isWriteable(classType: Class[_],
                  genericType: Type,
                  annotations: Array[Annotation],
                  mediaType: MediaType) = {
    println("::isWritable")
    println("classType=" + classType)
    println("genericType=" + genericType)
    println("annotations=" + annotations)
    println("mediaType=" + mediaType)
    false
  }

  def getSize(value: AnyRef,
              classType: Class[_],
              genericType: Type,
              annotations: Array[Annotation],
              mediaType: MediaType) = {
    println("::getSize")
    println("value=" + value)
    println("classType=" + classType)
    println("genericType=" + genericType)
    println("annotations...")
    annotations.foreach(a => println(a.getClass))
    println("mediaType=" + mediaType)
    0L
  }

  def writeTo(value: AnyRef,
              classType: Class[_],
              genericType: Type,
              annotations: Array[Annotation],
              mediaType: MediaType,
              httpHeaders: MultivaluedMap[String, AnyRef],
              entityStream: OutputStream): Unit = {
    println("::writeTo")
    println("value=" + value)
    println("classType=" + classType)
    println("genericType=" + genericType)
    println("annotations...")
    annotations.foreach(a => println(a.getClass))
    println("mediaType=" + mediaType)
    println("httpHeaders=" + httpHeaders)
  }

  def isReadable(classType: Class[_],
                 genericType: Type,
                 annotations: Array[Annotation],
                 mediaType: MediaType) = {
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

  def readFrom(classType: Class[AnyRef],
               genericType: Type,
               annotations: Array[Annotation],
               mediaType: MediaType,
               httpHeaders: MultivaluedMap[String, String],
               entityStream: InputStream) = {
    val buf = new scala.collection.mutable.StringBuilder()
    Source.createBufferedSource(entityStream).getLines().foreach(buf.append)
    classType.cast(parseAndExtract(buf.toString(), classType))
  }

  private def parseAndExtract(json: String, classType: Class[_]): AnyRef = {
    val jsonAst = parse(json)
    jsonAst.extract(DefaultFormats, Manifest.classType(classType))
  }
}