package io.github.mike10004.jettywebapp.testing;

import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public interface WebappServerConfig {

    default String contextPath() {
        return "/";
    }

    default JspServletConfig jspServletConfig() {
        return new JspServletConfig() {
        };
    }

    default ServletContextHandler newServletContextHandler() {
        return new ServletContextHandler(null, null, null, null, null, new ErrorPageErrorHandler(), ServletContextHandler.SESSIONS);
    }

    default void addServlets(ServletContextHandler context) {

    }

    default Stream<String> configurationClasses() {
        return Stream.of("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.webapp.WebXmlConfiguration");
    }

    /**
     * Gets a URI that specifies a directory that is the resource root.
     *
     * @return uri
     */
    default URI webResourceRootUri() {
        URL url = getClass().getResource("/WEB-INF/");
        if (url == null) {
            throw new IllegalStateException("could not detect web resource root; looked for /WEB-INF but not found");
        }
        if (!"file".equals(url.getProtocol())) {
            throw new IllegalStateException("expect resource root to be a file: URL, but got " + url);
        }
        try {
            File webInfDir = new File(url.toURI());
            File resourceRootDir = webInfDir.getParentFile(); // not null because in worst case / is parent
            return resourceRootDir.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    default Map<String, String> defaultServletInitParams() {
        return Collections.emptyMap();
    }

}
