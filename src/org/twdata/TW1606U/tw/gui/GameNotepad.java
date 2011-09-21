package org.twdata.TW1606U.tw.gui;

import org.swixml.SwingEngine;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.*;
import java.awt.Insets;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import org.swixml.*;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import java.awt.BorderLayout;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.twdata.TW1606U.tw.signal.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.data.DaoManager;
import org.twdata.TW1606U.tw.model.*;
import org.twdata.TW1606U.tw.*;
import javax.swing.text.JTextComponent;
import java.util.*;
import org.twdata.TW1606U.gui.SessionFieldEnabler;

/**
 *@created    October 18, 2003
 */
public class GameNotepad extends JPanel {
    
    public JComboBox titles;
    public JTextArea note;
    public JScrollPane notePane;
    private JPanel panel;
    public JButton xTitle;
    
    protected static final Logger log = Logger.getLogger(GameNotepad.class);

    private TWSession session;
    private Game game;
    private GameDao gameDao;
    private DefaultComboBoxModel titlesModel;
    private ResourceManager res;
    private SwingEngine swix;
    private SessionFieldEnabler enabler;
    
    public GameNotepad() {
    }
    

    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }    
    public void setSession(TWSession session) {
        this.session = session;
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
    }
    
    public void setMessageBus(MessageBus bus) {
        bus.plug(this);
    }
    
    public void setSrc(String xml) {
        setLayout(new BorderLayout());
        try {
            long start = System.currentTimeMillis();
            swix = new SwingEngine(this);
            panel = (JPanel)swix.render(res.getResourceAsReader(xml));
            add(BorderLayout.CENTER, panel);
            
            if (log.isInfoEnabled()) {
                log.info("Loaded Game Notepad - "+(System.currentTimeMillis() - start)+"ms");
            }
            
            if (titles != null) {
                titlesModel = (DefaultComboBoxModel)titles.getModel();
                titles.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent ie) {
                        String title = (String) ie.getItem();
                        if (ie.getStateChange()==ie.SELECTED) {
                            String val = game.getNote(title);
                            if (val == null) {
                                game.addNote(title, "");
                                note.setText("");
                                gameDao.update(game);
                                updateTitles(title);
                            } else {
                                note.setText(val);
                            }
                        } else if (ie.getStateChange() == ie.DESELECTED) {
                            updateNotes();
                        }
                    }
                });
            }
            
            if (xTitle != null) {
                xTitle.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        game.removeNote((String)titles.getSelectedItem());
                        gameDao.update(game);
                        updateTitles(null);
                    }
                });
            }
            if (titles != null) enabler.addField(titles);
            if (note != null) {
                enabler.addField(note);
                
                // Save note after every keystroke
                note.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) { updateNotes(); }
                    public void insertUpdate(DocumentEvent e) { updateNotes(); }
                    public void removeUpdate(DocumentEvent e) { updateNotes(); }
                });
            }
            if (notePane != null) enabler.addField(notePane);
            if (xTitle != null) enabler.addField(xTitle);   
        } catch (Exception e) {
            log.error(e, e);
        }
        
    }
    
    private void updateNotes() {
        String title = (String) titles.getSelectedItem();
        if (game.getNote(title) != null) {
            game.addNote(title, note.getText());
            gameDao.update(game);
        }
    }
    
    public void setSessionFieldEnabler(SessionFieldEnabler enabler) {
        this.enabler = enabler;
    }
    
    public void channel(ChangedSessionSignal signal) {
        if (signal.GAME == signal.getType()) {
            game = session.getGame();
            updateTitles(null);
        } 
    }
    
    private void updateTitles(String selected) {
        titlesModel.removeAllElements();
        String[] arr = game.getNoteTitles();
        Arrays.sort(arr);
        for (int x=0; x<arr.length; x++) {
            titlesModel.addElement(arr[x]);
        }
        if (selected != null) {
            titlesModel.setSelectedItem(selected);
        } else if (arr.length > 0) {
            titlesModel.setSelectedItem(arr[0]);
        }
    }
}
