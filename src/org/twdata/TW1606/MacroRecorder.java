package org.twdata.TW1606;

import org.twdata.TW1606.gui.Terminal;
import org.twdata.TW1606.signal.*;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.twdata.TW1606.*;

public class MacroRecorder implements StreamListener {
    
    private StringBuffer macro;
    private String val;
    private StreamFilter writer;
    private MessageBus bus;
    private StreamSplitter splitter = null;
    private static final Logger log = Logger.getLogger(MacroRecorder.class);
    
    public MacroRecorder() {
        super();
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    public void setStreamSplitter(StreamSplitter splitter) {
        this.splitter = splitter;
        splitter.addStreamListener(this, false);
    }
    
    public void setStreamWriter(StreamFilter writer) {
        this.writer = writer;
    }
    
    public void startRecording() {
        val = null;
        macro = new StringBuffer();
    }
    
    public void stopRecording() {
        if (macro != null) {
            val = macro.toString();
            macro = null;
        }
    }
    
    public boolean isRecording() {
        return macro != null;
    }
    
    public void hasRead(byte[] b, int len) {}
    
    public void hasWritten(byte[] b){
        record(new String(b));
    }
    
    public void record(String val) {
        if (isRecording()) {
            macro.append(val);
        }
    }
    
    public void play() {
        if (val != null) {
            try {
                writer.write(val.getBytes("Cp1252"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void channel(OnlineStatusSignal signal) {
        String command = signal.getCommand();
        if (!signal.ONLINE.equals(command)) {
            stopRecording();
        }
    }
}
