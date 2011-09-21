package org.twdata.TW1606U;

import java.io.*;
import org.twdata.TW1606U.signal.*;
import org.apache.log4j.Logger;

public class StreamCapture implements StreamListener {
    
    private StreamSplitter splitter = null;
    
    private FileOutputStream fout = null;
    private static final Logger log = Logger.getLogger(StreamCapture.class);
    
    public StreamCapture() {
    }
    
    public void setStreamSplitter(StreamSplitter splitter) {
        this.splitter = splitter;
    }
    
    public void startCapture(String path) {
        try {
            fout = new FileOutputStream(path);
        } catch (IOException ex) {
            log.error(ex, ex);
        }
        splitter.addStreamListener(this, true);
    }
    
    public void stopCapture() {
        splitter.removeStreamListener(this, true);
        if (fout != null) {
            try {
                fout.flush();
                fout.close();
            } catch (IOException ex) {
                log.error(ex, ex);
            }
        }
    }
            
    
    public void hasRead(byte[] buffer, int len) {
        try {
            fout.write(buffer, 0, len);
        } catch (IOException ex) {
            log.warn("Unable to write stream capture to file", ex);
        }
    }
    
    public void hasWritten(byte[] b) {
        try {
            fout.write(b, 0, b.length);
        } catch (IOException ex) {
            log.warn("Unable to write stream capture to file", ex);
        }

        
    }
        
}
