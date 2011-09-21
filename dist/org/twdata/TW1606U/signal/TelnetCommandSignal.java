package org.twdata.TW1606U.signal;

import org.werx.framework.bus.signals.BusSignal;

public class TelnetCommandSignal extends BusSignal{
    
    private byte command;
    
    public TelnetCommandSignal(byte command) {
        this.command = command;
    }
    
    public byte getCommand() {
        return command;
    }
}
