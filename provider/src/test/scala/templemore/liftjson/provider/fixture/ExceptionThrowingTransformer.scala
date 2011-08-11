package templemore.liftjson.provider.fixture

import templemore.liftjson.provider.JsonASTTransformer
import net.liftweb.json.JsonAST.JValue
import java.lang.IllegalStateException

class ExceptionThrowingTransformer extends JsonASTTransformer {
  def transform(json: JValue) = throw new IllegalStateException("Expected error message")
}