Jersey-Common is basically some glue code pulling together some great libraries in
very much the same mould as [Dropwizard](https://github.com/codahale/dropwizard). 
These libraries include:

* Jetty's HTTP server
* Jersey's JAX-RS implementation  
* Jackson's JSON processor
* Google's Guice
* slf4j's logging API
* Yammer's metrics

Main Classes/Packages of note
=============================

### HttpServer and HttpServerConfig

Encapsulates the configuration and lifecycle of a HTTP server which is
initialized with two servlet contexts:

The metrics context serves MetricsServlet straight from Yammer's excellent
metrics-servlet project. Should you choose to use the various metrics-XXX 
libraries to instrument your application any meters, gauges, timers etc that 
you gather will be made available in JSON form from this context. If you
don't collect any such metrics: a) you should be and b) you can still use 
the metrics context to get a thread dump from your app over HTTP. 
See https://github.com/codahale/metrics for the full glory of metrics

The root context is bound to a GuiceServletContextListener. Used in conjunction 
with JerseyServletModule (see below), you can specify the packages containing
your Jersey resources, which will then be served up under the root context.
See the examples below for how to plug all this together.

It's important to note that the HttpServer is hard-coded to use a [SPDY](http://www.chromium.org/spdy/spdy-whitepaper) 
connector since it'll degrade to HTTP if the client does not support SPDY. This, however, means that
Jersey-Common requires Java 7.

### JsonConfigReader

Reads a JSON file and maps it to an arbitrary class. When used in conjunction with `HttpServerConfig`
this class allows you to read the settings for your HttpServer (in particular the port and SSL settings)
in a JSON file.

<!--- This could be extended to watch for changes to the config file and restart the server. 
We could also allow an admin interface to change these values and then persist them to disk --->

### ObjectMapperProvider

Does a tiny amount of opinionated configuration to ensure that JSON output 
generated via Jackson's JAXB and POJO conversions is pretty printed. This can
be turned off by disabling the default filters in JerseyServletModule.

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
   
* ServerAgentHeaderFilter
 
Sets the Server header on HTTP responses to a value provided by an injected 
ServerInfo implementation. How you generate this is obviously app specific (we tend
to use a ServerInfo impl which reads from a properties file, created at build time).
GenericServerInfoModule binds ServerInfo to an anonymous instance which reads the 
desired String from a system property ( com.talis.jersey.guice.serverid )
 
### Guice

The main article of interest here is JerseyServletModule. This takes a varargs of
the package names which contain your Jersey resources and configures Jersey to 
use Guice for dependency injection. It's also a bit opinionated, and configures
the application with a standard set of filters and providers. The filters it adds
are the three custom filters mentioned above, plus Jersey's GZIPEncodingFilter which
provides conditional (de|en)coding of request and response entities based on the 
Content-Encoding & Accept-Encoding headers in the request. The pretty-printing
Jackson ObjectMapperProvider & exception formatting DefaultExceptionMapper (see above)
are also registered with the application by default. 
You can disable all of this default configuration by setting the system property
com.talis.jersey.guice.disable-default-filters to "true".
     
Also in this package are a couple of modules which bind the authentication & server
identifier implementations to defaults. So that if you don't care about these too
much, include these modules.
    
Examples
========

### Starting an HTTP Server

```java
public static void main(String[] args) throws Exception {
  System.setProperty("com.talis.jersey.guice.serverid", "AnExample");
  Module[] modules =
  	{
                             new ApplicationModule(),                     // contains app specific bindings
                             new NoopAuthenticationModule(),              // we don't need no stinking authentication
                             new GenericServerInfoModule(),               // to set the Server response header 
                             new JerseyServletModule("com.example.foo",   // packages containing  
                                                     "com.example.bar")); // your JAX-RS resources
	};
  HttpServer webserver = new HttpServer(new HttpServerConfig(8080));
  webserver.start(modules);    
}
```

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

