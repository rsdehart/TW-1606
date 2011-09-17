package org.twdata.TW1606.tw.model;

import java.util.Date;

public interface Planet {

    public static final int FUEL_ORE = 0;
    public static final int ORGANICS = 1;
    public static final int EQUIPMENT = 2;
    public static final int FIGHTERS = 3;
    
    public int getId();
    public void setId(int planetD);
    
    public int getTWId();
    public void setTWId(int planetD);
    
    public String getName();
    public void setName(String name);
    
    public Sector getCurSector();
    public void setCurSector(Sector curSector);

    public PlanetType getPlanetType();
    public void setPlanetType(PlanetType planetType);

    public Player getCreator();
    public void setCreator(Player creator);
    
    public Owner getOwner();
    public void setOwner(Owner owner);
    
    public int getFighters();
    public void setFighters(int fighters);
    
    public int getShields();
    public void setShields(int shields);
    
    public int getProduct(int type);
    public void setProduct(int type, int product);
    
    public int getColonists(int type);
    public void setColonists(int type, int colonists);

    public int getSumColonists();

    public int getCidadelLvl();
    public void setCidadelLvl(int cidadelLvl);

    public int getQCannonLvl();
    public void setQCannonLvl(int qCannonLvl);
    
    public int getSecReactionLvl();
    public void setSecReactionLvl(int lvl);
    
    public int getAtmosReactionLvl();
    public void setAtmosReactionLvl(int lvl);

    public int getMilReactionLvl();
    public void setMilReactionLvl(int milReactionLvl);

	 public boolean getInterdictorOn();
	 public void setInterdictorOn(boolean interdictorOn);

    public int getTransporterHops();
    public void setTransporterHops(int transporterHops);
    
    public long getCredits();
    public void setCredits(long credits);
	
    public Player[] getParkedTraders();
    public void addParkedTrader(Player trader);
    public void removeParkedTrader(Player trader);
    
    public Date getLastModified();
	 public void setLastModified(Date lastModified);
}

