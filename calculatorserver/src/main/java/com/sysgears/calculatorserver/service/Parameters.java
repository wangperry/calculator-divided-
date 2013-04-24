package com.sysgears.calculatorserver.service;

import com.sysgears.calculatorserver.messagesAndCommands.InterfaceMessages;
import com.sysgears.calculatorserver.userInterface.ServerUserInterface;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Reads and saves parameters from the resources/calculator.properties:
 */
public class Parameters {

    /**
     * Class' log.
     */
    private static Logger log = Logger.getLogger(Parameters.class);

    /**
     * The path to the file that contains properties for the program.
     */
    private static final String PATH_TO_FILE = "resources/calculator.properties";

    /**
     * Default IP address of server. If The server machine has more then one IP, and
     * the calculator server must work only on one of them, this IP address can be set.
     * Otherwise, "null" value is used.
     */
    private static final String DEFAULT_IP = null;

    /**
     * Number of port to listen by server.
     */
    private static final int DEFAULT_PORT = 3128;

    /**
     * Default limitation of statements that can be contained by the cache.
     */
    private static final int DEFAULT_CACHE_LIMIT = 200;

    /**
     * The default path to the cache file.
     */
    private static final String DEFAULT_CACHE_FILE = "resources/default.cache";

    /**
     * The default number of maximum connections at one time.
     */
    private static final int DEFAULT_MAX_CONNECTIONS = 5000;

    /**
     * Minimum number of port
     */
    private static final int MIN_PORT_NUMBER = 1;

    /**
     * Maximum number.of port
     */
    private static final int MAX_PORT_NUMBER = 65535;
    
    /**
     * IP address of server. If The server machine has more then one IP, and
     * the calculator server must work only on one of them, this IP address can be set.
     * Otherwise, "null" value is used.
     */
    private InetAddress ip;

    /**
     * Number of port to listen by server.
     */
    private int port;


    /**
     * A size of the calculator's fixed thread pool.
     */
    private int maxConnections;

    /**
     * Limitation of statements that can be contained by the cache.
     */
    private int cacheLimit;

    /**
     * The path to the cache file.
     */
    private File cacheFile;

