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
    public void exampleResourceBasic() {
        Set<Integer> statusCodes = new HashSet<>();
        for (String path : new String[]{"app/basic"}) {
//        for (String path : new String[]{"app/basic", "app", "basic"}) {
            WebTarget target = target().path(path);
            System.out.format("URI = %s%n", target.getUri());
            try (Response response = target.request().buildGet().invoke()) {
                statusCodes.add(response.getStatus());
            }
        }
        assertTrue("has 200/OK: " + statusCodes, statusCodes.contains(200));
//        assertEquals("http status", 200, response.getStatus());
//        assertEquals("content", "basic", response.readEntity(String.class));
    }
}