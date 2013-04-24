package com.sysgears.calculatorserver.service;

import com.sysgears.calculatorserver.kernell.cache.Cache;
import com.sysgears.calculatorserver.kernell.cache.ICache;
import com.sysgears.calculatorserver.kernell.cache.SaveLoadCache;
import com.sysgears.calculatorserver.messagesAndCommands.CommandLineCommands;
import com.sysgears.calculatorserver.messagesAndCommands.InterfaceMessages;
import com.sysgears.calculatorserver.server.CalculationThreadFactory;
import com.sysgears.calculatorserver.userInterface.ServerUserInterface;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Contains an Entry point. Parses the command line parameters.
 */
public class Main {

    private final static Logger log = Logger.getLogger(Main.class);

    /**
     * Use --help parameter to get list of supported parameters
     *
     * @param args Command line parameters
     */
    public static void main(String[] args) {
        try {
            if (log.isInfoEnabled()) {
                log.info("*******************");
                log.info("Application started.");
                for (int i = 0; i < args.length; i++) {
                    log.info("Parameter " + i + 1 + ": " + args[i]);
                }
            }
            if (args.length == 0) {
                //If there is 0 parameters then the message to use --help shows
                ServerUserInterface.sendMessageToConsole(InterfaceMessages.NO_PARAMETERS.toString(), false);
            } else if (args[0].equalsIgnoreCase(CommandLineCommands.HELP.toString())) {
                // --help  Displays help to user
                ServerUserInterface.sendMessageToConsole(InterfaceMessages.HELP.toString(), false);
                log.info("The help information was shown.");
            } else if (args[0].equalsIgnoreCase(CommandLineCommands.CLEAR_CACHE.toString())) {
                // -cc Clears cache
                Parameters parameters = new Parameters();
                parameters.loadParameters();
                ICache cache = new Cache(parameters, new SaveLoadCache());
                cache.clearCache();
                ServerUserInterface.sendMessageToConsole("Cache is cleared.\n", false);
            } else if (args[0].equalsIgnoreCase(CommandLineCommands.SHOW_CACHE.toString())) {
                // -sc Displays cache
                Parameters parameters = new Parameters();
                parameters.loadParameters();
                ICache cache = new Cache(parameters, new SaveLoadCache());
                ServerUserInterface.sendMessageToConsole(cache.toString(), false);
                log.info("Cache was shown.");
            } else if (args[0].equalsIgnoreCase(CommandLineCommands.SERVERMODE.toString())) {
                // -servermode The server mode branch
                Parameters parameters = new Parameters();
                parameters.loadParameters();
                // No parameters in server mode
                if (args.length == 1) {
                    log.debug("Server mode started with no parameters from command line.");
                    parameters.printParameters();
                    ServerUserInterface.runUserInterface(parameters);
                }
                // Server mode with parameters
                if (args.length >= 3) {
                    log.info("Server mode started with some parameters from command line.");
                    boolean startServer = true;
                    for (int i = 1; i < args.length - 1; i = i + 2) {
                        if (i == args.length - 1) {
                            ServerUserInterface.sendMessageToConsole(InterfaceMessages.PARAMETER_ERROR.toString() + args[i], false);
                            break;
                        }
                        if (args[i].equalsIgnoreCase(CommandLineCommands.HOST.toString())) {
                            parameters.setHost(args[i + 1]);
                        } else if (args[i].equalsIgnoreCase(CommandLineCommands.PORT.toString())) {
                            parameters.setPort(args[i + 1]);
                        } else if (args[i].equalsIgnoreCase(CommandLineCommands.MAX_CONNECTIONS.toString())) {
                            parameters.setMaxConnections(args[i + 1]);
                        } else if (args[i].equalsIgnoreCase(CommandLineCommands.CACHE_FILE.toString())) {
                            startServer = parameters.setCacheFile(args[i + 1]);  //If a user does not want to overwrite the cache file, server will not start
                        } else if (args[i].equalsIgnoreCase(CommandLineCommands.CACHE_LIMIT.toString())) {
                            parameters.setCacheLimit(args[i + 1]);
                        } else {
                            ServerUserInterface.sendMessageToConsole(InterfaceMessages.PARAMETER_ERROR.toString() + args[i], false);
                        }
                    }
                    if (startServer) {
                        parameters.printParameters();
                        ServerUserInterface.runUserInterface(parameters);
                    }
                }
            } else if (args[0].equalsIgnoreCase(CommandLineCommands.FILE.toString())) {
                // -file The file branch
                Parameters parameters = new Parameters();
                parameters.loadParameters();
                File inFile = null;
                File outFile = null;
                boolean toCalculate = true;
                if (args.length == 5 || args.length == 7 || args.length == 9) {
                    for (int i = 1; i < args.length - 1; i = i + 2) {
                        if (args[i].equalsIgnoreCase(CommandLineCommands.INPUT_FILE.toString())) {
                            inFile = new File(args[i + 1]);
                        } else if (args[i].equalsIgnoreCase(CommandLineCommands.OUTPUT_FILE.toString())) {
                            outFile = new File(args[i + 1]);
                        } else if (args[i].equalsIgnoreCase(CommandLineCommands.CACHE_FILE.toString())) {
                            toCalculate = parameters.setCacheFile(args[i + 1]);
                        } else if (args[i].equalsIgnoreCase(CommandLineCommands.CACHE_LIMIT.toString())) {
                            parameters.setCacheLimit(args[i + 1]);
                        } else {
                            ServerUserInterface.sendMessageToConsole(InterfaceMessages.PARAMETER_ERROR.toString() + args[i], false);
                        }
                    }
                    if (inFile == null || outFile == null) {
                        ServerUserInterface.sendMessageToConsole(InterfaceMessages.INFILE_OUTFILE_NOT_SET.toString(), false);
                        toCalculate = false;
                    }
                    if (toCalculate) {
                        ICache cache = new Cache(parameters, new SaveLoadCache());
                        CalculationThreadFactory factory = new CalculationThreadFactory(parameters);
                        factory.createThread(inFile, outFile, cache);
                    }
                }
            } else if (args[0].equalsIgnoreCase(CommandLineCommands.STATEMENT.toString())) {
                // Direct statement calculation branch
                Parameters parameters = new Parameters();
                parameters.loadParameters();
                if (args.length == 2) {
                    ICache cache = new Cache(parameters, new SaveLoadCache());
                    CalculationThreadFactory factory = new CalculationThreadFactory(parameters);
                    factory.createThread(args[1], cache);
                }
                if (args.length == 4 || args.length == 6) {
                    boolean toCalculate = true;
                    for (int i = 2; i < args.length - 1; i = i + 2) {
                        if (args[i].equalsIgnoreCase(CommandLineCommands.CACHE_FILE.toString())) {
                            toCalculate = parameters.setCacheFile(args[i + 1]);
                        } else if (args[i].equalsIgnoreCase(CommandLineCommands.CACHE_LIMIT.toString())) {
                            parameters.setCacheLimit(args[i + 1]);
                        } else {
                            ServerUserInterface.sendMessageToConsole(InterfaceMessages.PARAMETER_ERROR.toString() + args[i], false);
                        }
                    }
                    if (toCalculate) {
                        ICache cache = new Cache(parameters, new SaveLoadCache());
                        CalculationThreadFactory factory = new CalculationThreadFactory(parameters);
                        factory.createThread(args[0], cache);
                    }
                }
            } else ServerUserInterface.sendMessageToConsole(InterfaceMessages.ERROR_IN_PARAMETERS.toString(), false);
            log.info("Application closed");
            log.info("*******************");
        } catch (Exception e) {
            log.error("Unexpected exception", e);
        }
    }
}