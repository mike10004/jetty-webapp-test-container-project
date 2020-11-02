package io.github.mike10004.jettywebapp.testing.example;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

// Executed whenever a request is sent to the API server.
// Useful for debugging errors that don't reach the desired endpoint implementation.
@Provider
@Priority(value = 0)
public class MyFilter implements ContainerRequestFilter {

    @Context // request scoped proxy
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            System.out.format("request context uri absolute path: %s%n", requestContext.getUriInfo().getAbsolutePath());
        } catch (Exception e) {
            // ignore
        }
    }
}
