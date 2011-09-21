package org.twdata.TW1606U.tw.model;

import java.util.Date;

public interface Ship {

    public static final int NONE = -1;
    public static final int FUEL_ORE = 0;
    public static final int ORGANICS = 1;
    public static final int EQUIPMENT = 2;
    public static final int COLONISTS = 3;
    
    public static final int ARMID = 0;
    public static final int LIMPET = 1;
	
    public static final int HOLO_SCAN = 0;
    public static final int DENSITY_SCAN = 1;
    
    public int getId();
    public void setId(int id);
    
    public int getTWId();
    public void setTWId(int id);
    
    public String getName();
    public void setName(String name);

    public ShipType getShipType();
    public void setShipType(ShipType shipType);

    public Player getCurTrader();
    public void setCurTrader(Player curTrader);

    public Sector getCurSector();
    public void setCurSector(Sector curSector);

    public int getHolds();
    public void setHolds(int holds);

    public int getHoldContents(int type);
    public void setHoldContents(int type, int holdsContent);
    public int getEmptyHolds();
    public void clearHolds();

    public int getFighters();
    public void setFighters(int fighters);

    public int getShields();
    public void setShields(int shields);
	
    public int getLongRangeScanType();
    public void setLongRangeScanType(int longRangeScanType);
	
    public boolean getPlanetScanner();
    public void setPlanetScanner(boolean planetScanner);
	
    public int getPhotonMissiles();
    public void setPhotonMissiles(int photonMissile);
	
    public int getTransWarpType();
    public void setTransWarpType(int transWarpType);
	
    public int getCrombiteLvl();
    public void setCrombiteLvl(int crombiteLvl);
	
    public int getCloaks();
    public void setCloaks(int cloaks);
	
    public int getAtomics();
    public void setAtomics(int atomics);

    public int getGenesis();
    public void setGenesis(int genesis);

    public int getMines(int type);
    public void setMines(int type, int mines);
    
	 public int getMineDisrupters();
	 public void setMineDisrupters(int mineDisrupters);

    public int getProbes();
    public void setProbes(int probes);  
    
    public int getBeacons();
    public void setBeacons(int beacons);  
    
    public Date getLastModified();
	public void setLastModified(Date lastModified);
    
}	

