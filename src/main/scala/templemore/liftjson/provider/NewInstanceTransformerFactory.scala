package templemore.liftjson.provider

private[provider] class NewInstanceTransformerFactory extends TransformerFactory {

  def transformer[T <: JsonASTTransformer](transformerClass: Class[T]) = transformerClass.newInstance()
}