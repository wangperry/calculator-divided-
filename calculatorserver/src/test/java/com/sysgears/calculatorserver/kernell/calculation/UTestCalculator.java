package com.sysgears.calculatorserver.kernell.calculation;

import com.sysgears.calculatorserver.exceptions.CalculatorException;
import com.sysgears.calculatorserver.exceptions.StatementErrorException;
import com.sysgears.calculatorserver.kernell.Operations;
import com.sysgears.calculatorserver.kernell.cache.Cache;
import com.sysgears.calculatorserver.kernell.cache.ICache;
import com.sysgears.calculatorserver.kernell.calculation.stubs.CalculatorStub;
import org.easymock.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.easymock.EasyMock.*;

/**
 * Tests the Calculator class
 */
public class UTestCalculator {

    @DataProvider(name = "ForSubCalculate")
    public Object[][] forSubCalculate() {

        return new Object[][]{
                {"5x8"},
                {"40"},
                {"80/2"}};
    }

    @DataProvider(name = "ForCalculate")
    public Object[][] forCalculate() {

        return new Object[][]{
                {"2+4"},
        };
    }

    @DataProvider(name = "validateRegExpTest")
    public Object[][] forValidateRegExp() {

        return new Object[][]{
                {"(2+3)+17/8)11-(7"},
                {"0"},
                {"5656+55-44x6569"}
        };
    }

    @Test
    public void bracketsCalculateTest() throws Exception {
        String statement = "(2+2)";
        AnswerSearcher answerSearcher = EasyMock.createMock(AnswerSearcher.class);
        Operations operations = EasyMock.createMock(Operations.class);
        Cache cache = EasyMock.createMock(Cache.class);
        expect(answerSearcher.getAnswer(anyObject(String.class),
                anyObject(Calculator.class),
                anyObject(Operations.class),
                anyObject(Cache.class))).andStubReturn(4.0);
        EasyMock.replay(answerSearcher);
        final Method bracketsCalculate = Calculator.class.getDeclaredMethod
                ("bracketsCalculate", String.class, AnswerSearcher.class, Operations.class, ICache.class);
        bracketsCalculate.setAccessible(true);
        double testResult = (Double) bracketsCalculate.invoke
                (new Calculator(), statement, answerSearcher,
                        operations, cache);
        Assert.assertEquals(4.0, testResult);
        EasyMock.verify(answerSearcher);
    }

    @Test(dataProvider = "ForSubCalculate")
    public void subCalculateTest(final String statement) throws Exception {
        Operations operations = EasyMock.createMock(Operations.class);
        expect(operations.multiply(5, 8)).andStubReturn(40.0);
        expect(operations.divide(80, 2)).andStubReturn(40.0);
        EasyMock.replay(operations);
        Cache cache = EasyMock.createMock(Cache.class);
        AnswerSearcher answerSearcher = EasyMock.createMock(AnswerSearcher.class);
        final Method subCalculate = Calculator.class.getDeclaredMethod ("subCalculate",
                String.class,
                Operations.class,
                Calculator.class,
                AnswerSearcher.class,
                ICache.class);
        subCalculate.setAccessible(true);
        Double testResult = (Double) subCalculate.invoke
                (new Calculator(), statement, operations, new CalculatorStub(),
                        answerSearcher, cache);
        Assert.assertEquals(testResult, 40.0);
    }

    /**
     * Tests the calculate method isolated from all private methods in class.
     *
     * @param statementFromUser arguments from dataProvider
     * @throws Exception any exception
     */
    @Test(dataProvider = "ForCalculate")
    public void calculateTest(final String statementFromUser) throws Exception {
        Cache cache = EasyMock.createMock(Cache.class);
        Operations operations = EasyMock.createMock(Operations.class);
        AnswerSearcher answerSearcher = EasyMock.createMock(AnswerSearcher.class);
        answerSearcher.getAnswer(eq("+4"),
                anyObject(Calculator.class),
                anyObject(Operations.class),
                anyObject(Cache.class));
        expectLastCall().andStubReturn(4.0);
        expect(operations.add(2, 4)).andStubReturn(6.0);
        EasyMock.replay(operations);
        EasyMock.replay(answerSearcher);
        Assert.assertEquals(6.0, new Calculator().calculate
                (statementFromUser, operations, new CalculatorStub(),
                        answerSearcher, cache));
        EasyMock.verify(operations);
        EasyMock.verify(answerSearcher);
    }

    @Test(expectedExceptions = StatementErrorException.class)
    public void calculateStatementErrorExceptionTest() throws CalculatorException {
        new Calculator().calculate("foo",
                createMock(Operations.class),
                createMock(Calculator.class),
                createMock(AnswerSearcher.class),
                createMock(Cache.class));
    }

    @Test
    public void findClothingBracketTest() throws Exception {
        final Method findClothingBracket = Calculator.class.getDeclaredMethod
                ("findClothingBracket", String.class, int.class);
        findClothingBracket.setAccessible(true);
        int testResult = (Integer) findClothingBracket.invoke(new Calculator(), "(15+7*(11-9)+1)", 6);
        Assert.assertEquals(11, testResult);
    }

    @Test(expectedExceptions = InvocationTargetException.class)
    public void findClothingBracketTestIllegalArgument() throws Exception {
        final Method findClothingBracket = Calculator.class.getDeclaredMethod
                ("findClothingBracket", String.class, int.class);
        findClothingBracket.setAccessible(true);
        int testResult = (Integer) findClothingBracket.invoke(new Calculator(), "15+7*11-9+1", 6);
        Assert.assertEquals(11, testResult);
    }

    @Test(dataProvider = "validateRegExpTest")
    public void validateRegExpTest(final String statement) throws Exception {
        final Method validateRegExp = Calculator.class.getDeclaredMethod("validateRegExp", String.class);
        validateRegExp.setAccessible(true);
        Assert.assertEquals(true, validateRegExp.invoke(new Calculator(), statement));
    }
}

