package org.twdata.TW1606.tw.model;

import java.util.Date;
import java.io.Serializable;

public class PlanetTypeImpl extends BaseDaoModel implements PlanetType, Serializable {

    private String name;
    private String type;
    private String desc;
    private int[] colsToBuild;
    private int[] idealCols;
    private int[] maxProduct;
    private int[][] neededToLvl;
    
    public PlanetTypeImpl() {
        super();
        colsToBuild = new int[4];
        idealCols = new int[4];
        maxProduct = new int[4];
        neededToLvl = new int[7][];
        
        for (int x=0; x<neededToLvl.length; x++) {
            neededToLvl[x] = new int[7];
        }
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return desc;
    }
    public void setDescription(String description) {
        this.desc = description;
    }
    
    public int getColonistsToBuildOne(int productType) {
        return colsToBuild[productType];
    }
    public void setColonistsToBuildOne(int productType, int colonistsToBuildOne) {
        colsToBuild[productType] = colonistsToBuildOne;
    }
    
    public int getIdealColonists(int itemType) {
        return idealCols[itemType ];
    }
    public void setIdealColonists(int itemType, int maxColonists) {
        idealCols[itemType ] = maxColonists;
    }
    
    public int getMaxProduct(int itemType) {
        return maxProduct[itemType];
    }
    public void setMaxProduct(int itemType, int maxItem) {
        maxProduct[itemType] = maxItem;
    }
    
    public int getItemNeeded(int cididelLvl, int itemType) {
       return neededToLvl[cididelLvl][itemType];
    }
    
    public void setItemNeeded(int cididelLvl, int itemType, int itemNeeded) {
        neededToLvl[cididelLvl][itemType] = itemNeeded;
    }
}

