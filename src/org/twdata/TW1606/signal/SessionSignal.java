package org.twdata.TW1606.signal;

import org.werx.framework.bus.signals.BusSignal;
import java.awt.event.ActionEvent;

public class SessionSignal extends BusSignal{
    
    private ActionEvent event;
    
    public SessionSignal(ActionEvent e) {
        event = e;
    }
    
    public ActionEvent getEvent() {
        return event;
    }
    
    public String getCommand() {
        return event.getActionCommand();
    }
}
