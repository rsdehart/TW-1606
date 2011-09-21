package org.twdata.TW1606U.tw.gui;

import org.swixml.SwingEngine;
import org.twdata.TW1606U.script.flow.*;
import org.twdata.TW1606U.script.flow.javascript.*;
import org.twdata.TW1606U.signal.*;
import org.mozilla.javascript.*;
import org.twdata.TW1606U.*;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import org.swixml.*;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import java.awt.BorderLayout;
import org.twdata.TW1606U.tw.signal.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.tw.model.*;
import org.twdata.TW1606U.tw.*;
import org.twdata.TW1606U.data.DaoManager;
import org.apache.log4j.Logger;
import javax.swing.text.*;
import org.apache.commons.jxpath.*;

/**
 *@created    October 18, 2003
 */
public class XPathQuickQuery extends QuickQuery {
    
    private DaoManager dm;
    private TWSession session;
    
    /**  Constructor for the QuickQuery object */
    public XPathQuickQuery() { 
        super();
    }
    
    public void setSession(TWSession session) {
        this.session = session;
    }
    
    public void setDaoManager(DaoManager dm) {
        this.dm = dm;
    }
    
    protected void handleTyped(String query) {
        HashMap data = new HashMap();
        data.put("session", session);
        data.put("sectors", ((SectorDao)dm.getDao("sector")).getAll());
        data.put("ports", ((PortDao)dm.getDao("port")).getAll());
        data.put("games", ((GameDao)dm.getDao("game")).getAll());
        data.put("ships", ((ShipDao)dm.getDao("ship")).getAll());
        data.put("shipTypes", ((ShipTypeDao)dm.getDao("shipType")).getAll());
        data.put("planets", ((PlanetDao)dm.getDao("planet")).getAll());
        data.put("planetTypes", ((PlanetTypeDao)dm.getDao("planetType")).getAll());
        data.put("players", ((PlayerDao)dm.getDao("player")).getAll());
        data.put("corporations", ((CorporationDao)dm.getDao("corporation")).getAll());
        
        JXPathContext ctx = JXPathContext.newContext(data);
        ctx.setLenient(true);
        Object result = null;
        try {
            result = ctx.getValue(query);
            if (result != null) {
                JOptionPane.showMessageDialog(this, result, "Quick Query Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Unable to find path: "+query, "Quick Query Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to execute query: "+ex.getMessage(), "Quick Query Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
}

