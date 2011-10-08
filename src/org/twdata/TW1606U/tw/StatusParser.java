package org.twdata.TW1606U.tw;

import java.util.*;
import java.util.regex.*;
import org.twdata.TW1606U.tw.model.*;

public class StatusParser extends AbstractParser {

    private Pattern[] compactPtn;
    private Pattern[] infoPtn;
    private StringBuffer buffer;
    private int infoIndex = 0;
    private String[] goodRanks;
    private String[] evilRanks;
    private Pattern alignPtn;
    private Pattern cmdPtn;
    
    
    /*
    Sect 69Turns 65,452Creds 695,151Figs 2,500Shlds 400Hlds 75Ore 10Org 10
     Equ 15Col 0Phot 0Armd 10Lmpt 0GTorp 0TWarp NoClks 0Beacns 0AtmDt 0
     Crbo 100EPrb 10MDis 10PsPrb NoPlScn NoLRS NoneAln 33Exp 150
     Corp 1Ship 1 MerCru
    */
    
    public StatusParser() {
        super();
        compactPtn = new Pattern[] {
            Pattern.compile("Sect ([0-9,]+)"),
            Pattern.compile("Turns ([0-9,]+)"),
            Pattern.compile("Creds ([0-9,]+)"),
            Pattern.compile("Figs ([0-9,]+)"),
            Pattern.compile("Shlds ([0-9,]+)"),
            Pattern.compile("Hlds ([0-9,]+)"),
            Pattern.compile("Ore ([0-9,]+)"),
            Pattern.compile("Org ([0-9,]+)"),
            Pattern.compile("Equ ([0-9,]+)"),
            Pattern.compile("Col ([0-9,]+)"),
            Pattern.compile("Phot ([0-9,]+)"),
            Pattern.compile("Armd ([0-9,]+)"),
            Pattern.compile("Lmpt ([0-9,]+)"),
            Pattern.compile("GTorp (\\d+)"),
            Pattern.compile("TWarp (\\w+)"),
            Pattern.compile("Clks (\\d+)"),
            Pattern.compile("Beacns (\\d+)"),
            Pattern.compile("AtmDt (\\d+)"),
            Pattern.compile("Crbo ([0-9,]+)"),
            Pattern.compile("EPrb ([0-9,]+)"),
            Pattern.compile("MDis ([0-9]+)"),
            Pattern.compile("PsPrb (\\w+)"),
            Pattern.compile("PlScn (\\w+)"),
            Pattern.compile("LRS (\\w+)"),
            Pattern.compile("Aln (-?[0-9,]+)"),
            Pattern.compile("Exp ([0-9,]+)"),
            Pattern.compile("Corp (\\d+)"),
            Pattern.compile("Ship (\\d+) (\\w+)")
        };
        
        /*
        Trader Name    : Civilian jim bob
        Rank and Exp   : 0 points, Alignment=33 Tolerant
        Corp           # 1, jim
        Ship Name      : j
        Ship Info      : IonStream Merchant Cruiser Ported=0 Kills=0
        Date Built     : 02:31:53 PM Mon Feb 08, 2016
        Turns to Warp  : 3
        Current Sector : 82
        Turns left     : 65520
        Total Holds    : 75 - Empty=75
        Fighters       : 2,500
        Credits        : 949,999
        */
        infoPtn = new Pattern[] {
            Pattern.compile("Trader Name\\s+: (.*)"),
            Pattern.compile("Exp\\s+: ([0-9,]+) points, Alignment=(-?[0-9,]+)"),
            Pattern.compile("Times Blown Up\\s+: ([0-9,]+)"),
            Pattern.compile("Corp\\s+# (\\d+), (.*)"),
            Pattern.compile("Ship Name\\s+: (.*)"),
            Pattern.compile("Ship Info\\s+: (.*) Ported"),
            Pattern.compile("Turns to Warp\\s+: (\\d+)"),
            Pattern.compile("Turns left\\s+: (\\d+|Unlimited)"),
            Pattern.compile("Total Holds\\s+: (\\d+) -( Fuel Ore=(\\d+))?( Organics=(\\d+))?( Equipment=(\\d+))?( Colonists=(\\d+))?"),
            Pattern.compile("Fighters\\s+: ([0-9,]+)"),
            Pattern.compile("Shield points\\s+: ([0-9,]+)"),
            Pattern.compile("Credits\\s+: ([0-9,]+)")
        };
        
        cmdPtn = Pattern.compile("Command \\[TL=(\\d\\d):(\\d\\d):(\\d\\d)\\]:\\[(\\d+)\\]");
        alignPtn = Pattern.compile("alignment went (up|down) by \\d+");
        
        goodRanks = new String[] {
            "Civilian",
            "Private",
            "Private 1st Class",
            "Lance Corporal",
            "Corporal",
            "Sergeant",
            "Staff Sergeant",
            "Gunnery Sergeant",
            "1st Sergeant",
            "Sergeant Major",
            "Warrant Officer",
            "Chief Warrant Officer",
            "Ensign",
            "Lieutenant J.G.",
            "Lieutenant",
            "Lieutenant Commander",
            "Commander",
            "Captain",
            "Commodore",
            "Rear Admiral",
            "Vice Admiral",
            "Admiral",
            "Fleet Admiral"
        };

        evilRanks = new String[] {
            "Annoyance",
            "Nuisance 3rd Class",
            "Nuisance 2nd Class",
            "Nuisance 1st Class",
            "Menace 3rd Class",
            "Menace 2nd Class",
            "Menace 1st Class",
            "Smuggler 3rd Class",
            "Smuggler 2nd Class",
            "Smuggler 1st Class",
            "Smuggler Savant",
            "Robber",
            "Terrorist",
            "Pirate",
            "Infamous Pirate",
            "Notorious Pirate",
            "Dread Pirate",
            "Galactic Scourge",
            "Enemy of the State",
            "Enemy of the People",
            "Enemy of Humankind",
            "Heinous Overlord",
            "Prime Evil"
        };
        
    }
    
