package org.twdata.TW1606;

import java.io.InputStream;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.util.Properties;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.twdata.TW1606.gui.SplashScreen;
import org.twdata.TW1606.gui.View;
import org.twdata.TW1606.signal.MessageBus;
import org.twdata.TW1606.signal.ShutdownSignal;

import com.jgoodies.clearlook.ClearLookManager;
import com.jgoodies.clearlook.ClearLookMode;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.theme.ExperienceBlue;



/**
 *@created    October 18, 2003
 */
public class TW1606 {
    
    private static final TW1606 self = new TW1606();
    private MessageBus bus;
    private View view;
    
    public static TW1606 getInstance() {
        return self;
    }
    
    public void startup() throws Exception {
         Policy.setPolicy(
            new Policy() {
                public PermissionCollection getPermissions(CodeSource
                    codesource) {
                    Permissions perms = new Permissions();
                    perms.add(new AllPermission());
                    return(perms);
                }
                public void refresh() {}
            });
            
        ClearLookManager.setMode(ClearLookMode.OFF);
        PlasticLookAndFeel.setMyCurrentTheme(new ExperienceBlue());
        UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
        
        SplashScreen splash = new SplashScreen();
        splash.advance("Initializing Logging");
        initLogging();    

        try {
            splash.advance("Initializing program structure");
            XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/conf/beans.xml"));
            
            bus = (MessageBus)factory.getBean("message-bus");
            bus.plug(new Object() {
                public void channel(ShutdownSignal signal) {
                    if (!signal.isCancelled()) {
                        bus.stop();
                        view.dispose();
                        System.exit(0);
                    }
                }
            });
            splash.advance("Intializing main screen");
            view = (View) factory.getBean("main-view");
            
            splash.advance("Initializing data gathering modules");
            factory.preInstantiateSingletons();

            //factory.getBean("tw.lexer-manager");
            //factory.getBean("chat.lexer-manager");
            //factory.getBean("sector-graph");

            splash.advance("Loading main screen");
            view.load();
            
            //splash.dispose();
            //splash = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        } 
        final View v = view;
        final SplashScreen s = splash;
        splash = null;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                v.show();
                s.dispose();
            }
        });
    }
    
    /**  method desc */
    private void initLogging() {
        try {
            Properties props = new Properties();
            props.load(Main.class.getResourceAsStream("/conf/log4j.properties"));
            PropertyConfigurator.configure(props);
            Logger.getLogger(Main.class).info("Log4j initialized");
        } catch (Exception ex) {
            System.err.println("Log4j configuration failed!  Using default...");
            BasicConfigurator.configure();
        }
    }
    
    public void shutdown() throws Exception {
        
        // FIXME: possible deadlock
        Thread t = new Thread() {
            public void run() {
                ShutdownSignal signal = new ShutdownSignal();
                bus.broadcast(signal);
            }
        };
        t.start();
    }
}

