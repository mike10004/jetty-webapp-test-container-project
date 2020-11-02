package io.github.mike10004.jettywebapp.testing;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class ContextWebappServerConfig implements WebappServerConfig {

    private final WebappDeploymentContext context;

    public ContextWebappServerConfig(WebappDeploymentContext context) {
        this.context = context;
    }

    /**
     *
     * @param applicationPath string containing only alphanumeric and {@code -} or {@code _} characters, possibly surrounded by {@code /} characters
     * @return
     */
    static String applicationPathToSpec(String applicationPath) {
        String regex = "^/*([-\\w]+)/*$";
        Matcher m = Pattern.compile(regex).matcher(applicationPath);
        if (!m.find()) {
            throw new IllegalArgumentException("expect application path to match regex " + regex);
        }
        String slug = m.group(1);
        return String.format("/%s/*", slug);
    }

    @Override
    public String contextPath() {
        String contextPath = context.getContextPath();
        if (contextPath != null) {
            return contextPath;
        }
        return WebappServerConfig.super.contextPath();
    }

    @Override
    public JspServletConfig jspServletConfig() {
        JspServletConfig jspServletConfig = context.getJspServletConfig();
        if (jspServletConfig != null) {
            return jspServletConfig;
        }
        return WebappServerConfig.super.jspServletConfig();
    }

    @Override
    public ServletContextHandler newServletContextHandler() {
        Supplier<? extends ServletContextHandler> supplier = context.getServletContextHandlerCreator();
        if (supplier != null) {
            return supplier.get();
        }
        return WebappServerConfig.super.newServletContextHandler();
    }

    @Override
    public void addServlets(ServletContextHandler servletContextHandler) {
        ServletContainer servletContainer = new ServletContainer(context.getResourceConfig());
        ServletHolder jerseyServletHolder = new ServletHolder(servletContainer);
        servletContextHandler.addServlet(jerseyServletHolder, applicationPathToSpec(context.getApplicationPath()));
    }

    @Override
    public Stream<String> configurationClasses() {
        Collection<String> cfgClasses = context.getConfigurationClasses();
        if (cfgClasses != null) {
            return cfgClasses.stream();
        }
        return WebappServerConfig.super.configurationClasses();
    }

    @Override
    public URI webResourceRootUri() {
        URI uri = context.getWebResourceRootUri();
        if (uri != null) {
            return uri;
        }
        return WebappServerConfig.super.webResourceRootUri();
    }

    @Override
    public Map<String, String> defaultServletInitParams() {
        Map<String, String> m = context.getDefaultServletInitParams();
        if (m != null) {
            return m;
        }
        return WebappServerConfig.super.defaultServletInitParams();
    }
}
