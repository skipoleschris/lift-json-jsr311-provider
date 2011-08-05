package templemore.liftjson.provider

import javax.ws.rs.core.MultivaluedMap

class Jsr311MultiValuedMap extends java.util.HashMap[String, java.util.List[AnyRef]] with MultivaluedMap[String, AnyRef] {

    def putSingle(key: String, value: AnyRef): Unit = {
      val valueList = new java.util.ArrayList[AnyRef](1)
      valueList.add(value)
      put(key, valueList)
    }

    def add(key: String, value: AnyRef): Unit = {
      val valueList = get(key)
      if ( valueList == null ) putSingle(key, value)
      else valueList.add(value)
    }

    def getFirst(key: String) = {
      val valueList = get(key)
      if ( valueList == null || valueList.size == 0 ) null
      else valueList.get(0)
    }
}

object Jsr311MultiValuedMap {
  def apply() = new Jsr311MultiValuedMap()
}