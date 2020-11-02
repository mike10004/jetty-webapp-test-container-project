package io.github.mike10004.jettywebapp.testing;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.test.DeploymentContext;

import javax.annotation.Nullable;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class WebappDeploymentContext extends DeploymentContext implements WebappServerConfigurator {

    @Nullable
    private final URI webResourceRootUri;
    @Nullable
    private final Map<String, String> defaultServletInitParams;
    @Nullable
    private final Collection<String> configurationClasses;
    @Nullable
    private final Supplier<? extends ServletContextHandler> servletContextHandlerCreator;
    private final JspServletConfig jspServletConfig;
    private final String applicationPath;

    /**
     * Create new application deployment context.
     *
     * @param b {@link Builder} instance.
     */
    protected WebappDeploymentContext(Builder b) {
        super(b);
        this.applicationPath = b.applicationPath;
        this.webResourceRootUri = b.webResourceRootUri;
        this.defaultServletInitParams = b.defaultServletInitParams;
        this.configurationClasses = b.configurationClasses;
        this.servletContextHandlerCreator = b.servletContextHandlerCreator;
        this.jspServletConfig = new BasicJspServletConfig(b.servletContextTempDir, b.jspPathSpecs, b.jspServletInitParams);
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    @Nullable
    public URI getWebResourceRootUri() {
        return webResourceRootUri;
    }

    @Nullable
    public Map<String, String> getDefaultServletInitParams() {
        return defaultServletInitParams;
    }

    @Nullable
    public Collection<String> getConfigurationClasses() {
        return configurationClasses;
    }

    @Nullable
    public Supplier<? extends ServletContextHandler> getServletContextHandlerCreator() {
        return servletContextHandlerCreator;
    }

    public JspServletConfig getJspServletConfig() {
        return jspServletConfig;
    }

    public static Builder builder(Class<? extends Application> applicationClass, String applicationPath) {
        return new Builder(applicationClass, applicationPath);
    }

    public static Builder builder(Application application, String applicationPath) {
        return new Builder(application, applicationPath);
    }

    public static Builder builder(Class<? extends Application> applicationClass) {
        return builder(applicationClass, determineApplicationPath(applicationClass));
    }

    public static Builder builder(Application application) {
        return builder(application, determineApplicationPath(application.getClass()));
    }

    private static String determineApplicationPath(Class<? extends Application> applicationClass) {
        ApplicationPath anno = applicationClass.getAnnotation(ApplicationPath.class);
        if (anno == null) {
            throw new IllegalArgumentException(applicationClass + " must be annotated with " + ApplicationPath.class + " to auto-detect the application path");
        }
        return anno.value();
    }

    /**
     * Rebuilds a generic deployment context as a {@link WebappDeploymentContext} instance,
     * or returns the argument instance if it is already of that type.
     * @param context context
     * @param applicationPath application path; may be null if context argument is already an instance of {@link WebappDeploymentContext}
     * @return instance of class {@link WebappDeploymentContext}
     */
    public static WebappDeploymentContext rebuild(DeploymentContext context, String applicationPath) {
        if (context instanceof WebappDeploymentContext) {
            return (WebappDeploymentContext) context;
        }
        return new Builder(context.getResourceConfig(), applicationPath).contextPath(context.getContextPath()).build();
    }

    @Override
    public WebappServerConfig getServerConfig() {
        return new ContextWebappServerConfig(this);
    }

    public static class Builder extends DeploymentContext.Builder {

        private URI webResourceRootUri;
        private Map<String, String> defaultServletInitParams;
        private Map<String, String> jspServletInitParams;
        private Collection<String> jspPathSpecs;
        private File servletContextTempDir;
        private Collection<String> configurationClasses;
        private Supplier<? extends ServletContextHandler> servletContextHandlerCreator;
        private final String applicationPath;

        protected Builder(Application app, String applicationPath) {
            super(app);
            this.applicationPath = validateApplicationPath(applicationPath);
        }

        private static String validateApplicationPath(String applicationPath) {
            requireNonNull(applicationPath);
            if (applicationPath.trim().isEmpty()) {
                throw new IllegalArgumentException("application path must be nonempty");
            }
            return applicationPath;
        }

        protected Builder(Class<? extends Application> appClass, String applicationPath) {
            super(appClass);
            this.applicationPath = validateApplicationPath(applicationPath);
        }

        public Builder webResourceRootUri(URI webResourceRootUri) {
            this.webResourceRootUri = webResourceRootUri;
            return this;
        }

        public Builder defaultServletInitParams(Map<String, String> defaultServletInitParams) {
            this.defaultServletInitParams = defaultServletInitParams;
            return this;
        }

        public Builder jspServletInitParams(Map<String, String> jspServletInitParams) {
            this.jspServletInitParams = jspServletInitParams;
            return this;
        }

        public Builder jspPathSpecs(Collection<String> jspPathSpecs) {
            this.jspPathSpecs = jspPathSpecs;
            return this;
        }

        public Builder servletContextTempDir(File servletContextTempDir) {
            this.servletContextTempDir = servletContextTempDir;
            return this;
        }

        public Builder configurationClasses(Collection<String> configurationClasses) {
            this.configurationClasses = configurationClasses;
            return this;
        }

        public Builder servletContextHandlerCreator(Supplier<? extends ServletContextHandler> servletContextHandlerCreator) {
            this.servletContextHandlerCreator = servletContextHandlerCreator;
            return this;
        }

        @Override
        public WebappDeploymentContext.Builder contextPath(String contextPath) {
            super.contextPath(contextPath);
            return this;
        }

        @Override
        public WebappDeploymentContext build() {
            return new WebappDeploymentContext(this);
        }
    }
}
