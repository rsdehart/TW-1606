package org.twdata.TW1606U.tw.model;

import java.util.Collection;
import java.util.Map;
import java.util.Date;

public interface Game {

    public static final int TURNS_PER_DAY = 1;
    public static final int INITAL_FIGHTERS = 2;
    public static final int INITAL_CREDITS = 3;
    public static final int INITAL_HOLDS = 4;
    public static final int MAX_INACTVE = 5;
    public static final int MAX_TRADERS = 6;
    public static final int MAX_SECTORS = 7;
    public static final int MAX_PORTS = 8;
    public static final int MAX_SHIPS = 9;
    public static final int MAX_PLANETS = 10;
    public static final int PLANETS_PER_SECTOR = 11;
    public static final int TRADERS_PER_CORP = 12;
    public static final int SHIPS_PER_FEDSPACE = 13;
    public static final int STARDOCK = 14;
    public static final int ALPHA_CENTARI = 15;
    public static final int RYLOS = 16;
    public static final int PHOTON_DURATON = 17;
    public static final int GAME_DURATON = 18;
    public static final int BUSTS_CLEARED = 19;
    public static final int PORT_PRODUCTON_RATE = 20;
    public static final int PER_PLANETARY_TRADE = 21;

    public static final int STAT_PORTS = 100;
    public static final int STAT_PORTS_WORTH = 101;
    public static final int STAT_PLANETS = 102;
    public static final int STAT_PER_PLANETS_WTH_CITADELS = 103;
    public static final int STAT_TRADERS = 104;
    public static final int STAT_PER_TRADERS_GOOD = 105;
    public static final int STAT_FIGHTERS = 106;
    public static final int STAT_MINES = 107;
    public static final int STAT_CORPORATIONS = 108;
    public static final int STAT_SHIPS = 109;
    
    public String getName();
    public void setName(String name);
    
    public Object getGameSetting(int settingType);
    public void setGameSetting(int settingType, Object gameSetting);
    
    public Object getGameStatistic(int statisticType);
    public void setGameStatistic(int statisticType, Object gameStatistic);
    
    public void setTime(Date time);
    public Date getTime();
    
    public void setSectors(int sectors);
    public int getSectors();
    
    public void addNote(String title, String note);
    public void removeNote(String title);
    public String getNote(String title);
    public String[] getNoteTitles();
    
    public Date getLastModified();	
	public void setLastModified(Date lastModified);
}

