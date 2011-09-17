package org.twdata.TW1606.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606.signal.*;
import java.util.*;

public class SessionMapDao extends BaseMapDao implements SessionDao {
    
    public static final String NAME = "session";
    
    public Session create(String name) {
        Session g = new SessionImpl();
        g.setName(name);
        update(g);
        return g;
    }
    
    public Collection getAll() {
        Map m = db.getMap(NAME, false);
        return m.values();
    }
    
    public void update(Session session) {
        Map m = db.getMap(NAME, false);
        m.put(session.getName(), session);
        //bus.broadcast(new SessionStatusSignal(this, session));
    }
    
    public Session get(String name) {
        return get(name, false);
    }
    
    public Session get(String name, boolean create) {
        Map m = db.getMap(NAME, false);
        Session g = (Session)m.get(name);
        if (g == null && create) {
            g = create(name);
        }
        return g;
    }
    
    public void remove(Session session) {
        Map m = db.getMap(NAME, false);
        m.remove(session.getName());   
        db.removeSession(session.getName());
    }
    
}
