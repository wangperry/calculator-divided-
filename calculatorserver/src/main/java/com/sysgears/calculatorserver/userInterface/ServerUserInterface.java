package com.sysgears.calculatorserver.userInterface;

import com.sysgears.calculatorserver.messagesAndCommands.InterfaceMessages;
import com.sysgears.calculatorserver.server.Server;
import com.sysgears.calculatorserver.service.Parameters;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Contains methods that sends messages to the server's machine console.
 */
public class ServerUserInterface {

    /**
     * Class' logger.
     */
    private static Logger log = Logger.getLogger(ServerUserInterface.class);

    /**
     * The command that a user can input to stop the server.
     */
    private final static String STOP_SERVER_COMMAND = "exit";

    /**
     * Implements a main menu, that is proposed to a user who works on the server machine.
     *
     * @param parameters an object that contains program's settings
     */
    public static void runUserInterface(Parameters parameters) {
        log.debug("Initializing buffered reader");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int usersChoice;      // Contains the choice, that makes a user in the menu.
        log.info("Starting user's interface");
        for (; ; ) {
            System.out.println(InterfaceMessages.CLIENTS_MAIN_MENU.toString());
            try {
                log.debug("Reading user's choice");
                usersChoice = Integer.parseInt(br.readLine());
            } catch (Exception e) {
                System.out.println(InterfaceMessages.CLIENTS_MAIN_MENU_ERROR_VALUE.toString());
                log.info("User's choice reading error.");
                continue;
            }
            if (log.isInfoEnabled()) {
                log.info("User's choice: " + usersChoice);
            }
            if (usersChoice == 1) { //1. Run server
                runServer(parameters);
            } else if (usersChoice == 2) {   //2. Exit
                System.out.println(InterfaceMessages.SERVER_GOODBYE_MESSAGE.toString());
                break;
            } else {     //If user types an incorrect choice
                log.info("User's choice error.");
                System.out.println(InterfaceMessages.SERVER_INCORRECT_MENU_CHOICE.toString());
            }
        }
    }

    /**
     * Sends a message to server's console.
     *
     * @param message      A message to be sent to server's console
     * @param isSentToUser A flag, that shows if the message duplicates one, that was sent to client's machine
     */
    public static void sendMessageToConsole(String message, boolean isSentToUser) {
        if (isSentToUser) {
            System.out.println(InterfaceMessages.SERVER_LOG_PREFIX.toString() + "\"" + message + "\"");
        } else {
            System.out.println(message);
        }
    }

    /**
     * Provide a dialog with user. Asks if he/she wants to overwrite
     * a file.
     *
     * @return true if a user wants to overwrite the file, false otherwise
     */
    public static boolean overwriteFileDialog() {
        log.debug("Starting overwrite dialog");
        System.out.println(InterfaceMessages.OVERWRITE_FILE);
        log.debug("Initializing buffered reader.");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean result = false;
        log.debug("Starting endless cycle");
        while (true) {
            try {
                log.debug("Reading answer from user.");
                String answer = br.readLine();
                log.debug("Received from user: " + answer);
                if (answer.equalsIgnoreCase("n")) {
                    break;
                } else if (answer.equalsIgnoreCase("y")) {
                    result = true;
                    break;
                }
            } catch (IOException e) {
                log.error("IOError reading user's choice.");
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Returning: " + result);
        }

        return result;
    }

    /**
     * Starts and stops the server
     *
     * @param parameters an object that contains program's settings
     */
    private static void runServer(Parameters parameters) {
        log.debug("Initializing buffered reader.");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        log.debug("Initializing server.");
        Server server = new Server(parameters);
        log.debug("Starting server in a separate thread.");
        server.start();
        if (log.isInfoEnabled()) {
            log.info("Server started at port " + parameters.getPort());
        }
        System.out.println(InterfaceMessages.STOP_SERVER_GUIDE.toString());
        while (true) {
            try {
                log.debug("Reading command from the user.");
                String command = br.readLine();
                log.debug("Received from user: " + command);
                if (command.equalsIgnoreCase(STOP_SERVER_COMMAND)) {
                    server.turnOffServer();
                    System.out.println(InterfaceMessages.SERVER_IS_STOPPED.toString());
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("Server stopped.");
    }
}
