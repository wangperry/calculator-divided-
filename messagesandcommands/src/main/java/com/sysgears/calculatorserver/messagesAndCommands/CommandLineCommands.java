package com.sysgears.calculatorserver.messagesAndCommands;

/**
 * Enumeration that contains string representations of commands that
 * a user can enter in command line.
 */

public enum CommandLineCommands {
    HELP("--help"),
    CLEAR_CACHE("-cc"),
    SHOW_CACHE("-sc"),
    SERVERMODE("-servermode"),
    FILE("-file"),
    PORT("-port"),
    HOST("-host"),
    MAX_CONNECTIONS("-maxconn"),
    CACHE_FILE("-cf"),
    CACHE_LIMIT("-cl"),
    INPUT_FILE("-if"),
    OUTPUT_FILE("-of"),
    STATEMENT("-st");

    /**
     * Contains the string representation of the command.
     */
    private String command;

    /**
     * Initializes the command object.
     * @param command string representation of the command
     */
    CommandLineCommands(String command) {
        this.command = command;
    }

    /**
     * Returns a string message which is associated with a command.
     */
    public String toString() {

        return command;
    }

}

