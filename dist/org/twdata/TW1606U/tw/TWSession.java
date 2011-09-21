package org.twdata.TW1606U.tw;

import org.twdata.TW1606U.tw.model.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.signal.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.signal.*;

public class TWSession {

    public static final int PROMPT_NO = 0;
    public static final int PROMPT_COMMAND = 1;
    public static final int PROMPT_COMPUTER = 2;
    public static final int PROMPT_PLANET = 3;
    public static final int PROMPT_CITADEL = 4;
    public static final int PROMPT_CORP = 5;
    public static final int PROMPT_STARDOCK = 6;
    
    public static final String LAST_TRADER = "lastTrader";
    
    private Sector sector;
    private Planet planet;
    private Game game;
    private Player trader;
    private PlayerDao playerDao;
    private SessionDao sesDao;
    private GameDao gameDao;
    private int prompt;
    private MessageBus bus;
    
    public TWSession() {
    }
    
    public void setDaoManager(DaoManager dm) {
        playerDao = (PlayerDao) dm.getDao("player");
        gameDao = (GameDao) dm.getDao("game");
    }
    
    public void setSessionDao(SessionDao sdao) {
        sesDao = sdao;
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
		
    public Sector getSector() {
        return sector;
    }
    
    public void setSector(Sector sector) {
        if (this.sector != sector) {
            this.sector = sector;
            if (trader != null) {
                trader.setCurSector(sector);
                playerDao.update(trader);
            }
            bus.broadcast(new ChangedSessionSignal(ChangedSessionSignal.SECTOR));
        }
    }
    
    public Planet getPlanet() {
        return planet;
    }
    public void setPlanet(Planet planet) {
        if (this.planet != planet) {
            this.planet = planet;
            bus.broadcast(new ChangedSessionSignal(ChangedSessionSignal.PLANET));
        }
    }

    public Player getTrader() {
        return trader;
    }
    public void setTrader(Player trader) {
        if (this.trader != trader) {
            this.trader = trader;
            bus.broadcast(new ChangedSessionSignal(ChangedSessionSignal.TRADER));
        }
    }
    
    public int getPrompt() {
        return prompt;
    }
    public void setPrompt(int prompt) {
        if (this.prompt != prompt) {
            this.prompt = prompt;
            bus.broadcast(new ChangedSessionSignal(ChangedSessionSignal.PROMPT));
        }
    }
    
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        if (this.game != game) {
            this.game = game;
            bus.broadcast(new ChangedSessionSignal(ChangedSessionSignal.GAME));
        }
    }
    
    public void channel(SessionStatusSignal signal) {
        String name = signal.getName();
        
        if (signal.START.equals(signal.getStatus())) {
            setGame(gameDao.get(name, true));
            Session ses = signal.getSession();
            String trader = ses.getProperty(LAST_TRADER);
            if (trader != null) {
                setTrader(playerDao.get(trader, Player.TRADER, true));
            }
        } else if (signal.STOP.equals(signal.getStatus())) {
            //setGame(null);
            if (trader != null) {
                Session ses = signal.getSession();
                ses.setProperty(LAST_TRADER, trader.getName());
                sesDao.update(ses);
            }
            game = null;
            sector = null;
            planet = null;
            trader = null;
            prompt = PROMPT_NO;
        }
    }
    
}

