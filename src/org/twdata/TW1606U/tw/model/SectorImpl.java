package org.twdata.TW1606U.tw.model;

import java.util.*;
import java.io.Serializable;

public class SectorImpl extends BaseDaoModel implements Sector, Serializable {
		
    private static final long serialVersionUID = -2532035538200787320L;
    private int id;
    private boolean visited;
    private boolean inBubble;
    private String note;
    private String beacon;
    private String area;
    private int navHaz;
    private int[] warps;
    private transient Sector[] warpsRefs;
    private transient Set<Sector> incomingWarps = new HashSet<Sector>();
    
    private int[] planets;
    private transient Planet[] planetsRefs;
    
    private boolean hasAnom;
    private int density;
    private int figs;
    private int figType;
    private String figTraderOwner;
    private transient Player figTraderOwnerRef;
    
    private int figCorpOwner;
    private transient Corporation figCorpOwnerRef;
    
    private int[] mines;
    private String[] mineTraderOwner;
    private transient Player[] mineTraderOwnerRef;
    
    private int[] mineCorpOwner;
    private transient Corporation[] mineCorpOwnerRef;
    
    private String[] tradersPresent;
    private transient Player[] tradersPresentRefs;
    
    private String[] aliensPresent;
    private transient Player[] aliensPresentRefs;
    
    private String[] fedsPresent;
    private transient Player[] fedsPresentRefs;
    
    private int[] shipsPresent;
    private transient Ship[] shipsPresentRefs;
        
    public SectorImpl() {
        super();
        warps = new int[0];
        planets = new int[0];
        mines = new int[2];
        mineTraderOwner = new String[2];
        mineCorpOwner = new int[2];
        tradersPresent = new String[0];
        aliensPresent = new String[0];
        fedsPresent = new String[0];
        shipsPresent = new int[0];
    }
    
    public int getId() {
        return id;
    }
    public void setId(int sectorId) {
        this.id = sectorId;
    }
    
    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    
    public boolean isInBubble() {
        return inBubble;
    }
    public void setInBubble(boolean inBubble) {
        this.inBubble = inBubble;
    }
    
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    
    public Port getPort() {
        return portDao.get(id);
    }
    public void setPort(Port port) {
        portDao.update(port);
    }
    
    public String getBeacon() {
        return beacon;
    }
    public void setBeacon(String beacon) {
        this.beacon = beacon;
    }
    
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    
    public int getNavHaz() {
        return navHaz;
    }
    public void setNavHaz(int navHaz) {
        this.navHaz = navHaz;
    }
    
    public Sector[] getWarps() {
        if (warpsRefs == null) {
            warpsRefs = new Sector[warps.length];
            for (int x=0; x<warps.length; x++) {
                warpsRefs[x] = sectorDao.get(warps[x]);
            }
        }
        return warpsRefs;
    }    
    
    public void addWarp(Sector sector) {
        addWarp(sector.getId());
    }
    public void addWarp(int sectorId) {
        warps = addToArray(warps, sectorId);
        warpsRefs = null;
    }
    
    public Planet[] getPlanets() {
        if (planetsRefs == null) {
            planetsRefs = new Planet[planets.length];
            for (int x=0; x<planets.length; x++) {
                planetsRefs[x] = planetDao.get(planets[x]);
            }
        }    
        return planetsRefs;
    }
    
    public Planet[] getPlanets(String planetName) {
        ArrayList lst = new ArrayList();
        getPlanets();
        for (int x=0; x<planetsRefs.length; x++) {
            if (planetName.equals(planetsRefs[x].getName())) {
                lst.add(planetsRefs[x]);
            }
        }
        return (Planet[])lst.toArray(new Planet[]{});
    }
    public void addPlanet(Planet planet) {
        planets = addToArray(planets, planet.getId());
        planetsRefs = null;
    }
    public void removePlanet(Planet planet) {
        planets = delFromArray(planets, planet.getId());
        planetsRefs = null;
    }
    
    public Ship[] getShips() {
        Collection c = shipDao.getBySector(sectorDao.get(id));
        Ship[] s = new Ship[c.size()];
        return (Ship[]) c.toArray(s);
    }
    
    public boolean hasAnomaly() {
        return hasAnom;
    }
    public void setAnomaly(boolean anomaly) {
        this.hasAnom = anomaly;
    }
    
    public int getDensity() {
        return density;
    }
    public void setDensity(int density) {
        this.density = density;
    }
    
    public int getFighters() {
        return figs;
    }
    public void setFighters(int figs) {
        this.figs = figs;
    }
    
    public int getFighterType() {
        return figType;
    }
    public void setFighterType(int type) {
        this.figType = type;
    }
    
    public Owner getFighterOwner() {
        if (figTraderOwner != null) {
            if (figTraderOwnerRef == null) {
                figTraderOwnerRef = playerDao.get(figTraderOwner, Player.TRADER);
            }    
            return figTraderOwnerRef;
        } else {
            if (figCorpOwnerRef == null) {
                figCorpOwnerRef = corpDao.get(figCorpOwner);
            }
            return figCorpOwnerRef;
        }
    }
    
