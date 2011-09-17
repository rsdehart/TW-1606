package org.twdata.TW1606.tw.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.swixml.SwingEngine;
import org.twdata.TW1606.ResourceManager;

public class ConnectionDialog extends JPanel {

    private SwingEngine swix;
    private JPanel panel;
    private ResourceManager res;
    private static final Logger log = Logger.getLogger(ConnectionDialog.class);
    private List<GameListing> gameListings = new ArrayList<GameListing>() {{
       add(new GameListing("MyTradeWars.com <f>", "mytradewars.com", 23, 2000, 5000, 'f', new Date()));
       add(new GameListing("MyTradeWars.com <0>", "mytradewars.com", 23, 1000, 0, 'o', new Date()));
    }};
    
    public void setSrc(String xml) throws Exception {
        setLayout(new BorderLayout());
        long start = System.currentTimeMillis();
        swix = new SwingEngine(this);
        panel = (JPanel)swix.render(res.getResourceAsReader(xml));
        add(BorderLayout.CENTER, panel);
        
        if (log.isInfoEnabled()) {
            log.info("Loaded Connection Dialog - "+(System.currentTimeMillis() - start)+"ms");
        }
    }
        
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
    }
    
    static class GameListing {
        private String name;
        private String server;
        private int port;
        private int turns;
        private char game;
        private int sectors;
        private Date start;
        
        
        public GameListing(String name, String server, int port, int sectors, int turns, char game, Date start) {
            super();
            this.name = name;
            this.server = server;
            this.port = port;
            this.turns = turns;
            this.game = game;
            this.sectors = sectors;
            this.start = start;
        }
        public char getGame() {
            return game;
        }
        public void setGame(char game) {
            this.game = game;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getPort() {
            return port;
        }
        public void setPort(int port) {
            this.port = port;
        }
        public String getServer() {
            return server;
        }
        public void setServer(String server) {
            this.server = server;
        }
        public Date getStart() {
            return start;
        }
        public void setStart(Date start) {
            this.start = start;
        }
        public int getTurns() {
            return turns;
        }
        public void setTurns(int turns) {
            this.turns = turns;
        }
        
        
    }
    
}
