package org.twdata.TW1606U.tw.model;

import java.util.*;
import org.twdata.TW1606U.tw.data.*;
import java.io.Serializable;

public class CorporationImpl extends BaseDaoModel implements Corporation, Serializable {

    private int id;
    private String name;
    private String ceo;
    
    public CorporationImpl() {
        super();
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public Player getCeo() {
        return playerDao.get(ceo, Player.TRADER);
    }
    public void setCeo(Player ceo) {
        this.ceo = ceo.getName();   
    }
    
    public boolean isCorporate() {
        return true;
    }
    
    public boolean isFriendly(Player player) {
        if (player.getCorporation() == this) {
            return true;
        } 
        return false;
    }
}

