package com.sysgears.calculatorserver.userInterface;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Contains methods that sends messages to the client machine through the socket and accepts it's answers.
 */
public class ClientUserInterface {

    /**
     * Class' logger.
     */
    private static Logger log = Logger.getLogger(ClientUserInterface.class);

    /**
     * Sends message to client through socket.
     *
     * @param message a message that would be sent to a client
     * @param os      output stream to send message through
     */
    public static void sendMessage(String message, OutputStream os) {
        try {
            os.write(message.getBytes());
        } catch (IOException e) {
            log.error("Error writing to output stream.");
        }
    }
}
