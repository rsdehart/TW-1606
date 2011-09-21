package org.twdata.TW1606U.signal;

import org.werx.framework.bus.signals.BusSignal;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.Clipboard;

public class ClipboardSignal extends BusSignal{
    
    private String command;
    private Clipboard clipboard;
    
    public ClipboardSignal(String command, Clipboard clipboard) {
        this.command = command;
        this.clipboard = clipboard;
    }
    
    public Clipboard getClipboard() {
        return clipboard;
    }
    
    public String getCommand() {
        return command;
    }
}
