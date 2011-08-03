package templemore.liftjson.provider.spring

import org.springframework.beans.factory.FactoryBean
import reflect.BeanProperty
import templemore.liftjson.provider.{TransformerFactory, NewInstanceTransformerFactory, ProviderConfig}

class ProviderConfigFactory extends FactoryBean[ProviderConfig] {

  @BeanProperty var transformerFactory: TransformerFactory = NewInstanceTransformerFactory

  def getObject = ProviderConfig(transformerFactory)

  def getObjectType = classOf[ProviderConfig]

  def isSingleton = true
}
