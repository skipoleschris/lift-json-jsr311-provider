package templemore.liftjson.provider.spring

import templemore.liftjson.provider.{JsonASTTransformer, TransformerFactory}
import org.springframework.beans.factory.{BeanFactory, BeanFactoryAware}

class SpringAwareTransformerFactory extends TransformerFactory with BeanFactoryAware {

  private var beanFactory: BeanFactory = _

  def setBeanFactory(beanFactory: BeanFactory): Unit = this.beanFactory = beanFactory

  def transformer[T <: JsonASTTransformer](transformerClass: Class[T]) = beanFactory.getBean(transformerClass)
}
