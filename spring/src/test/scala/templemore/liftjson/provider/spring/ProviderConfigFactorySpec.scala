package templemore.liftjson.provider.spring

import org.specs2.Specification
import templemore.liftjson.provider.ProviderConfig


class ProviderConfigFactorySpec extends Specification { def is =

  "Specification for the spring provider config factory"             ^
                                                                     endp^
  "A provider config factory should"                                 ^
    "produce instances of a ProviderConfig"                          ! providerConfigType^
    "produce singleton instances"                                    ! singleton^
    "produce a default ProviderConfig instance"                      ! defaultInstance^
                                                                     end

  def providerConfigType = {
    val factory = new ProviderConfigFactory()
    factory.getObjectType must_==  classOf[ProviderConfig]
  }

  def singleton = {
    val factory = new ProviderConfigFactory()
    factory.isSingleton must_== true
  }

  def defaultInstance = {
    val factory = new ProviderConfigFactory()
    factory.getObject must_== ProviderConfig()
  }

}