package com.sysgears.calculatorserver.exceptions;

/**
 * This exception throws in {@link com.sysgears.calculatorserver.kernell.Operations#divide(double, double) divide}
 * method when it tries to divide by a number that trends to zero.
 * Accepts a dividend and a divider and returns them in the toString() method.
 */
public class DivisionByZeroInDoubleException extends CalculatorException {

    /**
     * A dividend.
     */
    private double dividend;

    /**
     * A divider.
     */
    private double divider;

    /**
     * Initializes a DivisionByZeroException object, and sends to it a dividend and a divider.
     *
     * @param dividend  the dividend
     * @param divider the divider
     */
    public DivisionByZeroInDoubleException(double dividend, double divider) {
        this.dividend = dividend;
        this.divider = divider;
    }

    /**
     * Returns the report about conditions (the dividend and the divider) under which the exception was thrown.
     *
     * @return report in String
     */
    @Override
    public String toString() {

        return ("\nfirst operand = " + dividend + "\nsecond operand = " + divider + "\n");
    }

    /**
     * Returns the report about conditions (the dividend and the divider) under which the exception was thrown.
     *
     * @return report in String
     */
    @Override
    public String getMessage() {

        return this.toString();
    }
}