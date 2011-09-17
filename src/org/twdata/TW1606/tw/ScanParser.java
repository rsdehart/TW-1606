package org.twdata.TW1606.tw;

import java.util.*;
import java.util.regex.*;
import org.twdata.TW1606.tw.model.*;

public class ScanParser extends AbstractParser {

    private Pattern densityPtn;
    private Pattern secWarpsPtn;
    private Pattern depFigsPtn;
    private Sector lastProbeSector = null;
    
    public ScanParser() {
        super();
        
        // Sector ( 166) ==>              0  Warps : 4    NavHaz :     0%    Anom : No
        densityPtn = Pattern.compile("(\\d+)\\)?\\s+==>\\s+(\\d+)\\s+Warps :\\s+(\\d+)\\s+NavHaz :\\s+(\\d+)%\\s+Anom : (\\w+)");
        
        // Sector 516 has warps to sector(s) :  26 - 143 - 154 - 309 - 406 - 725
        secWarpsPtn = Pattern.compile("Sector (\\d+) has warps to sector\\(s\\) : (.*)");
        
        //    731          30T       Personal    Offensive            N/A
        depFigsPtn = Pattern.compile("(\\d+)\\s+(\\d+T?)\\s+((?:Corp)|(?:Personal))\\s+((?:Toll)|(?:Defensive)|(?:Offensive))");
    }
    
    public void reset() {
    }
    
    public void parseDeployedFighters(String line) {
        Matcher m = depFigsPtn.matcher(line);
        if (m.find()) {
            Player t = session.getTrader();
            Sector s = sectorDao.get(parseInt(m.group(1)), true);
            String figText = m.group(2);
            int figs = 0;
            if (figText.endsWith("T")) {
                figs = parseInt(figText.substring(0, figText.length() - 2));
                figs+=1000;
            } else {
                figs = parseInt(figText);
            }
            if (s.getFighters() != figs) {
                if (figs < 1000 || (s.getFighters()-figs) < 1000) {
                    s.setFighters(figs);
                }
            }
            
            if (m.group(3).charAt(0)=='C') {
                s.setFighterOwner(t.getCorporation());
            } else {
                s.setFighterOwner(t);
            }
            
            char type = m.group(4).charAt(0);
            switch (type) {
                case 'D' : s.setFighterType(s.DEFENSIVE);
                           break;
                case 'O' : s.setFighterType(s.OFFENSIVE);
                           break;
                case 'T' : s.setFighterType(s.TOLL);
                           break;
            }
            sectorDao.update(s);
        }
    }
    
    public void parseEtherProbe(String line) {
        int id = parseInt(line);
        Sector s = sectorDao.get(id, true);
        
        if (lastProbeSector != null) {
            lastProbeSector.addWarp(s);
            sectorDao.update(lastProbeSector);
        }
        lastProbeSector = s;
    }
    
    public void endEtherProbe() {
        lastProbeSector = null;
    }
    
    public void parseDensityScan(String line) {
        Matcher m = densityPtn.matcher(line); 
        if (m.find()) {
            Sector s = sectorDao.get(parseInt(m.group(1), 0), true);
            s.setDensity(parseInt(m.group(2), 0));
            s.setNavHaz(parseInt(m.group(4), 0));
            s.setAnomaly("Yes".equals(m.group(5)));
            sectorDao.update(s);
        }
    }
    
    public void parseSectorWarps(String line) {
        Matcher m = secWarpsPtn.matcher(line);
        if (m.find()) {
            Sector s = sectorDao.get(parseInt(m.group(1), 0), true);
            parseWarps(s, m.group(2));
            sectorDao.update(s);
        }
    }
}
