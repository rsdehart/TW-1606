package org.twdata.TW1606.tw.model;

import java.util.Date;

public interface PlanetType {

    public static final int FUEL_ORE = 0;
    public static final int ORGANICS = 1;
    public static final int EQUIPMENT = 2;
    public static final int FIGHTERS = 3;
    public static final int COLONISTS = 4;
    public static final int DAYS = 5;
    
    public String getName();
    public void setName(String name);
    
    public String getType();
    public void setType(String type);
    
    public String getDescription();
    public void setDescription(String description);
    
    public int getColonistsToBuildOne(int productType);
    public void setColonistsToBuildOne(int productType, int colonistsToBuildOne);
    
    public int getIdealColonists(int itemType);
    public void setIdealColonists(int itemType, int maxColonists);
    
    public int getMaxProduct(int itemType);
    public void setMaxProduct(int itemType, int maxItem);
    
    public int getItemNeeded(int cididelLvl, int itemType);
    public void setItemNeeded(int cididelLvl, int itemType, int itemNeeded);
    
    public Date getLastModified();
	public void setLastModified(Date lastModified);
}

