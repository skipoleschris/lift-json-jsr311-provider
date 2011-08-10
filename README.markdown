# Lift-Json JSR-311 Provider #

This library implements a Provider adapter for JSR-311 (JAX-RS) to allow the lift-json library to be used for mapping between JSON documents and Scala case classes within a JAX-RS compliant server (e.g. Jersey). It supports automated mapping of incoming JSON messages to Scala case classes and returned case class instances back to JSON.

## Obtaining ##

The project is available from the Templemore maven repository at http://templemore.co.uk/repo.

### Maven dependency ###

In maven:

    <dependency>
        <groupId>templemore.json</groupId>
        <artifactId>lift-json-jsr311-provider_2.9.0-1</artifactId>
        <version>0.3</version>
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

    libraryDependencies += "templemore.json" %% "lift-json-jsr311-provider" % "0.3"

## Configuration ##

Configuration for the provider is simple. All you need to do is register the Provider with the JSR-311 implementation. For Jersey, this can be done as follows:

Using a manual configuration:

    import templemore.liftjson.provider.LiftJsonProvider

    val resourceConfig = new DefaultResourceConfig(classOf[LiftJsonProvider])
    // Create the server passing the resource config

Once registered, the provider will handle all JSON mappings where the object to map to is a Scala case class. Any non-Scala case class objects will be left unhandled so that they can be processed by an alternative JSON provider implementation (if installed).

If you want finer grained control you can create a custom ProviderConfig instance, overriding any of the default values and pass this to the provider instead:

    val resourceConfig = new DefaultResourceConfig()
    val config = ProviderConfig(...)  // Set the config options you want to override
    resourceConfig.getSingletons.add(new LiftJsonProvider(config))
    val serverHandle = SimpleServerFactory.create(LocalServer, resourceConfig)

## Transformers ##

In some cases it might be required that the JSON representation of a domain object is slightly different from the case class representation used in the domain model.

For example, consider a User object. In the incoming JSON we might allow a "password" field that contains the un-hashed password value to be set. In our domain model this is represented as pair of fields: salt and passwordHash. Additionally, when the user is returned as JSON we don't wish to include these salt and passwordHash fields in the JSON response.

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

### Transformer Factory ###

By default, instances of transformers are created from the class each time they are needed. For some applications this may not be appropriate. For example, you may need some injected dependencies in the transformer in order to lookup data values. It is therefore possible to create your own TransformerFactory instance and override the default in the provider config.

    class MyCustomTransformerFactory extends TransformerFactory {
      def transformer[T <: JsonASTTransformer](transformerClass: Class[T]) = ...
    }

    val config = ProviderConfig(transformerFactory = new MyCustomTransformerFactory())
    // Create the provider with the custom config

## Error Responses ##

When an error occurs during the incoming or outgoing transformation, between Json and case classes, then an error is generated. This error is returned as an HTTP status code and a document body defining the error.

To facilitate a fully Json based approach to application APIs, the default error generator returns a Json representation of the error. This follows the format shown below:

    {
        "httpStatusCode" : "400",
        "httpReasonPhrase" : "Bad Request",
        "cause" : "MappingError",
        "message" : "Unable to process supplied Json body. No usable value for firstName. Did not find value which can be converted into java.lang.String"
    }

The Json error document contains the following fields:

* httpStatusCode - HTTP status code (which is also sent as the HTTP response code)
* httpReasonPhrase - HTTP reason phrase
* cause - Cause string, which will either be "MappingError" or the name of an exception class
* message - The associated exception message or details of the failed mapping

Currently the provider will generate 'Bad Request' (status 400) responses if it cannot map incoming Json content to case classes. Failure to map returned case classes back to Json is seen as an application error and an 'Internal Application Error' (status 500) response will be sent. Any other exceptions trapped during the marshaling or unmarshaling will result in an 'Internal Application Error' (status 500) response containing details of the exception that was raised.


### Customised Error Response Generator ###

There may be cases where the above Json error responses are not suitable for your application. In these cases you can create an alternative error response generator and use it to replace the build in version.

