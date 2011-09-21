package org.twdata.TW1606U.tw.signal;

import org.werx.framework.bus.signals.BusSignal;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.model.*;

public class ChatSignal extends BusSignal {
    
    public static final int FEDCOM = 1;
    public static final int SUBSPACE = 2;
    public static final int PRIV = 3;
    
    private int type;
    private Player sender;
    private Player target;
    private String message;
    
    public ChatSignal(int type, Player sender, Player target, String message) {
        this.type = type;
        this.sender = sender;
        this.target = target;
        this.message = message;
    }
    
    public int getType() {
        return type;
    }
    
    public String getMessage() {
        return message;
    }
    public Player getSender() {
        return sender;
    }
    public Player getTarget() {
        return target;
    }
}
