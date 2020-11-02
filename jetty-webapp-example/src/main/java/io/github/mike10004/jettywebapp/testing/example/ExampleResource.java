package io.github.mike10004.jettywebapp.testing.example;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("foo")
public class ExampleResource {

    @Path("basic")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String basic() {
        return "basic";
    }

//    @Path("forward")
//    @GET
//    @SuppressWarnings("VoidMethodAnnotatedWithGET")
//    public void forward(@Context HttpServletRequest request, @Context HttpServletResponse response) throws ServletException, IOException {
//        request.getRequestDispatcher("/WEB-INF/views/view1.jsp").forward(request, response);
//    }

}
