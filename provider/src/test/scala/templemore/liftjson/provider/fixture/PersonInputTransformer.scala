package templemore.liftjson.provider.fixture

import templemore.liftjson.provider.JsonASTTransformer
import scala.None
import net.liftweb.json.JsonAST._

class PersonInputTransformer extends JsonASTTransformer{

  def transform(json: JValue) = {
    def splitNames(fullName: String) = {
      val parts = fullName.split(" ")
      (parts.head, parts.tail.mkString(" "))
    }

    var fullName: Option[String] = None
    val updated = json.children.filter (_ match {
      case JField("fullName", x) => fullName = Some(x.values.toString); false
      case _ => true
    }).asInstanceOf[List[JField]]

    val names = fullName.map(splitNames).getOrElse(throw new IllegalArgumentException("fullName not found"))
    new JObject(JField("firstName", new JString(names._1)) ::
               JField("surname", new JString(names._2)) ::
               updated)
  }
}