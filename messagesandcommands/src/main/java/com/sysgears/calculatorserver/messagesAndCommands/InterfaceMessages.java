package com.sysgears.calculatorserver.messagesAndCommands;

/**
 * This enumeration contains all messages that the interface sends to users in server mode.
 */
public enum InterfaceMessages {
    CLIENT_GREETINGS("\n*************Hi! This is a Calculator server*************\n"),
    REQUEST_FROM_CLIENT("\nPlease, enter a statement you want to calculate or \"exit\": "),
    RECEIVED_FROM_CLIENT("Received from client: "),
    STATEMENT_ERROR("Statement error\n"),
    CLIENT_GOODBYE_MESSAGE("Thank you for using calculator! Have a a nice day :)\n"),
    CONNECTION_CLOSED("Connection closed. "),
    CONNECTION_ESTABLISHED("Connection established."),
    EXCEPTION_MESSAGE("An error was detected. Please, contact the developer.\n"),
    RESULT_IS("Result is: "),
    DIVISION_BY_ZERO_EXCEPTION_ANSWER("Calculation error. A division by zero was detected."),
    CLIENTS_MAIN_MENU("\n\t\tGreetings!\n" +
            "\tWelcome to the Calculator's Server\n" +
            "Please, enter an option you need\n" +
            "1. Run server\n" +
            "2. Exit"),
    CLIENTS_MAIN_MENU_ERROR_VALUE("Error value: 1 or 2 required. Try again, please."),
    SERVER_GOODBYE_MESSAGE("Thank you for using the Calculator's Server! Have a nice day."),
    SERVER_INCORRECT_MENU_CHOICE("\n\nYou have entered an incorrect choice. Please, try again"),
    SERVER_LOG_PREFIX("Sent to client: "),
    SERVER_IS_STARTED_MESSAGE("Server is started at port "),
    NUMBER_OF_CONNECTIONS("Number of connections: "),
    CONNECTIONS_LIMIT_MESSAGE("Please wait, while server will be ready to accept your request. \n"),
    ERROR_IN_PROPERTIES_FILE("Error in properties file. Default parameters are set."),
    PARAMETERS_SET("The following parameters are set:\n"),
    ADDRESS_ERROR("Address parameter error. Default IP parameter will be loaded.\n"),
    PORT_ERROR("Port parameter error. Default port parameter will be loaded.\n"),
    MAX_CONNECTIONS_ERROR("Maximum connections parameter error. Default value will be loaded.\n"),
    CACHE_LIMIT_ERROR("Cache limit parameter error. Default value will be loaded."),
    NO_PARAMETERS("\nParameters required. Use \"--help\" parameter to view information about parameters that are supported."),
    HELP("\nApplication options:\n\n" +
            "\tServer part:\n" +
            "\t\t" + CommandLineCommands.SERVERMODE.toString() + "\tstart server with parameters defined in properties file\n" +
            "\t\t\t" + CommandLineCommands.HOST.toString() + " <host name or IP>\tset host\n" +
            "\t\t\t" + CommandLineCommands.PORT.toString() + " <port's number>\tset port for listening\n" +
            "\t\t\t" + CommandLineCommands.MAX_CONNECTIONS.toString() + " <number>\t\tset the maximum number of connections at one time\n" +
            "\t\t\t" + CommandLineCommands.CACHE_FILE.toString() + " <path to file>\t\tset the path to cache file\n" +
            "\t\t\t" + CommandLineCommands.CACHE_LIMIT.toString() + " <number>\t\t\tset the cache limit (number of statements)\n\n" +
            "\tCalculating from files:\n" +
            "\t\t" + CommandLineCommands.FILE.toString() + " " + CommandLineCommands.INPUT_FILE.toString() + " <path> " + CommandLineCommands.OUTPUT_FILE.toString() + " <path>" +
            "\tCalculate statement from input file and put\n\t\t\t\t\t\t\t\t\tthe calculated value to the output file\n" +
            "\t\t\t" + CommandLineCommands.CACHE_FILE.toString() + " <path to file>\t\tset the path to cache file\n" +
            "\t\t\t" + CommandLineCommands.CACHE_LIMIT.toString() + " <number>\t\t\tset the cache limit (number of statements)\n\n" +
            "\tDirect calculation:\n" +
            "\t\t" + CommandLineCommands.STATEMENT.toString() + " <mathematical statement>\tCount the statement\n" +
            "\t\t\t" + CommandLineCommands.CACHE_FILE.toString() + " <path to file>\t\tset the path to cache file\n" +
            "\t\t\t" + CommandLineCommands.CACHE_LIMIT.toString() + " <number>\t\t\tset the cache limit (number of statements)\n\n"),
    OVERWRITE_FILE("File is already exists. Do you want to overwrite it? (Y/N)"),
    PARAMETER_ERROR("Parameter error: "),
    INFILE_OUTFILE_NOT_SET("Input or output file is not set."),
    BRACKETS_ERROR("Statement error: error in using brackets.\n"),
    ERROR_IN_PARAMETERS ("Parameters error. Use \"--help\" parameter to view information about parameters that are supported. \n"),
    STOP_SERVER_GUIDE("Type \"exit\" if you want to stop server. New connections will not be accepted."),
    SERVER_IS_STOPPED ("Server is stopped.");

    private String message;


    InterfaceMessages(String message) {
        this.message = message;
    }

    /**
     * Returns a string message which is associated with a situation.
     */
    public String toString() {

        return message;
    }

}