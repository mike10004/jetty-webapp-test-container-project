package io.github.mike10004.jettywebapp.testing.example;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class MyApplicationTest extends MyApplicationTestBase {

    @Test
    public void basic() {
        WebTarget target = target().path("app/basic");
        System.out.format("URI = %s%n", target.getUri());
        try (Response response = target.request().buildGet().invoke()) {
            assertEquals("status", 200, response.getStatus());
            assertEquals("text", "basic", response.readEntity(String.class));
        }
    }

    @Test
    public void forward() {
        WebTarget target = target().path("app/forward");
        System.out.format("URI = %s%n", target.getUri());
        try (Response response = target.request().buildGet().invoke()) {
            assertEquals("status", 200, response.getStatus());
            assertTrue("text contains right stuff", response.readEntity(String.class).contains("Forwarding Destination"));
        }
    }
}