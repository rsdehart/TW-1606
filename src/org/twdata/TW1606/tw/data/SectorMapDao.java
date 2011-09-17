package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.signal.SectorStatusSignal;
import org.twdata.TW1606.tw.model.*;
import java.util.*;

public class SectorMapDao extends BaseMapDao implements SectorDao {
    
    public static final String NAME = "sector";
    private Map<Integer,Sector> sectors;
    
    private Map<Integer,Sector> getSectors() {
        if (sectors == null) {
            sectors = db.getMap(NAME);
            
            for (Sector s : sectors.values()) {
                for (Sector warp : s.getWarps()) {
                    warp.addIncomingWarp(s);
                }
            }
        }
        return sectors;
    }
    
    public Sector create(int id) {
        Sector s = (Sector) db.createModel(NAME);
        s.setId(id);
        update(s);
        return s;
    }
    
    public Collection getAll() {
        return getSectors().values();
    }
    
    public void update(Sector sector) {
        getSectors().put(new Integer(sector.getId()), sector);
        
        for (Sector warp : sector.getWarps()) {
            warp.addIncomingWarp(sector);
        }
            
        bus.broadcast(new SectorStatusSignal(this, sector));
    }
    
    public Sector get(int sector) {
        return get(sector, false);
    }
    
    public Sector get(int sector, boolean create) {
        Sector s = (Sector) getSectors().get(new Integer(sector));
        if (s == null && create) {
            s = create(sector);
        }
        
        return s;
    }
    
    public void remove(Sector sector) {
        getSectors().remove(new Integer(sector.getId()));
    }
    
    
    
}
