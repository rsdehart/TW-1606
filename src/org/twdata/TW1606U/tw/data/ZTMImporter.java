package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;
import java.net.URL;
import org.apache.log4j.Logger;
import java.awt.Component;
import javax.swing.ProgressMonitor;
import org.twdata.TW1606U.tw.gui.StatusGroup;

public class ZTMImporter {
    
   protected SectorDao sectorDao;
   protected TWSession session;
   
   private static final Logger log = Logger.getLogger(ZTMImporter.class);
   public CimParser cimParser = new CimParser();
   private StatusGroup status;
   public void setCimParser(CimParser tp) {
        cimParser = tp;
    }

   public void setDaoManager(DaoManager dm) {
        cimParser.setDaoManager(dm);
        sectorDao = (SectorDao)dm.getDao("sector");
    }

    public void setStatus(StatusGroup st) {
        status = st;
    }

    public void setSession(TWSession session) {
        this.session = session;
    }
    
    public boolean importData(Component parent, URL url) {
        
        InputStream in = null;
        ProgressMonitor pm = null;
        String input;
        Integer id = 1;
        try {
            in = url.openStream();
            //DataInputStream din = new DataInputStream(in);
            BufferedReader din = new BufferedReader(new InputStreamReader(in));

            log.info("Importing sectors");
         //   if (parent != null) {
           //     pm = new ProgressMonitor(parent, "Importing ZTM database...", null, 1, 1000);
             //   pm.setMillisToPopup(1000);
         //   }
            while ((input = din.readLine()) != null) {
                if (input!=null)
                {
                    try {
                     Thread.sleep(10);
                       } catch (InterruptedException e) {
                          log.debug("ZTM Sleep exception: "+e.getMessage());
                        }
                    log.info(input);
                status.setGeneralStatus("Importing "+input);
                cimParser.parseWarps(input);
                if (pm != null) {
                    if (pm.isCanceled()) {
                        return false;
                    }
                    pm.setProgress(id);
                }
//                id+=1;
//                if (id>1000) id=1;
                }
            }
            status.setGeneralStatus("ZTM Import Finished");
            if (pm != null) {
                pm.close();
            }
            in.close();
            return true;
        } catch (IOException ex) {
            try {
                in.close();
            } catch (IOException e) {}
        }
        return false;
    }
    
    
}
