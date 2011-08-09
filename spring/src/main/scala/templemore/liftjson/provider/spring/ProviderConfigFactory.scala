package templemore.liftjson.provider.spring

import org.springframework.beans.factory.FactoryBean
import reflect.BeanProperty
import templemore.liftjson.provider._

class ProviderConfigFactory extends FactoryBean[ProviderConfig] {

  @BeanProperty var transformerFactory: TransformerFactory = NewInstanceTransformerFactory
  @BeanProperty var errorResponseGenerator: ErrorResponseGenerator = JsonDocumentErrorResponseGenerator

  def getObject = ProviderConfig(transformerFactory, errorResponseGenerator)

  def getObjectType = classOf[ProviderConfig]

  def isSingleton = true
}