    public void setFighterOwner(Owner owner) {
        if (owner.isCorporate()) {
            this.figCorpOwner = ((Corporation)owner).getId();
            this.figCorpOwnerRef = (Corporation)owner;
            this.figTraderOwner = null;
            this.figTraderOwnerRef = null;
        } else {
            this.figTraderOwner = ((Player)owner).getName();
            this.figTraderOwnerRef = (Player)owner;
            this.figCorpOwner = 0;
            this.figCorpOwnerRef = null;
        }
    }
    
    public int getMines(int type) {
        return mines[type];
    }
    public void setMines(int type, int mines) {
        this.mines[type] = mines;
    }
    
    public Owner getMineOwner(int type) {
        if (mineTraderOwner[type] != null) {
            if (mineTraderOwnerRef == null) {
                mineTraderOwnerRef = new Player[2];
            }    
 
            if (mineTraderOwnerRef[type] == null) {
                mineTraderOwnerRef[type] = playerDao.get(mineTraderOwner[type], Player.TRADER);
            }
            return mineTraderOwnerRef[type];
        } else {
            if (mineCorpOwnerRef == null) {
                mineCorpOwnerRef = new Corporation[2];
            }    
 
            if (mineCorpOwnerRef[type] == null) {
                mineCorpOwnerRef[type] = corpDao.get(mineCorpOwner[type]);
            }
            return mineCorpOwnerRef[type];
        }
    }
    
    public void setMineOwner(int type, Owner owner) {
        mineTraderOwnerRef = null;
        mineCorpOwnerRef = null;
        if (owner.isCorporate()) {
            this.mineCorpOwner[type] = ((Corporation)owner).getId();
            this.mineTraderOwner[type] = null;
        } else {
            this.mineTraderOwner[type] = ((Player)owner).getName();
            this.mineCorpOwner[type] = 0;
        }
    }
    
    public Player[] getTradersPresent() {
        if (tradersPresentRefs == null) {
            tradersPresentRefs = new Player[tradersPresent.length];
            for (int x=0; x<tradersPresent.length; x++) {
                tradersPresentRefs[x] = playerDao.get(tradersPresent[x], Player.TRADER);
            }    
        }
        return tradersPresentRefs;
    }
    public void clearTradersPresent() {
        tradersPresent = new String[0];
        tradersPresentRefs = null;
    }
    public void addTraderPresent(Player player) {
        tradersPresent = addToArray(tradersPresent, player.getName());
        tradersPresentRefs = null;
    }
    
    public Ship[] getShipsPresent() {
        if (shipsPresentRefs == null) {
            shipsPresentRefs = new Ship[shipsPresent.length];
            for (int x=0; x<shipsPresent.length; x++) {
                shipsPresentRefs[x] = shipDao.get(shipsPresent[x]);
            }    
        }
        return shipsPresentRefs;   
    }
    public void clearShipsPresent() {
        shipsPresent = new int[0];
        shipsPresentRefs = null;
    }
    public void addShipPresent(Ship ship) {
        shipsPresent = addToArray(shipsPresent, ship.getId());   
        shipsPresentRefs = null;
    }
    
    public Player[] getAliensPresent() {
        if (aliensPresentRefs == null) {
            aliensPresentRefs = new Player[aliensPresent.length];
            for (int x=0; x<aliensPresent.length; x++) {
                aliensPresentRefs[x] = playerDao.get(aliensPresent[x], Player.ALIEN);
            }    
        }
        return aliensPresentRefs;
    }
    public void clearAliensPresent() {
        aliensPresent = new String[0];
        aliensPresentRefs = null;
    }
    public void addAlienPresent(Player alien) {
        aliensPresent = addToArray(aliensPresent, alien.getName());   
        aliensPresentRefs = null;
    }
    
    public Player[] getFedsPresent() {
        if (fedsPresentRefs == null) {
            fedsPresentRefs = new Player[fedsPresent.length];
            for (int x=0; x<fedsPresent.length; x++) {
                fedsPresentRefs[x] = playerDao.get(fedsPresent[x], Player.FED);
            }    
        }
        return fedsPresentRefs;
    }
    
    public void clearFedsPresent() {
        fedsPresent = new String[0];
        fedsPresentRefs = null;
    }
    public void addFedPresent(Player player) {
        fedsPresent = addToArray(fedsPresent, player.getName());
        fedsPresentRefs = null;
    }
    
    public String toString() {
        return String.valueOf(id);
    }

    public void addIncomingWarp(Sector sector) {
        if (incomingWarps == null) {
            incomingWarps = new HashSet<Sector>();
        }
        incomingWarps.add(sector);
    }

    public Set<Sector> getIncomingWarps() {
        if (incomingWarps == null) {
            incomingWarps = new HashSet<Sector>();
        }
        return incomingWarps;
    }
}	
