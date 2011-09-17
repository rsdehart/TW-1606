package org.twdata.TW1606.data;

import java.io.*;
import java.util.*;
import org.twdata.TW1606.*;
import org.apache.commons.logging.*;

public class MapDatastore implements Datastore {
    
    public static final String DEFAULT_SESSION = "def";
    
    private Map maps;
    private String curSessionName = DEFAULT_SESSION;
    
    private DaoManager dm;
    private ModelSupport ms;
    
    private static final Log log = LogFactory.getLog(MapDatastore.class);
    
    public MapDatastore() {
        maps = new HashMap();
    }
    
    public void setModelSupport(ModelSupport ms) {
        this.ms = ms;
    }
    
    public void setDaoManager(DaoManager dm) {
        this.dm = dm;
    }
    
    public String getCurrentSession() {
        return curSessionName;
    }
    
    public void init() {
    }
    
    public void removeSession(String name) {
        // TODO: need to implement this
    }
    
    public Map getMap(String name, boolean sessionSpecific) {
        String fullName;
        if (sessionSpecific) {
            fullName = curSessionName+"/"+name;
        } else {
            fullName = name;
        }
        
        Map m = null;
        synchronized (maps) {
            MapEntry me = _getMap(name);
            m = me.map;
            
            if (!me.initialized) {
                Object o;
                for (Iterator i = m.values().iterator(); i.hasNext(); ) {
                    o = i.next();
                    if (o instanceof DaoAwareModel) {
                        ((DaoAwareModel)o).setDaoManager(dm);
                    }
                }
                me.initialized = true;
            }
        }    
        return m;
    }
    
    public Map getMap(String name) {
        return getMap(name, true);
    }
    
    private MapEntry _getMap(String name) {
        if (!maps.containsKey(name)) {
                MapEntry me = new MapEntry(new HashMap()); 
                maps.put(name, me);
        } 
        return (MapEntry)maps.get(name);
    }
    
    public Object createModel(String name) {
        DaoAwareModel dam = ms.createModel(name);
        dam.setDaoManager(dm);
        return dam;
    }

    protected class MapEntry {
        public boolean initialized = false;
        public Map map;

        public MapEntry(Map map) {
            this.map = map;
            initialized = false;
        }
    }    
    
}
