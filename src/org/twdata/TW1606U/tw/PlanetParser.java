package org.twdata.TW1606U.tw;

import java.util.*;
import java.util.regex.*;
import org.twdata.TW1606U.tw.model.*;

public class PlanetParser extends AbstractParser {

    private Pattern[] displayPtn;
    private Pattern productPtn = Pattern.compile("[0-9,]+|N/A");
    private int disPos;
    private int prodPos;
    private Planet planet;
    private PlanetType planetType;
    private Ship ship;
    
    /*
    Planet #2 in sector 138: Ferrengal
    Class M, Earth Type
    Created by: jim
    Claimed by: jim and his friends [1]
    
      Item    Colonists  Colonists    Daily     Planet      Ship      Planet  
               (1000s)   2 Build 1   Product    Amount     Amount     Maximum 
     -------  ---------  ---------  ---------  ---------  ---------  ---------
    Fuel Ore        773          3        257      6,045          0    100,000
    Organics        774          7        110      3,518          0    100,000
    Equipment       772         13         59      2,037          0    100,000
    Fighters        N/A         55         42         42      5,090  1,000,000
    
    Planet has a level 3 Citadel, treasury contains 183,609 credits.
    Military reaction=40%, QCannon power=6%, AtmosLvl=30%, SectLvl=10%
    */
    public PlanetParser() {
        super();
        displayPtn = new Pattern[] {
            Pattern.compile("Planet #(\\d+) in sector (\\d+): (.*)"),
            Pattern.compile("Class (\\w+), (.*)"),
            Pattern.compile("Created by: (.*)"),
            Pattern.compile("Claimed by: (.+) \\[(\\d+)\\]"),
            Pattern.compile("Claimed by: (.+)"),
            Pattern.compile("Planet has a level (\\d) Citadel, treasury contains ([0-9,]+)"),
            Pattern.compile("ion=(\\d+)%, QCannon power=(\\d+)%, AtmosLvl=(\\d+)%, SectLvl=(\\d+)")
        };
    }
    
    public void parsePlanetDisplay(String line) {
        if (line.length() > 8 && line.charAt(7) == '#') {
            disPos = 0;
            prodPos = 0;
        }
        if (disPos < displayPtn.length) {
            Matcher m = displayPtn[disPos].matcher(line);
            if (m.find()) {
                Player p;
                switch (disPos) {
                    case 0 : Sector s = sectorDao.get(parseInt(m.group(2)), true);
                             planet = makePlanet(s, m.group(3), parseInt(m.group(1)));
                             session.setPlanet(planet);
                             break;
                    case 1 : planetType = planetTypeDao.get(m.group(1), true);
                             planetType.setName(m.group(2));
                             planet.setPlanetType(planetType);
                             break;
                    case 2 : if (m.group(1).charAt(0) != '<') {
                                p = playerDao.get(m.group(1), Player.TRADER, true);
                                planet.setCreator(p);
                             }   
                             break;
                    case 3 : Corporation c = corpDao.get(parseInt(m.group(2)), true);
                             c.setName(m.group(1));
                             corpDao.update(c);
                             planet.setOwner(c);
                             planetDao.update(planet);
                             disPos++;
                             break;
                    case 4 : p = playerDao.get(m.group(1), Player.TRADER, true);
                             planet.setOwner(p);
                             planetDao.update(planet);
                             break;
                    case 5 : planet.setCidadelLvl(parseInt(m.group(1)));
                             planet.setCredits(parseInt(m.group(2)));
                             planetDao.update(planet);
                             break;
                    case 6 : planet.setMilReactionLvl(parseInt(m.group(1)));
                             planet.setQCannonLvl(parseInt(m.group(2)));
                             planet.setAtmosReactionLvl(parseInt(m.group(3)));
                             planet.setSecReactionLvl(parseInt(m.group(4)));
                             planetDao.update(planet);
                             break;
                }
                disPos++;
            // only try another if we aren't on creator since creator could be 
            // <UNKNOWN>
            } else if (disPos != 2) {
                disPos++;
                parsePlanetDisplay(line);
            }
        }
    }
    
    public void parseProductDisplay(String line) {
        if (session.getTrader() == null) {
            if (log.isDebugEnabled()) {
                log.debug("Trader not set yet");
            }
            return;
        }    
        if (prodPos == 0) {
            ship = session.getTrader().getCurShip();
        }
        Matcher m = productPtn.matcher(line);
        int pos = 0;
        while (m.find()) {
            int val = parseInt(m.group(0));
            switch (pos++) {
                case 0  : if (prodPos != planet.FIGHTERS) {
                            planet.setColonists(prodPos, val);
                          }  
                          break;
                case 1  : planetType.setColonistsToBuildOne(prodPos, val);
                          break;
                case 3  : if (prodPos == planet.FIGHTERS) {
                            planet.setFighters(val);
                          } else {  
                            planet.setProduct(prodPos, val);
                          }  
                          break;
                case 4  : if (prodPos == planet.FIGHTERS) {
                            ship.setFighters(val);
                          } else {
                            ship.setHoldContents(prodPos, val);
                          }
                          break;
                case 5  : planetType.setMaxProduct(prodPos, val);
                          break;
            }
        }
        if (prodPos == planet.FIGHTERS) {
            planetDao.update(planet);
            planetTypeDao.update(planetType);
            shipDao.update(ship);
        }
        prodPos++;
    }
    
    private Planet makePlanet(Sector s, String name, int twid) {
        Planet[] planets = s.getPlanets(name);
        Planet p = null;
        
        // if more than one planet by the name in the sector
        if (planets.length > 1) {
            for (int x=0; x<planets.length; x++) {
                p = planets[x];
                if (p.getTWId() != twid) {
                    p = null;
                } else {
                    break;
                }
            }
            // planet twid not found in list
            if (p == null) {
                p = planets[0];
                p.setTWId(twid);
                p.setName(name);
            }
        // if planet by that name only one in sector    
        } else if (planets.length == 1) {
            p = planets[0];
            p.setTWId(twid);
            p.setName(name);
        // if no planets by that name in the sector
        } else {
            p = planetDao.create(s, twid);
            p.setName(name);
            s.addPlanet(p);
            sectorDao.update(s);
        }
        return p;
    }
    
}
