package templemore.liftjson.provider.spring.fixture

import templemore.liftjson.provider.JsonASTTransformer
import net.liftweb.json.JsonAST.{JString, JObject, JField, JValue}
import net.liftweb.json.MappingException

class UserInputTransformer extends JsonASTTransformer {

  val Regex = """(.*)[\s]?<(.*)>""".r

  def transform(json: JValue) = {
    val userField = json.children.find(_ match {
      case JField("user", _) => true
      case _ => false
    }).getOrElse(throw new MappingException("Unable to find user field in json document.",
                                            new IllegalArgumentException()))

    userField.asInstanceOf[JField].value.values match {
      case Regex(fullName, name) => new JObject(List(JField("username", new JString(name)),
                                                     JField("fullName", new JString(fullName))))
      case _ => throw new MappingException("User field did not match pattern 'fullName <username>'.",
                                           new IllegalArgumentException())
    }
  }
}