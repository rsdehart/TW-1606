package org.twdata.TW1606U.tw.model;

import java.util.Date;
import java.io.Serializable;

public class ShipTypeImpl extends BaseDaoModel implements ShipType, Serializable {
    
    private String name;
    private int id;
    private String catalog;
    private int holdsCost;
    private int driveCost;
    private int compCost;
    private int hullCost;
    private int baseCost;
    private int maxFigsPerAttack;
    private int maxHolds;
    private int initHolds;
    private int maxFigs;
    private int turns;
    private int maxMines;
    private int maxGenesis;
    private boolean transWarp;
    private int transRange;
    private int maxShields;
    private float offensive;
    private float defensive;
    private int maxBeacons;
    private boolean longScan;
    private boolean planetScan;
    private boolean photon;
    
    public ShipTypeImpl() {
        super();
    }
    
    public String getName() {
        return name;
    }
       
    public void setName(String name) {
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCatalog() {
        return catalog;
    }
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
    
    public int getHoldsCost() {
        return holdsCost;
    }
    public void setHoldsCost(int holdsCost) {
        this.holdsCost = holdsCost;
    }
    
    public int getDriveCost() {
        return driveCost;
    }
    public void setDriveCost(int driveCost) {
        this.driveCost = driveCost;
    }
    
    public int getComputerCost() {
        return compCost;
    }
    public void setComputerCost(int computerCost) {
        this.compCost = computerCost;
    }
    
    public int getHullCost() {
        return hullCost;
    }
    public void setHullCost(int hullCost) {
        this.hullCost = hullCost;
    }
    
    public int getBaseCost() {
        return baseCost;
    }
    public void setBaseCost(int baseCost) {
        this.baseCost = baseCost;
    }
    
    public int getMaxFightersPerAttack() {
        return maxFigsPerAttack;
    }
    public void setMaxFightersPerAttack(int maxFightersPerAttack) {
        this.maxFigsPerAttack = maxFightersPerAttack;
    }
    
    public int getMaxHolds() {
        return maxHolds;
    }
    public void setMaxHolds(int maxHolds) {
        this.maxHolds = maxHolds;
    }
    
    public int getInitialHolds() {
        return initHolds;
    }
    public void setInitialHolds(int initialHolds) {
        this.initHolds = initialHolds;
    }
    
    public int getMaxFighters() {
        return maxFigs;
    }
    public void setMaxFighters(int maxFighters) {
        this.maxFigs = maxFighters;
    }
    
    public int getTurnsPerWarp() {
        return turns;
    }
    public void setTurnsPerWarp(int turnsPerWarp) {
        this.turns = turnsPerWarp;
    }
    
    public int getMaxMines() {
        return maxMines;
    }
    public void setMaxMines(int maxMines) {
        this.maxMines = maxMines;
    }
    
    public int getMaxGenesis() {
        return maxGenesis;
    }
    public void setMaxGenesis(int maxGenesis) {
        this.maxGenesis = maxGenesis;
    }
    
    public boolean getTransWarp() {
        return transWarp;
    }
    public void setTransWarp(boolean transWarp) {
        this.transWarp = transWarp;
    }
    
    public int getTransWarpRange() {
        return transRange;
    }
    public void setTransWarpRange(int transWarpRange) {
        this.transRange = transWarpRange;
    }
    
    public int getMaxShields() {
        return maxShields;
    }
    public void setMaxShields(int maxShields) {
        this.maxShields = maxShields;
    }
    
    public float getOffensiveOdds() {
        return offensive;
    }
    public void setOffensiveOdds(float offensiveOdds) {
        this.offensive = offensiveOdds;
    }
    
    public float getDefensiveOdds() {
        return defensive;
    }
    public void setDefensiveOdds(float defensiveOdds) {
        this.defensive = defensiveOdds;
    }
    
    public int getMaxBeacons() {
        return maxBeacons;
    }
    public void setMaxBeacons(int maxBeacons) {
        this.maxBeacons = maxBeacons;
    }
    
    public boolean getLongRangeScanner() {
        return longScan;
    }
    public void setLongRangeScanner(boolean longRangeScanner) {
        this.longScan = longRangeScanner;
    }
    
    public boolean getPlanetScanner() {
        return planetScan;
    }
    public void setPlanetScanner(boolean planetScanner) {
        this.planetScan = planetScanner;
    }
    
    public boolean setPhotonMissile() {
        return photon;
    }
    public void getPhotonMissile(boolean photonMissile) {
        this.photon = photonMissile;
    }
}


