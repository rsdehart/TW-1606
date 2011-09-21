package org.twdata.TW1606U.signal;

import org.werx.framework.bus.signals.BusSignal;

public class ConnectionSignal extends BusSignal{
    
    private String command;
    private String url;
    private int port;
    
    public ConnectionSignal(String command) {
        this.command = command;
    }
    
    public ConnectionSignal(String command, String url, int port) {
        this.command = command;
        this.url = url;
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getCommand() {
        return command;
    }
}
