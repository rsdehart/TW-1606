package org.twdata.TW1606U.data;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import java.io.*;
import java.util.*;
import org.twdata.TW1606U.signal.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import org.twdata.TW1606U.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.zip.*;

public class JDBMDatastore implements Datastore {
    
    public static final String SESSION_MAP = "session";
    private static final char SESSION_SEPARATOR = '.';
    
    private ResourceManager rm;
    private boolean disableCommits = false;
    private boolean disableMapCaching = false;
    
    private boolean needsCommit = false;
    private Thread commitThread;
    
    private RecordManager db;
    private Map maps;
    private String curSessionName = null;
    
    private DaoManager dm;
    private ModelSupport ms;
    private MessageBus bus;
    
    private static final Logger log = Logger.getLogger(JDBMDatastore.class);
    
    public JDBMDatastore() {
        maps = new HashMap();
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.rm = rm;
    }
    
    public void setModelSupport(ModelSupport ms) {
        this.ms = ms;
    }
    
    public void setDaoManager(DaoManager dm) {
        this.dm = dm;
    }
    
    public void setDisableCommits(boolean val) {
        this.disableCommits = val;
    }
    
    public void setDisableMapCaching(boolean val) {
        this.disableMapCaching = val;
    }
    
    public void init() {
        log.info("initializing data store");
        File base = rm.getDirectory("/data");
        /*if (rm.isNewBuild()) {
            log.info("Backup old data files");
            File[] gameFiles = base.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("game.") && !name.endsWith(".bak") && !name.endsWith(".xml");
                }
            });
            for (int x=0; x<gameFiles.length; x++) {
                gameFiles[x].renameTo(new File(gameFiles[x].getAbsolutePath() + ".bak"));
                gameFiles[x].delete();
            }
        }
        */
            
        openRecordManager(base.getAbsolutePath()+File.separatorChar+"game");
        
        //if (rm.isNewBuild()) {
        //    loadFromXMLZip(rm.getDirectory("/data"));
        //}
        
        commitThread = new Thread() {
            public void run() {
                log.info("Database commit thread started");
                try {
                    while (!isInterrupted()) {
                        sleep(10*1000);
                        if (needsCommit) {
                            if (log.isDebugEnabled()) {
                                log.debug("Commiting database");
                            }
                            db.commit();
                        }
                    }
                } catch (InterruptedException ex) {
                    log.info("Database commit thread interrupted");
                } catch (IOException ex) {
                    log.warn("Unable to commit database", ex);
                }
                log.info("Commit thread stopped");
            }
        };
        
        commitThread.setName("DB Commits");
        commitThread.start();
    }
    
