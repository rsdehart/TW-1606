package org.twdata.TW1606.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.tw.signal.*;
import java.util.*;

public class PlayerMapDao extends BaseMapDao implements PlayerDao {
    
    public static final String NAME = "player";
    
    public Player create(String name, int type) {
        Player p = (Player) db.createModel(NAME);   
        p.setName(name);
        p.setType(type);
        update(p);
        return p;
    }
    
    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.values();
    }
    
    public Collection getAllByType(int type) {
        ArrayList list = new ArrayList();
        Map m = db.getMap(NAME);
        Player p;
        for (Iterator i = m.values().iterator(); i.hasNext(); ) {
            p = (Player)i.next();
            if (p.getType() == type) {
                list.add(p);
            }
        }
        return list;
    }   
    
    public void update(Player player) {
        Map m = db.getMap(NAME);
        m.put(player.getName() + player.getType(), player);
        bus.broadcast(new PlayerStatusSignal(this, player));
    }
    
    public Player get(String name, int type) {
        return get(name, type, false);
    }
    
    public Player get(String name, int type, boolean create) {
        Map m = db.getMap(NAME);
        Player p = (Player) m.get(name+type);
        if (p == null && create) {
            p = create(name, type);
        }
        return p;
    }
    
    public void remove(Player player) {
        Map m = db.getMap(NAME);
        m.remove(player.getName() + player.getType());   
    }
    
}
