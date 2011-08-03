package templemore.liftjson.provider

import javax.ws.rs.core.MediaType

private[provider] trait SupportedTypes {

  protected def isSupportedFor(mediaType: MediaType, classType: Class[_]): Boolean = {
    def isProduct(clazz: Class[_]) = clazz.getInterfaces.contains(classOf[Product])

    isJsonType(Option(mediaType)) &&
    (if ( classType.isArray) isProduct(classType.getComponentType) else isProduct(classType))
  }

  private def isJsonType(mediaType: Option[MediaType]) = {
    def checkSubType(m: MediaType) = {
      val subtype = m.getSubtype
      "json".equalsIgnoreCase (subtype) || subtype.endsWith ("+json")
    }
    mediaType.map(checkSubType).getOrElse(false)
  }
}