package com.sysgears.calculatorserver.kernell.calculation;

import com.sysgears.calculatorserver.exceptions.DivisionByZeroInDoubleException;
import com.sysgears.calculatorserver.exceptions.StatementErrorException;
import com.sysgears.calculatorserver.kernell.Operations;
import com.sysgears.calculatorserver.kernell.cache.Cache;
import com.sysgears.calculatorserver.kernell.cache.ICache;
import org.apache.log4j.Logger;

/**
 * Gets an answer for the mathematical statement. Looks for it in cache. If there is no
 * answer in cache, sends the statement to the calculation method.
 */
public class AnswerSearcher {

    /**
     * Class' logger.
     */
    private static Logger log = Logger.getLogger(AnswerSearcher.class);

    /**
     * Tries to find an answer in cache. If it is there, the method returns it.
     * If it is not - sends the statement to the calculation method.
     *
     * @param statement  a statement from the user
     * @param calculator contains the calculation method
     * @param operations is needed for arithmetic operations
     * @param cache      the Cache object (can be null)
     * @return the answer on the statement
     * @throws StatementErrorException  when the statement does not contain a correct
     *                                  mathematical statement
     * @throws IllegalArgumentException when the calculator object is null
     * @throws DivisionByZeroInDoubleException
     *                                  when tries to divide by number that trends to zero
     */
    public double getAnswer(final String statement,
                            final Calculator calculator,
                            final Operations operations,
                            final ICache cache)
            throws StatementErrorException, IllegalArgumentException, DivisionByZeroInDoubleException {

        log.debug("Searching answer for " + statement);
        double result;
        Double foundInCache = null;
        if (cache != null) {
            //foundInCache = cache.searchInCache(statement);
        }
        if (foundInCache == null) {
            log.debug("Result is not found in cache. Sent to calculator.");
            result = calculator.calculate(statement, operations, calculator, this, cache);
            if (cache != null) {
                //cache.addToCache(statement, result);
            }
        } else {
            log.debug("Result is found in cache.");
            result = foundInCache;
        }
        if (log.isDebugEnabled()) {
            log.debug("Returning result: " + result + ".");
        }

        return result;
    }
}
