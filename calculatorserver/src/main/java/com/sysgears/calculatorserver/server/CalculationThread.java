package com.sysgears.calculatorserver.server;

import com.sysgears.calculatorserver.exceptions.*;
import com.sysgears.calculatorserver.kernell.Operations;
import com.sysgears.calculatorserver.kernell.cache.Cache;
import com.sysgears.calculatorserver.kernell.cache.ICache;
import com.sysgears.calculatorserver.kernell.calculation.AnswerSearcher;
import com.sysgears.calculatorserver.kernell.calculation.Calculator;
import com.sysgears.calculatorserver.messagesAndCommands.InterfaceMessages;
import com.sysgears.calculatorserver.statement.ConvertOperations;
import com.sysgears.calculatorserver.userInterface.ClientUserInterface;
import com.sysgears.calculatorserver.userInterface.ServerUserInterface;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DecimalFormat;

/**
 * Creates a thread that establishes a dialog with a client, accepts a statement from a client,
 * validates it and sends to
 * calculation method. Also it sends logs to the server's console.
 */
public class CalculationThread extends Thread {

    /**
     * Is a pattern that is used in creating a DecimalFormat object. This
     * DecimalFormat object is used in formatting an answer for the statement.
     */
    private static final String DECIMAL_FORMAT_PATTERN = "############.##########; -############.##########";

    /**
     * A command that a user can enter to exit the program.
     */
    private static final String EXIT_COMMAND = "exit";

    /**
     * Size of the input stream buffer.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Number of byte in buffer from witch buffer decoding must be started.
     */
    private static final int OFFSET = 0;

    /**
     * Class' logger
     */
    private static Logger log = Logger.getLogger(CalculationThread.class);

    /**
     * Input stream from which the mathematical statement is get.
     * Can be null. If it is null then the usersStatement must be set in the constructor.
     */
    private InputStream is;

    /**
     * Output stream to which an answer will be sent.
     */
    private OutputStream os;

    /**
     * A statement to calculate.
     */
    private String usersStatement;

    /**
     * This object contains a getAnswer method that
     * finds an answer for the mathematical statement.
     */
    private AnswerSearcher answerSearcher;

    /**
     * A cache with previously calculated statements.
     */
    private ICache cache;

    /**
     * Is responsible for connections counting.
     */
    private ConnectionsCounter connectionsCounter;

    /**
     * Initializes thread.
     *
     * @param is                 an input stream. If it is null, a statement value must not be null
     * @param os                 an output stream; can not be null
     * @param statement          a mathematical statement. If this parameter is null, input stream must not be null
     * @param answerSearcher     an AnswerSearcher object witch provides a method to find an answer fo statement
     * @param cache              a cache with previously calculated statements
     * @param connectionsCounter is responsible for connections counting
     */
    public CalculationThread(final InputStream is,
                             final OutputStream os,
                             final String statement,
                             final AnswerSearcher answerSearcher,
                             final ICache cache,
                             final ConnectionsCounter connectionsCounter) {
        log.debug("Initializing thread.");
        this.is = is;
        this.os = os;
        usersStatement = statement;
        this.answerSearcher = answerSearcher;
        this.connectionsCounter = connectionsCounter;
        this.cache = cache;
    }

    /**
     * Runs a thread witch asks for (if it is needed) and calculates a mathematical statement.
     */
    public void run() {
        try {
            connectionsCounter.connect();
            //If a statement is received from factory.
            if (usersStatement != null) {
                log.debug("Statement is received from factory.");
                normalizeStatement();
                processStatement();
            }
            //If statement is not received from factory.
            log.debug("Input stream is received from factory.");
            if (is != null) {
                getAndProcessStatement();
            }
        } catch (Exception e) {
            log.error("Unexpected exception", e);
        }
    }

    /**
     * Calculates the statement and sends an answer to the output stream.
     */
    private void processStatement() {
        try {
            log.info("Statement is " + usersStatement);
            // Calculating statement
            double result = answerSearcher.getAnswer(usersStatement, new Calculator(), new Operations(), cache);
            log.debug("Sending answer to the output stream.");
            // Formatting the answer
            DecimalFormat doubleFormat = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
            String answer = doubleFormat.format(result);
            os.write(answer.getBytes());
            if (log.isInfoEnabled()) {
                log.info("Result is " + answer);
            }
        } catch (IOException e) {
            log.error("Error in writing to output stream.");
        } catch (StatementErrorException e) {
            try {
                log.info("Statement error.");
                os.write(InterfaceMessages.STATEMENT_ERROR.toString().getBytes());
                ServerUserInterface.sendMessageToConsole(InterfaceMessages.STATEMENT_ERROR.toString(), true);
            } catch (IOException e1) {
                log.error("Error in writing to output stream.");
            }
        } catch (DivisionByZeroInDoubleException e) {
            log.info("Division by zero.");
            try {
                os.write(InterfaceMessages.DIVISION_BY_ZERO_EXCEPTION_ANSWER.toString().getBytes());
                ServerUserInterface.sendMessageToConsole(InterfaceMessages.DIVISION_BY_ZERO_EXCEPTION_ANSWER.toString() + e, true);
            } catch (IOException e1) {
                log.error("Error in writing to output stream.");
            }
        }finally {
            // saving cache and closing streams
            connectionsCounter.disconnect();
            try {
                log.debug("Closing output stream.");
                os.close();
                log.debug("Output stream closed successfully.");
            } catch (IOException e) {
                log.error("Error in closing output stream.", e);
            }
        }
    }

