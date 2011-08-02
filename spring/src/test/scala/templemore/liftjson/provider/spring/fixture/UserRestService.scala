package templemore.liftjson.provider.spring.fixture

import javax.ws.rs.core.MediaType
import javax.ws.rs._
import templemore.liftjson.provider.Transformer

@Path("user")
class UserRestService {

  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getUsers: Array[UserInfo] = {
    Array(UserInfo("root", "Administrator"),
          UserInfo("chris", "Chris Turner"),
          UserInfo("foo", "Foo Bar"))
  }

  @PUT
  @Consumes(Array(MediaType.APPLICATION_JSON))
  def addUser(@Transformer(classOf[UserInputTransformer]) user: UserInfo): Unit = {

  }
}

case class UserInfo(username: String, fullName: String)
