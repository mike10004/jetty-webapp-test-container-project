# Jetty Webapp Test Container

The goal of this project is to enable unit tests of webapps that run on Jetty 
to exercise a wide array of webapp behaviors. Namely, we want our test container
to support JSPs.  

This code draws on https://github.com/jetty-project/embedded-jetty-jsp and the 
Jersey project's [JettyTestContainerFactory.java](https://github.com/eclipse-ee4j/jersey/blob/2.32/test-framework/providers/jetty/src/main/java/org/glassfish/jersey/test/jetty/JettyTestContainerFactory.java).
  