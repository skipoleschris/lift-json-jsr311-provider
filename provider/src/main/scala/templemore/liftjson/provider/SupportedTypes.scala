package templemore.liftjson.provider

private[provider] trait SupportedTypes {

  protected def isSupportedFor(mediaType: String, classType: Class[_]): Boolean = {
    def isProduct(clazz: Class[_]) = clazz.getInterfaces.contains(classOf[Product])

    isJsonType(Option(mediaType)) &&
    (if ( classType.isArray) isProduct(classType.getComponentType) else isProduct(classType))
  }

  private def isJsonType(mediaType: Option[String]) = {
    def checkSubType(m: String) = {
      val subTypeIndex = m.indexOf("/")
      val subType = if ( subTypeIndex == -1 ) "" else m.substring(subTypeIndex + 1)
      "json".equalsIgnoreCase (subType) || subType.endsWith ("+json")
    }
    mediaType.map(checkSubType).getOrElse(false)
  }
}