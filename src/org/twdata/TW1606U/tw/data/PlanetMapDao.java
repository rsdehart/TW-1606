package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;

public class PlanetMapDao extends BaseMapDao implements PlanetDao {
    
    public static final String NAME = "planet";
    
    private Planet create() {
        Planet p = (Planet)db.createModel(NAME);
        
        //find unique id
        Random rnd = new Random();
        while (p.getId() == 0) {
            int id = rnd.nextInt();
            if (get(id) == null) {
                p.setId(id);
            }
        }
        return p;
    }
    public Planet create(Sector sector, String name) {
        Planet p = create();
        p.setCurSector(sector);
        p.setName(name);
        update(p);
        return p;
    }
    
    public Planet create(Sector sector, int twid) {
        Planet p = create();
        p.setCurSector(sector);
        p.setTWId(twid);
        update(p);
        return p;
    }
    
    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.entrySet();
    }
    
    public void update(Planet planet) {
        Map m = db.getMap(NAME);
        m.put(new Integer(planet.getId()), planet);    
    }
    
    public Planet get(int id) {
        Map m = db.getMap(NAME);
        return (Planet) m.get(new Integer(id));
    }
    
    public Planet getByTWId(int id){
        Map m = db.getMap(NAME);
        Planet p;
        for (Iterator i = m.values().iterator(); i.hasNext(); ) {
            p = (Planet)i.next();
            if (p.getTWId() == id) {
                return p;
            }
        }
        return null;
    }
    
    public Planet getBySectorAndName(Sector sector, String name) {
        Planet p;
        Map m = db.getMap(NAME);
        int sid = sector.getId();
        for (Iterator i = m.values().iterator(); i.hasNext(); ) {
            p = (Planet)i.next();
            if (p.getCurSector().getId() == sid && name.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }
    
    public void remove(Planet planet) {
        Map m = db.getMap(NAME);
        m.remove(new Integer(planet.getId()));   
    }
    
}
