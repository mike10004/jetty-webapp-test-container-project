package io.github.mike10004.jettywebapp.testing;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ContextWebappServerConfigTest {

    @Test
    public void applicationPathToSpec() {

        String[][] testCases = {
                {"foo", "/foo/*"},
                {"/foo", "/foo/*"},
                {"/foo/", "/foo/*"},
                {"foo/", "/foo/*"},
                {"foo//", "/foo/*"},
        };
        for (String[] testCase : testCases) {
            String input = testCase[0];
            String expected = testCase[1];
            String actual = ContextWebappServerConfig.applicationPathToSpec(input);
            assertEquals(Arrays.toString(testCase), expected, actual);
        }
    }
}