package templemore.liftjson.provider.spring

import fixture.UserInputTransformer
import org.specs2.Specification
import org.springframework.beans.factory.support.{RootBeanDefinition, DefaultListableBeanFactory}

class SpringAwareTransformerFactorySpec extends Specification { def is =

  "Specificatin for the spring aware transformer factory"            ^
                                                                     endp^
  "A spring aware transformer factory should"                        ^
    "get transformer from the bean factory"                          ! transformerFromBeanFactory^
    "fail to get transformer that is not registered"                 ! unknownTransformer^
                                                                     end

  def transformerFromBeanFactory = {
    val factory = new SpringAwareTransformerFactory()
    val beanFactory = new DefaultListableBeanFactory()
    beanFactory.registerBeanDefinition("userInputTransformer", new RootBeanDefinition(classOf[UserInputTransformer]))

    factory.setBeanFactory(beanFactory)
    factory.transformer(classOf[UserInputTransformer]) must_!= null
  }

  def unknownTransformer = {
    val factory = new SpringAwareTransformerFactory()
    val beanFactory = new DefaultListableBeanFactory()

    factory.setBeanFactory(beanFactory)
    factory.transformer(classOf[UserInputTransformer]) must throwAn[Exception]
  }
}