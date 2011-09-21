package org.twdata.TW1606U;

import org.swixml.SwingEngine;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.gui.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import com.jgoodies.plaf.*;
import com.jgoodies.clearlook.*;
import com.jgoodies.plaf.plastic.theme.*;
import com.jgoodies.plaf.plastic.*;
import java.io.*;
import java.security.*;
import org.apache.log4j.*;
import java.util.*;



/**
 *@created    October 18, 2003
 */
public class Main {
    
    public static void main(String[] args) throws Exception {
        System.setProperty("swing.aatext","true");
        TW1606u tw1606u  = TW1606u.getInstance();
        tw1606u.startup();
    }
    
}

