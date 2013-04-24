package com.sysgears.calculatorserver.kernell.cache;

/**
 * Defines functionality of any cache class for the
 * Calculator.
 */
public interface ICache {

    /**
     * Searches the cache for the counted value
     * of the mathematical statement.
     *
     * @param statement mathematical statement
     * @return the calculated value or null if the
     *         entry for this statement was not found
     */
    public Double searchInCache(String statement);

    /**
     * Adds the entry of statement - value
     * to the cache.
     *
     * @param statement mathematical statement
     * @param value     the calculated value
     */
    public void addToCache(String statement, Double value);

    /**
     * Clears the cache.
     */
    public void clearCache();
}