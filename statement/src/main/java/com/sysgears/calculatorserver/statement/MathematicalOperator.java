package com.sysgears.calculatorserver.statement;

/**
 * Contains String representation
 * of operators plus, minus, division, multiply and a decimal separator.
 * toString method is overridden and returns the string representation of an operation.
 * By editing these representations, the quick syntax control of mathematical statements is available.
 */
public enum MathematicalOperator {

    MULTIPLY ("x"),
    DIVISION ("/"),
    PLUS ("+"),
    MINUS ("-"),
    POINT (".");

    /**
     * The string representation of an operation.
     */
    private String stringRepresentation;


    MathematicalOperator(String str) {
        stringRepresentation=str;
    }

    /**
     * Returns the string representation of an operation.
     *
     * @return String representation of an operation
     */
    public String toString () {

        return stringRepresentation;
    }
}
