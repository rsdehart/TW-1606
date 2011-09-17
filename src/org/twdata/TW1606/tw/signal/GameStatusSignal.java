package org.twdata.TW1606.tw.signal;

import org.werx.framework.bus.signals.BusSignal;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;

public class GameStatusSignal extends BusSignal{
    
    private GameDao gameDao;
    private Game game;
    
    public GameStatusSignal(GameDao dao, Game game) {
        this.gameDao = dao;
        this.game = game;
    }
    
    public GameDao getDao() {
        return gameDao;
    }
    
    public Game getGame() {
        return game;
    }
}
