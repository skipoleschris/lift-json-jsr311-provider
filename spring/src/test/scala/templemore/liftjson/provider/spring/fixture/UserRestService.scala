package templemore.liftjson.provider.spring.fixture

import javax.ws.rs.core.MediaType
import javax.ws.rs.{GET, Produces, Path}

@Path("user")
class UserRestService {

  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getUsers: Array[UserInfo] = {
    Array(UserInfo("root", "Administrator"),
          UserInfo("chris", "Chris Turner"),
          UserInfo("foo", "Foo Bar"))
  }
}

case class UserInfo(username: String, fullName: String)
