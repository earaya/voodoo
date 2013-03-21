// TODO: This needs to be updated to show changes.

Voodoo is basically some glue code pulling together some great libraries in
very much the same mould as [Dropwizard](https://github.com/codahale/dropwizard).
In fact, some of the code in here came straight from Dropwizard.

The libraries included here are:

* Jetty's HTTP server
* Jersey's JAX-RS implementation  
* Jackson's JSON processor
* Google's Guice
* slf4j's logging API
* Yammer's metrics

Main Classes/Packages of note
=============================

### VoodooApplication and Component

Encapsulates the configuration and lifecycle of an HTTP server which is
initialized with `Components`.

Two components provided are a `RestComponent` for Jersey API endpoints, and a `StaticComponent` for serving static assets.

Please see the samples on how to use these components. You can also implement your own components. Please not that in case of path
conflicts, the first component registered wins.

The metrics context serves MetricsServlet straight from Yammer's excellent
metrics-servlet project. Should you choose to use the various metrics-XXX 
libraries to instrument your application any meters, gauges, timers etc that 
you gather will be made available in JSON form from this context, you'll be able to access them by simply annotating your endpoints.
Please look at [yammer-metrics](https://github.com/codahale/metrics) for more details.

It's important to note that the HttpServer is hard-coded to use a [SPDY](http://www.chromium.org/spdy/spdy-whitepaper)
connector since it'll degrade to HTTP if the client does not support SPDY. This, however, means that
Voodoo requires Java 7.

### JsonConfigReader

Reads a JSON file and maps it to an arbitrary class. When used in conjunction with `HttpServerConfig`
this class allows you to read the settings for your HttpServer (in particular the port and SSL settings)
in a JSON file.

<!--- This could be extended to watch for changes to the config file and restart the server. 
We could also allow an admin interface to change these values and then persist them to disk --->

### JacksonMessageBodyProvider

The most important thing to know here is that this provider will validate representations annotated with `@Valid` and
will return an error to the client if the representation fails validation.

### Exceptions

Nothing too exciting or earth-shattering here, just some typed exceptions which
represent particular HTTP responses. All the provided exceptions extend HttpException, which itself
is a RuntimeException. When used in conjunction with DefaultExceptionMapper 
(again, this is on by default, but can be disabled via JerseyServletModule), 
the @GET/@POST/etc methods in your Jersey resources can throw these exceptions,
and a HTTP response will be generated with the appropriate status code, a 
text/plain media type and the message from the exception in the body.     
 
### Filters

Some request/response filters we found we were re-using in several places:

* LoggingFilter 

Uses the MDC (mapped diagnostic context) provided by slf4j to attach
a couple of tokens to each incoming request. First, the system time is recorded on 
the way in, so that when the response is emitted we can log the time to service the
request. Second, a (fairly) unique identifier is generated for the request. As this 
is put into the MDC, its available to the underlying logging framework and so can be 
included in every log statement via logging config (see the example below).
We use this extensively with log4j as the underlying logging provider, YMMV with
other providers. A header containing this identifier is also added to the HTTP 
response, which makes debugging much easier.

Examples
========

### Starting an application

```java
public class SampleApplication {
    public static void main(String[] args) throws Exception {
        RestComponent restComponent = new RestComponent("com.earaya.voodoo.sample").root("/api"); // Serves any resources in the package at /api
        restComponent.provider(new BasicAuthProvider<SampleUser>(new SampleAuthenticator(), "realm")); // Add a Jersey provider to the component; in this case it does auth.
        StaticComponent staticComponent = new StaticComponent(".").root("/static"); // Serves "this" folder under /static
        VoodooApplication vuduServer = new VoodooApplication(new HttpServerConfig(8080), staticComponent, restComponent);
        vuduServer.start();
    }
}
```

Please look at the sample package for more detailed examples.

### Reading a JSON config file

```java
public void readConfigSample() {
	ConfigFactory sampleConfigFactory = ConfigFactory.forClass(SampleConfig.class);
	SampleConfig config = sampleConfigFactory.buildConfig(new File("path/to/file"));
	
	int someValue = config.getSomeValue();
}
```

### Log4j Appender config that includes request id token

```xml
<appender name="stdout" class="org.apache.log4j.ConsoleAppender">
  <layout class="org.apache.log4j.PatternLayout"> 
    <param name="ConversionPattern" value="%d{ISO8601} -%5p %-25c{1} :%t: %X{R_UID}|%m%n"/>
  </layout>
</appender>
```

The ```%X{R_UID}``` is replaced with the value of the request id pulled from the MDC at runtime

