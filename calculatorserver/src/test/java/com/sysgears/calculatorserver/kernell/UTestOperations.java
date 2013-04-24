package com.sysgears.calculatorserver.kernell;

import com.sysgears.calculatorserver.exceptions.DivisionByZeroInDoubleException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UTestOperations {

    @Test
    public void testAdd() throws Exception {
        Assert.assertEquals(0.0, new Operations().add(-100, 100));
        Assert.assertEquals(0.0, new Operations().add(0,0));
    }

    @Test
    public void testMultiply() throws Exception {
        Assert.assertEquals(0.0, new Operations().multiply(100500,0));
        Assert.assertEquals(-1.0, new Operations().multiply(1,-1));
    }

    @Test(expectedExceptions = DivisionByZeroInDoubleException.class)
    public void testDivideZeroException() throws Exception {
        new Operations().divide(100500,0);
    }

    @Test
    public void testDivide() throws Exception {
        Assert.assertEquals(0.0, new Operations().divide(0,100500));
        Assert.assertEquals(2.5, new Operations().divide(5,2));
    }
}
