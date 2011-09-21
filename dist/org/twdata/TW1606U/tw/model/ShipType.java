package org.twdata.TW1606U.tw.model;

import java.util.Date;

public interface ShipType {
    
    public String getName();
    public void setName(String name);
    
    public int getId();
    public void setId(int id);
    
    public String getCatalog();
    public void setCatalog(String catalog);
    
    public int getHoldsCost();
    public void setHoldsCost(int holdsCost);
    
    public int getDriveCost();
    public void setDriveCost(int driveCost);
    
    public int getComputerCost();
    public void setComputerCost(int computerCost);
    
    public int getHullCost();
    public void setHullCost(int hullCost);
    
    public int getBaseCost();
    public void setBaseCost(int baseCost);
    
    public int getMaxFightersPerAttack();
    public void setMaxFightersPerAttack(int maxFightersPerAttack);
    
    public int getMaxHolds();
    public void setMaxHolds(int maxHolds);
    
    public int getInitialHolds();
    public void setInitialHolds(int initialHolds);
    
    public int getMaxFighters();
    public void setMaxFighters(int maxFighters);
    
    public int getTurnsPerWarp();
    public void setTurnsPerWarp(int turnsPerWarp);
    
    public int getMaxMines();
    public void setMaxMines(int maxMines);
    
    public int getMaxGenesis();
    public void setMaxGenesis(int maxGenesis);
    
    public boolean getTransWarp();
    public void setTransWarp(boolean transWarp);
    
    public int getTransWarpRange();
    public void setTransWarpRange(int transWarpRange);
    
    public int getMaxShields();
    public void setMaxShields(int maxShields);
    
    public float getOffensiveOdds();
    public void setOffensiveOdds(float offensiveOdds);
    
    public float getDefensiveOdds();
    public void setDefensiveOdds(float defensiveOdds);
    
    public int getMaxBeacons();
    public void setMaxBeacons(int maxBeacons);
    
    public boolean getLongRangeScanner();
    public void setLongRangeScanner(boolean longRangeScanner);  
    
    public boolean getPlanetScanner();
    public void setPlanetScanner(boolean planetScanner);
    
    public boolean setPhotonMissile();
    public void getPhotonMissile(boolean photonMissile);
    
    public Date getLastModified();
	 public void setLastModified(Date lastModified);
}


