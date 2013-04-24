package com.sysgears.calculatorserver.server;

import org.apache.log4j.Logger;

/**
 * A connections calculator
 */
public class ConnectionsCounter {

    /**
     * Class' log.
     */
    private static Logger log = Logger.getLogger(ConnectionsCounter.class);
    
    /**
     * Number of connections.
     */
    private int connectionsCounter = 0;

    /**
     * Increase the connections counter by one.
     */
    public synchronized void connect() {
        connectionsCounter++;
        if(log.isDebugEnabled()) {
            log.debug("Increasing connections counter. Current value is "+connectionsCounter);
        }
    }

    /**
     * Decrease the connections counter by one.
     */
    public synchronized void disconnect() {
        connectionsCounter--;
        if(log.isDebugEnabled()) {
            log.debug("Decreasing connections counter. Current value is "+connectionsCounter);
        }
    }

    /**
     * Returns the current number of connections.
     *
     * @return the current number of connections
     */
    public int getConnectionsCounter() {

        return connectionsCounter;
    }
}
