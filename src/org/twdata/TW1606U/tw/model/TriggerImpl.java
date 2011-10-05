package org.twdata.TW1606U.tw.model;

import java.util.*;
import org.twdata.TW1606U.tw.data.*;
import java.io.Serializable;

public class TriggerImpl extends BaseDaoModel implements Trigger, Serializable {

    private int id;
    private String name;
    private String macroInText;
    private String macroOutText;
    
    public TriggerImpl() {
        super();
        name="";
        macroInText="";
        macroOutText="";
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
    
    public String getInText() {
        return macroInText;
    }
    public void setInText(String text) {
        this.macroInText = text;
    }

    public String getOutText() {
        return macroOutText;
    }
    public void setOutText(String text) {
        this.macroOutText = text;
    }

}

