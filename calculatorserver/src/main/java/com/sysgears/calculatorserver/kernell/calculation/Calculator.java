package com.sysgears.calculatorserver.kernell.calculation;

import com.sysgears.calculatorserver.exceptions.DivisionByZeroInDoubleException;
import com.sysgears.calculatorserver.exceptions.StatementErrorException;
import com.sysgears.calculatorserver.kernell.Operations;
import com.sysgears.calculatorserver.kernell.cache.ICache;
import com.sysgears.calculatorserver.messagesAndCommands.InterfaceMessages;
import com.sysgears.calculatorserver.statement.MathematicalOperator;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains methods to calculate a mathematical statement that is represented in string.
 */
public class Calculator {

    /**
     * Class' logger.
     */
    private static Logger log = Logger.getLogger(Calculator.class);

    /**
     * Tries to calculate the mathematical statement. If the statement is invalid, it throws exception.
     *
     * @param statementFromUser mathematical statement to calculate
     * @param operations        contains arithmetic operations
     * @param calculator        is needed to calculate statements with multiplying and brackets
     * @param answerSearcher    an AnswerSearcher object is needed to find answers for parts of large statements
     * @param cache             a cache object is needed to find answers for parts of large statements
     * @return the result of calculation
     * @throws StatementErrorException when the statementFromUser does not contain
     *                                 a correct mathematical statement
     * @throws DivisionByZeroInDoubleException
     *                                 when tries to divide by number that trends to zero
     */
    public double calculate(final String statementFromUser,
                            final Operations operations,
                            final Calculator calculator,
                            final AnswerSearcher answerSearcher,
                            final ICache cache)
            throws StatementErrorException, DivisionByZeroInDoubleException {
        log.debug("Calculating " + statementFromUser);
        // Checks if the statementFromUser is a correct mathematical statement
        if (!validate(statementFromUser)) {
            throw new StatementErrorException(InterfaceMessages.STATEMENT_ERROR.toString());
        }
        String statement = statementFromUser;
        if (!statementFromUser.matches("(\\+|\\-).*")) {
            statement = "+" + statement;
        }
        String doublePattern = ("(\\-|\\+)?\\d+(\\.\\d+)?((x|/)\\d+(\\.\\d+)?)*");
        if (statement.matches(doublePattern)) {

            // if the statement contains only multiplies, divisions and/or statements is brackets.
            return calculator.subCalculate(statement, operations, this, answerSearcher, cache);
        }
        double result = 0.0;
        for (int i = 1; i < statement.length(); i++) {
            // skipping statements in brackets
            if (statement.charAt(i) == '(') {
                i = findClothingBracket(statement, i) + 1;
            }
            // searching for plus or minus operation.
            if (statement.substring(i, statement.length()).matches("(\\" + MathematicalOperator.PLUS.toString() + "|\\" +
                    MathematicalOperator.MINUS.toString() + ").*") ||
                    i == statement.length()) {
                //adding the subStatement before the plus or minus operation and the rest of the statement.
                //The subCalculate method calculates this subStatement
                result = operations.add(calculator.subCalculate(statement.substring(0, i), operations, this, answerSearcher, cache),
                        (i == statement.length() ? 0.0 : answerSearcher.getAnswer(statement.substring(i), this, operations, cache)));
                break;
            }
        }
        log.debug("Calculating of " + statementFromUser + ". Result is: " + result);

        return result;
    }

    /**
     * Calculates statements that contains operations *, / and brackets.
     *
     * @param statement      the statement to calculate
     * @param operations     is needed in arithmetic operations
     * @param calculator     is needed to calculate statements in brackets
     * @param answerSearcher an AnswerSearcher object is needed to find answers for parts of large statements
     * @param cache          a cache object is needed to find answers for parts of large statements
     * @return the result of calculation
     * @throws StatementErrorException when the statementFromUser does not contain
     *                                 a correct mathematical statement
     * @throws DivisionByZeroInDoubleException
     *                                 when tries to divide by number that trends to zero
     */
    private double subCalculate(final String statement,
                                final Operations operations,
                                final Calculator calculator,
                                final AnswerSearcher answerSearcher,
                                final ICache cache)
            throws StatementErrorException, DivisionByZeroInDoubleException {
        if (statement.matches("(\\-|\\+)?\\d+(\\.\\d+)?")) {
            // If the subStatement is a number, then returns it
            return Double.parseDouble(statement);
        }
        double result = 0.0;
        for (int i = 0; i < statement.length(); i++) {
            // skipping statements in brackets
            if (statement.charAt(i) == '(') {
                i = findClothingBracket(statement, i) + 1;
            }
            // searching for multiply or division operation.
            if (statement.substring(i, statement.length()).matches(MathematicalOperator.MULTIPLY.toString() + ".*") ||
                    i == statement.length()) {
                //multiplying the subSubStatement before the multiply operation and the rest of the subStatement.
                //The calculateBrackets method calculates subSubStatement
                result = operations.multiply(calculator.bracketsCalculate(statement.substring(0, i),
                        answerSearcher, operations, cache),
                        (i == statement.length() ? 1.0 : calculator.subCalculate(statement.substring(i + 1),
                                operations, this, answerSearcher, cache)));
                break;
            } else if (statement.substring(i, statement.length()).matches(MathematicalOperator.DIVISION.toString() + ".*") ||
                    i == statement.length()) {
                //dividing the subSubStatement before the division operation and the rest of the subStatement.
                //The calculateBrackets method calculates subSubStatement
                result = operations.divide(calculator.bracketsCalculate(statement.substring(0, i), answerSearcher, operations, cache),
                        (i == statement.length() ? 1.0 : calculator.subCalculate(statement.substring(i + 1), operations, this, answerSearcher, cache)));
                break;
            }
        }

        return result;
    }

