package com.sysgears.calculatorserver.kernell.calculation;

import com.sysgears.calculatorserver.kernell.Operations;
import com.sysgears.calculatorserver.kernell.cache.Cache;
import com.sysgears.calculatorserver.kernell.cache.ICache;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;

/**
 * Tests {@link AnswerSearcher#getAnswer(String, Calculator,
 * com.sysgears.calculatorserver.kernell.Operations,
 * com.sysgears.calculatorserver.kernell.cache.ICache) AnswerSearcher.getAnswer()} method.
 */
public class UTestAnswerSearcher {
    @DataProvider(name = "dataForGetAnswer")
    public Object[][] dataForGetAnswer() {

        return new Object[][]{
                {"cache"},
                {"foo"},
        };
    }

    @Test(dataProvider = "dataForGetAnswer")
    public void testGetAnswer(final String statement) throws Exception {
        Calculator calculatorStub = EasyMock.createMock(Calculator.class);
        ICache cacheStub = EasyMock.createMock(Cache.class);
        expect(cacheStub.searchInCache("foo")).andStubReturn(null);
        cacheStub.addToCache("foo", 25.0);
        expectLastCall().asStub();
        expect(cacheStub.searchInCache("cache")).andStubReturn(50.0);
        expect(calculatorStub.calculate(eq("foo"),
                anyObject(Operations.class),
                anyObject(Calculator.class),
                anyObject(AnswerSearcher.class),
                anyObject(Cache.class))).andStubReturn(25.0);
        replay(cacheStub);
        replay(calculatorStub);
        if (statement.equals("cache")) {
            Assert.assertEquals(50.0,
                    new AnswerSearcher().getAnswer(
                            statement,
                            calculatorStub,
                            createMock(Operations.class),
                            cacheStub));
        } else {
            Assert.assertEquals(25.0,
                    new AnswerSearcher().getAnswer(
                            statement,
                            calculatorStub,
                            createMock(Operations.class),
                            cacheStub));
        }
    }
}

