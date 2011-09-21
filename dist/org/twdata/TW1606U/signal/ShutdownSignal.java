package org.twdata.TW1606U.signal;

import org.werx.framework.bus.signals.BusSignal;

public class ShutdownSignal extends BusSignal{
    
    private boolean cancelled = false;
    
    public ShutdownSignal() {
    }
    
    public void setCancelled(boolean val) {
        cancelled = val;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
}
