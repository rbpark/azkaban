/*
 * Copyright 2012 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.webapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.joda.time.DateTimeZone;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;

import azkaban.database.MySQLConnection;
import azkaban.utils.Props;
import azkaban.utils.Utils;
import azkaban.webapp.servlet.AzkabanServletContextListener;
import azkaban.webapp.servlet.IndexServlet;
import azkaban.webapp.servlet.JobManagerServlet;
import azkaban.webapp.session.SessionCache;
import azkaban.webapp.user.DefaultUserManager;
import azkaban.webapp.user.UserManager;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Starting up the Jetty server
 */
public class AzkabanWebServer {
    public static final String AZKABAN_HOME = "AZKABAN_HOME";
    public static final String DEFAULT_CONF_PATH = "conf";
    public static final String AZKABAN_PROPERTIES_FILE = "azkaban.properties";

    private static final String DEFAULT_TIMEZONE_ID = "default.timezone.id";
    private static final Logger logger = Logger
            .getLogger(AzkabanWebServer.class);
    private static final int DEFAULT_PORT_NUMBER = 8081;
    private static final int DEFAULT_THREAD_NUMBER = 10;
    private static final String VELOCITY_DEV_MODE_PARAM = "velocity.dev.mode";
    private static final String USER_MANAGER_CLASS_PARAM = "user.manager.class";
    private static final String DEFAULT_STATIC_DIR = "";

    private final VelocityEngine velocityEngine;
    private UserManager userManager;
    private Props props;
    private SessionCache sessionCache;
    private File tempDir;

    /**
     * Constructor usually called by tomcat AzkabanServletContext to create the
     * initial server
     */
    public AzkabanWebServer() {
        this(loadConfigurationFromAzkabanHome());
    }

    /**
     * Constructor
     */
    public AzkabanWebServer(Props props) {
        this.props = props;
        velocityEngine = configureVelocityEngine(props.getBoolean(
                VELOCITY_DEV_MODE_PARAM, false));
        sessionCache = new SessionCache(props);
        userManager = loadUserManager(props);

        tempDir = new File(props.getString("azkaban.temp.dir", "temp"));

        // Setup time zone
        if (props.containsKey(DEFAULT_TIMEZONE_ID)) {
            String timezone = props.getString(DEFAULT_TIMEZONE_ID);
            TimeZone.setDefault(TimeZone.getTimeZone(timezone));
            DateTimeZone.setDefault(DateTimeZone.forID(timezone));
        }

        // MySQLConnection connection = new MySQLConnection(props);
    }

    private UserManager loadUserManager(Props props) {
        Class<?> userManagerClass = props.getClass(USER_MANAGER_CLASS_PARAM,
                null);
        logger.info("Loading user manager class " + userManagerClass.getName());
        UserManager manager = null;

        if (userManagerClass != null
                && userManagerClass.getConstructors().length > 0) {
            if (userManagerClass.getConstructors().length > 0) {
                try {
                    manager = (UserManager) userManagerClass.getConstructors()[0]
                            .newInstance();
                } catch (Exception e) {
                    logger.error("Could not instantiate UserManager "
                            + userManagerClass.getName());
                    throw new RuntimeException(e);
                }
            } else {
                logger.error("Could not instantiate UserManager. No empty constructor for "
                        + userManagerClass.getName());
                throw new RuntimeException(
                        "UserManager empty constructor doesn't exist for "
                                + userManagerClass.getName());
            }
            manager.init(props);
        } else {
            manager = new DefaultUserManager();
            manager.init(props);
        }

        return manager;
    }

    /**
     * Returns the web session cache.
     * 
     * @return
     */
    public SessionCache getSessionCache() {
        return sessionCache;
    }

    /**
     * Returns the velocity engine for pages to use.
     * 
     * @return
     */
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    /**
     * 
     * @return
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * Creates and configures the velocity engine.
     * 
     * @param devMode
     * @return
     */
    private VelocityEngine configureVelocityEngine(final boolean devMode) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loader", "classpath");
        engine.setProperty("classpath.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        engine.setProperty("classpath.resource.loader.cache", !devMode);
        engine.setProperty(
                "classpath.resource.loader.modificationCheckInterval", 5L);
        engine.setProperty("resource.manager.logwhenfound", false);
        engine.setProperty("input.encoding", "UTF-8");
        engine.setProperty("output.encoding", "UTF-8");
        engine.setProperty("directive.foreach.counter.name", "idx");
        engine.setProperty("directive.foreach.counter.initial.value", 0);
        engine.setProperty("directive.set.null.allowed", true);
        engine.setProperty("resource.manager.logwhenfound", false);
        engine.setProperty("velocimacro.permissions.allow.inline", true);
        engine.setProperty("velocimacro.library.autoreload", devMode);
        engine.setProperty("velocimacro.library",
                "/azkaban/webapp/servlet/velocity/macros.vm");
        engine.setProperty(
                "velocimacro.permissions.allow.inline.to.replace.global", true);
        engine.setProperty("velocimacro.arguments.strict", true);
        engine.setProperty("runtime.log.invalid.references", devMode);
        engine.setProperty("runtime.log.logsystem.class", Log4JLogChute.class);
        engine.setProperty("runtime.log.logsystem.log4j.logger",
                Logger.getLogger("org.apache.velocity.Logger"));
        engine.setProperty("parser.pool.size", 3);
        return engine;
    }

