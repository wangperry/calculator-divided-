package com.sysgears.calculatorserver.kernell.cache;

import com.sysgears.calculatorserver.kernell.cache.stubs.ParametersStub;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.*;
import java.util.LinkedHashMap;

/**
 * Testing methods from SaveLoadCache class.
 */
public class UTestSaveLoadCache {

    // Testing object.
    private SaveLoadCache saveLoadCache;

    private LinkedHashMap<String, Double> testMap;

    private File testFile;

    @BeforeClass
    public void createTestFile() throws IOException {
        File emptyTestFile = new File(ParametersStub.TEST_FILE);
        if (!emptyTestFile.exists()) {
            emptyTestFile.createNewFile();
        }
    }
    
    @BeforeMethod
    public void beforeMethod() throws IOException {
        saveLoadCache = new SaveLoadCache();
        boolean generatorFlag = true;
        testFile = null;
        int fileNameSuffix = 0;
        while (generatorFlag) {
            testFile = new File("/home/eugene/test" + fileNameSuffix + ".cache");
            if (!testFile.exists()) {
                generatorFlag = false;
            } else fileNameSuffix++;
        }
        testFile.createNewFile();
        testMap = new LinkedHashMap<String, Double>(Cache.MAP_CAPACITY, Cache.LOAD_FACTOR, Cache.ACCESS_ORDER);
        testMap.put("2+2", 4.0);
        testMap.put("3x8", 24.0);

    }

    @Test
    public void testWriteFile() throws Exception {
        saveLoadCache.writeFile(testFile, testMap);
        testMap.clear();
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(testFile));
        testMap = (LinkedHashMap<String, Double>) is.readObject();
        Assert.assertEquals(2, testMap.size());
    }

    @Test
    public void testLoadFile() throws Exception {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(testFile));
        os.writeObject(testMap);
        testMap.clear();
        testMap = saveLoadCache.loadFile(testFile);
        Assert.assertEquals(2, testMap.size());
    }

    @AfterMethod
    public void afterMethod() {
        testFile.delete();
    }
}
