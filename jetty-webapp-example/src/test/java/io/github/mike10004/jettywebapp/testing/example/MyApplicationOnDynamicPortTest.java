package io.github.mike10004.jettywebapp.testing.example;

import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class MyApplicationOnDynamicPortTest extends MyApplicationTestBase {

    public MyApplicationOnDynamicPortTest() {
        set(TestProperties.CONTAINER_PORT, "0");
    }

    @Test
    public void basic() {
        WebTarget target = target().path("app/basic");
        System.out.format("URI = %s%n", target.getUri());
        try (Response response = target.request().buildGet().invoke()) {
            assertEquals("status", 200, response.getStatus());
            assertEquals("text", "basic", response.readEntity(String.class));
        }
    }

}