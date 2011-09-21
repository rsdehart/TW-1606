package org.twdata.TW1606U.tw.model;

import java.util.Collection;
import java.util.*;
import java.util.Date;
import java.io.Serializable;

public class PlayerImpl extends BaseDaoModel implements Player, Serializable {

    private int id;
    private String name;
    private int turns;
    private int type;
    private int exp;
    private int align;
    private String passwd;
    private long credits;
    private int curShip;
    private int corp;
    private int curSector;
    private Map busts = new HashMap();
    private int lastSteal;
    
    public PlayerImpl() {
        super();
    }
    
    public int getId()  {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
	
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    	
    public int getExperience() {
        return exp;
    }
    public void setExperience(int experience) {
        this.exp = experience;
    }
    
    public int getAlignment() {
        return align;
    }
    public void setAlignment(int alignment) {
        this.align = alignment;
    }
    
	public String getPassword() {
        return passwd;
    }
	public void setPassword(String password) {
        this.passwd = password;
    }

    public long getCredits() {
        return credits;
    }
    public void setCredits(long credits) {
        this.credits = credits;
    }
    
    public int getTurns() {
        return turns;
    }
    public void setTurns(int turns) {
        this.turns = Math.max(turns, 0);
    }
    
    public Ship getCurShip() {
        return shipDao.get(curShip);
    }
    public void setCurShip(Ship curShip) {
        this.curShip = curShip.getId();
    }
    
    public Corporation getCorporation() {
        return corpDao.get(corp);
    }
    public void setCorporation(Corporation corporation) {
        this.corp = corporation.getId();
    }
    
    public Sector getCurSector() {
        return sectorDao.get(curSector);
    }
    public void setCurSector(Sector curSector) {
        this.curSector = curSector.getId();
    }
    
    public Map getBusts() {
        Map m = new HashMap();
        Map.Entry entry;
        int id;
        for (Iterator i = busts.entrySet().iterator(); i.hasNext(); ) {
            entry = (Map.Entry)i.next();
            id = ((Integer)entry.getKey()).intValue();
            m.put(sectorDao.get(id), entry.getValue());
        }
        return m;
    }
    
    public Date getBust(Sector s) {
        return (Date) busts.get(new Integer(s.getId()));
    }
    
    public void setBusts(Map busts) {
        Map m = new HashMap();
        Map.Entry entry;
        for (Iterator i = busts.entrySet().iterator(); i.hasNext(); ) {
            entry = (Map.Entry)i.next();
            m.put(new Integer(((Sector)entry.getKey()).getId()), entry.getValue());
        }
        this.busts = m;
    }
    public void setBust(Sector s, Date time) {
        busts.put(new Integer(s.getId()), time);
    }
    public void removeBust(Sector s) {
        busts.remove(new Integer(s.getId()));
    }

    public Sector getLastSteal() {
        return sectorDao.get(lastSteal);
    }
    public void setLastSteal(Sector lastSteal) {
        this.lastSteal = lastSteal.getId();
    }
    
    public boolean isCorporate() {
        return false;
    }
    
    public boolean isFriendly(Player player) {
        if (player == this) {
            return true;
        } else if (player.getType()==player.TRADER) {
            Corporation c = getCorporation();
            if (c != null) {
                if (player.getCorporation() == c) {
                    return true;
                }
            }
        }
        return false;
    }
}

