package org.twdata.TW1606U.tw;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.twdata.TW1606U.data.DaoManager;
import org.twdata.TW1606U.tw.data.CorporationDao;
import org.twdata.TW1606U.tw.data.GameDao;
import org.twdata.TW1606U.tw.data.PlanetDao;
import org.twdata.TW1606U.tw.data.PlanetTypeDao;
import org.twdata.TW1606U.tw.data.PlayerDao;
import org.twdata.TW1606U.tw.data.PortDao;
import org.twdata.TW1606U.tw.data.SectorDao;
import org.twdata.TW1606U.tw.data.ShipDao;
import org.twdata.TW1606U.tw.data.ShipTypeDao;
import org.twdata.TW1606U.tw.model.Sector;

public abstract class AbstractParser {

    protected static final Pattern numPattern = Pattern.compile("-?[0-9]+");
    protected static final Pattern warpPtn = Pattern.compile("\\(?([0-9]+)");

    protected SectorDao sectorDao;
    protected PortDao portDao;
    protected GameDao gameDao;
    protected ShipDao shipDao;
    protected ShipTypeDao shipTypeDao;
    protected PlanetDao planetDao;
    protected PlanetTypeDao planetTypeDao;
    protected CorporationDao corpDao;
    protected PlayerDao playerDao;
    protected TWSession session;
    protected Logger log;

    public void setDaoManager(DaoManager dm) {
        sectorDao = (SectorDao) dm.getDao("sector");
        portDao = (PortDao) dm.getDao("port");
        gameDao = (GameDao) dm.getDao("game");
        shipDao = (ShipDao) dm.getDao("ship");
        shipTypeDao = (ShipTypeDao) dm.getDao("shipType");
        planetDao = (PlanetDao) dm.getDao("planet");
        planetTypeDao = (PlanetTypeDao) dm.getDao("planetType");
        playerDao = (PlayerDao) dm.getDao("player");
        corpDao = (CorporationDao) dm.getDao("corporation");
    }

    public void setSession(TWSession session) {
        this.session = session;
    }

    public AbstractParser() {
        log = Logger.getLogger(this.getClass());
    }

    protected int parseInt(String s) {
        return parseInt(s, 0);
    }

    protected int parseInt(String s, int def) {
        Matcher m = numPattern.matcher(s);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            sb.append(m.group());
        }
        if (sb.length() > 0) {
            return Integer.parseInt(sb.toString());
        }
        return def;
    }

    public void parseWarps(Sector sector, String line) {
        Matcher m = warpPtn.matcher(line);
        int w;
        Sector ws;
        while (m.find()) {
            w = Integer.parseInt(m.group(1));
            ws = sectorDao.get(w, true);
            sector.addWarp(ws);
        }
    }
}
