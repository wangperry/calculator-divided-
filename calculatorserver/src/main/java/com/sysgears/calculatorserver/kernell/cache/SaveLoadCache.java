package com.sysgears.calculatorserver.kernell.cache;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contains static methods that save and load
 * cache.
 */
public class SaveLoadCache {

    /**
     * Class' logger.
     */
    private static Logger log = Logger.getLogger(SaveLoadCache.class);

    /**
     * Output stream is used to write the cache to a file.
     */
    private ObjectOutputStream os;

    /**
     * Input stream is used to read the cache from a file.
     */
    private ObjectInputStream is;

    /**
     * Writes the cache object to cacheFile file.
     *
     * @param cacheFile      the file to write to
     * @param cacheContainer a LinkedHashMap object witch contains the cache
     */
    public synchronized void writeFile(final File cacheFile, final Map<String, Double> cacheContainer) {
        try {
            log.debug("Initializing output stream with file.");
            os = new ObjectOutputStream(new FileOutputStream(cacheFile));
            os.writeObject(cacheContainer);
            if (log.isDebugEnabled()) {
                log.debug("Cache saved to file "+cacheFile+". File size: "+cacheFile.length()+"b.");
            }
        } catch (IOException e) {
            log.error("IO Exception. ", e);
            e.printStackTrace();
        } finally {
            try {
                log.debug("Clothing output stream.");
                os.close();
                log.debug("Output stream clothed successfully.");
            } catch (IOException e) {
                log.error("Output stream closing error.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads cache from cacheFile file.
     *
     * @param cacheFile file from witch to download cache
     * @return a LinkedHashMap object witch contains the cache
     */
    public synchronized LinkedHashMap<String, Double> loadFile(final File cacheFile) {
        LinkedHashMap<String, Double> cacheContainer = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Initializing input stream from cache file " + cacheFile);
            }
            is = new ObjectInputStream(new FileInputStream(cacheFile));
            cacheContainer = (LinkedHashMap<String, Double>) is.readObject();
            log.debug("Cache is loaded from file successfully.");
        } catch (Exception e) {
            log.error("Error while loading cache file.", e);
        } finally {
            try {
                log.debug("Clothing input stream.");
                is.close();
                log.debug("Input stream closed successfully.");
            } catch (IOException e) {
                log.error("Input stream clothing error.", e);
            }
        }

        return cacheContainer;
    }
}
