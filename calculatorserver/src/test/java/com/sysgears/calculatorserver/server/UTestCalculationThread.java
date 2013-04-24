package com.sysgears.calculatorserver.server;

import com.sysgears.calculatorserver.server.stubs.AnswerSearcherStub;
import com.sysgears.calculatorserver.server.stubs.CacheStub;
import com.sysgears.calculatorserver.server.stubs.ConnectionCounterStub;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UTestCalculationThread {

    @DataProvider(name = "process statement")
    public Object[][] providerProcessStatement() {

        return new Object[][]{
                {"2+2"},
                {"foo"}};
    }

    @Test(dataProvider = "process statement")
    public void testProcessStatement(String statement) throws Exception {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        CalculationThread calculationThreadWithStatement = new CalculationThread(null, pipedOutputStream, statement,
                new AnswerSearcherStub(), new CacheStub(), new ConnectionCounterStub());
        final Method processStatement = CalculationThread.class.getDeclaredMethod("processStatement");
        processStatement.setAccessible(true);
        processStatement.invoke(calculationThreadWithStatement);
        byte[] buff = new byte[1024];
        int byteCounter = pipedInputStream.read(buff, 0, pipedInputStream.available());
        String result = new String(buff, 0, byteCounter);
        if (statement.equals("2+2")) {
            Assert.assertEquals("4", result);
        } else {
            Assert.assertEquals("Statement error\n", result);
        }
    }

    @Test(dataProvider = "process statement")
    public void testGetAndProcessStatement(String statement) throws Exception {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        PipedOutputStream pipeSendsStatement = new PipedOutputStream();
        PipedInputStream pipeReceiveStatement = new PipedInputStream(pipeSendsStatement);
        pipeSendsStatement.write(statement.getBytes());
        CalculationThread calculationThreadWithIS = new CalculationThread(pipeReceiveStatement, pipedOutputStream, null,
                new AnswerSearcherStub(), new CacheStub(), new ConnectionCounterStub());
        final Method getAndProcessStatement = CalculationThread.class.getDeclaredMethod("getAndProcessStatement");
        getAndProcessStatement.setAccessible(true);
        getAndProcessStatement.invoke(calculationThreadWithIS);
        byte[] buff = new byte[1024];
        int byteCounter = pipedInputStream.read(buff, 0, pipedInputStream.available());
        String result = new String(buff, 0, byteCounter);
        if (statement.equals("2+2")) {
            Assert.assertEquals("4", result);
        } else {
            Assert.assertEquals("Statement error\n", result);
        }
    }

    @DataProvider(name = "normalizing statement")
    public Object[][] normalizingStatement() {

        return new Object[][] {
                {1,"2++2\n"},
                {1,"2--2"},
                {2,"2+-2\r\n"},
                {2,"2-+2"},
                {3, "+-2--2"},
                {3, "-2+2"}};
    }

    @Test(dataProvider = "normalizing statement")
    public void testNormalizeStatement(final int variant, final String statement) throws Exception {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        final Method normalizeStatement = CalculationThread.class.getDeclaredMethod("normalizeStatement");
        normalizeStatement.setAccessible(true);
        CalculationThread calculationThread = new CalculationThread(null, pipedOutputStream, statement,
                new AnswerSearcherStub(), new CacheStub(), new ConnectionCounterStub());
        final Field usersStatement = CalculationThread.class.getDeclaredField("usersStatement");
        usersStatement.setAccessible(true);
        normalizeStatement.invoke(calculationThread);
        String result = String.valueOf(usersStatement.get(calculationThread));
        switch (variant) {
            case 1:
                Assert.assertEquals("2+2", result);
                break;
            case 2:
                Assert.assertEquals("2-2", result);
                break;
            case 3:
                Assert.assertEquals("-2+2", result);
        }
    }
}
