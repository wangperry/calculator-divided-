package com.sysgears.calculatorserver.server.stubs;

import com.sysgears.calculatorserver.exceptions.StatementErrorException;
import com.sysgears.calculatorserver.kernell.Operations;
import com.sysgears.calculatorserver.kernell.cache.Cache;
import com.sysgears.calculatorserver.kernell.calculation.AnswerSearcher;
import com.sysgears.calculatorserver.kernell.calculation.Calculator;
import com.sysgears.calculatorserver.messagesAndCommands.InterfaceMessages;


public class AnswerSearcherStub extends AnswerSearcher {
    public double getAnswer (String statement, Calculator calculator, Operations operations, Cache cache) throws StatementErrorException {
        double result;
        if (statement.equals("2+2") ||
                statement.equals("-4+8")) {
            result = 4.0;
        } else {
            throw new StatementErrorException(InterfaceMessages.STATEMENT_ERROR.toString());
        }

        return result;
    }
}
