package templemore.liftjson.provider

import java.lang.reflect.Type
import java.lang.annotation.Annotation
import javax.ws.rs.ext.{Provider, MessageBodyReader, MessageBodyWriter}
import java.io.{OutputStream, InputStream}
import javax.ws.rs.{WebApplicationException, Consumes, Produces}
import javax.ws.rs.core.{Response, MultivaluedMap, MediaType}
import javax.ws.rs.core.Response.Status

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
    convertToJson(value, entityStream, transformerClass(annotations))

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
    try {
      convertFromJson(classType, entityStream, transformerClass(annotations))
    }
    catch {
      case e => {
        throw new WebApplicationException(new Response {
          def getMetadata = Jsr311MultiValuedMap()
          def getStatus = Status.INTERNAL_SERVER_ERROR.getStatusCode
          def getEntity = """{
            "applicationCode" : "0",
            "httpStatusCode" : "%d",
            "httpReasonPhrase" : "%s",
            "cause" : "%s",
            "message" : "%s"
            }""".format(Status.INTERNAL_SERVER_ERROR.getStatusCode,
                        Status.INTERNAL_SERVER_ERROR.getReasonPhrase,
                        e.getClass.getName,
                        e.getMessage)
        })
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