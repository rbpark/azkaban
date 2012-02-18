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

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.mortbay.jetty.Server;

import azkaban.utils.Props;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Starting up the Jetty server
 */
public class AzkabanServer {
    public static final String AZKABAN_HOME = "AZKABAN_HOME";
    public static final String DEFAULT_CONF_PATH = "conf";
    public static final String AZKABAN_PROPERTIES_FILE = "azkaban.properties";
    
    private static final Logger logger = Logger.getLogger(AzkabanServer.class);
    private static final int DEFAULT_PORT_NUMBER = 8081;
    private static final int DEFAULT_THREAD_NUMBER = 10;
    private static final String VELOCITY_DEV_MODE_PARAM = "velocity.dev.mode";
    
    private final VelocityEngine velocityEngine;
    private Props props;
    
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
        }
        else {
            logger.info("Conf parameter not set, attempting to get value from AZKABAN_HOME env.");
            azkabanSettings = loadConfigurationFromAzkabanHome();
        }
    
        if (azkabanSettings == null) {
            logger.error("Azkaban Properties not loaded.");
            logger.error("Exiting Azkaban...");
            return;
        }
        
        
        int portNumber = azkabanSettings.getInt("jetty.port", DEFAULT_PORT_NUMBER);
        int httpThreads = azkabanSettings.getInt("jetty.numThreads", DEFAULT_THREAD_NUMBER);

        logger.info("Setting up Jetty Server with port:" + portNumber + " and numThreads:" + httpThreads);
        
        final Server server = new Server(portNumber);
        //
        //
        // final HttpServer server = new HttpServer();
        // SocketListener listener = new SocketListener();
        //
        // listener.setPort(portNumber);
        // listener.setMinThreads(1);
        // listener.setMaxThreads(httpThreads);
        // server.addListener(listener);
    }

    /**
     * Loads the Azkaban property file from the AZKABAN_HOME conf directory
     * @return
     */
    private static Props loadConfigurationFromAzkabanHome() {
        String azkabanHome = System.getenv("AZKABAN_HOME");
        
        if (azkabanHome == null) {
            logger.error("AZKABAN_HOME not set.");
            return null;
        }
        
        if (!new File(azkabanHome).isDirectory() || !new File(azkabanHome).canRead()) {
            logger.error(azkabanHome + " is not a readable directory.");
            return null;
        }
        
        File confPath = new File(azkabanHome, DEFAULT_CONF_PATH);
        if (!confPath.exists() || !confPath.isDirectory() || !confPath.canRead()) {
            logger.error(azkabanHome + " does not contain a readable conf directory.");
            return null;
        }
        
        File confFile = new File(confPath, AZKABAN_PROPERTIES_FILE);
        if (!confFile.exists() || confFile.isDirectory() || !confPath.canRead()) {
            logger.error(confFile + " does not contain a readable azkaban.properties file.");
            return null;
        }
        
        return loadAzkabanConfiguration(confFile.getPath());
    }
    
    /**
     * Loads the Azkaban conf file int a Props object
     * @param path
     * @return
     */
    private static Props loadAzkabanConfiguration(String path) {
        try {
           return new Props(null, path);
        }
        catch (FileNotFoundException e) {
            logger.error("File not found. Could not load azkaban config file " + path);
        }
        catch (IOException e) {
            logger.error("File found, but error reading. Could not load azkaban config file " + path);
        }
        
        return null;
    }
    
    /**
     * Constructor usually called by tomcat AzkabanServletContext to create 
     * the initial server
     */
    public AzkabanServer() {
        this(loadConfigurationFromAzkabanHome());
    }
    
    /**
	 * Constructor
	 */
    public AzkabanServer(Props props) {
        this.props = props;
        velocityEngine = configureVelocityEngine(props.getBoolean(VELOCITY_DEV_MODE_PARAM, false));
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
        engine.setProperty("classpath.resource.loader.modificationCheckInterval", 5L);
        engine.setProperty("resource.manager.logwhenfound", false);
        engine.setProperty("input.encoding", "UTF-8");
        engine.setProperty("output.encoding", "UTF-8");
        engine.setProperty("directive.foreach.counter.name", "idx");
        engine.setProperty("directive.foreach.counter.initial.value", 0);
        //engine.setProperty("runtime.references.strict", true);
        engine.setProperty("directive.set.null.allowed", true);
        engine.setProperty("resource.manager.logwhenfound", false);
        engine.setProperty("velocimacro.permissions.allow.inline", true);
        engine.setProperty("velocimacro.library.autoreload", devMode);
        engine.setProperty("velocimacro.library", "/azkaban/webapp/servlet/velocity/macros.vm");
        engine.setProperty("velocimacro.permissions.allow.inline.to.replace.global", true);
        engine.setProperty("velocimacro.context.localscope", true);
        engine.setProperty("velocimacro.arguments.strict", true);
        engine.setProperty("runtime.log.invalid.references", devMode);
        engine.setProperty("runtime.log.logsystem.class", Log4JLogChute.class);
        engine.setProperty("runtime.log.logsystem.log4j.logger",
                           Logger.getLogger("org.apache.velocity.Logger"));
        engine.setProperty("parser.pool.size", 3);
        return engine;
    }
}