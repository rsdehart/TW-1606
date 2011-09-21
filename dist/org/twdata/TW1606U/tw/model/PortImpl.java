package org.twdata.TW1606U.tw.model;

import java.util.Date;
import java.io.Serializable;

public class PortImpl extends BaseDaoModel implements Port, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5291055034528492219L;

    public static final int UNKNOWN = -1;
    
    private boolean visited;
    private int sector;
    private String name;
    private int portClass;
    private long credits;
    private int[] products;
    private int[] maxProducts;
    private Date lastBust;
    private Date lastVisited;
    private String lastVisitor;
    private static final int[][] CLASSES = new int[][] {
        {-1, -1, -1},
        { 0,  0,  1},
        { 0,  1,  0},
        { 1,  0,  0},
        { 1,  1,  0},
        { 1,  0,  1},
        { 0,  1,  1},
        { 1,  1,  1},
        { 0,  0,  0}, 
        {-1, -1, -1}
    };
    
    private static final String[] CLASSNAMES = new String[] {
        "Special",
        "BBS",
        "BSB",
        "SBB",
        "SSB",
        "SBS",
        "BSS",
        "SSS",
        "BBB",
        "Stardock"
    };
    
    public PortImpl() {
        super();
        products = new int[3];
        maxProducts = new int[3];
        for (int x=0; x<products.length; x++) {
            products[x] = -1;
            maxProducts[x] = -1;
        }
    }
    
    public int getId() {
        return sector;
    }
    public void setId(int sectorId) {
        this.sector = sectorId;
    }
    
    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    
    public Sector getSector() {
        return sectorDao.get(sector);
    }
    public void setSector(Sector sector) {
        this.sector = sector.getId();
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public int getPortClass() {
        return portClass;
    }
    public void setPortClass(int portClass) {
        this.portClass = portClass;
    }
    
    public String getPortClassName() {
        return CLASSNAMES[portClass];
    }
    
    public long getCredits() {
        return credits;
    }
    public void setCredits(long credits) {
        this.credits = credits;
    }
    
    public int getCurProduct(int type) {
        return products[type];
    }
    public void setCurProduct(int type, int curProduct) {
        products[type] = curProduct;
    }

    public int getMaxProduct(int type) {
        return maxProducts[type];
    }
    public void setMaxProduct(int type, int maxProduct) {
        maxProducts[type] = maxProduct;
    }
    
    public boolean buysProduct(int type) {
        return CLASSES[portClass][type] == 0;
    }
    
    public int getProductPercentage(int type) {
        if (maxProducts[type ] > 0) {
            return (int) (((long)products[type] / (long)maxProducts[type]) * 100);
        } else { 
            return 0;
        }
    }
    
    public Date getLastBust() {
        return lastBust;
    }
	public void setLastBust(Date lastBust) {
        this.lastBust = lastBust;
    }
    
    public Date getLastVisited() {
        return lastVisited;
    }
    public void setLastVisited(Date lastVisited) {
        this.lastVisited = lastVisited;
    }
    
    public Player getLastVisitor() {
        return playerDao.get(lastVisitor, Player.TRADER);
    }
    public void setLastVisitor(Player lastVisitor) {
        this.lastVisitor = lastVisitor.getName();
    }
    
    private boolean tradeable(Port target, int type1, int type2, int pair1, int pair2) {
        if ((getPortClass() == type1 || getPortClass() == type2) &&
            (target.getPortClass() == pair1 || target.getPortClass() == pair2)) {
            return true;
        } else if ((target.getPortClass() == type1 || target.getPortClass() == type2) &&
            (getPortClass() == pair1 || getPortClass() == pair2)) {
            return true;
        } else {
            return false;
        }
        
    }
    
    public boolean isPairTradeable(Port target) {
        return determineUntradeable(target) > -1;
    }
    
    public int getMaxTradeable(Port target) {
        int testNot = determineUntradeable(target);
        
        int min = Integer.MAX_VALUE;
        for (int x=0; x<3; x++) {
            if (x != testNot) {
                min = Math.min(min, Math.min(getCurProduct(x), target.getCurProduct(x)));
            }
        }
        if (min == Integer.MAX_VALUE) {
            min = -1;
        }
        
        return min;
    }

    public int determineUntradeable(Port target) {
        int testNot = -1;
        if (tradeable(target, 1, 6, 3, 4)) {
            testNot = 1;
        } else if (tradeable(target, 1, 5, 2, 4)) {
            testNot = 0;
        } else if (tradeable(target, 2, 6, 3, 5)) {
            testNot = 3;
        } else {
            return -1;
        }
        return testNot;
    }
    
    public String toString() {
        return super.toString();
    }
}

