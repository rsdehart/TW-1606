package org.twdata.TW1606.tw;

import java.util.*;
import java.util.regex.*;
import org.twdata.TW1606.tw.model.*;

public class ComputerParser extends AbstractParser {

    private Pattern shipCatLinePtn;
    
    
    public ComputerParser() {
        super();
        /*
        <B> Merchant Cruiser
        */
        shipCatLinePtn = Pattern.compile("<([A-Z])> (.*)");
    }
    
    public void parseShipEntry(String line) {
        Matcher m = shipCatLinePtn.matcher(line);
        if (m.find()) {
            String id = m.group(1);
            String name = m.group(2);
            
            shipTypeDao.get(name, true);
        }
    }
}
