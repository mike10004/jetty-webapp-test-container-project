package io.github.mike10004.jettywebapp.testing.example;

import org.junit.Test;

import javax.servlet.ServletContext;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class MyApplicationSubclassTest extends MyApplicationTestBase {

    @Test
    public void basic() {
        WebTarget target = target().path("app/basic");
        System.out.format("URI = %s%n", target.getUri());
        try (Response response = target.request().buildGet().invoke()) {
            assertEquals("status", 200, response.getStatus());
            assertEquals("text", "basic", response.readEntity(String.class));
        }
    }

    @Override
    protected Class<? extends MyApplication> getWebappClass() {
        return MyApplicationSubclass.class;
    }

    public static class MyApplicationSubclass extends MyApplication {

        public MyApplicationSubclass(@Context ServletContext servletContext) {
            super(servletContext);
        }
    }

}