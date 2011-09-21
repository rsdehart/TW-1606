package org.twdata.TW1606U.tw.signal;

import org.werx.framework.bus.signals.BusSignal;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.model.*;

public class ShipStatusSignal extends BusSignal{
    
    private ShipDao shipDao;
    private Ship ship;
    
    public ShipStatusSignal(ShipDao dao, Ship ship) {
        this.shipDao = dao;
        this.ship = ship;
    }
    
    public ShipDao getDao() {
        return shipDao;
    }
    
    public Ship getShip() {
        return ship;
    }
}
