package org.twdata.TW1606.tw.gui;

import org.swixml.SwingEngine;
import org.twdata.TW1606.script.flow.*;
import org.twdata.TW1606.script.flow.javascript.*;
import org.twdata.TW1606.signal.*;
import org.mozilla.javascript.*;
import org.twdata.TW1606.*;
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
import org.twdata.TW1606.tw.signal.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.tw.*;
import org.twdata.TW1606.data.DaoManager;
import org.apache.log4j.Logger;
import javax.swing.text.*;

/**
 *@created    October 18, 2003
 */
public class ScriptQuickQuery extends QuickQuery {
    
    private JavaScriptInterpreter interpreter;
    private String globalScript;
    private ResourceManager res;

    /**  Constructor for the ScriptQuickQuery object */
    public ScriptQuickQuery() { 
        super();
    }
    
    public void setGlobalScript(String s) {
        Reader in = res.getResourceAsReader(s);
        if (in == null) {
            throw new IllegalArgumentException("Unable to locate "+s);
        }
        
        try {
            StringWriter writer = new StringWriter();
            int len = 0;
            char[] buffer = new char[128];
            while ((len = in.read(buffer)) > 0) {
                writer.write(buffer, 0, len);
            }
            in.close();
            globalScript = writer.toString();
        } catch (IOException ex) {
            log.error("Unable to read global script", ex);
        }
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
    }
    
    public void setInterpreter(JavaScriptInterpreter inter) {
        this.interpreter = inter;
    }
    
    protected void handleTyped(String query) {
        if (log.isDebugEnabled()) {
            log.debug("Executing query: "+query);
        }
        String script = globalScript + "\n" + query;
        Object result = null;
        try {
            Scriptable scope = interpreter.enterContext(null);
            result = interpreter.eval("quickQuery", script, scope);
            interpreter.exitContext(scope);
            if (result != null) {
                JOptionPane.showMessageDialog(this, result, "Quick Query Result", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to execute query: "+e.getMessage(), "Quick Query Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

