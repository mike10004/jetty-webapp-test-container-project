/*
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

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for testing {@link org.glassfish.jersey.jetty.JettyHttpContainer}.
 *
 * @author Arul Dhesiaseelan (aruld@acm.org)
 * @author Marek Potociar
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

    private static class JettyWebappTestContainer implements TestContainer {

        private static final Logger LOGGER = Logger.getLogger(JettyWebappTestContainer.class.getName());

        private final URI baseUri;
        private final Server server;

        /**
         *  @param baseUri
         * @param serverOptions
         */
        private JettyWebappTestContainer(URI baseUri, WebappServerConfig serverOptions) throws IOException, URISyntaxException {
            String contextPath = serverOptions.contextPath();
            // The official test container factory throws an exception here if
            // the context path is not "/", but I do not know why
            this.baseUri = UriBuilder.fromUri(baseUri).path(contextPath).build();

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Creating JettyTestContainer configured at the base URI "
                        + TestHelper.zeroPortToAvailablePort(baseUri));
            }
            this.server = new WebappServerCreator().createServer(this.baseUri, serverOptions);
        }

        @Override
        public ClientConfig getClientConfig() {
            return null;
        }

        @Override
        public URI getBaseUri() {
            return baseUri;
        }

        @Override
        public void start() {
            if (server.isStarted()) {
                LOGGER.log(Level.WARNING, "Ignoring start request - JettyTestContainer is already started.");
            } else {
                LOGGER.log(Level.FINE, "Starting JettyTestContainer...");
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

                        URI completedBaseUri = UriBuilder.fromUri(this.baseUri).port(port).build();

                        LOGGER.log(Level.INFO, "Started JettyTestContainer at the base URI " + completedBaseUri);
                    }
                } catch (Exception e) {
                    throw new TestContainerException(e);
                }
            }
        }

        @Override
        public void stop() {
            if (server.isStarted()) {
                LOGGER.log(Level.FINE, "Stopping JettyTestContainer...");
                try {
                    this.server.stop();
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error Stopping JettyTestContainer...", ex);
                }
            } else {
                LOGGER.log(Level.WARNING, "Ignoring stop request - JettyTestContainer is already stopped.");
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
