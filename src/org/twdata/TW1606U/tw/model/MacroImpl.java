package org.twdata.TW1606U.tw.model;

import java.util.*;
import org.twdata.TW1606U.tw.data.*;
import java.io.Serializable;

public class MacroImpl extends BaseDaoModel implements Macro, Serializable {

    private int id;
    private String name;
    private String macroText;
    
    public MacroImpl() {
        super();
        name="";
        macroText="";
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
    
    public String getText() {
        return macroText;
    }
    public void setText(String text) {
        this.macroText = text;
    }
    
}

