package templemore.liftjson.provider

import net.liftweb.json.JsonAST.JValue


trait JsonASTTransformer {

  def transform(json: JValue): JValue
}