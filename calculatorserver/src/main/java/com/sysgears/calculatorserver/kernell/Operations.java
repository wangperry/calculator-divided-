package com.sysgears.calculatorserver.kernell;

import com.sysgears.calculatorserver.exceptions.DivisionByZeroInDoubleException;

/**
 * Contains static methods that implements operations that are supported by the calculator.
 */
public class Operations {

    /**
     * Initializes Operations object
     */
    public Operations() {
    }

    /**
     * Implements the addition operation.
     *
     * @param firstOperand  the first term
     * @param secondOperand the second term
     * @return the sum of the first and the second terms
     */
    public double add(final double firstOperand,
                      final double secondOperand) {

        return firstOperand + secondOperand;
    }

    /**
     * Implements the multiplication operation.
     *
     * @param firstOperand  the first multiplier
     * @param secondOperand the second multiplier
     * @return the multiplication of the first factor on the second
     */
    public double multiply(final double firstOperand,
                           final double secondOperand) {

        return firstOperand * secondOperand;
    }

    /**
     * Implements the division operation.
     *
     * @param firstOperand  the dividend
     * @param secondOperand the divider
     * @return the quotient
     * @throws DivisionByZeroInDoubleException
     *          if the divider trends to zero
     */
    public double divide(final double firstOperand,
                         final double secondOperand)
            throws DivisionByZeroInDoubleException {

        if (secondOperand < 0.000001 && secondOperand > -0.000001) {
            throw new DivisionByZeroInDoubleException(firstOperand, secondOperand);
        }
        return firstOperand / secondOperand;
    }
}