    /**
     * Returns the global azkaban properties
     * 
     * @return
     */
    public Props getAzkabanProps() {
        return props;
    }

    /**
     * Azkaban using Jetty
     * 
     * @param args
     */
    public static void main(String[] args) {
        OptionParser parser = new OptionParser();

        OptionSpec<String> configDirectory = parser
                .acceptsAll(Arrays.asList("c", "conf"),
                        "The conf directory for Azkaban.").withRequiredArg()
                .describedAs("conf").ofType(String.class);

        logger.error("Starting Jetty Azkaban...");

        Props azkabanSettings = null;
        OptionSet options = parser.parse(args);
        if (options.has(configDirectory)) {
            String path = options.valueOf(configDirectory);
            logger.info("Loading azkaban settings file from " + path);
            File file = new File(path, AZKABAN_PROPERTIES_FILE);
            if (!file.exists() || file.isDirectory() || !file.canRead()) {
                logger.error("Cannot read file " + file);
            }

            azkabanSettings = loadAzkabanConfiguration(file.getPath());
        } else {
            logger.info("Conf parameter not set, attempting to get value from AZKABAN_HOME env.");
            azkabanSettings = loadConfigurationFromAzkabanHome();
        }

        if (azkabanSettings == null) {
            logger.error("Azkaban Properties not loaded.");
            logger.error("Exiting Azkaban...");
            return;
        }
        AzkabanWebServer app = new AzkabanWebServer(azkabanSettings);

        int portNumber = azkabanSettings.getInt("jetty.ssl.port",
                DEFAULT_PORT_NUMBER);
        int maxThreads = azkabanSettings.getInt("jetty.maxThreads",
                DEFAULT_THREAD_NUMBER);

        logger.info("Setting up Jetty Server with port:" + portNumber
                + " and numThreads:" + maxThreads);

        final Server server = new Server();
        SslSocketConnector secureConnector = new SslSocketConnector();
        secureConnector.setPort(portNumber);
        secureConnector
                .setKeystore(azkabanSettings.getString("jetty.keystore"));
        secureConnector
                .setPassword(azkabanSettings.getString("jetty.password"));
        secureConnector.setKeyPassword(azkabanSettings
                .getString("jetty.keypassword"));
        secureConnector.setTruststore(azkabanSettings
                .getString("jetty.truststore"));
        secureConnector.setTrustPassword(azkabanSettings
                .getString("jetty.trustpassword"));
        server.addConnector(secureConnector);

        QueuedThreadPool httpThreadPool = new QueuedThreadPool(maxThreads);
        server.setThreadPool(httpThreadPool);

        String staticDir = azkabanSettings.getString("web.resource.dir",
                DEFAULT_STATIC_DIR);
        logger.info("Setting up web resource dir " + staticDir);
        Context root = new Context(server, "/", Context.SESSIONS);

        root.setResourceBase(staticDir);
        root.addServlet(new ServletHolder(new DefaultServlet()), "/static/*");
        root.addServlet(new ServletHolder(new IndexServlet()), "/");
        root.addServlet(new ServletHolder(new JobManagerServlet()), "/manager");
        root.setAttribute(
                AzkabanServletContextListener.AZKABAN_SERVLET_CONTEXT_KEY, app);

        try {
            server.start();
        } catch (Exception e) {
            logger.warn(e);
            Utils.croak(e.getMessage(), 1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {
                logger.info("Shutting down http server...");
                try {
                    server.stop();
                    server.destroy();
                } catch (Exception e) {
                    logger.error("Error while shutting down http server.", e);
                }
                logger.info("kk thx bye.");
            }
        });
        logger.info("Server running on port " + portNumber + ".");
    }

    /**
     * Loads the Azkaban property file from the AZKABAN_HOME conf directory
     * 
     * @return
     */
    private static Props loadConfigurationFromAzkabanHome() {
        String azkabanHome = System.getenv("AZKABAN_HOME");

        if (azkabanHome == null) {
            logger.error("AZKABAN_HOME not set.");
            return null;
        }

        if (!new File(azkabanHome).isDirectory()
                || !new File(azkabanHome).canRead()) {
            logger.error(azkabanHome + " is not a readable directory.");
            return null;
        }

        File confPath = new File(azkabanHome, DEFAULT_CONF_PATH);
        if (!confPath.exists() || !confPath.isDirectory()
                || !confPath.canRead()) {
            logger.error(azkabanHome
                    + " does not contain a readable conf directory.");
            return null;
        }

        File confFile = new File(confPath, AZKABAN_PROPERTIES_FILE);
        if (!confFile.exists() || confFile.isDirectory() || !confPath.canRead()) {
            logger.error(confFile
                    + " does not contain a readable azkaban.properties file.");
            return null;
        }

        return loadAzkabanConfiguration(confFile.getPath());
    }

    public File getTempDirectory() {
        return tempDir;
    }

    /**
     * Loads the Azkaban conf file int a Props object
     * 
     * @param path
     * @return
     */
    private static Props loadAzkabanConfiguration(String path) {
        try {
            return new Props(null, path);
        } catch (FileNotFoundException e) {
            logger.error("File not found. Could not load azkaban config file "
                    + path);
        } catch (IOException e) {
            logger.error("File found, but error reading. Could not load azkaban config file "
                    + path);
        }

        return null;
    }
}
