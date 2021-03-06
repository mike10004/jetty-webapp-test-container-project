package io.github.mike10004.jettywebapp.testing.example;

import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

@ApplicationPath("app")
public class MyApplication extends ResourceConfig {

    public MyApplication(@Context ServletContext servletContext) {
        super(BasicResource.class, ForwardingResource.class);
        register(MyFilter.class);
        property("jersey.config.server.tracing.type", "ALL");
        property("jersey.config.server.tracing.threshold", "VERBOSE");
        property("jersey.config.server.wadl.disableWadl", true);
        servletContext.getServletRegistrations().forEach((name, registration) -> {
            System.out.format("%s <- %s%n", name, registration.getMappings());
        });
        System.out.format("contextPath=%s%n", servletContext.getContextPath());
        System.out.format("serverInfo=%s%n", servletContext.getServerInfo());
    }

}