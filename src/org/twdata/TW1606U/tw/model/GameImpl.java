package org.twdata.TW1606U.tw.model;

import java.util.*;
import org.twdata.TW1606U.tw.data.*;
import java.io.Serializable;

public class GameImpl extends BaseDaoModel implements Game, Serializable {

    private String name;
    private Map gameSettings = new HashMap();
    private Map gameStats = new HashMap();
    private Map notes = new HashMap();
    private int sectors;

    private Date time;
    
    public GameImpl() {
        super();
    }
		
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSectors(int sectors) {
        this.sectors = sectors;
    }
    
    public int getSectors() {
        return sectors;
    }
    
    public void addNote(String title, String note) {
        notes = ensureMap(notes);
        notes.put(title, note);
    }
    
    public void removeNote(String title) {
        notes = ensureMap(notes);
        notes.remove(title);
    }
    
    public String getNote(String title) {
        notes = ensureMap(notes);
        return (String) notes.get(title);
    }
    
    public String[] getNoteTitles() {
        notes = ensureMap(notes);
        Set keys = notes.keySet();
        String[] arr = new String[keys.size()];
        arr = (String[]) keys.toArray(arr);
        return arr;
    }
        
    
    public Object getGameSetting(int settingType) {
        return gameSettings.get(new Integer(settingType));
    }
    public void setGameSetting(int settingType, Object gameSetting) {
        gameSettings.put(new Integer(settingType), gameSetting);
    }
    
    public Object getGameStatistic(int statisticType) {
        return gameStats.get(new Integer(statisticType));
    }
    public void setGameStatistic(int statisticType, Object gameStatistic) {
        gameStats.put(new Integer(statisticType), gameStatistic);   
    }
    
    public void setTime(Date time) {
        this.time = time;
    }
    public Date getTime() {
        return time;
    }
    
    private Map ensureMap(Map map) {
        if (map == null) {
            map = new HashMap();
        }
        return map;
    }
}

