package org.twdata.TW1606.tw.model;

import java.util.Date;
import java.io.Serializable;

public class ShipImpl extends BaseDaoModel implements Ship, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1739137126250623351L;
    
    private int id;
    private int twid;
    private String name;
    private String shipType;
    private String curTrader;
    private int curSector;
    private int numHolds;
    private int[] holds;
    private int figs;
    private int shields;
    private int scanType;
    private boolean planetScanner;
    private int photon;
    private int transType;
    private int crombLvl;
    private int cloaks;
    private int atomics;
    private int gens;
    private int[] mines;
    private int mineDisrupters;
    private int probes;
    private int beacons;
    private int corpOwner;
    private String traderOwner;
    
    public ShipImpl() {
        super();
        holds = new int[4];
        mines = new int[2];
    }
   
    
    public int getId() {
        return id;
    }
        
    public void setId(int id) {
        this.id = id;
    }
    
    public int getTWId() {
        return twid;
    }
    public void setTWId(int id) {
        this.twid = id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ShipType getShipType() {
        return shipTypeDao.get(shipType);
    }
    public void setShipType(ShipType shipType) {
        this.shipType = (shipType != null ? shipType.getName() : null);
    }

    public Player getCurTrader() {
        return playerDao.get(curTrader, Player.TRADER);
    }
    public void setCurTrader(Player curTrader) {
        this.curTrader = curTrader.getName();
    }

    public Sector getCurSector() {
        return sectorDao.get(curSector);
    }
    public void setCurSector(Sector curSector) {
        this.curSector = curSector.getId();
    }

    public int getHolds() {
        return numHolds;
    }
    public void setHolds(int holds) {
        this.numHolds = holds;
    }

    public int getHoldContents(int type) {
        return holds[type];
    }
    public void setHoldContents(int type, int holdContents) {
        holds[type] = holdContents;
    }
    
    public int getEmptyHolds() {
        int sum = 0;
        for (int x=0; x<holds.length; x++) {
            sum += holds[x];
        }
        return numHolds - sum;
    }
    
    public void clearHolds() {
        for (int x=0; x<holds.length; x++) {
            holds[x] = 0;
        }
    }

    public int getFighters() {
        return figs;
    }
    public void setFighters(int fighters) {
        this.figs = fighters;
    }

    public int getShields() {
        return shields;
    }
    public void setShields(int shields) {
        this.shields = shields;
    }
	
    public int getLongRangeScanType() {
        return scanType;
    }
    public void setLongRangeScanType(int longRangeScanType) {
        scanType = longRangeScanType;
    }
	
    public boolean getPlanetScanner() {
        return planetScanner;
    }
    public void setPlanetScanner(boolean planetScanner) {
        this.planetScanner = planetScanner;
    }
	
    public int getPhotonMissiles() {
        return photon;
    }
    public void setPhotonMissiles(int photonMissiles) {
        this.photon = photonMissiles;
    }
	
    public int getTransWarpType() {
        return transType;
    }
    public void setTransWarpType(int transWarpType) {
        this.transType = transWarpType;
    }
	
    public int getCrombiteLvl() {
        return crombLvl;
    }
    public void setCrombiteLvl(int crombiteLvl) {
        this.crombLvl = crombiteLvl;
    }
	
    public int getCloaks() {
        return cloaks;
    }
    public void setCloaks(int cloaks) {
        this.cloaks = cloaks;
    }
	
    public int getAtomics() {
        return atomics;
    }
    public void setAtomics(int atomics) {
        this.atomics = atomics;
    }

    public int getGenesis() {
        return gens;
    }
    public void setGenesis(int genesis) {
        this.gens = genesis;
    }

    public int getMines(int type) {
        return mines[type];
    }
    public void setMines(int type, int mines) {
        this.mines[type] = mines;
    }
    
	public int getMineDisrupters() {
        return mineDisrupters;
    }
	public void setMineDisrupters(int mineDisrupters) {
        this.mineDisrupters = mineDisrupters;
    }

    public int getProbes() {
        return probes;
    }
    public void setProbes(int probes) {
        this.probes = probes;
    }
    
    public int getBeacons() {
        return beacons;
    }
    public void setBeacons(int beacons) {
        this.beacons = beacons;
    }
    
    public Owner getOwner() {
        if (traderOwner != null) {
            return playerDao.get(traderOwner, Player.TRADER);
        } else {
            return corpDao.get(corpOwner);
        }
    }
    
    public void setOwner(Owner owner) {
        if (owner.isCorporate()) {
            this.corpOwner = ((Corporation)owner).getId();
            this.traderOwner = null;
        } else {
            this.traderOwner = ((Player)owner).getName();
            this.corpOwner = 0;
        }
    }

}	

