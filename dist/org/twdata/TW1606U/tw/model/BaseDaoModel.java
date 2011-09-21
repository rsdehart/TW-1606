package org.twdata.TW1606U.tw.model;

import org.apache.log4j.Logger;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.signal.*;
import java.util.*;

public abstract class BaseDaoModel implements DaoAwareModel {
    
    transient DaoManager dm;
    transient SectorDao sectorDao;
    transient PortDao portDao;
    transient GameDao gameDao;
    transient ShipDao shipDao;
    transient ShipTypeDao shipTypeDao;
    transient PlanetDao planetDao;
    transient PlanetTypeDao planetTypeDao;
    transient CorporationDao corpDao;
    transient PlayerDao playerDao;
    Date lastMod;
    transient Logger log;
    transient boolean initialized = false;
    
    public BaseDaoModel() {
        //log = Logger.getLogger(this.getClass());
    }
    
    public void setDaoManager(DaoManager dm) {
        if (!initialized) {
            log = Logger.getLogger(this.getClass());
            //if (log.isDebugEnabled()) {
            //    log.debug("initializing model:"+this.getClass().getName());
            //}
            this.dm = dm;
            sectorDao = (SectorDao)dm.getDao("sector");
            portDao = (PortDao)dm.getDao("port");
            gameDao = (GameDao)dm.getDao("game");
            shipDao = (ShipDao)dm.getDao("ship");
            shipTypeDao = (ShipTypeDao)dm.getDao("shipType");
            planetDao = (PlanetDao)dm.getDao("planet");
            planetTypeDao = (PlanetTypeDao)dm.getDao("planetType");
            playerDao = (PlayerDao)dm.getDao("player");
            corpDao = (CorporationDao)dm.getDao("corporation");
            initialized = true;
        }
    }
    
    public Date getLastModified() {
        return lastMod;
    }
	public void setLastModified(Date lastModified) {
        this.lastMod = lastModified;
    }
    
    protected int[] addToArray(int[] orig, int val) {
        return addToArray(orig, val, true);
    }
    
    protected int[] addToArray(int[] orig, int val, boolean unique) {
        if (unique) {
            for (int x=0; x<orig.length; x++) {
                if (orig[x] == val) {
                    return orig;
                }
            }
        }
        int[] lst = new int[orig.length+1];
        System.arraycopy(orig, 0, lst, 0, orig.length);
        lst[orig.length] = val;
        return lst;
    }
    
    protected String[] addToArray(String[] orig, String val) {
        return addToArray(orig, val);
    }
    
    protected String[] addToArray(String[] orig, String val, boolean unique) {
        if (unique) {
            for (int x=0; x<orig.length; x++) {
                if (orig[x].equals(val)) {
                    return orig;
                }
            }
        }
        String[] lst = new String[orig.length+1];
        System.arraycopy(orig, 0, lst, 0, orig.length);
        lst[orig.length] = val;
        return lst;
    }
    
    protected int[] delFromArray(int[] orig, int val) {
        int[] lst = new int[orig.length - 1];
        for (int x=0, y=0; x<orig.length; x++) {
            if (x == orig.length - 1 && x == y) {
                return orig;
            } else if (orig[x] != val) {
                lst[y++] = orig[x];
            }
        }
        return lst;
    }
    
    protected String[] delFromArray(String[] orig, String val) {
        String[] lst = new String[orig.length - 1];
        for (int x=0, y=0; x<orig.length; x++) {
            if (x == orig.length - 1 && x == y) {
                return orig;
            } else if (orig[x] != val) {
                lst[y++] = orig[x];
            }
        }
        return lst;
    }
    
}
