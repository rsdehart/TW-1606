package org.twdata.TW1606.tw.data;

import com.thoughtworks.xstream.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.data.*;

public class TWModelSupport implements ModelSupport {
    
    public void registerAliases(XStream xs) {
        xs.alias("sector", SectorImpl.class);
        xs.alias("port", PortImpl.class);
        xs.alias("game", GameImpl.class);
        xs.alias("corporation", CorporationImpl.class);
        xs.alias("player", PlayerImpl.class);
        xs.alias("planet", PlanetImpl.class);
        xs.alias("planetType", PlanetTypeImpl.class);
        xs.alias("ship", ShipImpl.class);
        xs.alias("shipType", ShipTypeImpl.class);
    }
    
    public DaoAwareModel createModel(String name) {
        return 
            "sector".equals(name)      ? (DaoAwareModel)new SectorImpl() :
            "port".equals(name)        ? (DaoAwareModel)new PortImpl() :
            "game".equals(name)        ? (DaoAwareModel)new GameImpl() :
            "corporation".equals(name) ? (DaoAwareModel)new CorporationImpl() :
            "player".equals(name)      ? (DaoAwareModel)new PlayerImpl() :
            "planet".equals(name)      ? (DaoAwareModel)new PlanetImpl() :
            "planetType".equals(name)  ? (DaoAwareModel)new PlanetTypeImpl() :
            "ship".equals(name)        ? (DaoAwareModel)new ShipImpl() :
            "shipType".equals(name)    ? (DaoAwareModel)new ShipTypeImpl() :
            null;
    }
}

