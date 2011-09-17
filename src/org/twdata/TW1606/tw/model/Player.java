package org.twdata.TW1606.tw.model;

import java.util.Collection;
import java.util.Map;
import java.util.Date;

public interface Player extends Owner{

    public static final int TRADER = 0;
    public static final int ALIEN = 1;
    public static final int FED = 2;
    
    public int getId();
    public void setId(int id); 
	
    public String getName();
    public void setName(String name);
    
    public int getType();
    public void setType(int type);
    	
    public int getExperience();
    public void setExperience(int experience);
    
    public int getAlignment();
    public void setAlignment(int alignment);
    
	public String getPassword();
	public void setPassword(String password);

    public long getCredits();
    public void setCredits(long credits);
    
    public int getTurns();
    public void setTurns(int turns);
    
    public Ship getCurShip();
    public void setCurShip(Ship curShip);
    
    public Corporation getCorporation();
    public void setCorporation(Corporation corporation);
    
    public Sector getCurSector();
    public void setCurSector(Sector curSector);
    
    public Map getBusts();
    public Date getBust(Sector s);
    public void setBusts(Map busts);
    public void setBust(Sector s, Date time);
    public void removeBust(Sector s);

    public Sector getLastSteal();
    public void setLastSteal(Sector lastSteal);

    public Date getLastModified();
	 public void setLastModified(Date lastModified);
}

