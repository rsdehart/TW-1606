package org.twdata.TW1606.signal;

import org.werx.framework.bus.signals.BusSignal;

public class ScriptStatusSignal extends BusSignal{
    
    public static final String START = "start";
    public static final String STOP = "stop";
    
    private String command;
    private String id;
    
    public ScriptStatusSignal(String id, String command) {
        this.command = command;
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public String getCommand() {
        return command;
    }
}
