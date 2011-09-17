package org.twdata.TW1606.tw.model;

import java.util.*;
import java.io.Serializable;

public class PlanetImpl extends BaseDaoModel implements Planet, Serializable {

    private int id;
    private int twid;
    private String name;
    private int curSector;
    private String planetType;
    private String creator;
    private int fighters;
    private int shields;
    private int[] products;
    private int[] colonists;
    private int cidLevel;
    private int cannonLevel;
    private int milLevel;
    private int atmosLevel;
    private int secLevel;
    private boolean interdOn;
    private int transHops;
    private long credits;
    private String[] parkedTraders;
    private transient Player[] parkedTradersRefs;
    private int corpOwner;
    private String traderOwner;
    
    public PlanetImpl() {
        super();
        products = new int[3];
        colonists = new int[3];
        parkedTraders = new String[0];
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
    public void setTWId(int twid) {
        this.twid = twid;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public Sector getCurSector() {
        return sectorDao.get(curSector);
    }
    public void setCurSector(Sector curSector) {
        this.curSector = curSector.getId();
    }

    public PlanetType getPlanetType() {
        return planetTypeDao.get(planetType);
    }
    public void setPlanetType(PlanetType planetType) {
        this.planetType = planetType.getType();
    }

    public Player getCreator() {
        return playerDao.get(creator, Player.TRADER);
    }
    public void setCreator(Player creator) {
        this.creator = creator.getName();
    }
    
    public int getFighters() {
        return fighters;
    }
    public void setFighters(int fighters) {
        this.fighters = fighters;
    }
    
    public int getShields() {
        return shields;
    }
    public void setShields(int shields) {
        this.shields = shields;
    }
    
    public int getProduct(int type) {
        return products[type];
    }
    public void setProduct(int type, int product) {
        products[type] = product;
    }
    
    public int getColonists(int type) {
        return colonists[type];
    }
    public void setColonists(int type, int colonists) {
        this.colonists[type] = colonists;
    }

    public int getSumColonists() {
        int sum = 0;
        for (int x=0; x<colonists.length; x++) {
            sum += colonists[x];
        }
        return sum;
    }

    public int getCidadelLvl() {
        return cidLevel;
    }
    public void setCidadelLvl(int cidadelLvl) {
        this.cidLevel = cidadelLvl;
    }

    public int getQCannonLvl() {
        return cannonLevel;
    }
    public void setQCannonLvl(int qCannonLvl) {
        this.cannonLevel = qCannonLvl;
    }

    public int getMilReactionLvl() {
        return milLevel;
    }
    public void setMilReactionLvl(int milReactionLvl) {
        this.milLevel = milReactionLvl;
    }
    
    public int getSecReactionLvl() {
        return secLevel;
    }
    public void setSecReactionLvl(int lvl) {
        this.secLevel = lvl;
    }
    
    public int getAtmosReactionLvl() {
        return atmosLevel;
    }
    public void setAtmosReactionLvl(int lvl) {
        this.atmosLevel = lvl;
    }

	public boolean getInterdictorOn() {
        return interdOn;
    }
	public void setInterdictorOn(boolean interdictorOn) {
        this.interdOn = interdictorOn;
    }

    public int getTransporterHops() {
        return transHops;
    }
    public void setTransporterHops(int transporterHops) {
        this.transHops = transporterHops;
    }
    
    public long getCredits() {
        return credits;
    }
    public void setCredits(long credits) {
        this.credits = credits;
    }
	
    public Player[] getParkedTraders() {
        if (parkedTradersRefs == null) {
            parkedTradersRefs = new Player[parkedTraders.length];
            for (int x=0; x<parkedTraders.length; x++) {
                parkedTradersRefs[x] = playerDao.get(parkedTraders[x], Player.TRADER);
            }
        }    
        return parkedTradersRefs;
    }
    public void addParkedTrader(Player trader) {
        parkedTraders = addToArray(parkedTraders, trader.getName());
        parkedTradersRefs = null;
    }
    public void removeParkedTrader(Player trader) {
        parkedTraders = delFromArray(parkedTraders, trader.getName());
        parkedTradersRefs = null;
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

