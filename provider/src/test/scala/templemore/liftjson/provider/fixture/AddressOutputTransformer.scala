package templemore.liftjson.provider.fixture

import templemore.liftjson.provider.JsonASTTransformer
import net.liftweb.json.JsonAST.{JObject, JField, JValue}

class AddressOutputTransformer extends JsonASTTransformer {

  def transform(json: JValue) = {
    var linesArray: Option[JValue] = None
    val lines = json.children.filter(_ match {
      case JField("lines", value) => linesArray = Some(value); true
      case _ => false
    })

    val updated = json.children.filterNot(lines.contains(_)).asInstanceOf[List[JField]]

    val newLines = (linesArray.get.children.foldLeft((1, List[JField]())) { (acc, value) =>
      val index = acc._1
      (index + 1, JField("line" + index, value) :: acc._2)
    })._2.reverse

    new JObject(newLines ++ updated)
  }
}