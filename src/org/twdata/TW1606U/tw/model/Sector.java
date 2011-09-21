package org.twdata.TW1606U.tw.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface Sector {
		
    public static final int ARMID = 0;
    public static final int LIMPET = 1;

    public static final int NONE = 0;
    public static final int OFFENSIVE = 1;
    public static final int DEFENSIVE = 2;
    public static final int TOLL = 3;
    public static final int MERCHENARY = 4;    
        
    public int getId();
    public void setId(int sectorId);
    
    public boolean isVisited();
    public void setVisited(boolean visited);
    
    public boolean isInBubble();
    public void setInBubble(boolean inBubble);
    
    public String getNote();
    public void setNote(String note);
    
    public Port getPort();
    public void setPort(Port port);
    
    public String getBeacon();
    public void setBeacon(String beacon);
    
    public String getArea();
    public void setArea(String area);
    
    public int getNavHaz();
    public void setNavHaz(int navHaz);
    
    public Sector[] getWarps();
    public void addWarp(Sector sector);
    public void addWarp(int sectorId);
    
    public Set<Sector> getIncomingWarps();
    public void addIncomingWarp(Sector sector);
    
    public Planet[] getPlanets();
    public Planet[] getPlanets(String planetName);
    public void addPlanet(Planet planet);	
    public void removePlanet(Planet planet);
    
    public Ship[] getShips();
    
    public boolean hasAnomaly();
    public void setAnomaly(boolean anomaly);
    
    public int getDensity();
    public void setDensity(int density);
    
    public int getFighters();
    public void setFighters(int figs);
    
    public int getFighterType();
    public void setFighterType(int type);
    
    public Owner getFighterOwner();
    public void setFighterOwner(Owner owner);
    
    public int getMines(int type);
    public void setMines(int type, int mines);
    
    public Owner getMineOwner(int type);
    public void setMineOwner(int type, Owner owner);
    
    public Player[] getTradersPresent();
    public void clearTradersPresent();
    public void addTraderPresent(Player player);
    
    public Ship[] getShipsPresent();
    public void clearShipsPresent();
    public void addShipPresent(Ship ship);
    
    public Player[] getAliensPresent();
    public void clearAliensPresent();
    public void addAlienPresent(Player alien);
    
    public Player[] getFedsPresent();
    public void clearFedsPresent();
    public void addFedPresent(Player player);
    
    public Date getLastModified();
	 public void setLastModified(Date lastModified);
}	
