package com.sysgears.calculatorserver.kernell.cache.stubs;

import com.sysgears.calculatorserver.service.Parameters;

import java.io.File;

public class ParametersStub extends Parameters {

    public static final String TEST_FILE = "/home/eugene/test_file";

    public File getCacheFile () {
        
        return new File(TEST_FILE);
    }

    public int getCacheLimit() {

        return 4;
    }
}
