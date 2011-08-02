package templemore.liftjson.provider.spring

import org.springframework.beans.factory.FactoryBean
import templemore.liftjson.provider.ProviderConfig

class ProviderConfigFactory extends FactoryBean[ProviderConfig] {

  def getObject = ProviderConfig()

  def getObjectType = classOf[ProviderConfig]

  def isSingleton = true
}