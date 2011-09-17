package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.tw.signal.*;
import java.util.*;

public class ShipMapDao extends BaseMapDao implements ShipDao {
    
    public static final String NAME = "ship";
    
    private Ship create() {
        Ship s = (Ship) db.createModel(NAME);
        
        //find unique id
        Random rnd = new Random();
        while (s.getId() == 0) {
            int id = rnd.nextInt();
            if (get(id) == null) {
                s.setId(id);
            }
        }
        return s;
    }
    
    public Ship create(String name) {
        Ship s = create();
        s.setName(name);
        update(s);
        return s;
    }
    
    public Ship create(int twid) {
        Ship s = create();
        s.setTWId(twid);
        if (log.isDebugEnabled()) {
            log.debug("creating ship:"+s.getId());
        }
        update(s);
        return s;
    }
    
    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.values();
    }
    
    public void update(Ship ship) {
        Map m = db.getMap(NAME);
        m.put(new Integer(ship.getId()), ship);
        bus.broadcast(new ShipStatusSignal(this, ship));
    }
    
    public Ship get(int id) {
        Map m = db.getMap(NAME);
        return (Ship) m.get(new Integer(id));
    }
    
    public void remove(Ship ship) {
        Map m = db.getMap(NAME);
        m.remove(new Integer(ship.getId()));
    }
    
    public Collection getBySector(Sector sec) {
        Map m = db.getMap(NAME);
        List lst = new ArrayList();
        Ship ship;
        for (Iterator i = m.values().iterator(); i.hasNext(); ) {
            ship = (Ship)i.next();
            if (ship.getCurSector().getId() == sec.getId()) {
                lst.add(ship);
            }
        }
        return lst;
    }
    
    public Ship getByTWId(int twid) {
        return getByTWId(twid, false);
    }
    
    public Ship getByTWId(int twid, boolean create) {
        Map m = db.getMap(NAME);
        Ship ship = null;
        for (Iterator i = m.values().iterator(); i.hasNext(); ) {
            ship = (Ship)i.next();
            if (ship.getTWId() == twid) {
                return ship;
            }
        }
        if (create) {
            ship = create(twid);
        }
        return ship;
    }
    
}
