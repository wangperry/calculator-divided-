package com.sysgears.calculatorserver.server.stubs;

import com.sysgears.calculatorserver.kernell.cache.Cache;

public class CacheStub extends Cache {
    public String toString() {

        return "";
    }

    public Double searchInCache(String statement) {
        Double result;
        if (statement.equalsIgnoreCase("cache")) {
            result = 50.0;
        } else {
            result = null;
        }

        return result;
    }

    public void addToCache(String statement, Double value) {
    }

    public void saveCache() {
    }

}

