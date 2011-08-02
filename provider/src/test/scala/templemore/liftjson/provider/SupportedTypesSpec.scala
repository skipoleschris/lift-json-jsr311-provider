package templemore.liftjson.provider

import org.specs2.Specification
import javax.ws.rs.core.MediaType

class SupportedTypesSpec extends Specification with SupportedTypes { def is =

  "Specification for the supported types trait"                      ^
                                                                     endp^
  "Supported types should"                                           ^
    "support json media type for a scala case class"                 ! supported(MediaType.APPLICATION_JSON_TYPE,
                                                                                 classOf[CaseClass])^
    "support json media type for a scala case class in an array"     ! supported(MediaType.APPLICATION_JSON_TYPE,
                                                                                 classOf[Array[CaseClass]])^
    "not support json media type for a normal scala class"           ! notSupported(MediaType.APPLICATION_JSON_TYPE,
                                                                                    classOf[NormalClass])^
    "not support json media type for a normal scala class in an array" ! notSupported(MediaType.APPLICATION_JSON_TYPE,
                                                                                      classOf[Array[NormalClass]])^
    "not support text media type for a scala case class"             ! notSupported(MediaType.TEXT_PLAIN_TYPE,
                                                                                    classOf[CaseClass])^
                                                                     end


  def supported(mediaType: MediaType, classType: Class[_]) = {
    isSupportedFor(mediaType, classType) must_==  true
  }

  def notSupported(mediaType: MediaType, classType: Class[_]) = {
    isSupportedFor(mediaType, classType) must_==  false
  }

  case class CaseClass(name: String)
  class NormalClass
}