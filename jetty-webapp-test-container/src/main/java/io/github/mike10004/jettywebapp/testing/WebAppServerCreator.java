package io.github.mike10004.jettywebapp.testing;

import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.Configuration;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

class WebAppServerCreator {

    public Server createServer(URI baseUri, ServerOptionSet options) throws URISyntaxException, IOException {

        Server server = JettyHttpContainerFactory.createServer(baseUri, false);

        // Add annotation scanning (for WebAppContexts)
        Configuration.ClassList classlist_ = Configuration.ClassList
                .setServerDefault(server);
        options.configurationClasses().forEach(configClass -> {
            classlist_.addBefore(
                    configClass,
                    "org.eclipse.jetty.annotations.AnnotationConfiguration");
        });
        URI webRootResourceUri = options.webResourceRootUri();

        // Create Servlet context
        ServletContextHandler servletContextHandler = options.newServletContextHandler();
        servletContextHandler.setContextPath(options.contextPath());
        servletContextHandler.setResourceBase(webRootResourceUri.toASCIIString());

        // Since this is a ServletContextHandler we must manually configure JSP support.
        enableEmbeddedJspSupport(servletContextHandler, options.jspOptions());

        options.addServlets(servletContextHandler);

        // Default Servlet (always last, always named "default")
        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
        holderDefault.setInitParameter("resourceBase", webRootResourceUri.toASCIIString());
        options.defaultServletInitParams().forEach(holderDefault::setInitParameter);
        servletContextHandler.addServlet(holderDefault, "/"); // is that pathSpec correct?

        server.setHandler(servletContextHandler);
        return server;
    }


    /**
     * Setup JSP Support for ServletContextHandlers.
     * <p>
     * NOTE: This is not required or appropriate if using a WebAppContext.
     * </p>
     *
     * @param servletContextHandler the ServletContextHandler to configure
     * @throws IOException if unable to configure
     */
    private void enableEmbeddedJspSupport(ServletContextHandler servletContextHandler, JspServletOptionSet options) throws IOException {
        File scratchDir = options.servletContextTempDir();
        //noinspection ResultOfMethodCallIgnored
        scratchDir.mkdirs();
        if (!scratchDir.isDirectory()) {
            throw new IOException("Unable to create or use scratch directory at pathname " + scratchDir);
        }
        servletContextHandler.setAttribute("javax.servlet.context.tempdir", scratchDir);

        // Set Classloader of Context to be sane (needed for JSTL)
        // JSP requires a non-System classloader, this simply wraps the
        // embedded System classloader in a way that makes it suitable
        // for JSP to use
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        servletContextHandler.setClassLoader(jspClassLoader);

        // Manually call JettyJasperInitializer on context startup
        servletContextHandler.addBean(new JspStarter(servletContextHandler));

        // Create / Register JSP Servlet (must be named "jsp" per spec)
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);
        options.initParams().forEach(holderJsp::setInitParameter);
        options.pathSpecs().forEach(pathSpec -> {
            servletContextHandler.addServlet(holderJsp, pathSpec);
        });
    }

    public interface JspServletOptionSet {

        default File servletContextTempDir() {
            return new File(System.getProperty("java.io.tmpdir"), "servlet-context-tmp");
        }

        default Map<String, String> initParams() {
            Map<String, String> jspInitParams = new LinkedHashMap<>();
            jspInitParams.put("logVerbosityLevel", "DEBUG");
            jspInitParams.put("fork", "false");
            jspInitParams.put("xpoweredBy", "false");
            jspInitParams.put("compilerTargetVM", "1.8");
            jspInitParams.put("compilerSourceVM", "1.8");
            jspInitParams.put("keepgenerated", "true");
            return jspInitParams;
        }

        default Stream<String> pathSpecs() {
            return Stream.of("*.jsp");
        }

    }

    public interface ServerOptionSet {

        default String contextPath() {
            return "/";
        }

        default JspServletOptionSet jspOptions() {
            return new JspServletOptionSet() {};
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
         * @return uri
         */
        default URI webResourceRootUri() {
            URL url = getClass().getResource("/WEB-INF/");
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

    /**
     * JspStarter for embedded ServletContextHandlers
     * <p>
     * This is added as a bean that is a jetty LifeCycle on the ServletContextHandler.
     * This bean's doStart method will be called as the ServletContextHandler starts,
     * and will call the ServletContainerInitializer for the jsp engine.
     */
    public static class JspStarter extends AbstractLifeCycle implements ServletContextHandler.ServletContainerInitializerCaller {

        private final JettyJasperInitializer sci;
        private final ServletContextHandler context;

        public JspStarter(ServletContextHandler context) {
            this(context, new org.apache.tomcat.util.scan.StandardJarScanner());
        }

        public JspStarter(ServletContextHandler context, org.apache.tomcat.JarScanner jarScanner) {
            this.sci = new JettyJasperInitializer();
            this.context = context;
            this.context.setAttribute("org.apache.tomcat.JarScanner", jarScanner);
        }

        @Override
        protected void doStart() throws Exception {
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(context.getClassLoader());
            try {
                sci.onStartup(null, context.getServletContext());
                super.doStart();
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        }
    }
}
