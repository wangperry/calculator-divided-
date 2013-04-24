package com.sysgears.calculatorserver.exceptions;

/**
 * Statement error exception representation. Must be thrown
 * when the statement does not contain a correct mathematical statement.
 */
public class StatementErrorException extends CalculatorException {

    private final String message;

    /**
     * Initializes object with a message.
     *
     * @param message an error message
     */
    public StatementErrorException(String message) {
        this.message = message;
    }

    /**
     * Returns the error message.
     *
     * @return an error message
     */
    @Override
    public String getMessage() {

        return message;
    }

    /**
     * Returns the error message.
     *
     * @return an error message
     */
    @Override
    public String toString() {

        return message;
    }
}
