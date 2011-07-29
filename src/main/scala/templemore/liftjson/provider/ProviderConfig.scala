package templemore.liftjson.provider

case class ProviderConfig(transformerFactory: TransformerFactory = new NewInstanceTransformerFactory())
