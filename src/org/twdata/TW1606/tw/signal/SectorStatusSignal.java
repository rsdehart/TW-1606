package org.twdata.TW1606.tw.signal;

import org.werx.framework.bus.signals.BusSignal;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;

public class SectorStatusSignal extends BusSignal{
    
    private SectorDao sectorDao;
    private Sector sector;
    
    public SectorStatusSignal(SectorDao dao, Sector sector) {
        this.sectorDao = dao;
        this.sector = sector;
    }
    
    public SectorDao getDao() {
        return sectorDao;
    }
    
    public Sector getSector() {
        return sector;
    }
}
