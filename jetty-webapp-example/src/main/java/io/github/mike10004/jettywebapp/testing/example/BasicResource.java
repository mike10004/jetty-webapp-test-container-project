package io.github.mike10004.jettywebapp.testing.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("basic")
public class BasicResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String basic() {
        return "basic";
    }

}
