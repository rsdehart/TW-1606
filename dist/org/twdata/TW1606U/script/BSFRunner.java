package org.twdata.TW1606U.script;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import org.twdata.TW1606U.tw.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.gui.*;

import org.twdata.TW1606U.*;

import org.apache.bsf.*;
import org.apache.bsf.util.*;
import org.springframework.beans.factory.*;

import org.apache.log4j.Logger;

/**
 *  Description of the Class
 *
 *@created    October 25, 2003
 */
public class BSFRunner implements BeanFactoryAware {

    private BeanFactory factory;
    private MessageBus bus;
    private BSFManager manager;
    private Thread scriptThread = null;
    private static final Logger log = Logger.getLogger(BSFRunner.class);


    /**
     *  Constructor for the BSFRunner object
     *
     */
    public BSFRunner() {

        BSFManager.registerScriptingEngine(
            "beanshell", "bsh.util.BeanShellBSFEngine", 
            new String[]{"bsh"});
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setBeanFactory(BeanFactory factory) {
        this.factory = factory;
    }
    
    public void channel(ShutdownSignal signal) {
        stopScript();   
    }

    /*
    public boolean canRunScript(Script script) {
        String path = script.getPath();
        if (path != null) {
            try {
                String lang = manager.getLangFromFilename(path);
                if (manager.loadScriptingEngine(lang) != null) {
                    return true;
                }
            }
            catch (BSFException ex) {
                log.warn("Unable to determine if BSF engine supports script", ex);
            }
        }
        return false;
    }
    
    public void runScript(Script script) {
        runScript(script.getPath());
    }
    */
    
    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    public void runScript(final String file) {
        manager = new BSFManager();
        try {
            manager.declareBean("reg", factory, factory.getClass());
        } catch (Exception ex) {
            log.error("Unable to setup default variables", ex);
            return;
        }
        scriptThread = new Thread(
            new Runnable() {
                public void run() {
                    try {
                        manager.exec(manager.getLangFromFilename(file), file, 0, 0, IOUtils.getStringFromReader(new FileReader(file)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    scriptThread = null;
                }
            });
        scriptThread.start();
    }
    
    public void stopScript() {
        if (manager != null) {
            manager.terminate();
        }
    }

}
