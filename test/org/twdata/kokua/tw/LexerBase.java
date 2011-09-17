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

public class LexerBase extends DaoBase {

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
   
    static {
        //BasicConfigurator.configure();
    }    
   
    public LexerBase(String name) {
        super(name);
    } 
    
    public void setUp() {
        factory = new XmlBeanFactory(new ClassPathResource("/beans-test.xml"));
        DaoManager dm = (DaoManager)factory.getBean("dao-manager");
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
    
    protected TWLexer createLexer(String text) {
        TWLexer lex = new TWLexer(new StringReader(text));
        factory.autowireBeanProperties(lex, factory.AUTOWIRE_BY_TYPE, true);
        lex.setFirstCommand(false);
        lex.init();
        return lex;
    }
    
    protected void runTextFile(String file) throws Exception {
        //BasicConfigurator.configure();
        InputStream in = getClass().getResourceAsStream(file);
        Reader reader = new InputStreamReader(in, "Cp1252");
        Reader tmp = new BufferedReader(reader, 128);
        TWLexer lex = new TWLexer(tmp); //reader);
        factory.autowireBeanProperties(lex, factory.AUTOWIRE_BY_TYPE, true);
        lex.setFirstCommand(false);
        lex.init();
        lex.yylex();
    }

}    


