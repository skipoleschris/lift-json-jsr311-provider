package templemore.liftjson.provider.fixture

import templemore.liftjson.provider.JsonASTTransformer
import net.liftweb.json.JsonAST.JField._
import net.liftweb.json.JsonAST.{JString, JArray, JField, JValue}

class AddressInputTransformer extends JsonASTTransformer{

  def transform(json: JValue) = {
    val lines = json.children.filter(_ match {
      case JField(name, value) if (name.matches("line[0-9]+")) => true
      case _ => false
    })

    val updated = json.children.filterNot(lines.contains(_))
    val linesArray = new JArray(lines.map(line => line.children.head))
    new JArray(new JField("lines", linesArray) :: updated)
  }
}