    void openRecordManager(String path) {
        try {
            db = RecordManagerFactory.createRecordManager(path);
            log.info("Opened datastore: "+path);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void channel(ShutdownSignal signal) {
        try {
            commitThread.interrupt();
            long start = System.currentTimeMillis();
            saveAsXMLZip(rm.getDirectory("/data"));
            if (log.isInfoEnabled()) {
                log.info("Backed up data as XML: "+(System.currentTimeMillis() - start)+" ms");
                start = System.currentTimeMillis();
            }
            db.close();
            if (log.isInfoEnabled()) {
                log.info("Saved datastore: "+(System.currentTimeMillis() - start)+" ms");
            }
        } catch (IOException ex) {
            log.error(ex, ex);
        }
        log.info("Shutdown datastore");
    }
    
    protected boolean loadFromXMLFiles(File base) {
        setDisableCommits(true);
        log.info("Trying to load from xml");
        File file = new File(base, SESSION_MAP+".xml");
        if (file.exists()) {
            log.info("Restoring sessions");
            XStream xs = getXStream();
            JDBMMapConverter cnv = new JDBMMapConverter(xs.getClassMapper(), "class", dm);
            xs.registerConverter(cnv);
            try {
                synchronized(maps) {
                    FileReader reader = new FileReader(file);
                    cnv.setMap(getMap(SESSION_MAP, false));
                    Map sessions = (Map) xs.fromXML(reader);
                    reader.close();
                    for (Iterator i = sessions.keySet().iterator(); i.hasNext(); ) {
                        final String session = (String)i.next();
                        File[] sessionFiles = base.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.startsWith(session+SESSION_SEPARATOR) && name.endsWith(".xml");
                            }
                        });
                        setDisableMapCaching(true);
                        for (int x=0; x<sessionFiles.length; x++) {
                            String name = sessionFiles[x].getName();
                            long start = System.currentTimeMillis();
                            reader = new FileReader(sessionFiles[x]);
                            String mapName = name.substring(0, name.lastIndexOf(SESSION_SEPARATOR));
                            cnv.setMap(getMap(mapName, false));
                            xs.fromXML(reader);
                            reader.close();
                            log.debug("Read in "+(System.currentTimeMillis() - start)+" ms");
                            maps.remove(mapName);
                        }
                        setDisableMapCaching(false);
                    }
                    maps.remove(SESSION_MAP);
                }
                setDisableCommits(false);
                return true;
             } catch (IOException ex) {
                log.error("Unable to load xml", ex);
            }
        }
        setDisableCommits(false);
        setDisableMapCaching(false);
        return false;
    }
    
    protected boolean loadFromXMLZip(File base) {
        setDisableCommits(true);
        log.info("Trying to load from zip");
        File file = new File(base, "gameXml.zip");
        if (!file.exists()) {
            return loadFromXMLFiles(base);
        } else {
            log.info("Restoring sessions from zip");
            XStream xs = getXStream();
            JDBMMapConverter cnv = new JDBMMapConverter(xs.getClassMapper(), "class", dm);
            xs.registerConverter(cnv);
            try {
                synchronized(maps) {
                    FileInputStream fin = new FileInputStream(file);
                    ZipInputStream zin = new ZipInputStream(fin);
                    InputStreamReader reader = new InputStreamReader(zin);
                    ZipEntry entry = null;
                    String name = null;
                    setDisableMapCaching(true);
                    while ((entry = zin.getNextEntry()) != null) {
                        name = entry.getName();
                        name = name.substring(0, name.length()-4);
                        long start = System.currentTimeMillis();
                        cnv.setMap(getMap(name, false));
                        xs.fromXML(reader);
                        log.debug("Read in "+(System.currentTimeMillis() - start)+" ms");
                        maps.clear();
                    }
                    setDisableMapCaching(false);
                }
                setDisableCommits(false);
                return true;
             } catch (IOException ex) {
                log.error("Unable to load xml from zip", ex);
            }
        }
        setDisableCommits(false);
        setDisableMapCaching(false);
        return false;
    }
    
    protected void saveAsXMLZip(File base) {
        FileOutputStream fout = null;
        XStream xs = getXStream();
        JDBMMapConverter cnv = new JDBMMapConverter(xs.getClassMapper(), "class", dm);
        xs.registerConverter(cnv);
        try {
            fout = new FileOutputStream(new File(base, "gameXml.zip"));
            ZipOutputStream zout = new ZipOutputStream(fout);
            OutputStreamWriter owriter = new OutputStreamWriter(zout);
            synchronized(maps) {
                // get names directory
                long namesId = db.getRoot(0);
                if (namesId != 0) {
                    Map dir = (Map)db.fetch(namesId);
                    Map.Entry entry;
                    Map map;
                    String name;
                    long id;
                    for (Iterator i = dir.entrySet().iterator(); i.hasNext(); ) {
                        entry = (Map.Entry)i.next();
                        name = (String)entry.getKey();
                        id = ((Long)entry.getValue()).longValue();
                        
                        if (id != namesId) {
                            map = getMap(name, false);
                            cnv.setMap(map);
                            ZipEntry zentry = new ZipEntry(name+".xml");
                            zout.putNextEntry(zentry);
                            xs.toXML(map, owriter);
                            zout.closeEntry();
                        }
                    }
                    zout.close();
                }
            }
         } catch (IOException ex) {
            log.error("Unable to save as xml", ex);
            try {
                fout.close();
            } catch (IOException exe) {}
        }
    }
    
    protected void saveAsXMLFiles(File base) {
        File file;
        FileWriter fout = null;
        XStream xs = getXStream();
        JDBMMapConverter cnv = new JDBMMapConverter(xs.getClassMapper(), "class", dm);
        xs.registerConverter(cnv);
        try {
            synchronized(maps) {
                // get names directory
                long namesId = db.getRoot(0);
                if (namesId != 0) {
                    Map dir = (Map)db.fetch(namesId);
                    Map.Entry entry;
                    Map map;
                    String name;
                    long id;
                    for (Iterator i = dir.entrySet().iterator(); i.hasNext(); ) {
                        entry = (Map.Entry)i.next();
                        name = (String)entry.getKey();
                        id = ((Long)entry.getValue()).longValue();
                        
                        if (id != namesId) {
                            map = getMap(name, false);
                            cnv.setMap(map);
                            file = new File(base, name+".xml");
                            fout = new FileWriter(file);
                            xs.toXML(map, fout);
                            fout.close();
                        }
                    }
                }
            }
         } catch (IOException ex) {
            log.error("Unable to save as xml", ex);
            try {
                fout.close();
            } catch (IOException exe) {}
        }
    }
    
    public void removeSession(String session) {
        try {
            // get names directory
            long namesId = db.getRoot(0);
            if (namesId != 0) {
                Map dir = (Map)db.fetch(namesId);
                String name;
                Map.Entry entry;
                long id;
                String prefix = session+SESSION_SEPARATOR;
                synchronized (maps) {
                    Set entries = new HashSet(dir.entrySet());
                    for (Iterator i = entries.iterator(); i.hasNext(); ) {
                        entry = (Map.Entry)i.next();
                        name = (String)entry.getKey();;
                        id = ((Long)entry.getValue()).longValue();
                        if (name.startsWith(prefix)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Removing "+name+" for session "+session);
                            }
                            maps.remove(name);
                            db.setNamedObject(name, 0);
                            db.delete(id);
                        } 
                    }
                }
            } else {
                throw new IllegalStateException("The names directory doesn't exist");
            }
        } catch (IOException ex) {
            log.error("Unable to remove session:"+session, ex);
        }
    }
    
    private XStream getXStream() {
        XStream xs = new XStream();
        xs.alias("session", SessionImpl.class);
        xs.alias("map", JDBMMap.class);
        if (ms != null) {
            ms.registerAliases(xs);
        }
        xs.setMode(xs.NO_REFERENCES);
        return xs;
    }
    
    public void channel(SessionStatusSignal signal) {
        String name = signal.getName();
        
        if (signal.STOP.equals(signal.getStatus())) {
            //close existing session-dependent maps
            synchronized(maps) {
                Map.Entry entry;
                for (Iterator i = maps.entrySet().iterator(); i.hasNext(); ) {
                    entry = (Map.Entry)i.next();
                    if (String.valueOf(entry.getKey()).indexOf(SESSION_SEPARATOR) > 0) {
                        i.remove();
                    }
                }
            }
            curSessionName = null;
        } else if (signal.START_REQUEST.equals(signal.getStatus())) { 
            if (log.isDebugEnabled()) {
                log.debug("starting session:"+name);
            }
            curSessionName = name;
            bus.broadcast(new SessionStatusSignal(SessionStatusSignal.START, signal.getSession()));
        }
    }
    
    public String getCurrentSession() {
        return curSessionName;
    }
    
    public Map getMap(String name, boolean sessionSpecific) {
        String fullName;
        if (sessionSpecific) {
            if (curSessionName == null) {
                throw new IllegalArgumentException("Unable to retrieve session map with no session loaded");
            }
            fullName = curSessionName+SESSION_SEPARATOR+name;
        } else {
            fullName = name;
        }
        
        Map m = null;
        synchronized (maps) {
            MapEntry me = _getMap(fullName);
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
            try {
                // create or load hashtable
                HTree hashtable = null;
                long recid = db.getNamedObject(name);
                if ( recid != 0 ) {
                    if (log.isDebugEnabled()) {
                        log.debug( "Reloading existing hashtable: "+name);
                    }
                    hashtable = HTree.load( db, recid );
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug( "Creating new hashtable: "+name);
                    }
                    hashtable = HTree.createInstance( db );
                    db.setNamedObject(name, hashtable.getRecid() );
                }
                synchronized(maps) {
                    MapEntry me = new MapEntry(new JDBMMap(hashtable)); 
                    maps.put(name, me);
                }
            } catch (IOException ex) {
                log.error(ex, ex);
            }
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

    protected class KeyComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            boolean b1 = ((String)o1).indexOf(SESSION_SEPARATOR) > -1;
            boolean b2 = ((String)o2).indexOf(SESSION_SEPARATOR) > -1;
            if (!b1 && b2) {
                return -1;
            } else if (b2 && !b1) {
                return 1;
            } else {
                return ((String)o1).compareTo((String)o2);
            }
        } 
        
        public boolean equals(Object o) {
            return false;
        }
    }
    
    protected class JDBMMap extends HashMap {
        
        private HTree data;
        
        public JDBMMap(HTree data) {
            this.data = data;
            try {
                FastIterator iter = data.keys();
                Object key;
                Object value;
                while ((key = iter.next()) != null) {
                    value = data.get(key);
                    /*if (value instanceof DaoAwareModel) {
                        ((DaoAwareModel)value).setDaoManager(dm);
                    }
                    */
                    super.put(key, value);
                }
            } catch (IOException ex) {
                log.error(ex, ex);
            }
        }   
        
        public Object put(Object key, Object value) {
            try {
                data.put(key, value);
                if (!disableCommits) {
                    needsCommit = true;
                    //db.commit();
                }
            } catch (IOException ex) {
                log.error(ex, ex);
            }
            if (!disableMapCaching) {
                return super.put(key, value);
            } else {
                return null;
            }
        }
        
        public Object remove(Object key) {
            try {
                data.remove(key);
                if (!disableCommits) {
                    needsCommit = true;
                    //db.commit();
                }
            } catch (IOException ex) {
                log.error(ex, ex);
            }
            if (!disableMapCaching) {
                return super.remove(key);
            } else {
                return null;
            }
        }
    }
    
}
