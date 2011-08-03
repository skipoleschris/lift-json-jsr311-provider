package templemore.liftjson.provider.fixture

import templemore.liftjson.provider.JsonASTTransformer
import net.liftweb.json.JsonAST._

class PersonOutputTransformer extends JsonASTTransformer {

  def transform(json: JValue) = {
    var firstName = ""
    var surname = ""
    val updated = json.children.filter (_ match {
        case JField("firstName", x) => firstName = x.values.toString; false
        case JField("surname", x) => surname = x.values.toString; false
        case _ => true
      }).asInstanceOf[List[JField]]

    new JObject(JField("fullName", new JString(firstName + " " + surname)) :: updated)
  }
}