package templemore.liftjson.provider

import java.lang.reflect.Type
import java.lang.annotation.Annotation
import javax.ws.rs.ext.{Provider, MessageBodyReader, MessageBodyWriter}
import java.io.{OutputStream, InputStream}
import javax.ws.rs.core.{MultivaluedMap, MediaType}
import javax.ws.rs.{WebApplicationException, Consumes, Produces}
import jsr311.Jsr311ResponseAdapter

@Provider
@Consumes(Array(MediaType.APPLICATION_JSON, "text/json"))
@Produces(Array(MediaType.APPLICATION_JSON, "text/json"))
class LiftJsonProvider(val config: ProviderConfig) extends MessageBodyReader[AnyRef]
                                                   with MessageBodyWriter[AnyRef]
                                                   with LiftJsonIntegration
                                                   with SupportedTypes {

  def this() = this(ProviderConfig())

  def isWriteable(classType: Class[_],
                  genericType: Type,
                  annotations: Array[Annotation],
                  mediaType: MediaType) =
    isSupportedFor(mediaType.toString, classType)

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
    try {
      convertToJson(value, entityStream, transformerClass(annotations))
    }
    catch {
      case e => {
        val error = config.errorResponseGenerator.generate(e)
        throw new WebApplicationException(Jsr311ResponseAdapter(error))
      }
    }
  }

  def isReadable(classType: Class[_],
                 genericType: Type,
                 annotations: Array[Annotation],
                 mediaType: MediaType) = {
    isSupportedFor(mediaType.toString, classType)
  }

  def readFrom(classType: Class[AnyRef],
               genericType: Type,
               annotations: Array[Annotation],
               mediaType: MediaType,
               httpHeaders: MultivaluedMap[String, String],
               entityStream: InputStream) =  {
    def handleMappingError(mappingError: MappingError) = {
      val error = config.errorResponseGenerator.generate(mappingError)
      throw new WebApplicationException(Jsr311ResponseAdapter(error))
    }

    try {
      convertFromJson(classType, entityStream, transformerClass(annotations))
              .fold[AnyRef](handleMappingError, (obj => obj))
    }
    catch {
      case wae: WebApplicationException => throw wae
      case e => {
        val error = config.errorResponseGenerator.generate(e)
        throw new WebApplicationException(Jsr311ResponseAdapter(error))
      }
    }
  }

  // Yuk, converting between Java and Scala can sometimes be messy!
  private def transformerClass(annotations: Array[Annotation]) =
    annotations.find(_.isInstanceOf[Transformer])
               .asInstanceOf[Option[Transformer]]
               .map(_.value)
               .asInstanceOf[Option[Class[JsonASTTransformer]]]
}