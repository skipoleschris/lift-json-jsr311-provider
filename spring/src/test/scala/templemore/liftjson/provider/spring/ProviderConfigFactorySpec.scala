package templemore.liftjson.provider.spring

import org.specs2.Specification
import templemore.liftjson.provider._

class ProviderConfigFactorySpec extends Specification { def is =

  "Specification for the spring provider config factory"             ^
                                                                     endp^
  "A provider config factory should"                                 ^
    "produce instances of a ProviderConfig"                          ! providerConfigType^
    "produce singleton instances"                                    ! singleton^
    "produce a default ProviderConfig instance"                      ! defaultInstance^
    "allow a custom transformer factory to be applied"               ! customTransformerFactory^
    "allow a custom error response generator to be applied"          ! customErrorResponseGenerator^
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

  def customTransformerFactory = {
    val factory = new ProviderConfigFactory()
    factory.setTransformerFactory(TestTransformerFactory)
    factory.getObject.transformerFactory must_==  TestTransformerFactory
  }

  def customErrorResponseGenerator = {
    val factory = new ProviderConfigFactory()
    factory.setErrorResponseGenerator(TestErrorResponseGenerator)
    factory.getObject.errorResponseGenerator must_==  TestErrorResponseGenerator
  }

  object TestTransformerFactory extends TransformerFactory {
    def transformer[T <: JsonASTTransformer](transformerClass: Class[T]) = transformerClass.newInstance()
  }

  object TestErrorResponseGenerator extends ErrorResponseGenerator {
    private val JsonContentType = "application/json"

    def generate(cause: Throwable) = ErrorResponse(500, JsonContentType, cause.getMessage)

    def generate(error: MappingError) = ErrorResponse(400, JsonContentType, error.message)
  }
}