    public void setGoodRanks(String[] ranks) {
        goodRanks = ranks;
    }
    
    public void setEvilRanks(String[] ranks) {
        evilRanks = ranks;
    }
    
    public void reset() {
        buffer = new StringBuffer();
        infoIndex = 0;
    }
    
    public void parseCompactStatus(String line) {
        Matcher m = compactPtn[compactPtn.length - 1].matcher(line);
        buffer.append(line).append(":");
        if (m.find()) {
            Player trader = session.getTrader();
            Ship ship = trader.getCurShip();
            Sector sector;
            
            int shipId = parseInt(m.group(1), 0);
            if (ship == null) {
                ship = shipDao.getByTWId(shipId);
                if (ship == null) {
                    ship = shipDao.create(shipId);
                } 
            } else {
                ship.setTWId(shipId);
            }
            trader.setCurShip(ship);
            
            int index = -1;
            int pos = 0;
            line = buffer.toString();
            while (++index < compactPtn.length - 1) {
                m = compactPtn[index].matcher(line);
                if (m.find(pos)) {
                    switch (index) {
                        case 0  : sector = sectorDao.get(parseInt(m.group(1), 0), true);
                                  session.setSector(sector);
                                  break;
                        case 1  : trader.setTurns(parseInt(m.group(1), 0));
                                  break;
                        case 2  : trader.setCredits(parseInt(m.group(1), 0));
                                  break;
                        case 3  : ship.setFighters(parseInt(m.group(1), 0));
                                  break;
                        case 4  : ship.setShields(parseInt(m.group(1), 0));
                                  break;
                        case 5  : ship.setHolds(parseInt(m.group(1), 0)); 
                                  break;
                        case 6  : ship.setHoldContents(ship.FUEL_ORE, parseInt(m.group(1), 0)); 
                                  break;
                        case 7  : ship.setHoldContents(ship.ORGANICS, parseInt(m.group(1), 0)); 
                                  break;
                        case 8  : ship.setHoldContents(ship.EQUIPMENT, parseInt(m.group(1), 0)); 
                                  break;
                        case 9  : ship.setHoldContents(ship.COLONISTS, parseInt(m.group(1), 0)); 
                                  break;
                        case 10 : ship.setPhotonMissiles(parseInt(m.group(1), 0)); 
                                  break;
                        case 11 : ship.setMines(ship.ARMID, parseInt(m.group(1), 0)); 
                                  break;
                        case 12 : ship.setMines(ship.LIMPET, parseInt(m.group(1), 0));  
                                  break;
                        case 13 : ship.setGenesis(parseInt(m.group(1), 0)); 
                                  break;
                        case 14 : // TODO: handle transwarp drive 
                                  break;
                        case 15 : ship.setCloaks(parseInt(m.group(1), 0)); 
                                  break;
                        case 16 : ship.setBeacons(parseInt(m.group(1), 0)); 
                                  break;
                        case 17 : ship.setAtomics(parseInt(m.group(1), 0)); 
                                  break;                                 
                        case 18 : ship.setCrombiteLvl(parseInt(m.group(1), 0)); 
                                  break;                                 
                        case 19 : ship.setProbes(parseInt(m.group(1), 0)); 
                                  break;                                 
                        case 20 : ship.setMineDisrupters(parseInt(m.group(1), 0)); 
                                  break;                                 
                        case 21 : // TODO: handle psy probes
                                  break;
                        case 22 : ship.setPlanetScanner("Yes".equals(m.group(1)));
                                  break;
                        case 23 : if ("Dens".equals(m.group(1))) {
                                      ship.setLongRangeScanType(ship.DENSITY_SCAN);
                                  } else if ("Holo".equals(m.group(1))) {
                                      ship.setLongRangeScanType(ship.HOLO_SCAN);
                                  } else {
                                      ship.setLongRangeScanType(ship.NONE);
                                  }
                                  break;                                 
                        case 24 : trader.setAlignment(parseInt(m.group(1), 0)); 
                                  break;   
                        case 25 : trader.setExperience(parseInt(m.group(1), 0)); 
                                  break;           
                        case 26 : int corp = parseInt(m.group(1), 0);
                                  Corporation c = corpDao.get(corp, true);
                                  trader.setCorporation(c); 
                                  break;
                                                        
                    }
                    pos = m.end();
                }
            }
            shipDao.update(ship);
            playerDao.update(trader);
        }
    }
    
