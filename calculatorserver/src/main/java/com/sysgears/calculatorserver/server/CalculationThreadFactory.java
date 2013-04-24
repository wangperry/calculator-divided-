package com.sysgears.calculatorserver.server;

import com.sysgears.calculatorserver.kernell.cache.Cache;
import com.sysgears.calculatorserver.kernell.cache.ICache;
import com.sysgears.calculatorserver.kernell.calculation.AnswerSearcher;
import com.sysgears.calculatorserver.messagesAndCommands.InterfaceMessages;
import com.sysgears.calculatorserver.service.Parameters;
import com.sysgears.calculatorserver.userInterface.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Creates threads using socket, statement or paths to files.
 */
public class CalculationThreadFactory {

    /**
     * Class' logger.
     */
    private static Logger log = Logger.getLogger(CalculationThreadFactory.class);

    /**
     * A Fixed Thread Pool container.
     */
    protected ExecutorService pool;

    /**
     * Contains a getAnswer method that
     * finds an answer for the mathematical statement.
     */
    protected AnswerSearcher answerSearcher;

    /**
     * Is responsible for connections counting.
     */
    protected ConnectionsCounter connectionsCounter;

    /**
     * An object that contains program's settings.
     */
    protected Parameters parameters;

    /**
     * Default constructor. Is used in testing.
     */
    public CalculationThreadFactory() {
    }

    /**
     * Initializes factory with a fixed thread pool of MAX_CONNECTIONS value.
     * MAX_CONNECTIONS is a property in parameters object.
     *
     * @param parameters object with parameters
     */
    public CalculationThreadFactory(Parameters parameters) {
        this.parameters = parameters;
        pool = Executors.newFixedThreadPool(parameters.getMaxConnections());
        answerSearcher = new AnswerSearcher();
        connectionsCounter = new ConnectionsCounter();
    }

    /**
     * Creates a new thread for calculation using socket. If number of connections
     * is more than MAX_CONNECTIONS, it asks a client to wait until another client finishes work.
     * MAX_CONNECTIONS is a property in parameters object.
     *
     * @param socket a socket which is used to communicate with a client
     * @param cache  a cache with previously calculated statements
     */
    public void createThread(final Socket socket,
                             final ICache cache) {
        log.debug("Creating thread using socket.");
        try {
            log.debug("Initializing output stream with the socket.");
            OutputStream os = socket.getOutputStream();
            log.debug("Output stream initialized successfully.");
            log.debug("Initializing input stream with the socket.");
            InputStream is = socket.getInputStream();
            log.debug("Input stream initialized successfully.");
            //Checks if current number of connections is bigger then MAX_CONNECTIONS parameter.
            if (connectionsCounter.getConnectionsCounter() >= parameters.getMaxConnections()) {
                log.info("Thread pull is full. Client is waiting.");
                ClientUserInterface.sendMessage(InterfaceMessages.CONNECTIONS_LIMIT_MESSAGE.toString(), os);
                ServerUserInterface.sendMessageToConsole(InterfaceMessages.CONNECTIONS_LIMIT_MESSAGE.toString(), true);
            }
            // Creating and running calculation thread.
            CalculationThread calculationThread = new CalculationThread(is, os, null, answerSearcher, cache, connectionsCounter);
            pool.submit(calculationThread);
        } catch (IOException e) {
            log.error("Error in initializing stream.");
        }
    }

    /**
     * Creates a calculation thread using a statement from command line parameter
     *
     * @param statement a mathematical statement from command line parameter
     * @param cache     a cache with previously calculated statements
     */
    public void createThread(final String statement,
                             final ICache cache) {
        log.debug("Creating thread using statement from command line.");
        log.debug("Initializing output stream.");
        OutputStream os = new DataOutputStream(System.out);
        //Checks if current number of connections is bigger then MAX_CONNECTIONS parameter.
        if (connectionsCounter.getConnectionsCounter() >= parameters.getMaxConnections()) {
            log.info("Thread pull is full. Client is waiting.");
            ClientUserInterface.sendMessage(InterfaceMessages.CONNECTIONS_LIMIT_MESSAGE.toString(), os);
        }
        // Creating and running calculation thread.
        CalculationThread calculationThread = new CalculationThread(null, os, statement, answerSearcher, cache, connectionsCounter);
        pool.submit(calculationThread);
        pool.shutdown();
    }

    /**
     * Creates a calculation thread using paths to input and output files.
     *
     * @param inputFile  a file which contains a correct mathematical statement
     * @param outputFile a file to write an answer to
     * @param cache      a cache with previously calculated statements
     */
    public void createThread(final File inputFile,
                             final File outputFile,
                             final ICache cache) {
        boolean overwrite = true;
        // If a user does not want to overwrite file, then thread will not be created.
        if (outputFile.exists()) {
            overwrite = ServerUserInterface.overwriteFileDialog();
        }
        if (overwrite) {
            try {
                log.info("Creating thread using files.");
                log.debug("Initializing input and output streams.");
                InputStream is = new FileInputStream(inputFile);
                OutputStream os = new FileOutputStream(outputFile);
                CalculationThread calculationThread = new CalculationThread(is, os, null, answerSearcher, cache, connectionsCounter);
                calculationThread.setDaemon(true);
                pool.submit(calculationThread);
                pool.shutdown();
            } catch (FileNotFoundException e) {
                log.error("File not found");
            }
        }
    }

    public void shutDownFactory() {
        pool.shutdownNow();
    }
}