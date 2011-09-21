package org.twdata.TW1606U;

import java.io.*;
import org.twdata.TW1606U.signal.*;
import org.apache.log4j.Logger;

public class LexerManager implements StreamListener {
    
    private LexerRunner lexerRunner;
    private Thread lexThread;
    
    private PipedOutputStream outStream = null;
    private PipedInputStream inStream = null;
    private StreamSplitter splitter = null;
    private MessageBus bus;
    private byte[] lastSend;
    private int lastSendLen;
    private static final Logger log = Logger.getLogger(LexerManager.class);
    
    public LexerManager() {
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setLexerRunner(LexerRunner lexerRunner) {
        this.lexerRunner = lexerRunner;   
    }
    
    public Lexer getLexer() {
        return lexerRunner.getLexer();
    }    
    
    public void setStreamSplitter(StreamSplitter splitter) {
        this.splitter = splitter;
        splitter.addStreamListener(this, true);
    }
    
    public void channel(SessionStatusSignal signal) {
        if (signal.START.equals(signal.getStatus())) {
            startup();
        } else if (signal.STOP.equals(signal.getStatus())) {
            shutdown();        
        }
    }
    
    private synchronized void startup() {
        try {
            inStream = new PipedInputStream();
            outStream = new PipedOutputStream(inStream);
        }
        catch (IOException ex) {
            log.error("Unable to initialize lexerRunner streams", ex);
        }
        //splitter.addStreamListener(this, true);
        log.info("firing up the lexerRunner");
        lexerRunner.init(new BufferedInputStream(inStream));
        lexThread = new Thread(lexerRunner);
        lexThread.start();
    }
            
    private synchronized void shutdown() {
        lexerRunner.shutdown();
        //splitter.removeStreamListener(this, true);
        try {
            lexThread.interrupt(); 
            if (inStream != null) {
                inStream.close();
                outStream.close();
            }
        }
        catch (IOException ex) {
            log.info("Unable to close lexerRunner streams cleanly", ex);
        }
        inStream = null;
        outStream = null;
         
        lexThread = null;
    }
    
    public synchronized void hasRead(byte[] buffer, int len) {
        if (outStream != null) {
            
            // If the lexerRunner died for some reason, fire it back up
            if (!lexThread.isAlive()) {
                log.info("The Lexer has apparently died, restarting...");
                try {
                    log.info("Last sent:"+new String(lastSend, 0, lastSendLen, "Cp1252")+"|");
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
                startup();
                try {
                    outStream.write(lastSend, 0, lastSendLen);
                } catch (IOException ex) {
                    log.warn("Unable to send last-sent data before crash", ex);
                }
            }
            try {
                outStream.write(buffer, 0, len);
            } catch (IOException ex) {
                log.warn("Unable to write to lex stream", ex);
            }
            lastSend = buffer;
            lastSendLen = len;
        }
        //outStream.flush();
    }
    
    /**
     *  Called when data has been written to the stream from a filter
     *
     * @param s The data
     */         
    public void hasWritten(byte[] b)  {}
        
}
