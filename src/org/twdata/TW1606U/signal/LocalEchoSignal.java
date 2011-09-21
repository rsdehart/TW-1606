package org.twdata.TW1606U.signal;

import org.werx.framework.bus.signals.BusSignal;

public class LocalEchoSignal extends BusSignal{
    
    private boolean echo;
    
    public LocalEchoSignal(boolean echo) {
        this.echo = echo;
    }
    
    public boolean getEcho() {
        return echo;
    }
}
