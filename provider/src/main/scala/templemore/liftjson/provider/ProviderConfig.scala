package templemore.liftjson.provider

case class ProviderConfig(transformerFactory: TransformerFactory = NewInstanceTransformerFactory,
                          errorResponseGenerator: ErrorResponseGenerator = JsonDocumentErrorResponseGenerator)
