package io.github.mike10004.jettywebapp.testing;

import org.glassfish.jersey.test.DeploymentContext;

import javax.ws.rs.core.Application;

public class WebappDeploymentContext extends DeploymentContext {
    /**
     * Create new application deployment context.
     *
     * @param b {@link Builder} instance.
     */
    protected WebappDeploymentContext(Builder b) {
        super(b);
    }

    public static Builder builder(Class<? extends Application> applicationClass) {
        return new Builder(applicationClass);
    }

    public static Builder builder(Application application) {
        return new Builder(application);
    }

    public static WebappDeploymentContext wrap(DeploymentContext context) {
        if (context instanceof WebappDeploymentContext) {
            return (WebappDeploymentContext) context;
        }
        return new Builder(context.getResourceConfig()).contextPath(context.getContextPath()).build();
    }

    public WebAppServerCreator.ServerOptionSet getServerOptionSet() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public static class Builder extends DeploymentContext.Builder {

        protected Builder(Application app) {
            super(app);
        }

        protected Builder(Class<? extends Application> appClass) {
            super(appClass);
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
