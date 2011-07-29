package templemore.liftjson.provider

trait TransformerFactory {

  def transformer[T <: JsonASTTransformer](transformerClass: Class[T]): T
}