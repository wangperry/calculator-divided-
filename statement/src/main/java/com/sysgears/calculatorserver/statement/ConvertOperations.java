package com.sysgears.calculatorserver.statement;

import org.apache.log4j.Logger;

/**
 * Contains a static method that converts combinations of operations.
 * For example, it converts +- to -, -- to + etc.
 */
public class ConvertOperations {

    /**
     * Class' logger.
     */
    private static Logger log = Logger.getLogger(ConvertOperations.class);

    /**
     * Converts combinations of two operations + and - due to mathematical rules.
     * For example, it converts +- to -, -- to + etc.
     *
     * @param statementFromUser a mathematical statement,that was sent by a client
     * @return the corrected mathematical statement
     */
    public static String convert(String statementFromUser) {
        StringBuilder statementInStringBuilder = new StringBuilder(statementFromUser);
        //Char representation of operator minus.
        char minusOperator = MathematicalOperator.MINUS.toString().charAt(0);
        //Char representation of operator plus.
        char plusOperator = MathematicalOperator.PLUS.toString().charAt(0);
        // Looking for double operations and correcting them
        for (int i = 0; i < statementInStringBuilder.length() - 1; i++) {
            if ((statementInStringBuilder.charAt(i) == plusOperator &&
                    statementInStringBuilder.charAt(i + 1) == plusOperator) ||
                    (statementInStringBuilder.charAt(i) == minusOperator &&
                            statementInStringBuilder.charAt(i + 1) == minusOperator)) {
                statementInStringBuilder.delete(i, i + 2);
                statementInStringBuilder.insert(i, plusOperator);
                log.debug("Double plus or double minus operator detected. Converting them to plus operator.");
            }
            if (statementInStringBuilder.charAt(i) == plusOperator &&
                    statementInStringBuilder.charAt(i + 1) == minusOperator ||
                    (statementInStringBuilder.charAt(i) == minusOperator &&
                            statementInStringBuilder.charAt(i + 1) == plusOperator)) {
                statementInStringBuilder.delete(i, i + 2);
                statementInStringBuilder.insert(i, minusOperator);
                log.debug("Minus and plus consecutive detected. Converting them to minus operator.");
            }
        }

        return statementInStringBuilder.toString();
    }
}
