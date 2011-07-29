# Lift-Json JSR-311 Provider #

This library implements a Provider adapter for JSR-311 (JAX-RS) to allow the lift-json library to be used for mapping between JSON documents and Scala case classes within a JAX-RS compliant server (e.g. Jersey). It supports automated mapping of incoming JSON messages to Scala case classes and returned case class instances back to JSON.

## Obtaining ##

The project is available from the Templemore maven repository at http://templemore.co.uk/repo.

### Maven dependency ###

In maven:

    <dependency>
        <groupId>templemore</groupId>
        <artifactId>lift-json-jsr311-provider_2.9.0-1</artifactId>
        <version>0.2</version>
    </dependency>

    <repository>
        <id>templemore</id>
        <url>http://templemore.co.uk/repo</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>

In SBT:

    resolvers += "Templemore Repository" at "http://templemore.co.uk/repo"

    libraryDependencies += "templemore" %% "lift-json-jsr311-provider" % "0.2"

## Configuration ##

Configuration for the provider is simple. All you need to do is register the Provider with the JSR-311 implementation. For Jersey, this can be done as follows:

Using a manual configuration:

    import templemore.liftjson.provider.LiftJsonProvider

    val resourceConfig = new DefaultResourceConfig(classOf[LiftJsonProvider])
    // Create the server passing the resource config

Or, if using Spring and Jersey, in the spring context config:

    <bean class="templemore.liftjson.provider.LiftJsonProvider" scope="singleton"/>

Once registered, the provider will handle all JSON mappings where the object to map to is a Scala case class. Any non-Scala case class objects will be left unhandled so that they can be processed by an alternative JSON provider implementation (if installed).

## Transformers ##

In some cases it might be required that the JSON representation of a domain object is slightly different from the case class representation used in the domain model.

For example, consider a User object. In the incoming JSON we might allow a "password" field that contains the unhashed password value to be set. In our domain model this is represented as pair of fields: salt and passwordHash. Additionally, when the user is returned as JSON we don't wish to include these salt and passwordHash fields in the JSON response.

Fortunately the lift-json framework has the ability to support these use cases. When transforming between JSON and case classes, the lift-json framework uses an intermediate Abstract Syntax Tree (AST) representation. We can intercept at this point and make changes to the AST prior to completing the generation of the JSON or case class instance.

### Incoming Transformers ###

On incoming parameters we can use an @Transformer annotation on the parameter in the method. For example:

    @PUT
    @Consumes(Array(MediaType.APPLICATION_JSON))
    def addUser(@Transformer(classOf[UserInputTransformer]) user: User): Unit = {
      ...
    }

In this case, the JSON will be parsed into an AST and a transformer of the specified class will be created and invoked with the AST structure. The transformer can modify the structure however it wants and return the result, which is then converted into the case class.

### Outgoing Transformers ###

To transform return values from methods we can annotate the method with the @Transformer annotation. For example:

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Transformer(classOf[UserOutputTransformer])
    def getUser(@PathParam id: String): User = {
      ...
    }

In this case, the returned User case class will be translated into an AST and a transformer of the specified class will be created and invoked with the AST structure. The transformer can modify the structure however it wants and return the result, which is then converted into the response JSON.

### Transformer Implementation ###

To implement a transformer just create a class that extends the JsonASTTransformer trait:

    class AddressInputTransformer extends JsonASTTransformer {

      def transform(json: JValue) = {
        // Transform and return the resulting json AST
      }
    }

