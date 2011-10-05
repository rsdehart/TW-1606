package org.twdata.TW1606U.tw;

import java.util.*;
import java.util.regex.*;
import org.twdata.TW1606U.tw.model.*;

public class CimParser extends AbstractParser {

    private Pattern plotPtn;
    private Pattern warpsIntroPtn;
    private Pattern warpsPtn;
    private Pattern portPtn;
    private static final int[][] CLASSES = new int[][] {
        {-1, -1, -1},
        { 0,  0,  1},
        { 0,  1,  0},
        { 1,  0,  0},
        { 1,  1,  0},
        { 1,  0,  1},
        { 0,  1,  1},
        { 1,  1,  1},
        { 0,  0,  0}, 
        {-1, -1, -1}
    };
    
    public CimParser() {
        super();
        
        // 300 > (248) > (753) > 359 > 892 > (598) > (600)
        plotPtn = Pattern.compile("\\(?(\\d+)\\)?(?: > )?");
        
        // 983    7   84  675  731  737
        warpsIntroPtn = Pattern.compile("(\\d+)");
        warpsPtn = Pattern.compile("\\s+(\\d+)");
        
        //3 - 2910 100% - 2840 100%   2500 100% 
        portPtn = Pattern.compile("(\\d+) (-?)\\s+(\\d+)\\s+(\\d+)% (-?)\\s+(\\d+)\\s+(\\d+)% (-?)\\s+(\\d+)\\s+(\\d+)%");
    }
    
    public void parseCoursePlot(String line) {
        Matcher m = plotPtn.matcher(line);
        m.find();
        Sector prev = sectorDao.get(parseInt(m.group(1)), true);
        Sector next;
        while (m.find()) {
            next = sectorDao.get(parseInt(m.group(1)), true);
            prev.addWarp(next);
            sectorDao.update(prev);
            prev = next;
        }
    }
    
    public void parseWarps(String line) {
        Matcher m = warpsIntroPtn.matcher(line);
        m.find();
        Sector base = sectorDao.get(parseInt(m.group(1)), true);

        m = warpsPtn.matcher(line);
        Sector warp;
        while (m.find()) {
            warp = sectorDao.get(parseInt(m.group(1)), true);
            base.addWarp(warp);
        }
        sectorDao.update(base);
    }
    
    public void parsePort(String line) {
        Matcher m = portPtn.matcher(line);
        int[] cls = new int[3];
        int grp = 2;
        int amt, per;
        if (m.find()) {
            int portId = parseInt(m.group(1));
            Port p = portDao.get(portId);
            if (p == null) {
                p = portDao.create(portId);
                Sector s = sectorDao.get(portId, true);
                s.setPort(p);
                sectorDao.update(s);
            }
            for (int x=p.FUEL_ORE; x<=p.EQUIPMENT; x++) {
                cls[x] = "-".equals(m.group(grp++)) ? 0 : 1;
                amt = parseInt(m.group(grp++));
                per = parseInt(m.group(grp++));
                p.setCurProduct(x, amt);
                if (per > 0) {
                    p.setMaxProduct(x, (amt*100)/per);
                }
            }
            for (int x=1; x<CLASSES.length-1; x++) {
                if (Arrays.equals(CLASSES[x], cls)) {
                    p.setPortClass(x);
                    break;
                }
            }
            portDao.update(p);
        }
    }
}