    /**
     * Calculates statements in brackets.
     *
     * @param statement      the statement to calculate
     * @param answerSearcher an AnswerSearcher object is needed to find answers for parts of large statements
     * @param operations     is needed in arithmetic operations
     * @param cache          a cache object is needed to find answers for parts of large statements
     * @return the calculated statement
     * @throws StatementErrorException when the statementFromUser does not contain
     *                                 a correct mathematical statement
     * @throws DivisionByZeroInDoubleException
     *                                 when tries to divide by number that trends to zero
     */
    private double bracketsCalculate(final String statement,
                                     final AnswerSearcher answerSearcher,
                                     final Operations operations,
                                     final ICache cache)
            throws StatementErrorException, DivisionByZeroInDoubleException {
        if (statement.matches("(\\-|\\+)?\\d+(\\.\\d+)?")) {

            // If the statement is a number, then returns it
            return Double.parseDouble(statement);
        }
        //Calculating the statement in brackets
        double result = answerSearcher.getAnswer(statement.substring(statement.indexOf('(') + 1, statement.lastIndexOf(')')), this, operations, cache);
        if (statement.charAt(0) == '-') {
            result *= -1;
        }

        return result;
    }

    /**
     * Looks for a clothing bracket in statement that refers to the opening bracket at bracketPosition.
     *
     * @param statement       a statement with brackets
     * @param bracketPosition character position in statement with an opening bracket
     * @return character position in statement with a clothing bracket
     *         that refers to the opening bracket at bracketPosition.
     * @throws StatementErrorException when there is an error in using brackets
     */
    private int findClothingBracket(final String statement,
                                    final int bracketPosition)
            throws StatementErrorException {
        // index of closing bracket
        int result = -1;
        /*
         * If the opening bracket is found, counter++,
         * if the closing bracket is found, counter--
         * if counter becomes 0 after founding the opening bracket,
         * it means that we have found the closing bracket for the first
         * opening one.
         * If counter becomes <0, it means that there is an error in arguments.
         * if the counter is not 0 after searching, it means that there is an error
         * in using brackets in the statement.
         */
        int counter = 0;
        for (int i = bracketPosition; i < statement.length(); i++) {
            if (statement.charAt(i) == '(') {
                counter++;
            }
            if (statement.charAt(i) == ')') {
                counter--;
                if (counter == 0) {
                    result = i;
                    break;
                }
                if (counter < 0) {
                    throw new StatementErrorException(InterfaceMessages.BRACKETS_ERROR.toString());
                }
            }
        }
        if (counter != 0) {
            throw new StatementErrorException(InterfaceMessages.BRACKETS_ERROR.toString());
        }
        if (result == -1) {
            throw new IllegalArgumentException("No brackets");
        }

        return result;
    }

    /**
     * Checks if the uncheckedStatement contains the correct mathematical statement.
     *
     * @param uncheckedStatement the statement to validate
     * @return true if the uncheckedStatement contains the correct mathematical statement
     */
    private boolean validate(final String uncheckedStatement) {
        boolean result = true;
        if (uncheckedStatement.equals("+") ||
                !validateRegExp(uncheckedStatement)) {
            result = false;
        }

        return result;
    }

    /**
     * Checks if the uncheckedStatement contains the right mathematical statement.
     * This method does not check the uncheckedStatement for correctness in using brackets.
     *
     * @param uncheckedStatement a statement to check
     * @return true if the uncheckedStatement contains the right mathematical statement
     */
    private boolean validateRegExp(final String uncheckedStatement) {
        boolean result = true;
        String patternInString = "(\\" + MathematicalOperator.MINUS.toString() + "|" + "\\" + MathematicalOperator.PLUS.toString() + ")?(\\d+" +
                "(\\" + MathematicalOperator.POINT.toString() + "?\\d+)?)?((\\" + MathematicalOperator.PLUS.toString() +
                "|\\" + MathematicalOperator.MINUS.toString() + "|" + MathematicalOperator.DIVISION.toString() + "|" +
                MathematicalOperator.MULTIPLY.toString() + "|\\(|\\)){1,3}(\\(|\\))?" +
                "(\\" + MathematicalOperator.PLUS.toString() + "|\\" + MathematicalOperator.MINUS.toString() + ")?" +
                "(\\d+(\\" + MathematicalOperator.POINT.toString() + "?\\d+)?)){0,15}\\)?";
        Pattern patternBeginningEnd = Pattern.compile(patternInString);
        Matcher matcher = patternBeginningEnd.matcher(uncheckedStatement);
        if (!matcher.matches()) {

            result = false;
        }

        return result;
    }
}