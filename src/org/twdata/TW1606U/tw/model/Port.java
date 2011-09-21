package org.twdata.TW1606U.tw.model;

import java.util.Date;

public interface Port {
    
    public static final int EQUIPMENT = 2;
    public static final int ORGANICS = 1;
    public static final int FUEL_ORE = 0;
    public static final int CLASS_BBS = 1;
    public static final int CLASS_BSB = 2;
    public static final int CLASS_SBB = 3;
    public static final int CLASS_SSB = 4;
    public static final int CLASS_SBS = 5;
    public static final int CLASS_BSS = 6;
    public static final int CLASS_SSS = 7;
    public static final int CLASS_BBB = 8;	
    
    public int getId();
    public void setId(int id);
    
    public boolean isVisited();
    public void setVisited(boolean visited);
    
    public Sector getSector();
    public void setSector(Sector sector);
    
    public String getName();
    public void setName(String name);
    
    public int getPortClass();
    public void setPortClass(int portClass);
    
    public String getPortClassName();
    
    public int getMaxTradeable(Port port);
    public boolean isPairTradeable(Port target);
    public int determineUntradeable(Port target);
    
    public long getCredits();
    public void setCredits(long credits);
    
    public int getCurProduct(int type);
    public void setCurProduct(int type, int curProduct);

    public int getMaxProduct(int type);
    public void setMaxProduct(int type, int maxProduct);
    
    public boolean buysProduct(int type);
    public int getProductPercentage(int type);
    
    public Date getLastModified();
	 public void setLastModified(Date lastModified);

	 public Date getLastBust();
	 public void setLastBust(Date lastBust);
     
    public Date getLastVisited();
    public void setLastVisited(Date lastVisited);
    
    public Player getLastVisitor();
    public void setLastVisitor(Player lastVisitor);
}

