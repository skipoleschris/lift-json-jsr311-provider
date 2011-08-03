package templemore.liftjson.provider

private[provider] object NewInstanceTransformerFactory extends TransformerFactory {

  def transformer[T <: JsonASTTransformer](transformerClass: Class[T]) = transformerClass.newInstance()
}
