package org.twdata.TW1606.signal;

import org.werx.framework.bus.signals.BusSignal;

public class OnlineStatusSignal extends BusSignal{
    
    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    
    private String command;
    
    public OnlineStatusSignal(String command) {
        this.command = command;
    }
    
    public String getCommand() {
        return command;
    }
}
