package templemore.liftjson.provider

import fixture.AddressInputTransformer
import org.specs2.Specification


class NewInstanceTransformerFactorySpec extends Specification { def is =

  "Specification for the new instance transformer factory"           ^
                                                                     endp^
  "A new instance transformer factory should"                        ^
    "create a transformer instance when requested"                   ! createTransformer^
                                                                     end

  def createTransformer = {
    val factory = NewInstanceTransformerFactory
    factory.transformer(classOf[AddressInputTransformer]) must_!= null
  }
}