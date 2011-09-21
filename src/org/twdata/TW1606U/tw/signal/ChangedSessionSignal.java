package org.twdata.TW1606U.tw.signal;

import org.werx.framework.bus.signals.BusSignal;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.model.*;

public class ChangedSessionSignal extends BusSignal{
    
    public static final int TRADER = 1;
    public static final int SECTOR = 2;
    public static final int PLANET = 3;
    public static final int GAME = 4;
    public static final int PROMPT = 5;
    
    private int type;
    
    public ChangedSessionSignal(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
}
