= Lift-Json JSR-311 Provider =

This library implements a Provider adapter for JSR-311 (JAX-RS) to allow the lift-json library to be used for mapping between JSON documents and Scala case classes within a JAX-RS compliant server (e.g. Jersey). It supports automated mapping of incoming JSON messages to Scala case classes and returned case class instances back to JSON.

== Obtaining ==

The project is available from the Templemore maven repository at http://templemore.co.uk/repo.

=== Maven dependency ===

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

== Configuration ==

Configuration for the provider is simple. All you need to do is register the Provider with the JSR-311 implementation. For Jersey, this can be done as follows:

Using a manual configuration:

    import templemore.liftjson.provider.LiftJsonProvider

    val resourceConfig = new DefaultResourceConfig(classOf[LiftJsonProvider])
    // Create the server passing the resource config

Or, if using Spring and Jersey, in the spring context config:

    <bean class="templemore.liftjson.provider.LiftJsonProvider" scope="singleton"/>

Once registered, the provider will handle all JSON mappings where the object to map to is a Scala case class. Any non-Scala case class objects will be left unhandled so that they can be processed by an alternative JSON provider implementation (if installed).