    public void parseInfoLine(String line) {
        Matcher m = infoPtn[infoIndex].matcher(line);
        Player t;
        Ship s;
        ShipType st;
        if (m.find()) {
            if (log.isDebugEnabled()) {
                log.debug("Looking for pattern "+infoIndex+" in "+line);
            }    
            switch (infoIndex) {
                case 0  : t = playerDao.get(findTraderName(m.group(1)), Player.TRADER, true);
                          session.setTrader(t);
                          break;
                case 1  : t = session.getTrader();
                          t.setExperience(parseInt(m.group(1), 0));
                          t.setAlignment(parseInt(m.group(2), 0));
                          break;
                case 3  : t = session.getTrader();
                          Corporation c = corpDao.get(parseInt(m.group(1)), true);
                          c.setName(m.group(2));
                          t.setCorporation(c);
                          break;
                case 4  : s = session.getTrader().getCurShip();
                          if (s == null) {
                              s = shipDao.create(m.group(1));
                              session.getTrader().setCurShip(s);
                          }
                          s.setName(m.group(1));
                          break;
                case 5  : st = findShipType(m.group(1));
                          session.getTrader().getCurShip().setShipType(st);
                          break;
                case 6  : int ttw = parseInt(m.group(1));
                          st = session.getTrader().getCurShip().getShipType();
                          if (st != null) {
                              session.getTrader().getCurShip().getShipType().setTurnsPerWarp(ttw);
                          }
                          break;
                case 7  : session.getTrader().setTurns(parseInt(m.group(1), 0));
                          break;
                case 8  : s = session.getTrader().getCurShip();
                          s.clearHolds();
                          s.setHolds(parseInt(m.group(1), 0));
                          for (int x=2; x<=8; x+=2) {
                              String str = m.group(x);
                              if (str != null) {
                                  switch (str.charAt(1)) {
                                      case 'F' : s.setHoldContents(s.FUEL_ORE, parseInt(m.group(x+1)));
                                                 break;
                                      case 'O' : s.setHoldContents(s.ORGANICS, parseInt(m.group(x+1)));
                                                 break;
                                      case 'E' : s.setHoldContents(s.EQUIPMENT, parseInt(m.group(x+1)));
                                                 break;
                                      case 'C' : s.setHoldContents(s.COLONISTS, parseInt(m.group(x+1)));
                                                 break;           
                                  }
                              } 
                          }
                          break;
                case 9  : s = session.getTrader().getCurShip();
                          s.setFighters(parseInt(m.group(1), 0));
                          shipDao.update(s);
                          break;
                case 10  : s = session.getTrader().getCurShip();
                          s.setShields(parseInt(m.group(1), 0));
                          shipDao.update(s);
                          break;
                case 11  : t = session.getTrader();
                          t.setCredits(parseInt(m.group(1), 0));
                          playerDao.update(t);
                          break;
            }
            infoIndex++;
        } else {
           //  In case the trader isn't in a corporation
            if (infoIndex==2) {
                infoIndex++;
                parseInfoLine(line);
            }
        }
    }
    
