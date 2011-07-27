package templemore.liftjson.provider

import javax.ws.rs.core.MediaType

private[provider] trait SupportedTypes {

  protected def isSupportedFor(mediaType: MediaType, classType: Class[_]): Boolean = {
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