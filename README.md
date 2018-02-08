utterlyidle
===========

Another REST library for Java inspired by the good bits in JSR-311 

This project is the Java rewrite of [WebFabric](http://code.google.com/p/webfabric/)

Some of the core features
  * Configuration in code (no xml)
  * No static state (i.e. easily testable unlike Play)
  * Multiple [Containers web containers] support:
   * Servlets (Tomcat, Jetty tested)
   * Jetty in embedded mode
   * SimpleWeb
   * Embedded HttpServer from Java 6
   * Undertow (V2 only)
   * In-Memory
  * Very flexible / extensible
    * You can new up the Application and reach in and replace any dependency for a test
  * Super simple SSL setup support
  * Uniform client / server API (like Restlet)
  * Composition preferred over class inheritance (unlike Restlet)
  * Resources can be defined with Annotations, DSL, static files or role your own convention
    * By using the DSL you can even bind a HTTP method directly to a 3rd party Java class
  * Very fast startup times (around 1ms)
  * Request and Response can be 'new'd up, 'toString'd and parsed
  * Resources can consume and return Requests and Responses or any Java classes
    * Renderers can be registered for any response object

##Versions##

 * 1.x - Stable - Requires Java 7+
 * 2.x - Development - Requires Java 8+
  
 
