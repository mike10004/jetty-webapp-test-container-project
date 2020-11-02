package io.github.mike10004.jettywebapp.testing;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

class BasicJspServletConfig implements JspServletConfig {

    @Nullable
    private final File servletContextTempDir;
    @Nullable
    private final Collection<String> pathSpecs;
    @Nullable
    private final Map<String, String> initParams;

    public BasicJspServletConfig(@Nullable File servletContextTempDir,
                                 @Nullable Collection<String> pathSpecs,
                                 @Nullable Map<String, String> initParams) {
        this.servletContextTempDir = servletContextTempDir;
        this.pathSpecs = pathSpecs;
        this.initParams = initParams;
        org.eclipse.jetty.apache.jsp.JuliLog.class.getName();
    }

    @Override
    public File servletContextTempDir() {
        if (servletContextTempDir != null) {
            return servletContextTempDir;
        }
        return JspServletConfig.super.servletContextTempDir();
    }

    @Override
    public Map<String, String> initParams() {
        if (initParams != null) {
            return initParams;
        }
        return JspServletConfig.super.initParams();
    }

    @Override
    public Stream<String> pathSpecs() {
        if (pathSpecs != null) {
            return pathSpecs.stream();
        }
        return JspServletConfig.super.pathSpecs();
    }
}
