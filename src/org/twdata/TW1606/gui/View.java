package org.twdata.TW1606.gui;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Dimension;
import org.swixml.SwingEngine;
import org.twdata.TW1606.signal.*;
import org.twdata.TW1606.*;
import org.twdata.TW1606.tw.gui.*;
import javax.swing.*;
import java.io.File;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import org.swixml.*;
import java.util.Iterator;
import java.util.List;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import org.twdata.TW1606.action.JHelpAction;

/**
 *@created    October 18, 2003
 */
public class View extends WindowAdapter {
    private SwingEngine swix;
    private static final Logger log = Logger.getLogger(View.class);

    public JFrame frame;
    public JPanel statusPanel;
    private Clipboard clipb = null;
    private List tagNames;
    private List preloadBeanNames;
    private String viewPath;
    
    private ActionResolver actionResolver;
    private ActionManager actionManager;
    private SpringPluginFactory pluginFactory;
    private JHelpAction helpAction;
    
    private ResourceManager res;

    /**
     *  Constructor for the View object
     */
    public View() {
        
    }
    
    public void load() {
        try {
            long start = System.currentTimeMillis();
            swix = new SwingEngine(this);
            
            String name;
            for (Iterator i = tagNames.iterator(); i.hasNext(); ) {
                name = (String)i.next();
                swix.getTaglib().registerTag(name, pluginFactory.cloneFactory(name));
            }
            
            swix.setActionResolver(actionResolver);
            swix.render(res.getResourceAsReader(viewPath));
            if (log.isInfoEnabled()) {
                log.info("Loaded GUI - "+(System.currentTimeMillis() - start)+"ms");
            }
            frame.addWindowListener(this); 
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            
            frame.pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // set up the clipboard
        try {
          clipb = frame.getToolkit().getSystemClipboard();
        } catch (Exception e) {
          // system clipboard access denied
          clipb = new Clipboard("org.twdata.TW1606.gui.View");
        }
        
        // intialize help
        JHelpAction.startHelpWorker("docs/tw1606ReferenceLibrary.hs");
    }
    
    public void show() {
        Toolkit theKit= frame.getToolkit();
        //Dimension size = frame.getSize();
        //Dimension screen= theKit.getScreenSize();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        //frame.setLocation((screen.width - size.width) / 2,
		//	(screen.height - size.height) / 2);
        frame.setVisible(true);
    }
    
    public void setActionManager(ActionManager actionManager) {
        this.actionManager = actionManager;
    }
    
    public void setViewPath(String val) {
        this.viewPath = val;
    }
    
    public void setPreloadBeanNames(List beans) {
        this.preloadBeanNames = beans;
    }
    
    public void setTagNames(List lst) {
        this.tagNames = lst;
    }
     
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
    }
    
    public void setActionResolver(ActionResolver actionResolver) {
        this.actionResolver = actionResolver;
    }
    public void setPluginFactory(SpringPluginFactory pluginFactory) {
        this.pluginFactory = pluginFactory;
    }
    
    public void dispose() {
        frame.dispose();
    }


    /**
     *  Invoked when the user attempts to close the window from the window's
     *  system menu. If the program does not explicitly hide or dispose the
     *  window while processing this event, the window close operation will be
     *  cancelled.
     *
     *@param  e  Description of the Parameter
     */
    public void windowClosing(WindowEvent e) {
        org.twdata.TW1606.action.Action exit = actionManager.getAction("exit");
        exit.invoke();
    }
    
}

