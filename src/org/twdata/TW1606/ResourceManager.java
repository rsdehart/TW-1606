package org.twdata.TW1606;

import java.io.*;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.twdata.TW1606.util.*;

/**
 *  The socket plugin acts as the data source for networked operations. <P>
 *
 *  <B>Maintainer:</B> Matthias L. Jugel
 *
 * @author     Matthias L. Jugel, Marcus Meißner
 * @version    $Id: ResourceManager.java,v 1.11 2004/09/20 06:11:15 mrdon Exp $
 */
public class ResourceManager {
    
    protected File kdir;
    protected static final Logger log = Logger.getLogger(ResourceManager.class);
    protected Backup backup;
    protected boolean newBuild = false;
    protected List backupPaths;
    
    private void initHomeDirectory() {
        String home = System.getProperty("user.home");
        if (home != null) {
            File base = new File(home);
            kdir = new File(base, ".tw1606");
            if (!kdir.exists()) {
                if (log.isInfoEnabled()) {
                    log.info("Creating tw1606 home directory:"+kdir.getAbsolutePath());
                }
                kdir.mkdir();
            }
        }
    }
    
    public void setBasePath(String path) {
        kdir = new File(path);
    }
    
    public void setBackup(Backup backup) {
        this.backup = backup;
    }
    
    public void setBackupPaths(List paths) {
        backupPaths = paths;
    }
    
    public void init() {
        if (kdir == null) {
            initHomeDirectory();
        }
        if (kdir != null) {
            log.info("Initializing resource manager for "+kdir.getAbsolutePath());   
            try {
                File fbuild = new File(kdir, "/conf/build.number");
                if (fbuild.exists()) {
                    int oldb = getBuildNumber(new FileInputStream(fbuild));
                    int newb = getBuildNumber(getClass().getResourceAsStream("/conf/build.number"));
                    log.info("Old build:"+oldb+" new build:"+newb);
                    if (newb > oldb) {
                        newBuild = true;
                    }
                } else {
                    newBuild = true;
                }
    
                
                if (newBuild) {
                    try {
                        String[] paths = new String[backupPaths.size()];
                        backupPaths.toArray(paths);
                        backup.backupFiles(kdir, paths, "backup.zip");
                        
                        backup.restore(kdir, getClass().getResourceAsStream("/resources.zip"));
                    } catch (IOException ex) {
                        log.error(ex, ex);
                    }
                } 
            } catch (IOException ex) {
                log.error("Unable to determine build number", ex);
            }    
        } else {
            log.warn("Unable to locate home directory, keeping resources in the jar");
        }
    }
    
    public boolean isNewBuild() {
        return newBuild;
    }
    
    private int getBuildNumber(InputStream in) {
        Properties props = new Properties();
        try {
            props.load(in);
            return Integer.parseInt(props.getProperty("build.number"));
        } catch (Exception ex) {
            log.warn("Unable to load build number", ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {}
        }
        return -1;
    }

    public File getDirectory(String path) {
        File f = new File(kdir, path);
        if (log.isDebugEnabled()) {
            log.debug("locating:"+f.getAbsolutePath());
        }
        if (!f.exists()) {
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        return f;
    }
    
    public File getFile(String path) {
        File f = new File(kdir, path);
        if (log.isDebugEnabled()) {
            log.debug("locating:"+f.getAbsolutePath());
        }
        return f;
    }
    
    /** 
     *  Get a resource's input stream, extracting to the filesystem first
     *  if desired.
     *
     *@param path The path to the resource
     *@param extract If the resource should be extracted from the jar first
     */
    public InputStream getResource(String path) {
        InputStream in = null;
        log.debug("trying to load:"+path);
        if (log.isDebugEnabled()) {
            log.debug("Trying to load from jar");
        }
        in = getClass().getResourceAsStream(path);
        
        if (in == null) {
            // Only try getting the file from the filesystem if a home directory 
            // exists
            if (kdir != null) {
                File f = new File(kdir, path);
                try {
                    // If the file can be found
                    if (f.exists()) {
                        if (log.isDebugEnabled()) {
                            log.debug("File located, loading from filesystem");
                        }
                        in = new FileInputStream(f);
                    } 
                } catch (IOException ex) {
                    log.warn("Unable to load from filesystem:"+f.getAbsolutePath(), ex);
                }
            }
        }
        return in;
    }
    
    /** 
     *  Get a resource's stream reader
     *
     *@param path The path to the resource
     *@param extract If the resource should be extracted from the jar first
     */
    public Reader getResourceAsReader(String path) {
        InputStream in = getResource(path);
        Reader r = null;
        if (in != null) {
            r = new InputStreamReader(in);
        }
        return r;
    }
        
}
