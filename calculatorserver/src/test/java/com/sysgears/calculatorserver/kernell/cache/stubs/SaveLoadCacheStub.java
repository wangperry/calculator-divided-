package com.sysgears.calculatorserver.kernell.cache.stubs;

import com.sysgears.calculatorserver.kernell.cache.SaveLoadCache;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class SaveLoadCacheStub extends SaveLoadCache {

    private static final String TEST_FILE = "/home/eugene/test_file";

    public void writeFile(final File cacheFile, Map<String, Double> cacheContainer) {
        if (cacheFile.getPath().equalsIgnoreCase(TEST_FILE) &&
                cacheContainer.size() == 2) {
            cacheContainer.put("Test passed", 1.0);
        }
    }
    
    public LinkedHashMap<String, Double> loadFile (final File cacheFile) {
        LinkedHashMap<String, Double> result = new LinkedHashMap<String, Double>(16,0.75f,true);
        if (cacheFile.getPath().equalsIgnoreCase(TEST_FILE)) {
            result.put("Test passed", 3.0);
        }

        return result;
    }
}
