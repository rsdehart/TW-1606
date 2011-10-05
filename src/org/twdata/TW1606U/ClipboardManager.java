package org.twdata.TW1606U;

import org.twdata.TW1606U.signal.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.awt.datatransfer.*;
import javax.swing.JFrame;

/**
 *  The socket plugin acts as the data source for networked operations. <P>
 *
 *  <B>Maintainer:</B> Matthias L. Jugel
 *
 * @author     Matthias L. Jugel, Marcus Meiner
 * @version    $Id: ClipboardManager.java,v 1.3 2004/07/25 00:54:04 mrdon Exp $
 */
public class ClipboardManager implements ClipboardOwner {
  
    private JFrame frame;
    private Clipboard clipboard;

    /**
     *  Create a new socket plugin.
     */
    public ClipboardManager() {}
    
    public void setFrame(JFrame frame) {
        this.frame = frame;
        
        // set up the clipboard
        try {
          clipboard = frame.getToolkit().getSystemClipboard();
        } catch (Exception e) {
          // System.err.println("jta: system clipboard access denied");
          // System.err.println("jta: copy & paste only within the JTA");
          clipboard = new Clipboard("org.twdata.TW1606U.gui.View");
        }
    }
    
    public void copy(String data) {
        StringSelection selection = new StringSelection(data);
        clipboard.setContents(selection, this);   
    }
    
    public String paste() {
        if (clipboard == null) {
            return null;
        }
        
        Transferable t = clipboard.getContents(this);
        String data = null;
        try {
          /*
          InputStream is =
            (InputStream)t.getTransferData(DataFlavor.plainTextFlavor);
          if(debug > 0)
            System.out.println("Clipboard: available: "+is.available());
          byte buffer[] = new byte[is.available()];
          is.read(buffer);
          is.close();
          */
          data = (String) t.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
          // ignore any clipboard errors
          e.printStackTrace();
        }
        return data;
    }
    
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
      // terminal.clearSelection();
    }
}

