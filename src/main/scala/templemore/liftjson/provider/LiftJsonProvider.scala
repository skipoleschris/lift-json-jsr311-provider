package templemore.liftjson.provider

import javax.ws.rs.{Consumes, Produces}
import java.lang.reflect.Type
import java.lang.annotation.Annotation
import javax.ws.rs.core.{MultivaluedMap, MediaType}
import javax.ws.rs.ext.{Provider, MessageBodyReader, MessageBodyWriter}
import java.io.{OutputStream, InputStream}

@Provider
@Consumes(Array(MediaType.APPLICATION_JSON, "text/json"))
@Produces(Array(MediaType.APPLICATION_JSON, "text/json"))
class LiftJsonProvider extends MessageBodyReader[AnyRef]
                       with MessageBodyWriter[AnyRef]
                       with LiftJsonIntegration
                       with SupportedTypes {

  def isWriteable(classType: Class[_],
                  genericType: Type,
                  annotations: Array[Annotation],
                  mediaType: MediaType) =
    isSupportedFor(mediaType, classType)

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
              entityStream: OutputStream): Unit =
    convertToJson(value, entityStream)

  def isReadable(classType: Class[_],
                 genericType: Type,
                 annotations: Array[Annotation],
                 mediaType: MediaType) = {
    isSupportedFor(mediaType, classType)
  }

  def readFrom(classType: Class[AnyRef],
               genericType: Type,
               annotations: Array[Annotation],
               mediaType: MediaType,
               httpHeaders: MultivaluedMap[String, String],
               entityStream: InputStream) =  {
    convertFromJson(classType, entityStream, transformerClass(annotations))
  }

  // Yuk, converting between Java and Scala can sometimes be messy!
  private def transformerClass(annotations: Array[Annotation]) =
    annotations.find(_.isInstanceOf[Transformer])
               .asInstanceOf[Option[Transformer]]
               .map(_.value)
               .asInstanceOf[Option[Class[JsonASTTransformer]]]
}