    public void parsePlanetPrompt(String line) {
        session.setPrompt(TWSession.PROMPT_PLANET);
    }
    
    public void parseStardockPrompt(String line) {
        session.setPrompt(TWSession.PROMPT_STARDOCK);
    }

    public void parseAutoWarpMove(String line) {
        int secId = parseInt(line);
        Sector s = sectorDao.get(secId, true);
        session.setSector(s);
        parseWarpMove(line);
    }    
    
    public void parseWarpMove(String line) {
        Player trader = session.getTrader();
        ShipType st = trader.getCurShip().getShipType();
        if (st != null) {
            trader.setTurns(trader.getTurns() - st.getTurnsPerWarp());
            playerDao.update(trader);
        }
    }    
    
    public void parseChangeAlignment(String line) {
        Matcher m = alignPtn.matcher(line);
        if (m.find()) {
            Player p = session.getTrader();
            int val = parseInt(line);
            if(p != null) {
                if ("up".equals(m.group(1))) {
                    p.setAlignment(p.getAlignment() + val);
                } else {
                    p.setAlignment(p.getAlignment() - val);
                }
                playerDao.update(p);
            }
        }
    }
    
    public void parseGainExperience(String line) {
        int exp = parseInt(line);
        Player p = session.getTrader();
        if(p != null) {
            p.setExperience(p.getExperience()+exp);
            playerDao.update(p);
        }
    }    
    public void parseLoseExperience(String line) {
        int exp = parseInt(line);
        Player p = session.getTrader();
        p.setExperience(p.getExperience()-exp);
        playerDao.update(p);
    } 
    
    public void parseTurnsLeft(String line) {
        int turns = parseInt(line);
        Player p = session.getTrader();
        if (p != null) {
            p.setTurns(turns);
            playerDao.update(p);
        }
    } 
    
    public void parseCommandPrompt(String line) {
        Matcher m = cmdPtn.matcher(line);
        if (m.find()) {
            int hours = parseInt(m.group(1), 0);
            int minutes = parseInt(m.group(2), 0);
            int seconds = parseInt(m.group(3), 0);
            // TODO: do something with the date
            
            int secId = parseInt(m.group(4), 0);
            Sector s = sectorDao.get(secId, true);
            session.setSector(s);
            session.setPrompt(TWSession.PROMPT_COMMAND);
        }
    }
    
    protected ShipType findShipType(String companyAndType) {
        String name;
        ShipType st = null;
        int pos = companyAndType.indexOf(' ');
        while (pos > -1) {
            name = companyAndType.substring(pos+1);
            st = shipTypeDao.get(name, false);
            if (st != null) {
                break;
            }
            pos = companyAndType.indexOf(' ',pos+1);
        }
        if (st == null) {
            log.warn("Cannot find ship type in string:"+companyAndType);
        }
        return st;
    }
    
    private String findTraderName(String nameAndRank) {
        int size = -1;
        for (int x=0; x<goodRanks.length; x++) {
            if (nameAndRank.startsWith(goodRanks[x])) {
                size = Math.max(size, goodRanks[x].length());
            }
        }
        for (int x=0; x<evilRanks.length; x++) {
            if (nameAndRank.startsWith(evilRanks[x])) {
                size = Math.max(size, evilRanks[x].length());
            }
        }
        if (size > -1) {
            return nameAndRank.substring(size+1);
        }
        log.warn("Unable to determine true name:"+nameAndRank);
        return nameAndRank;
    }
    
}