    /**
     * Deletes symbols of caret returning (\r) and line ending (\n)
     * from the end of the line. Then it converts combinations of two operations +
     * and - due to mathematical rules. After these, the statement will be ready
     * for calculation.
     */
    private void normalizeStatement() {
        if (usersStatement.charAt(usersStatement.length() - 1) == '\n' &&
                usersStatement.charAt(usersStatement.length() - 2) == '\r') {
            log.debug("\\n and \\r is detected in the end of the statement. Deleting them.");
            usersStatement = usersStatement.substring(0, usersStatement.length() - 2);
        }
        if (usersStatement.charAt(usersStatement.length() - 1) == '\n') {
            log.debug("\\n and is detected in the end of the statement. Deleting it.");
            usersStatement = usersStatement.substring(0, usersStatement.length() - 1);
        }
        usersStatement = ConvertOperations.convert(usersStatement);
    }

    /**
     * Gets a statement from the input stream and sends it to answer searcher.
     */
    private void getAndProcessStatement() {
        boolean calculationIsNeeded = true;
        try {
            // Preparing for reading statement from input stream
            log.debug("Initializing buffer.");
            byte[] buf = new byte[BUFFER_SIZE];
            log.debug("Reading from input stream.");
            // Reading and saving statement
            int length = is.read(buf);
            usersStatement = new String(buf, OFFSET, length);
            // Preparing the statement for calculation
            normalizeStatement();
            if (log.isInfoEnabled() && !usersStatement.equalsIgnoreCase(EXIT_COMMAND)) {
                log.info("Statement is " + usersStatement);
            }
            if (log.isDebugEnabled()) {
                log.debug("received from input stream: " + usersStatement + ".");
            }
            if (usersStatement.equals(EXIT_COMMAND)) {
                calculationIsNeeded = false;
            }
            // calculationIsNeeded = false only when the exit command was received from input stream
            if (calculationIsNeeded) {
                try {
                    // Calculating statement
                    double result = answerSearcher.getAnswer(usersStatement, new Calculator(), new Operations(), cache);
                    // Formatting the answer
                    DecimalFormat doubleFormat = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
                    String answer = doubleFormat.format(result);
                    if (log.isInfoEnabled()) {
                        log.info("Result is " + answer);
                    }
                    log.debug("Sending an answer to the output stream.");
                    // Sending the answer to the output stream
                    ClientUserInterface.sendMessage(answer, os);
                    log.debug("Sending an answer to the server's console.");
                    // Sending the answer to the server's console,
                    ServerUserInterface.sendMessageToConsole(String.valueOf(result), true);
                } catch (StatementErrorException e) {
                    try {
                        log.error("Statement error.");
                        os.write(InterfaceMessages.STATEMENT_ERROR.toString().getBytes());
                        ServerUserInterface.sendMessageToConsole("Statement error.", true);
                    } catch (IOException e1) {
                        log.error(e1);
                        e1.printStackTrace();
                    }
                } catch (DivisionByZeroInDoubleException e) {
                    log.error("Division by zero.");
                    try {
                        os.write(InterfaceMessages.STATEMENT_ERROR.toString().getBytes());
                        ServerUserInterface.sendMessageToConsole(InterfaceMessages.DIVISION_BY_ZERO_EXCEPTION_ANSWER.toString() + e, true);
                    } catch (IOException e1) {
                        log.error("Error in writing to the output stream.");
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error reading input stream.");
        } catch (Exception e) {
            log.error("Exception.", e);
        } finally {
            // saving cache and closing streams
            try {
                log.debug("Clothing output stream.");
                os.close();
                log.debug("Output stream closed successfully.");
                log.debug("Clothing input stream.");
                is.close();
                log.debug("Input stream clothed successfully.");
                connectionsCounter.disconnect();
            } catch (IOException e) {
                log.error("Error in clothing stream.");
            }
        }
    }
}