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

    public void clearCurrentData(){
        Collection sectors = sectorDao.getAll();
        for (Iterator i = sectors.iterator(); i.hasNext(); ){
            Sector s = (Sector)i.next();
            status.setGeneralStatus("Clearing "+s.getId());
            s.clearWarps();
            s.clearIncomingWarps();
            sectorDao.update(s);
             try {
             Thread.sleep(50);
               } catch (InterruptedException e) {
                  log.debug("ZTM Sleep (cleardata) exception: "+e.getMessage());
                }
       }
    }

    public void UpdateSectorTypes(){
        Collection sectors = sectorDao.getAll();
        for (Iterator i = sectors.iterator(); i.hasNext(); ){
            Sector s = (Sector)i.next();
            status.setGeneralStatus("Updating type "+s.getId());
            s.updateSectorType();
            sectorDao.update(s);
            try {
             Thread.sleep(75);
               } catch (InterruptedException e) {
                  log.debug("ZTM Sleep (updateSectorTypes) exception: "+e.getMessage());
                }
        }

    }

    public void FollowTunnel(Sector base, Sector prev){
        base.setInBubble(true);
        sectorDao.update(base);

            Sector[] mykids = base.getWarps();
            int otherkid=0;
            if (mykids[0].getId()==prev.getId()){
                otherkid=1;
            }
            if (mykids[otherkid].getSectorType() != null && mykids[otherkid].getSectorType().equals("T") && mykids.length>1) {
                FollowTunnel(mykids[otherkid],base);
            }
            else
            {
                mykids[otherkid].setSectorType("W");
                sectorDao.update(mykids[otherkid]);
            }

            try {
             Thread.sleep(50);
            }
            catch (InterruptedException e) {
             log.debug("ZTM Sleep (updateTunnels) exception: "+e.getMessage());
            }
    }

    public void UpdateTunnels(){
        Collection sectors = sectorDao.getAll();
        for (Iterator i = sectors.iterator(); i.hasNext(); ){
            Sector s = (Sector)i.next();
            status.setGeneralStatus("Updating type "+s.getId());
            if (s.getSectorType() != null){
                if (s.getSectorType().equals("B")){
                    Sector[] kids = s.getWarps();
                    if (kids[0].getSectorType()!=null && kids[0].getSectorType().equals("T")){
                        kids[0].setInBubble(true);
                        sectorDao.update(kids[0]);
                        FollowTunnel(kids[0],s);
                    }
                    else {
                        kids[0].setSectorType("W");
                        sectorDao.update(kids[0]);
                    }

                }
            }


        }

    }

    public void UpdateWheel(Sector wheel){
       Sector[] sectors = wheel.getWarps();
       Integer bubble = sectors.length;
       for (int x=0; x<sectors.length; x++) {
           if (sectors[x].isInBubble()){
               bubble--;
           }
       }

       if (bubble==1){
           wheel.setInBubble(true);
           sectorDao.update(wheel);
       }


    }

    public void UpdateWheels(){
        Collection sectors = sectorDao.getAll();
        for (Iterator i = sectors.iterator(); i.hasNext(); ){
            Sector s = (Sector)i.next();
            status.setGeneralStatus("Updating type "+s.getId());
            if (s.getSectorType() != null){
                if (s.getSectorType().equals("W")){
                  UpdateWheel(s);
                }
            }


        }

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
                     Thread.sleep(20);
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
