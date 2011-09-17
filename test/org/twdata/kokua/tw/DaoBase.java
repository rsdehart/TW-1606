package org.twdata.TW1606.tw;

import org.twdata.TW1606.tw.data.SectorDao;
import org.twdata.TW1606.tw.data.PlanetDao;
import org.twdata.TW1606.tw.data.PlanetTypeDao;
import org.twdata.TW1606.tw.data.GameDao;
import org.twdata.TW1606.tw.data.CorporationDao;
import org.twdata.TW1606.tw.data.PortDao;
import org.twdata.TW1606.tw.data.ShipDao;
import org.twdata.TW1606.data.DaoManager;
import org.twdata.TW1606.tw.data.ShipTypeDao;
import org.twdata.TW1606.tw.data.PlayerDao;
import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.springframework.core.io.ClassPathResource;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.data.*;
import org.twdata.TW1606.tw.model.*;
import org.apache.log4j.BasicConfigurator;

public class DaoBase extends TestCase {

    protected XmlBeanFactory factory;
    protected TWSession session;

    
    
    
     protected SectorDao sectorDao;
   protected PortDao portDao;
   protected GameDao gameDao;
   protected ShipDao shipDao;
   protected ShipTypeDao shipTypeDao;
   protected PlanetDao planetDao;
   protected PlanetTypeDao planetTypeDao;
   protected CorporationDao corpDao;
   protected PlayerDao playerDao;
   protected DaoManager dm;
   
    static {
        BasicConfigurator.configure();
    }    
   
    public DaoBase(String name) {
        super(name);
    } 
    
    public void setUp() {
        factory = new XmlBeanFactory(new ClassPathResource("/beans-test.xml"));
        dm = (DaoManager)factory.getBean("dao-manager");
        session = (TWSession)factory.getBean("session");
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
   

}    


