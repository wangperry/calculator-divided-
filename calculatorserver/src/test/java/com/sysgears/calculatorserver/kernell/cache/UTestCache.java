package com.sysgears.calculatorserver.kernell.cache;

import com.sysgears.calculatorserver.kernell.cache.stubs.ParametersStub;
import com.sysgears.calculatorserver.kernell.cache.stubs.SaveLoadCacheStub;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class UTestCache {

    private Field cacheContainer;

    private Map<String, Double> tempCacheContainer;

    private ICache cache;

    @BeforeClass
    public void beforeClass() throws NoSuchFieldException, IOException {
        cacheContainer = Cache.class.getDeclaredField("cacheContainer");
        cacheContainer.setAccessible(true);
    }
    @BeforeMethod
    public void beforeMethod() {
        tempCacheContainer = new LinkedHashMap<String, Double>(Cache.MAP_CAPACITY, Cache.LOAD_FACTOR, Cache.ACCESS_ORDER);
        cache = new Cache(new ParametersStub(), new SaveLoadCacheStub());
    }

    @Test
    public void testSearchInCache() throws Exception {
        tempCacheContainer.put("2+2", 4.0);
        cacheContainer.set(cache, tempCacheContainer);
        Assert.assertEquals(4.0, cache.searchInCache("2+2"));
    }

    @Test
    public void testAddToCacheAdd() throws Exception {
        cache.addToCache("2+2", 4.0);
        tempCacheContainer = (Map<String, Double>) cacheContainer.get(cache);
        Assert.assertEquals(4.0, tempCacheContainer.get("2+2"));
    }

    @Test
    public void testAddToCacheOverflow() throws Exception {
        cache.addToCache("2+2", 4.0);
        cache.addToCache("2+3", 5.0);
        cache.addToCache("2+4", 6.0);
        cache.searchInCache("2+2");
        cache.addToCache("2+5", 7.0);
        cache.addToCache("2+6", 8.0);
        tempCacheContainer = (Map<String, Double>) cacheContainer.get(cache);
        Assert.assertEquals(4.0, tempCacheContainer.get("2+2"));
        Assert.assertEquals(null, tempCacheContainer.get("2+3"));
    }


    @Test
    public void testSaveCache() throws Exception {
        Method saveCache = Cache.class.getDeclaredMethod("saveCache");
        saveCache.setAccessible(true);
        tempCacheContainer.put("2+2", 4.0);
        tempCacheContainer.put("2+3", 5.0);
        cacheContainer.set(cache, tempCacheContainer);
        saveCache.invoke(cache);
        tempCacheContainer = (Map<String, Double>) cacheContainer.get(cache);
        Assert.assertEquals(1.0, tempCacheContainer.get("Test passed"));
    }

    @Test
    public void testLoadCache() throws Exception {
        Method loadCache = Cache.class.getDeclaredMethod("loadCache");
        loadCache.setAccessible(true);
        loadCache.invoke(cache);
        tempCacheContainer = (Map<String, Double>) cacheContainer.get(cache);
        Assert.assertEquals(tempCacheContainer.get("Test passed"), 3.0);
    }

    @Test
    public void testClearCache() throws Exception {
        tempCacheContainer.put("2+2", 4.0);
        tempCacheContainer.put("2+3", 5.0);
        cacheContainer.set(cache, tempCacheContainer);
        cache.clearCache();
        tempCacheContainer = (Map<String, Double>) cacheContainer.get(cache);
        Assert.assertTrue(tempCacheContainer.isEmpty());
    }
}