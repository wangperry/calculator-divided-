package com.sysgears.calculatorserver.kernell.calculation.stubs;

import com.sysgears.calculatorserver.kernell.Operations;
import com.sysgears.calculatorserver.kernell.cache.Cache;
import com.sysgears.calculatorserver.kernell.calculation.AnswerSearcher;
import com.sysgears.calculatorserver.kernell.calculation.Calculator;

public class CalculatorStub extends Calculator {
    private double bracketsCalculate(final String statement,
                                     final AnswerSearcher answerSearcher,
                                     final Cache cache) {

        return Double.parseDouble(statement);
    }

    private double subCalculate(final String statement,
                                final Operations operations,
                                final Calculator calculator,
                                final AnswerSearcher answerSearcher,
                                final Cache cache) {

        return Double.parseDouble(statement);
    }

    public double calculate(final String statement,
                            final Operations operations,
                            final Calculator calculator,
                            final AnswerSearcher answerSearcher,
                            final Cache cache) {
        return 25.0;
    }
}
