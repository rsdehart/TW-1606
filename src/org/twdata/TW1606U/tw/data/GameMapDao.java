package org.twdata.TW1606U.tw.data;

import jdbm.*;
import java.io.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import org.twdata.TW1606U.tw.signal.*;
import java.util.*;

public class GameMapDao extends BaseMapDao implements GameDao {
    
    public static final String NAME = "game";
    
    public Game create(String name) {
        Game g = (Game) db.createModel(NAME);
        g.setName(name);
        update(g);
        return g;
    }
    
    public Collection getAll() {
        Map m = db.getMap(NAME);
        return m.values();
    }
    
    public void update(Game game) {
        Map m = db.getMap(NAME);
        m.put(game.getName(), game);
        bus.broadcast(new GameStatusSignal(this, game));
    }
    
    public Game get(String name) {
        return get(name, false);
    }
    
    public Game get(String name, boolean create) {
        Map m = db.getMap(NAME);
        Game g = (Game)m.get(name);
        if (g == null && create) {
            g = create(name);
        }
        return g;
    }
    
    public void remove(Game game) {
        Map m = db.getMap(NAME);
        m.remove(game.getName());   
    }
    
}