    /**
     * Does nothing in fact except of sending debug message to the log.
     */
    public Parameters() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing parameters.");
        }
    }

    /**
     * Loads the default parameters.
     * The default values are:
     * ip = null;
     * port = 3128;
     * maxConnections = 5000
     * cacheFile is at "../CalculatorServer/resources/default.cache"
     * Cache limit = 200
     */
    public void setDefaultParameters() {
        ip = null;
        port = DEFAULT_PORT;
        maxConnections = DEFAULT_MAX_CONNECTIONS;
        cacheFile = new File(DEFAULT_CACHE_FILE);
        cacheLimit = DEFAULT_CACHE_LIMIT;
        if (log.isDebugEnabled()) {
            log.debug("Setting default parameters. IP=" + ip + " port=" + port + " max connections="
                    + maxConnections + " cache file=" + cacheFile.getAbsolutePath() + " cache limit=" + cacheLimit);
        }
    }

    /**
     * Reads parameters from ../CalculatorServer/resources/calculator.properties.
     * If the file can not be found, the default values would be loaded.
     * The default values are:
     * ip = null;
     * port = 3128;
     * maxConnections = 5000
     * cacheFile is at "../CalculatorServer/resources/default.cache"
     * Cache limit = 200
     */
    public void loadParameters() {

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(PATH_TO_FILE)));
            String temp = prop.getProperty("IP", DEFAULT_IP);
            port = Integer.parseInt(prop.getProperty("PORT", String.valueOf(DEFAULT_PORT)));
            maxConnections = Integer.parseInt(prop.getProperty("MAX_CONNECTIONS", String.valueOf(DEFAULT_MAX_CONNECTIONS)));
            cacheLimit = Integer.parseInt(prop.getProperty("CACHE_LIMIT", String.valueOf(DEFAULT_CACHE_LIMIT)));
            cacheFile = new File(prop.getProperty("cache", DEFAULT_CACHE_FILE));
            if (temp.equalsIgnoreCase("null")) {
                ip = null;
            } else {
                ip = InetAddress.getByName(temp);
            }
            if (log.isDebugEnabled()) {
                log.debug("Parameters are loaded from file (" + PATH_TO_FILE + ")");
            }
        } catch (UnknownHostException e) {
            ServerUserInterface.sendMessageToConsole(InterfaceMessages.ADDRESS_ERROR.toString(), false);
            ip = null;
            log.warn("Unknown host exception. IP is set to null.");
        } catch (Exception e) {
            ServerUserInterface.sendMessageToConsole(InterfaceMessages.ERROR_IN_PROPERTIES_FILE.toString(), false);
            setDefaultParameters();
            log.warn("Error in loading parameters. Default parameters are loaded.");
        }

    }

    /**
     * Sets host name. If host name is invalid, the host name is set to null.
     *
     * @param host host name
     */
    public void setHost(final String host) {
        try {
            ip = InetAddress.getByName(host);
            if (log.isInfoEnabled()) {
                log.info("Host is specified from command line: " + host);
            }
        } catch (UnknownHostException e) {
            log.warn("Host that is set from command line is unknown. IP is set to null.");
            ip = null;
            ServerUserInterface.sendMessageToConsole(InterfaceMessages.ADDRESS_ERROR.toString(), false);
        }
    }

    /**
     * Sets port. If port parameter from command line is invalid, it is set to default value.
     *
     * @param usersPort a string statement from command line with a port number
     */
    public void setPort(final String usersPort) {
        try {
            int usersPortInt = Integer.parseInt(usersPort);
            if (usersPortInt > MIN_PORT_NUMBER && usersPortInt < MAX_PORT_NUMBER) {
                port = usersPortInt;
                if (log.isInfoEnabled()) {
                    log.info("Port is specified from command line: " + port);
                }
            } else {
                port = DEFAULT_PORT;
                log.warn("Port that is specified from command line is invalid. Default port is set.");
            }
        } catch (Exception e) {
            log.warn("Port that is specified from command line is invalid. Default port is set.");
            ServerUserInterface.sendMessageToConsole(InterfaceMessages.PORT_ERROR.toString(), false);
            port = DEFAULT_PORT;
        }
    }

    /**
     * Sets maximum connections value. If this parameter from command line is invalid, it is set to default value.
     *
     * @param maxConFromUser a string statement from command line with a max connections number
     */
    public void setMaxConnections(final String maxConFromUser) {
        try {
            int maxConnInt = Integer.parseInt(maxConFromUser);
            if (maxConnInt < 1) {
                maxConnections = Integer.parseInt(maxConFromUser);
                if (log.isInfoEnabled()) {
                    log.info("Maximum connections parameter is specified from command line: " + maxConnections);
                }
            } else {
                maxConnections = maxConnInt;
                log.warn("Maximum connections parameter that is set from the command line is invalid. Default is set.");
            }
        } catch (Exception e) {
            log.warn("Maximum connections parameter that is set from the command line is invalid. Default is set.");
            ServerUserInterface.sendMessageToConsole(InterfaceMessages.MAX_CONNECTIONS_ERROR.toString(), false);
            maxConnections = DEFAULT_MAX_CONNECTIONS;
        }
    }

    /**
     * Sets a path to the cache file.
     *
     * @param file path to cache file
     * @return false if user does not want to overwrite this file
     */
    public boolean setCacheFile(final String file) {
        boolean cacheFileIsSet = true;
        cacheFile = new File(file);
        if (log.isInfoEnabled()) {
            log.info("The path to cache file is set from command line: " + file);
        }
        if (cacheFile.exists()) {
            log.info("File exists. Sending overwrite dialog to user.");
            if (!ServerUserInterface.overwriteFileDialog()) {
                cacheFileIsSet = false;
            }
        }

        return cacheFileIsSet;
    }

    /**
     * Sets cache limit. If this parameter from command line is invalid, it is set to default value.
     *
     * @param limit a string statement from command line with a cache limit number
     */
    public void setCacheLimit(final String limit) {
        try {
            int limitInt = Integer.parseInt(limit);
            if (limitInt > 1) {
                cacheLimit = limitInt;
                if (log.isInfoEnabled()) {
                    log.info("Cache limit is specified from command line: " + cacheLimit);
                }
            } else {
                cacheLimit = DEFAULT_CACHE_LIMIT;
                log.warn("Cache limit that is set from the command line is invalid. Default is set.");
            }
        } catch (Exception e) {
            ServerUserInterface.sendMessageToConsole(InterfaceMessages.CACHE_LIMIT_ERROR.toString(), false);
            log.warn("Cache limit that is set from the command line is invalid. Default is set.");
            cacheLimit = DEFAULT_CACHE_LIMIT;
        }
    }

    /**
     * Returns IP parameter.
     *
     * @return a value of IP parameter
     */
    public InetAddress getIP() {

        return ip;
    }

    /**
     * Returns the port parameter.
     *
     * @return a value of the port parameter
     */
    public int getPort() {

        return port;
    }

    /**
     * Returns MAX_CONNECTIONS parameter.
     *
     * @return a value of MAX_CONNECTIONS parameter
     */
    public int getMaxConnections() {

        return maxConnections;
    }

    /**
     * Returns the current cache file.
     *
     * @return The current cache file.
     */
    public File getCacheFile() {

        return cacheFile;
    }

    /**
     * Returns the current value of the cache limit
     *
     * @return cache size in number of statements
     */
    public int getCacheLimit() {

        return cacheLimit;
    }

    /**
     * Prints parameters' values in console.
     */
    public void printParameters() {
        log.debug("Parameters sent to console");
        ServerUserInterface.sendMessageToConsole(InterfaceMessages.PARAMETERS_SET.toString() + "ip: " + ip + "\nport: " + port +
                "\nMaximum connections: " + maxConnections + "\ncacheFile is set to: " + cacheFile.getAbsolutePath() +
                "\nCache limit: " + cacheLimit, false);
    }
}