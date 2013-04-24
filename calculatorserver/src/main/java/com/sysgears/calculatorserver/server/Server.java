package com.sysgears.calculatorserver.server;

import com.sysgears.calculatorserver.kernell.cache.Cache;
import com.sysgears.calculatorserver.kernell.cache.ICache;
import com.sysgears.calculatorserver.kernell.cache.SaveLoadCache;
import com.sysgears.calculatorserver.messagesAndCommands.InterfaceMessages;
import com.sysgears.calculatorserver.service.Parameters;
import com.sysgears.calculatorserver.userInterface.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * A class that implements a multi-thread server through the thread factory.
 */
public class Server extends Thread {

    /**
     * The maximum queue length for incoming connection indications (a request to connect).
     * If a connection indication arrives when the queue is full, the connection is refused.
     * The backlog argument must be a positive value greater than 0. If the value passed if equal
     * or less than 0, then the default value will be assumed.
     */
    private static final int BACKLOG = 0;

    /**
     * Time in milliseconds after which the <code>SocketTimeoutException</code> will be thrown.
     */
    private static final int SOCKET_TIMEOUT = 1000;

    /**
     * Class' logger.
     */
    private static Logger log = Logger.getLogger(Server.class);

    /**
     * Is used to listen a port for a connection.
     */
    private ServerSocket serverSocket;

    /**
     * Shows if the server should stop accepting connections.
     */
    private boolean isTurnedOn = true;

    /**
     * An object that contains program's settings.
     */
    private Parameters parameters;

    /**
     * Initializes server with parameters
     *
     * @param parameters contains program's settings
     */
    public Server(Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Starts a multi-thread server.
     */
    public void run() {
        try {
            // Reading port from parameters.
            int port = parameters.getPort();
            // Reading IP from parameters.
            InetAddress ip = parameters.getIP();
            // Initialising cache.
            ICache cache = new Cache(parameters, new SaveLoadCache());
            // Loading cache from file that is defined in parameters.
            log.debug("Initializing thread factory.");
            CalculationThreadFactory factory = new CalculationThreadFactory(parameters);
            log.debug("Initializing socket.");
            Socket socket = new Socket();
            try {
                log.debug("Initializing server socket using values from parameters.");
                serverSocket = new ServerSocket(port, BACKLOG, ip);
                ServerUserInterface.sendMessageToConsole(InterfaceMessages.SERVER_IS_STARTED_MESSAGE.toString() + port + ".", false);
                log.debug("Starting endless cycle of connections.");
                serverSocket.setSoTimeout(SOCKET_TIMEOUT);
                while (isTurnedOn) {
                    try {
                        socket = serverSocket.accept();
                        if (isTurnedOn) {
                            log.debug("Connection accepted.");
                            log.debug("Creating thread for calculation.");
                            factory.createThread(socket, cache);
                        }
                    } catch (SocketTimeoutException ignored) { //Every SOCKET_TIMEOUT milliseconds server checks if it is turned on.
                    }
                }
                factory.shutDownFactory();
            } catch (IOException e) {
                ServerUserInterface.sendMessageToConsole(InterfaceMessages.EXCEPTION_MESSAGE.toString() + e, false);
                log.error("Error in accepting connection.");
            } finally {
                try {
                    log.debug("Closing socket.");
                    socket.close();
                    log.debug("Socket clothed successfully.");
                    log.debug("Closing server socket.");
                    serverSocket.close();
                    log.debug("Server socket clothed successfully.");
                } catch (Exception e) {
                    log.error("Socket closing error.");
                }
            }
        } catch (Exception e) {
            // Catching all unexpected exceptions in this thread.
            log.error("Unexpected exception", e);
        }
    }

    /**
     * Stops the server. New connections would not be established.
     * Existing connections will be kept until clients will close them.
     */
    public void turnOffServer() {
        isTurnedOn = false;
    }
}