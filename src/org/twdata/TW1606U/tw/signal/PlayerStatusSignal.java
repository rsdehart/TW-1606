package org.twdata.TW1606U.tw.signal;

import org.werx.framework.bus.signals.BusSignal;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.model.*;

public class PlayerStatusSignal extends BusSignal{
    
    private PlayerDao playerDao;
    private Player player;
    
    public PlayerStatusSignal(PlayerDao dao, Player player) {
        this.playerDao = dao;
        this.player = player;
    }
    
    public PlayerDao getDao() {
        return playerDao;
    }
    
    public Player getPlayer() {
        return player;
    }
}
