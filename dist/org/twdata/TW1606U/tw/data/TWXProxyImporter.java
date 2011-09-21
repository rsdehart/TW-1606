package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.*;
import org.twdata.TW1606U.tw.model.*;
import java.util.*;
import java.net.URL;
import org.apache.log4j.Logger;
import java.awt.Component;
import javax.swing.ProgressMonitor;

public class TWXProxyImporter {
    
   protected SectorDao sectorDao;
   protected PortDao portDao;
   protected GameDao gameDao;
   protected ShipDao shipDao;
   protected ShipTypeDao shipTypeDao;
   protected PlanetDao planetDao;
   protected PlanetTypeDao planetTypeDao;
   protected CorporationDao corpDao;
   protected PlayerDao playerDao;
   protected TWSession session;
   private static final Logger log = Logger.getLogger(TWXProxyImporter.class);
   
   public void setDaoManager(DaoManager dm) {
        sectorDao = (SectorDao)dm.getDao("sector");
        portDao = (PortDao)dm.getDao("port");
        gameDao = (GameDao)dm.getDao("game");
        shipDao = (ShipDao)dm.getDao("ship");
        shipTypeDao = (ShipTypeDao)dm.getDao("shipType");
        planetDao = (PlanetDao)dm.getDao("planet");
        planetTypeDao = (PlanetTypeDao)dm.getDao("planetType");
        playerDao = (PlayerDao)dm.getDao("player");
        corpDao = (CorporationDao)dm.getDao("corporation");
    }
    
    public void setSession(TWSession session) {
        this.session = session;
    }
    
    public boolean importData(Component parent, URL url) {
        
        InputStream in = null;
        ProgressMonitor pm = null;
        try {
            in = url.openStream();
            DataInputStream din = new DataInputStream(in);
            
            int size = readHeader(din);
            log.info("Importing "+size+" sectors");
            if (parent != null) {
                pm = new ProgressMonitor(parent, "Importing TWX Proxy database...", null, 1, size);
                pm.setMillisToPopup(1000);
            }
            for (int id = 1; id<=size; id++) {
                readSector(din, id);
                if (pm != null) {
                    if (pm.isCanceled()) {
                        return false;
                    }
                    pm.setProgress(id);
                }
            }
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
    
    private int readHeader(DataInputStream din) throws IOException {
        String id = readString(din, 4);
        int timeStamp = din.readInt();
        int ver = din.readInt();
        int sectors = din.readInt();
        int stardock = din.readInt();
        int sol = din.readInt();
        int alpha = din.readInt();
        int rylos = din.readInt();
        int crc32 = din.readInt();
        readByteArray(din, 220);
        
        if (alpha > 0) {
            addPort(alpha, "Alpha Centauri", 0);
        }
        if (rylos > 0) {
            addPort(rylos, "Rylos", 0);
        }
        if (stardock > 0) {
            addPort(stardock, "Stardock", 0);
        }
        session.getGame().setSectors(sectors);
        return sectors;
    }
    
    private void readSector(DataInputStream din, int id) throws IOException {
        byte secInfo = din.readByte();
        byte navhaz = din.readByte();
        short reserved2 = din.readShort();
        int sectorUpdate = din.readInt();
        int figs = din.readInt();
        short figOwner = din.readShort();
        byte figType = din.readByte();
        byte anom = din.readByte();
        short armids = din.readShort();
        short armidOwner = din.readShort();
        short limpets = din.readShort();
        short limpetOwner = din.readShort();
        int[] portAmounts = readIntArray(din, 3);
        byte[] portPercents = readByteArray(din, 3);
        byte numWarps = din.readByte();
        int[] warps = readIntArray(din, 6);
        int portUpdate = din.readInt();
        int density = din.readInt();
        readByteArray(din, 24);
        
        if (numWarps > -1) {
            
            Sector s = sectorDao.get(id, true);
            // TODO: handle destroyed, construction ports
            if (secInfo <= 9) {
                addPort(s, secInfo, portAmounts, portPercents, portUpdate);
            }
            s.setNavHaz(navhaz);
            s.setLastModified(new Date(sectorUpdate));
            s.setFighters(figs);
            switch (figType) {
                case 0 : s.setFighterType(s.MERCHENARY); break;
                case 1 : s.setFighterType(s.TOLL); break;
                case 2 : s.setFighterType(s.OFFENSIVE); break;
                case 3 : s.setFighterType(s.DEFENSIVE); break;
            }
            
            // TODO: handle fighter owners
            
            s.setMines(s.ARMID, armids);
            s.setMines(s.LIMPET, limpets);
            
            Sector warp;
            // TODO: handle mine owners
            for (int x=0; x<numWarps; x++) {
                warp = sectorDao.get(warps[x], true);
                s.addWarp(warp);
            }
            
            s.setAnomaly((anom == 1 ? true : false));
            s.setDensity(density);
            sectorDao.update(s);
        }
    }
    
    private void addPort(Sector sector, int cls, int[] amounts, byte[] percents, int ts) {
        Port p = portDao.get(sector.getId(), true);
        p.setPortClass(cls);
        for (int x=0; x<amounts.length; x++) {
            p.setCurProduct(x, amounts[x]);
            p.setMaxProduct(x, (int)((float)amounts[x] * ((float)percents[x]/100f)));
        }
        p.setLastModified(new Date(ts));
        sector.setPort(p);
        portDao.update(p);
    }
    
    private String readString(DataInputStream din, int size) throws IOException {
        byte[] buffer = new byte[size];
        din.readFully(buffer);
        return new String(buffer);
    }
    
    private int[] readIntArray(DataInputStream din, int size) throws IOException {
        int[] array = new int[size];
        for (int x=0; x<size; x++) {
            array[x] = din.readInt();
        }
        return array;
    }
    
    private byte[] readByteArray(DataInputStream din, int size) throws IOException {
        byte[] buffer = new byte[size];
        din.readFully(buffer);
        return buffer;
    }
        
    private void addPort(int sector, String name, int cls) {
        Sector s = sectorDao.get((int)sector, true);
        Port p = portDao.get((int)sector, true);
        p.setPortClass(cls);
        p.setName(name);
        portDao.update(p);
        s.setPort(p);
        sectorDao.update(s);
    }
}
