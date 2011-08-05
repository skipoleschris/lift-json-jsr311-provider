package templemore.liftjson.provider

import org.specs2.Specification

class Jsr311MultiValuedMapSpec extends Specification { def is =

  "Specification for the Jsr-311 Multi Valued Map"                   ^
                                                                     endp^
  "A Jsr-311 multi valued map should"                                ^
    "put a single value into the map"                                ! singleValuePut^
    "add a single value into the map"                                ! singleValueAdd^
    "add an additional value into the map"                           ! additionalValueAdd^
    "get a value from the map"                                       ! getValue^
    "get a value not in the map"                                     ! getValueNotInMap^
                                                                     end

  def singleValuePut = {
    val map = Jsr311MultiValuedMap()
    map.putSingle("key", "value")
    map.get("key").get(0) must_==  "value"
  }

  def singleValueAdd = {
    val map = Jsr311MultiValuedMap()
    map.add("key", "value")
    map.get("key").get(0) must_==  "value"
  }

  def additionalValueAdd = {
    val map = Jsr311MultiValuedMap()
    map.add("key", "value")
    map.add("key", "value")
    map.get("key").size must_==  2
  }

  def getValue = {
    val map = Jsr311MultiValuedMap()
    map.add("key", "value")
    map.getFirst("key") must_==  "value"
  }

  def getValueNotInMap = {
    val map = Jsr311MultiValuedMap()
    map.getFirst("key") must_==  null
  }
}