First you need to code a class (or object) that extends the templemore.liftjson.provider.ErrorResponseGenerator interface:

    import templemore.liftjson.provider.ErrorResponse
    import templemore.liftjson.provider.ErrorResponseGenerator

    class MyErrorResponseGenerator extends ErrorResponseGenerator {
      def generate(cause: Throwable): ErrorResponse = ...
      def generate(error: MappingError): ErrorResponse = ...
    }

The two generate methods each take information about what caused the error and must return an instance of the ErrorResponse class. The error response contains three fields:

* The HTTP status code to return
* The content type of the associated entity
* The entity body that describes the error

(Note that the default Json error response generator utilises the templemore.liftjson.provider.jsr311.Jsr311StatusAdapter object to simplify access to HTTP status codes and reason phrases. This object can be utilised by other error generator implementations if required.)

Finally, the provider needs to be configured to use the alternative error response generator:

    val config = ProviderConfig(errorResponseGenerator = new MyErrorResponseGenerator())
    // Create the provider with the custom config

## Spring Integration ##

So, you're using Spring Framework? Really? Oh, well, if you must! There is an optional library that provides some pretty decent integration with the Spring Framework. It allows you to register the provider using spring and to configure the provider and transformers via injected dependencies.

### Additional dependency ###

In maven:

    <dependency>
        <groupId>templemore.json</groupId>
        <artifactId>lift-json-jsr311-spring_2.9.0-1</artifactId>
        <version>0.3</version>
    </dependency>

In SBT:

    libraryDependencies += "templemore.json" %% "lift-json-jsr311-spring" % "0.3"

### Basic Configuration ###

The most simple way to integrate the provider with Spring is to just declare the provider as a singleton bean in the spring application context:

    <bean class="templemore.liftjson.provider.LiftJsonProvider" scope="singleton"/>

In this model the provider will be created using the default configuration and will utilise its own internal factories.

### Extended Configuration ###

The configuration can be extended to allow Spring to provide the provider config, which allows for custom factories to be used that use the Spring Bean Factory as the source for things such as transformers.

An example of this extended configuration is:

    <bean class="templemore.liftjson.provider.LiftJsonProvider" scope="singleton">
        <constructor-arg ref="providerConfig"/>
    </bean>

    <bean id="providerConfig" class="templemore.liftjson.provider.spring.ProviderConfigFactory">
        <property name="transformerFactory" ref="transformerFactory"/>
        <property name="errorResponseGenerator" ref="errorResponseGenerator"/>
    </bean>

    <bean id="transformerFactory" class="templemore.liftjson.provider.spring.SpringAwareTransformerFactory"/>

    <!-- Resources -->
    <bean id="userRestService" class="templemore.liftjson.provider.spring.fixture.UserRestService"/>

    <bean id="userInputTransformer" class="templemore.liftjson.provider.spring.fixture.UserInputTransformer"/>

    <bean id="errorResponseGenerator" class="templemore.liftjson.provider.spring.fixture.PlainTextErrorResponseGenerator"/>

In the above configuration, the provider is created with a custom provider config factory. This factory utilises a special Spring aware transformer factory that obtains transformers from the Spring Bean Factory rather than creating them directly.

Additionally, it defines a custom implementation of the error response generator that is created and configured via spring.

## Using Outside JSR-311 ##

This library is primarily about integrating Lift-Json with JSR-311 compatible servers. However, all of the power of the framework is contained in Scala code that has no direct dependencies on JSR-311 APIs. It is therefore fully possible to utilise all of the library features by making your application component(s) extends the LiftJsonIntegration trait.

Additionally it is possible to utilise the ErrorResponseGenerator and Jsr311ResponseAdapter classes within other parts of your Jsr-311 compatible application. More work will be done in this area on a future release.

## Roadmap ##

Please see the git issues list for details of all planed features for this library. The issues list can be found at: http://github.com/skipoleschris/lift-json-jsr311-provider/issues

## Release History ##

### Release 0.3 ###

* Implementation of error handling and responses for mapping errors and any exceptions thrown within the provider
* Implementation of pluggable error response generator
* General library and version upgrades
* Cleaner separation of generic code from code that depends on the Jsr-311 api

### Release 0.2 ###

* Implementation of custom transformers for incoming and outgoing Json
* Implementation of configuration with pluggable transformer factory
* Spring integration support

### Release 0.1 ###

* Initial release providing the implementation of the core provider support.
