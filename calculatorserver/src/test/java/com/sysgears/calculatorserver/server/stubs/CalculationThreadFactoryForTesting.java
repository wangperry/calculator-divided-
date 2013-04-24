package com.sysgears.calculatorserver.server.stubs;

import com.sysgears.calculatorserver.kernell.calculation.AnswerSearcher;
import com.sysgears.calculatorserver.server.CalculationThreadFactory;
import com.sysgears.calculatorserver.server.ConnectionsCounter;
import com.sysgears.calculatorserver.service.Parameters;
import org.easymock.EasyMock;

import java.util.concurrent.ExecutorService;

public class CalculationThreadFactoryForTesting extends CalculationThreadFactory{

    public CalculationThreadFactoryForTesting(Parameters parameters) {
        super();
        this.parameters=parameters;
        pool = EasyMock.createMock(ExecutorService.class);
        answerSearcher = new AnswerSearcher();
        connectionsCounter = new ConnectionsCounter();
    }
}
