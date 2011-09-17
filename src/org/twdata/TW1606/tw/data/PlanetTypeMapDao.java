package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import java.util.*;

public class PlanetTypeMapDao extends BaseMapDao implements PlanetTypeDao {
    
    public static final String NAME = "planetType";
    
    public PlanetType create(String type) {
        PlanetType pt = (PlanetType) db.createModel(NAME);
        pt.setType(type);
        update(pt);
        return pt;
    }
    
    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.values();
    }   
    
    public void update(PlanetType planetType) {
        Map m = db.getMap(NAME);
        m.put(planetType.getType(), planetType);
    }
    
    public PlanetType get(String type) {
        return get(type, false);
    }
    
    public PlanetType get(String type, boolean create) {
        Map m = db.getMap(NAME);
        PlanetType pt = (PlanetType) m.get(type);
        if (pt == null && create) {
            pt = create(type);
        }
        return pt;
    }
    
    public void remove(PlanetType planetType) {
        Map m = db.getMap(NAME);
        m.remove(planetType.getType());   
    }
    
}
