package io.github.mike10004.jettywebapp.testing;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.glassfish.jersey.test.spi.TestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Factory for testing {@link org.glassfish.jersey.jetty.JettyHttpContainer}.
 *
 * @author Arul Dhesiaseelan (aruld@acm.org)
 * @author Marek Potociar
 * @author Mike Chaberski
 */
public class JettyWebappTestContainerFactory implements TestContainerFactory {

    @Nullable
    private final String applicationPath;

    /**
     * Constructs an instance of the class. If this zero-arg constructor is
     * used, then an instance of {@link WebappDeploymentContext} must be
     * passed to {@link #create(URI, DeploymentContext)}.
     */
    public JettyWebappTestContainerFactory() {
        this(null);
    }

    /**
     * Constructs an instance of the class.
     * @param applicationPath application path, if a {@link WebappDeploymentContext} is not passed to {@link #create(URI, DeploymentContext)}
     */
    public JettyWebappTestContainerFactory(@Nullable String applicationPath) {
        this.applicationPath = applicationPath;
    }

    /*
     * Portions of this code are:
     *
     * Copyright (c) 2013, 2019 Oracle and/or its affiliates. All rights reserved.
     *
     * This program and the accompanying materials are made available under the
     * terms of the Eclipse Public License v. 2.0, which is available at
     * http://www.eclipse.org/legal/epl-2.0.
     *
     * This Source Code may also be made available under the following Secondary
     * Licenses when the conditions for such availability set forth in the
     * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
     * version 2 with the GNU Classpath Exception, which is available at
     * https://www.gnu.org/software/classpath/license.html.
     *
     * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
     */

    private static class JettyWebappTestContainer implements TestContainer {

        private static final Logger log = LoggerFactory.getLogger(JettyWebappTestContainer.class);

        /**
         * Base URI. Not final because if the port is specified as zero, then a port
         * is selected when the container is started, and the base URI must be updated.
         */
        private URI baseUri;
        private final Server server;

        /**
         * Constructs an instance.
         * @param baseUri base URI; use port=0 for dynamic
         * @param serverOptions server options
         */
        private JettyWebappTestContainer(URI baseUri, WebappServerConfig serverOptions) throws IOException, URISyntaxException {
            String contextPath = serverOptions.contextPath();
            // The official test container factory throws an exception here if
            // the context path is not "/", but I do not know why
            this.baseUri = UriBuilder.fromUri(baseUri).path(contextPath).build();
            log.info("Creating JettyWebappTestContainer configured at the base URI {}",
                    TestHelper.zeroPortToAvailablePort(baseUri));
            this.server = new WebappServerCreator().createServer(this.baseUri, serverOptions);
        }

        @Override
        public ClientConfig getClientConfig() {
            return null;
        }

        /**
         * Gets the base URI of the application. If this container has not been started,
         * the URI may not be entirely correct, because the port may not have been selected yet.
         * @return
         */
        @Override
        public URI getBaseUri() {
            return baseUri;
        }

        /**
         * Starts the container.
         * This method is adapted from {@code JettyTestContainerFactory}.
         */
        @Override
        public void start() {
            if (server.isStarted()) {
                log.warn("Ignoring start request - JettyWebappTestContainer is already started.");
            } else {
                log.debug("Starting JettyWebappTestContainer...");
                try {
                    server.start();

                    if (baseUri.getPort() == 0) {
                        int port = 0;
                        for (final Connector connector : server.getConnectors()) {
                            if (connector instanceof ServerConnector) {
                                port = ((ServerConnector) connector).getLocalPort();
                                break;
                            }
                        }

                        baseUri = UriBuilder.fromUri(this.baseUri).port(port).build();

                        log.info("Started JettyWebappTestContainer at the base URI {}", baseUri);
                    }
                } catch (Exception e) {
                    throw new TestContainerException(e);
                }
            }
        }

        /**
         * Stops the container.
         * This method is adapted from {@code JettyTestContainerFactory}.
         */
        @Override
        public void stop() {
            if (server.isStarted()) {
                log.debug("Stopping JettyTestContainer...");
                try {
                    this.server.stop();
                } catch (Exception ex) {
                    log.warn("Error Stopping JettyTestContainer...", ex);
                }
            } else {
                log.warn("Ignoring stop request - JettyTestContainer is already stopped.");
            }
        }
    }

    @Override
    public TestContainer create(URI baseUri, DeploymentContext context) throws IllegalArgumentException {
        WebappServerConfigurator configurator = getConfigurator(context);
        try {
            return new JettyWebappTestContainer(baseUri, configurator.getServerConfig());
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected WebappServerConfigurator getConfigurator(DeploymentContext context) {
        if (context instanceof WebappServerConfigurator) {
            return (WebappServerConfigurator) context;
        }
        WebappDeploymentContext rebuiltContext = WebappDeploymentContext.rebuild(context, applicationPath);
        return rebuiltContext;
    }
}
