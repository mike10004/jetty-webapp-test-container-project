package io.github.mike10004.jettywebapp.testing.example;

import io.github.mike10004.jettywebapp.testing.JettyWebappTestContainerFactory;
import io.github.mike10004.jettywebapp.testing.WebappDeploymentContext;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

import javax.ws.rs.core.Application;
import java.io.File;
import java.nio.file.Path;

public class MyApplicationTestBase extends JerseyTest {

    @ClassRule
    public static TemporaryFolder servletContextTempFolder = new TemporaryFolder();

    public MyApplicationTestBase() {
        super(new JettyWebappTestContainerFactory());
    }

    @Override
    protected final Application configure() {
        throw new UnsupportedOperationException("this must not be invoked; this class overrides configureDeployment() instead");
    }

    @Override
    protected DeploymentContext configureDeployment() {
        return WebappDeploymentContext.builder(getWebappClass())
                .contextPath("/example")
                .servletContextTempDir(servletContextTempFolder.getRoot())
                .webResourceRootUri(getJettyWebappExampleProjectDir().resolve("src/main/webapp").toUri())
                .build();
    }

    /**
     * Returns the test target. Make sure this does not rely on an instance field
     * (because it is invoked during superclass constructor).
     * @return webapp class
     */
    protected Class<? extends MyApplication> getWebappClass() {
        return MyApplication.class;
    }

    private static Path getJettyWebappExampleProjectDir() {
        File projectDir = new File(System.getProperty("user.dir"));
        if (!"jetty-webapp-example".equals(projectDir.getName())) {
            throw new IllegalStateException("not in expected directory: expected jetty-webapp-example, but we are in " + projectDir);
        }
        return projectDir.toPath();
    }
}
