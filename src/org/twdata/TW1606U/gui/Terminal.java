package org.twdata.TW1606U.gui;

import de.mud.terminal.SwingTerminal;
import de.mud.terminal.VDUBuffer;

import de.mud.terminal.vt320;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.swing.*;
import org.apache.log4j.Logger;
import org.twdata.TW1606U.*;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.tw.gui.terminal.TerminalHoverListener;

/**
 *  The terminal plugin represents the actual terminal where the data will be
 *  displayed and the gets the keyboard input to sent back to the remote host.
 *  <P>
 *
 *  <B>Maintainer:</B> Matthias L. Jugel
 *
 *@author     Matthias L. Jugel, Marcus Meiner
 *@created    October 19, 2003
 */
public class Terminal extends JPanel
         implements StreamFilter, ClipboardOwner {

    private static final Logger log = Logger.getLogger(Terminal.class);

    /**
     *  The default encoding is ISO 8859-1 (western). However, as you see the
     *  value is set to latin1 which is a value that is not even documented and
     *  thus incorrect, but it forces the default behaviour for western
     *  encodings. The correct value does not work in most available browsers.
     */
    protected String encoding = "latin1";

    /**  Description of the Field */
    protected StreamFilter source;
    
    protected StreamFilter writer;

    /**  holds the actual terminal emulation */
    private vt320 emulation;
    private SwingTerminal terminal;
    private MessageBus bus;
    private JFrame frame;
    private ActionManager actionManager;
    
    private Map colors = null;
    // "ISO8859_1";

    private boolean localecho_overridden = false;

    private ResourceManager res;

    private TerminalHoverListener terminalHoverListener;
    
    /**
     *  Create a new terminal plugin and initialize the terminal emulation.
     */
    public Terminal() {
        super();

        initColors();

        // create the terminal emulation
        emulation =
            new vt320() {
                public void write(byte[] b) {
                    try {
                        writer.write(b);
                        /*Terminal.this.source.write(b);
                        if (macroRecorder.isRecording()) {
                            macroRecorder.record(new String(b));
                        }
                        */
                    } catch (IOException e) {
                        e.printStackTrace();
                        // reader = null;
                    }
                }


                // provide audio feedback if that is configured

                public void beep() {
                    //if (audioBeep != null) {
                    //    bus.broadcast(audioBeep);
                    //}
                }


                public void sendTelnetCommand(byte cmd) {
                    bus.broadcast(new TelnetCommandSignal(cmd));
                }
            };

        terminal = new SwingTerminal(emulation);

        terminal.addFocusListener(
            new FocusListener() {
                public void focusGained(FocusEvent evt) {
                    terminal.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                    //bus.broadcast(new FocusStatus(Terminal.this, evt));
                }


                public void focusLost(FocusEvent evt) {
                    terminal.setCursor(Cursor.getDefaultCursor());
                    //bus.broadcast(new FocusStatus(Terminal.this, evt));
                }
            });

        setLayout(new BorderLayout());
        final JScrollBar scrollBar = new JScrollBar();
        add(BorderLayout.EAST, scrollBar);
        terminal.setScrollbar(scrollBar);
        terminal.getVDUBuffer().setBufferSize(10000);
        terminal.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                scrollBar.setValue(scrollBar.getValue() + e.getWheelRotation());
            }
        });
        
        // TODO: register online status listener
        // TODO: register terminal type listener
        // TODO: register window size listener
        // TODO: register local echo listener
        // TODO: register configuration listener
        // TODO: register return focus listener

        add(BorderLayout.CENTER, terminal);
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
        configure(res.getResource("/conf/terminal.conf"));
    }
    

    // reduce flickering
    /**
     *  Description of the Method
     *
     *@param  g  Description of the Parameter
     */
    public void update(java.awt.Graphics g) {
        paint(g);
    }


    // we don't want to print the container, just the terminal contents
    /**
     *  Description of the Method
     *
     *@param  g  Description of the Parameter
     */
    public void print(java.awt.Graphics g) {
        terminal.print(g);
    }
    
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }


    /**
     *  Gets the selection attribute of the Terminal object
     *
     *@return    The selection value
     */
    public String getSelection() {
        return terminal.getSelection();
    }


    /**
     *  Description of the Method
     *
     *@param  signal  Description of the Parameter
     */
    public void channel(TerminalSignal signal) {
        String command = signal.getCommand();
    }


    /**
     *  Description of the Method
     *
     *@param  signal  Description of the Parameter
     */
    public void channel(LocalEchoSignal signal) {
        if (log.isDebugEnabled()) {
            log.debug("receiving local echo signal:" + signal.getEcho());
        }
        if (!localecho_overridden) {
            emulation.setLocalEcho(signal.getEcho());
        }
    }


    /**
     *  Description of the Method
     *
     *@param  signal  Description of the Parameter
     */
    public void channel(OnlineStatusSignal signal) {
        String command = signal.getCommand();
        if (signal.ONLINE.equals(command)) {
            if (log.isDebugEnabled()) {
                log.debug("Requesting terminal focus");
            }
            terminalHoverListener = new TerminalHoverListener(terminal, frame, actionManager);
            terminal.requestFocus();
        } else {
            terminalHoverListener.destroy();
            emulation.putString("\r\n\r\nDISCONNECTED");
        }
    }


    /**
     *  Description of the Method
     *
     *@param  signal  Description of the Parameter
     */
    public void channel(ClipboardSignal signal) {
        String command = signal.getCommand();
        Clipboard clipboard = signal.getClipboard();

        if ("copy".equals(command)) {
            String data = terminal.getSelection();
            // check due to a bug in the hotspot vm
            if (data == null) {
                return;
            }
            StringSelection selection = new StringSelection(data);
            clipboard.setContents(selection, this);
        } else if ("paste".equals(command)) {
            if (clipboard == null) {
                return;
            }
            Transferable t = clipboard.getContents(this);
            try {
                byte buffer[] =
                        ((String) t.getTransferData(DataFlavor.stringFlavor)).getBytes("Cp1252");
                try {
                    write(buffer);
                } catch (IOException e) {
                    // reader = null;
                }
            } catch (Exception e) {
                // ignore any clipboard errors
            }
        }
    }


    /**
     *  Sets the filterSource attribute of the Terminal object
     *
     *@param  source  The new filterSource value
     */
    public void setFilterSource(StreamFilter source) {
        this.source = source;
    }
    
    /**
     *  Gets the filterSource attribute of the Terminal object
     *
     *@return    The filterSource value
     */
    public StreamFilter getFilterSource() {
        return source;
    }

    public void setStreamWriter(StreamFilter writer) {
        this.writer = writer;
    }
    

    /**
     *  Description of the Method
     *
     *@param  b                Description of the Parameter
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    public int read(byte[] b)
        throws IOException {
        int n = source.read(b);
        if (log.isDebugEnabled()) {
            log.debug("reading " + n + " bytes");
        }
        synchronized (this) {
            if (n > 0) {
                emulation.putString(new String(b, 0, n, encoding));
            }
            repaint();
        }
        return n;
    }


    /**
     *  Description of the Method
     *
     *@param  b                Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    public void write(byte[] b)
        throws IOException {
        
        source.write(b);

    }


    /**
     *  Description of the Method
     *
     *@param  clipboard  Description of the Parameter
     *@param  contents   Description of the Parameter
     */
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        terminal.clearSelection();
    }


    /**
     *  Gets the terminalType attribute of the Terminal object
     *
     *@return    The terminalType value
     */
    public String getTerminalType() {
        return emulation.getTerminalID();
    }


    /**
     *  Gets the windowSize attribute of the Terminal object
     *
     *@return    The windowSize value
     */
    public Dimension getWindowSize() {
        return new Dimension(emulation.getColumns(), emulation.getRows());
    }


    /**
     *  Sets the localEcho attribute of the Terminal object
     *
     *@param  echo  The new localEcho value
     */
    public void setLocalEcho(boolean echo) {
        if (!localecho_overridden) {
            emulation.setLocalEcho(echo);
        }
    }
    
    /**
     *  Description of the Method
     *
     *@param  s  Description of the Parameter
     */
    public void printString(String s) {
        synchronized (terminal) {
            emulation.putString(s);
            repaint();
        }
    }


    /**
     *  Gets the string attribute of the Terminal class
     *
     *@param  x    Description of the Parameter
     *@param  y    Description of the Parameter
     *@param  len  Description of the Parameter
     *@return      The string value
     */
    public String getString(int x, int y, int len) {
        //synchronized (terminal) {
        return emulation.getString(x, y, len);
        //}
    }


    /**  Description of the Method */
    public void clearScreen() {
        synchronized (terminal) {
            emulation.clearScreen();
            repaint();
        }
    }


    /**
     *  Gets the cursorPosition attribute of the Terminal object
     *
     *@return    The cursorPosition value
     */
    public Dimension getCursorPosition() {
        return new Dimension(emulation.getCursorColumn(), emulation.getCursorRow());
    }


    /**
     *  Description of the Method
     *
     *@param  in   Description of the Parameter
     */
    protected void configure(InputStream in) {
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException ex) {
            log.warn("Unable to locate properties");
        }
        String tmp;
        if ((tmp = props.getProperty("Terminal.foreground")) != null) {
            terminal.setForeground(Color.decode(tmp));
        }
        if ((tmp = props.getProperty("Terminal.background")) != null) {
            terminal.setBackground(Color.decode(tmp));
        }
        if ((tmp = props.getProperty("Terminal.print.color")) != null) {
            try {
                terminal.setColorPrinting(Boolean.valueOf(tmp).booleanValue());
            } catch (Exception e) {
                log.warn("Terminal.color.print: must be either true or false, not " + tmp);
            }
        }
        if ((tmp = props.getProperty("Terminal.colorSet")) != null) {
            if (log.isDebugEnabled()) {
                log.debug("colorSet: "+tmp);
            }
            Properties colorSet = new Properties();
            try {
                colorSet.load(res.getResource(tmp));
            } catch (Exception e) {
                try {
                    colorSet.load(new URL(tmp).openStream());
                } catch (Exception ue) {
                    log.warn("cannot find colorSet: " + tmp);
                    log.warn("resource access failed: " + e);
                    log.warn("URL access failed: " + ue);
                    colorSet = null;
                }
            }
            if (colorSet != null) {
                Color set[] = terminal.getColorSet();
                Color color = null;
                for (int i = 0; i < 8; i++) {
                    if ((tmp = colorSet.getProperty("color" + i)) != null &&
                            (color = codeToColor(tmp)) != null) {
                        set[i] = color;
                    }
                }
                // special color for bold
                if ((tmp = colorSet.getProperty("bold")) != null &&
                        (color = codeToColor(tmp)) != null) {
                    set[SwingTerminal.COLOR_BOLD] = color;
                }
                // special color for invert
                if ((tmp = colorSet.getProperty("invert")) != null &&
                        (color = codeToColor(tmp)) != null) {
                    set[SwingTerminal.COLOR_INVERT] = color;
                }
                terminal.setColorSet(set);
            }
        }
        String cFG = props.getProperty("Terminal.cursor.foreground");
        String cBG = props.getProperty("Terminal.cursor.background");
        if (cFG != null || cBG != null) {
            try {
                Color fg = (cFG == null ?
                        terminal.getBackground() :
                        (Color.getColor(cFG) != null ? Color.getColor(cFG) : Color.decode(cFG)));
                Color bg = (cBG == null ?
                        terminal.getForeground() :
                        (Color.getColor(cBG) != null ? Color.getColor(cBG) : Color.decode(cBG)));
                terminal.setCursorColors(fg, bg);
            } catch (Exception e) {
                log.warn("ignoring unknown cursor color code: " + tmp);
            }
        }
        if ((tmp = props.getProperty("Terminal.border")) != null) {
            String size = tmp;
            boolean raised = false;
            if ((tmp = props.getProperty("Terminal.borderRaised")) != null) {
                raised = Boolean.valueOf(tmp).booleanValue();
            }
            terminal.setBorder(Integer.parseInt(size), raised);
        }
        if ((tmp = props.getProperty("Terminal.localecho")) != null) {
            emulation.setLocalEcho(Boolean.valueOf(tmp).booleanValue());
            localecho_overridden = true;
        }
        /*if ((tmp = props.getProperty("Terminal.scrollBar")) != null) {
            String direction = tmp;
            if (!direction.equals("none")) {
                if (!direction.equals("East") && !direction.equals("West")) {
                    direction = "East";
                }
                JScrollBar scrollBar = new JScrollBar();
                tPanel.add(direction, scrollBar);
                terminal.setScrollbar(scrollBar);
            }
        }
        */
        if ((tmp = props.getProperty("Terminal.id")) != null) {
            emulation.setTerminalID(tmp);
        }
        if ((tmp = props.getProperty("Terminal.answerback")) != null) {
            emulation.setAnswerBack(tmp);
        }
        if ((tmp = props.getProperty("Terminal.buffer")) != null) {
            emulation.setBufferSize(Integer.parseInt(tmp));
        }
        if ((tmp = props.getProperty("Terminal.size")) != null) {
            try {
                int idx = tmp.indexOf(',');
                int width = Integer.parseInt(tmp.substring(1, idx).trim());
                int height = Integer.parseInt(tmp.substring(idx + 1, tmp.length() - 1).trim());
                emulation.setScreenSize(width, height);
            } catch (Exception e) {
                log.warn("screen size is wrong: " + tmp);
                log.warn("error: " + e);
            }
        }
        if ((tmp = props.getProperty("Terminal.resize")) != null) {
            if (tmp.equals("font")) {
                terminal.setResizeStrategy(SwingTerminal.RESIZE_FONT);
            } else if (tmp.equals("screen")) {
                terminal.setResizeStrategy(SwingTerminal.RESIZE_SCREEN);
            } else {
                terminal.setResizeStrategy(SwingTerminal.RESIZE_NONE);
            }
        }
        if ((tmp = props.getProperty("Terminal.font")) != null) {
            String font = tmp;
            int style = Font.PLAIN;
            int fsize = 12;
            if ((tmp = props.getProperty("Terminal.fontSize")) != null) {
                fsize = Integer.parseInt(tmp);
            }
            String fontStyle = props.getProperty("Terminal.fontStyle");
            if (fontStyle == null || fontStyle.equals("plain")) {
                style = Font.PLAIN;
            } else if (fontStyle.equals("bold")) {
                style = Font.BOLD;
            } else if (fontStyle.equals("italic")) {
                style = Font.ITALIC;
            } else if (fontStyle.equals("bold+italic")) {
                style = Font.BOLD | Font.ITALIC;
            }
            terminal.setFont(new Font(font, style, fsize));
        }
        if ((tmp = props.getProperty("Terminal.keyCodes")) != null) {
            Properties keyCodes = new Properties();
            try {
                keyCodes.load(res.getResource(tmp));
            } catch (Exception e) {
                try {
                    keyCodes.load(new URL(tmp).openStream());
                } catch (Exception ue) {
                    log.warn("cannot find keyCodes: " + tmp);
                    log.warn("resource access failed: " + e);
                    log.warn("URL access failed: " + ue);
                    keyCodes = null;
                }
            }
            // set the key codes if we got the properties
            if (keyCodes != null) {
                emulation.setKeyCodes(keyCodes);
            }
        }
        if ((tmp = props.getProperty("Terminal.VMS")) != null) {
            emulation.setVMS((Boolean.valueOf(tmp)).booleanValue());
        }
        if ((tmp = props.getProperty("Terminal.IBM")) != null) {
            emulation.setIBMCharset((Boolean.valueOf(tmp)).booleanValue());
        }
        if ((tmp = props.getProperty("Terminal.encoding")) != null) {
            encoding = tmp;
        }
        /*if ((tmp = props.getProperty("Terminal.beep")) != null) {
            try {
                audioBeep = new SoundRequest(new URL(tmp));
            } catch (MalformedURLException e) {
                log.warn("incorrect URL for audio ping: " + e);
            }
        }
        */
        setBackground(terminal.getBackground());
    }


    /**  Description of the Method */
    private void initColors() {
        colors = new HashMap();
        colors.put("black", Color.black);
        colors.put("red", Color.red);
        colors.put("green", Color.green);
        colors.put("yellow", Color.yellow);
        colors.put("blue", Color.blue);
        colors.put("magenta", Color.magenta);
        colors.put("orange", Color.orange);
        colors.put("pink", Color.pink);
        colors.put("cyan", Color.cyan);
        colors.put("white", Color.white);
        colors.put("gray", Color.white);
        colors = Collections.unmodifiableMap(colors);
    }


    /**
     *  Description of the Method
     *
     *@param  code  Description of the Parameter
     *@return       Description of the Return Value
     */
    private Color codeToColor(String code) {
        if (colors.get(code) != null) {
            return (Color) colors.get(code);
        } else {
            try {
                if (Color.getColor(code) != null) {
                    return Color.getColor(code);
                } else {
                    return Color.decode(code);
                }
            } catch (Exception e) {
                log.warn("ignoring unknown color code: " + code);
            }
        }
        return null;
    }

    public void setActionManager(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

}

