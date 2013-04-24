package com.sysgears.calculatorserver.kernell.cache;

import com.sysgears.calculatorserver.service.Parameters;
import com.sysgears.calculatorserver.userInterface.ServerUserInterface;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contains the cache of calculated statements.
 */
public class Cache implements ICache {

    /**
     * Class' logger.
     */
    private static Logger log = Logger.getLogger(Cache.class);

    /**
     * The LinkedHashMap max capacity.
     */
    static final int MAP_CAPACITY = 16;

    /**
     * The LinkedHashMap load factor witch sets
     * the time when the max capacity of LinkedHashMap
     * will be doubled. If the current capacity reaches
     * the value of MAX_CAPACITY*LOAD_FACTOR then this time
     * has come.
     */
    static final float LOAD_FACTOR = 0.75f;

    /**
     * Defines if an order of entries in LinkedHashMap
     * should go in an access order instead of insertion order.
     */
    static final boolean ACCESS_ORDER = true;

    /**
     * A container for the cache.
     */
    private Map<String, Double> cacheContainer;

    /**
     * An object that contains program's settings.
     */
    private Parameters parameters;

    /**
     * Is needed to save and load cache to/from file.
     */
    private SaveLoadCache saveLoadCache;

    /**
     * Constructor without parameters should not be used.
     */
    protected Cache() {
    }

    /**
     * Initializes an empty cache with loaded parameters.
     *
     * @param parameters    object with parameters
     * @param saveLoadCache is needed to save and load cache to/from file
     */
    public Cache(final Parameters parameters,
                 final SaveLoadCache saveLoadCache) {
        log.debug("Initializing cache.");
        cacheContainer = Collections.synchronizedMap
                (new LinkedHashMap<String, Double>(MAP_CAPACITY, LOAD_FACTOR, ACCESS_ORDER));
        this.parameters = parameters;
        this.saveLoadCache = saveLoadCache;
        loadCache();
    }

    /**
     * Searches the cache for a statement and returns the calculated value.
     * If the cache does not contain this statement, it returns null.
     *
     * @param statement the mathematical statement that must be found
     * @return calculated value or null if the statement is not in cache
     */
    public synchronized Double searchInCache(String statement) {

        return cacheContainer.get(statement);
    }

    /**
     * Adds an element to the cache.
     *
     * @param statement a mathematical statement
     * @param value     calculated value of the mathematical statement
     */
    public synchronized void addToCache(String statement, Double value) {
        cacheContainer.put(statement, value);
        deleteOldAccessedObjects();
        saveCache();
    }


    /**
     * Clears the cache and saves it to file, that is defined in properties.
     */
    public void clearCache() {
        log.info("Clearing cache.");
        cacheContainer.clear();
        saveCache();
    }

    @Override
    public String toString() {

        return cacheContainer.toString();
    }

    /**
     * Deletes old accessed entries when it's number is
     * greater then the limit that is set in parameters.
     *
     * @return true if any entry was deleted
     */
    private boolean deleteOldAccessedObjects() {
        boolean isSomethingDeleted = false;
        if (cacheContainer.size() >= parameters.getCacheLimit()) {
            log.debug("Cache is full. Deleting old accessed objects.");
            isSomethingDeleted = true;
            int difference = cacheContainer.size() - parameters.getCacheLimit();
            while (difference > 0) {
                cacheContainer.remove(cacheContainer.entrySet().iterator().next().getKey());
                difference--;
            }
        }

        return isSomethingDeleted;
    }
    /**
     * Saves cache to file.
     * The path to file is defined in properties.
     */
    private void saveCache() {
        File currentCacheFile = parameters.getCacheFile();
        if (log.isInfoEnabled()) {
            log.info("Saving cache to file " + currentCacheFile);
        }
        saveLoadCache.writeFile(currentCacheFile, cacheContainer);
    }

    /**
     * Loads cache from file that is defined in properties.
     */
    private void loadCache() {
        File currentCacheFile = parameters.getCacheFile();
        if (currentCacheFile.exists()) {
            cacheContainer = saveLoadCache.loadFile(currentCacheFile);
        } else {
            ServerUserInterface.sendMessageToConsole("Cache file not found.", false);
            log.warn("Cache file not found.");
        }
    }
}

