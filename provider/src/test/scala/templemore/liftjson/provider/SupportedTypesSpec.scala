package templemore.liftjson.provider

import org.specs2.Specification

class SupportedTypesSpec extends Specification with SupportedTypes { def is =

  "Specification for the supported types trait"                      ^
                                                                     endp^
  "Supported types should"                                           ^
    "support json media type for a scala case class"                 ! supported("application/json",
                                                                                 classOf[CaseClass])^
    "support json media type for a scala case class in an array"     ! supported("application/json",
                                                                                 classOf[Array[CaseClass]])^
    "not support json media type for a normal scala class"           ! notSupported("application/json",
                                                                                    classOf[NormalClass])^
    "not support json media type for a normal scala class in an array" ! notSupported("application/json",
                                                                                      classOf[Array[NormalClass]])^
    "not support text media type for a scala case class"             ! notSupported("text/plain",
                                                                                    classOf[CaseClass])^
                                                                     end


  def supported(mediaType: String, classType: Class[_]) = {
    isSupportedFor(mediaType, classType) must_==  true
  }

  def notSupported(mediaType: String, classType: Class[_]) = {
    isSupportedFor(mediaType, classType) must_==  false
  }

  case class CaseClass(name: String)
  class NormalClass
}