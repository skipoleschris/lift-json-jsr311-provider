package templemore.liftjson.provider.spring.fixture

import templemore.liftjson.provider.JsonASTTransformer
import com.sun.codemodel.internal.JArray
import net.liftweb.json.JsonAST.{JString, JObject, JField, JValue}

class UserInputTransformer extends JsonASTTransformer {

  val Regex = """(.*)[\s]?<(.*)>""".r

  def transform(json: JValue) = {
    var userText: Option[String] = None
    json.children.find(_ match {
      case JField("user", x) => userText = Some(x.values.toString); true
      case _ => false
    }).getOrElse(throw new IllegalArgumentException())

    userText.getOrElse(throw new IllegalArgumentException()) match {
      case Regex(fullName, name) => new JObject(List(JField("username", new JString(name)),
                                                     JField("fullName", new JString(fullName))))
      case _ => throw new IllegalArgumentException()
    }
  }

}