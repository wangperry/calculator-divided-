package com.sysgears.calculatorserver.server.stubs;

import com.sysgears.calculatorserver.server.ConnectionsCounter;


public class ConnectionCounterStub extends ConnectionsCounter {
    public synchronized void connect() {
    }

    public synchronized void disconnect(){
    }
}
