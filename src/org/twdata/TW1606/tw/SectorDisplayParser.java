package org.twdata.TW1606.tw;

import java.util.*;
import java.util.regex.*;
import org.twdata.TW1606.tw.model.*;

public class SectorDisplayParser extends AbstractParser {

    private Pattern sectorPtn;
    private Pattern beaconPtn;
    private Pattern portPtn;
    private Pattern figPtn;
    private Pattern planetPtn;
    
    private boolean figsExist = false;
    private List foundPlanets;
    private Sector sector;
    
    public SectorDisplayParser() {
        super();
        sectorPtn = Pattern.compile(": ([0-9,]+) in ([0-9A-Za-z ]+)(\\(unexplored\\))?");
        beaconPtn = Pattern.compile(": (.*)");
        portPtn = Pattern.compile(": (.*), Class ([0-9])");
        figPtn = Pattern.compile(": ([0-9,]+) \\((.*)\\) \\[(.*)\\]");
        planetPtn = Pattern.compile("\\(([A-Za-z])\\) (.*)\\s*");
    }
    
    public void parseSectorNumber(String line) {
        Matcher m = sectorPtn.matcher(line);
        if (m.find()) {
            figsExist = false;
            foundPlanets = new ArrayList();
    
            int s = parseInt(m.group(1), -1);
            if (s == -1) {
                log.warn("Invalid sector header:"+line);
                return;
            }
            
            sector = sectorDao.get(s, true);
            sector.setVisited(m.group(3) == null);
            resetPlayersPresent();
            sectorDao.update(sector);
        } else {
            log.warn("Couldn't match sector number:"+line);
        }    
    }        
    
    public void parseBeacon(String line) {
        Matcher m = beaconPtn.matcher(line);
        if (m.find()) {
            sector.setBeacon(m.group(1));
        }
        sectorDao.update(sector);
    }
    
    public void parsePorts(String line) {
        Port p = sector.getPort();
	    if (line.indexOf("<=-DANGER-=>") > -1)
	    {       
	        if (p != null)
	        {
	            sector.setPort(null);
	        }
            portDao.remove(p);
	    }   
	    else
	    {
            if (p == null)
            {
                p = portDao.create(sector.getId());
                sector.setPort(p);
            }    
            
            Matcher m = portPtn.matcher(line);
            if (m.find()) {
                p.setName(m.group(1));
                p.setPortClass(Integer.parseInt(m.group(2)));
                portDao.update(p);
            }
	    } 
        sectorDao.update(sector);
    }
    
    public void parseNavHaz(String line) {
        sector.setNavHaz(parseInt(line, 0));
        sectorDao.update(sector);
    }

    public void parseFighters(String line) {
        Matcher m = figPtn.matcher(line);
        if (m.find()) {
            figsExist = true;
            sector.setFighters(parseInt(m.group(1), 0));
            String owner = m.group(2);
            if (owner.equals("yours")) {
                sector.setFighterOwner(session.getTrader());
            } else if (owner.equals("belong to your Corp")) {
                sector.setFighterOwner(session.getTrader().getCorporation());
            }
            // TODO: Should parse other trader or corp ownership	    
            
            String type = m.group(3);
            if ("Toll".equals(type))
            {
                sector.setFighterType(Sector.TOLL);		
            }
            else if ("Defensive".equals(type))
            {	
                sector.setFighterType(Sector.DEFENSIVE);		
            }
            else 
            {
                sector.setFighterType(Sector.OFFENSIVE);		
            }
            sectorDao.update(sector);
        }
    }
    
    public void parseWarps(String line) {
        if (!figsExist) {
            sector.setFighters(0);
            sector.setFighterType(Sector.NONE);
        }
        
        parseWarps(sector, line);
        addPlanets();
        sectorDao.update(sector);
    }
    
    public void parsePlanets(String line) {
        Matcher m = planetPtn.matcher(line);
        if (m.find()) {
            String type = m.group(1);
            String name = m.group(2);
            if (log.isDebugEnabled()) {
                log.debug("found planet type:"+type+" name:"+name);
            }
            
            foundPlanets.add(new FoundPlanet(type, name));
            sectorDao.update(sector);
        }
    }
    
    private void addPlanets() {
        Planet[] planets = sector.getPlanets();
        FoundPlanet fp;
        Planet existingPlanet, p;
        boolean exists = false;
        for (Iterator i = foundPlanets.iterator(); i.hasNext(); ) {
            fp = (FoundPlanet)i.next();
            exists = false;
            for (int x=0; x<planets.length; x++) {
                existingPlanet = planets[x];
                if (existingPlanet.getName().equals(fp.name)) {
                    exists = true;
                    break;
                } 
            }
            
            if (!exists) {
                p = planetDao.create(sector, fp.name);
                p.setPlanetType(planetTypeDao.get(fp.type, true));
                sector.addPlanet(p);
                planetDao.update(p);
            }
        }   
    }
        
    
    private void resetPlayersPresent()
	{
        sector.clearTradersPresent();
        sector.clearShipsPresent();
        sector.clearFedsPresent();
        sector.clearAliensPresent();
	}
    
    private class FoundPlanet {
        public String type;
        public String name;
        
        public FoundPlanet(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}
