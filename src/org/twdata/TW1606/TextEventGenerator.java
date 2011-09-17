package org.twdata.TW1606;

import java.io.*;
import java.util.*;

import org.twdata.TW1606.tw.*;
import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.signal.*;
import org.twdata.TW1606.gui.*;

import org.twdata.TW1606.*;

import org.apache.log4j.Logger;

/**
 *  Description of the Class
 *
 *@created    October 25, 2003
 */
public class TextEventGenerator implements StreamListener {

    Object waitForSynch = new Object();
    Object backBufferSynch = new Object();
    boolean waitingForLine = false;
    boolean waitingFor = false;
    char watchString[][] = null;
    int currentIndex[];
    SavePoint savePoints[];
    long lineCounter = 0;
    int matchNumber = -1;
    boolean online = false;
    StringBuffer prevLine, currentLine = new StringBuffer();

    StringBuffer buffer = new StringBuffer();
    boolean doBuffer;
    int bufferMax;
    private static final Logger log = Logger.getLogger(TextEventGenerator.class);

    Vector responseKeys = new Vector();
    Vector responses = new Vector();
    Vector responseIndex = new Vector();

    StringBuffer backBuffer = new StringBuffer();
    StreamFilter filter;
    private StreamSplitter splitter;
    private MessageBus bus;
    


    public TextEventGenerator() {
    }
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
        bus.plug(this);
    }
    
    /**
     *  Sets the streamSplitter attribute of the BSFRunner object
     *
     *@param  splitter  The new streamSplitter value
     */
    public void setStreamSplitter(StreamSplitter splitter) {
        this.splitter = splitter;
        filter = (StreamFilter)splitter;
    }
    
    /**
     *  Description of the Method
     *
     *@param  signal  Description of the Parameter
     */
    public void channel(OnlineStatusSignal signal) {
        if (signal.ONLINE.equals(signal.getCommand())) {
            log.debug("firing up the text event generator");
            splitter.addStreamListener(this, true);
            online = true;
        } else {
            splitter.removeStreamListener(this, true);
            online = false;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  buffer           Description of the Parameter
     *@param  len              Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    public void hasRead(byte[] buffer, int len) {
        if (len > -1 ) {
            handleIncomingData(new String(buffer, 0, len));
        }
    }
    
    public void hasWritten(byte[] data) {}


    /**
     *  Description of the Method
     *
     *@param  toWait  Description of the Parameter
     *@return         Description of the Return Value
     */
    public int waitFor(String toWait[], long timeOut) throws InterruptedException {
        int result = -1;
        synchronized (waitForSynch) {
            waitingFor = true;
            int[] slots = new int[toWait.length];
            for (int x=0; x<toWait.length; x++) {
                int slot = 0;
                
                // Find an empty slot if one exists
                if (watchString != null) {
                    for (slot=0; slot<watchString.length; slot++) {
                        if (watchString[slot] == null) {
                            break;
                        }
                    }
                }
                
                // create or expand the watch array
                if (watchString == null || slot == watchString.length) {
                    char[][] tmp = new char[slot+1][];
                    int[] tmp2 = new int[slot+1];
                    SavePoint[] tmp3 = new SavePoint[slot+1];
                    if (slot > 0) {
                        System.arraycopy(watchString, 0, tmp, 0, slot);
                        System.arraycopy(currentIndex, 0, tmp2, 0, slot);
                        System.arraycopy(savePoints, 0, tmp3, 0, slot);
                    }
                    watchString = tmp;
                    currentIndex = tmp2;
                    savePoints = tmp3;
                }
                
                watchString[slot] = new char[toWait[x].length()];
                toWait[x].getChars(0, toWait[x].length(), watchString[slot], 0);
                currentIndex[slot] = -1;
                slots[x] = slot;
            }
            
            currentLine = new StringBuffer();

            // TODO: not sure about this one
            clearResponseState();

            log.debug("handling back buffer:"+backBuffer.toString());
            handleIncomingData(backBuffer.toString());
            /*int m = checkForMatch(slots);
            if (m > -1) {
                return m;
            }*/

            if (waitingFor) {

                clearBackBuffer();
                boolean hit = false;
                long startTime = System.currentTimeMillis();
                long timeSinceStart;
                while (!hit && online && !Thread.currentThread().isInterrupted()) {
                    timeSinceStart = System.currentTimeMillis() - startTime;
                    if (timeSinceStart < timeOut) {
                        log.debug("waiting for match...");
                        waitForSynch.wait(timeOut - timeSinceStart);
                        log.debug("waking up or timeout expired");
                        result = checkForMatch(slots);
                        hit = true;
                    } else {
                        hit = true;
                    }
                }
            } 
            for (int y=0; y<slots.length; y++) {
                watchString[y] = null;
                currentIndex[y] = -1;
            }
        }
        return result;

        //return result;
    }
    
    private int checkForMatch(int[] slots) {
        for (int slot = 0; slot < slots.length; slot++) {
            if (matchNumber == slots[slot]) {
                if (log.isDebugEnabled()) {
                    log.debug("match found");
                }
                //hit = true;
                synchronized (waitForSynch) {
                    for (int y=0; y<slots.length; y++) {
                        watchString[y] = null;
                        currentIndex[y] = -1;
                    }
                    waitingFor = false;
                    for (int x=0; x< watchString.length; x++) {
                        if (watchString[x] != null) {
                            waitingFor = true;
                        }
                    }
                }
                return slot;
            }
        }
        return -1;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public int waitForLine() {
        synchronized (waitForSynch) {
            waitingForLine = true;

            clearResponseState();
            handleIncomingData(backBuffer.toString());

            if (waitingForLine) {

                clearBackBuffer();
                try {
                    waitForSynch.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return 640;
                }
            }
        }

        return 0;
    }


    /**
     *  Gets the lastLine attribute of the BSFRunner object
     *
     *@return    The lastLine value
     */
    public String getLastLine() {
        if (prevLine != null) {
            return prevLine.toString();
        } else {
            return null;
        }
    }


    /**
     *  Sets the buffer attribute of the BSFRunner object
     *
     *@param  size  The new buffer value
     *@return       Description of the Return Value
     */
    public String setBuffer(int size) {
        String result;

        synchronized (waitForSynch) {
            result = buffer.toString();
            buffer = new StringBuffer();
            bufferMax = size;
            if (size > 0) {
                doBuffer = true;
            } else {
                doBuffer = false;
            }
        }

        return result;
    }


    /**
     *  Adds a feature to the Response attribute of the BSFRunner object
     *
     *@param  key       The feature to be added to the Response attribute
     *@param  response  The feature to be added to the Response attribute
     */
    public void addResponse(String key, String response) {
        synchronized (waitForSynch) {
            char keyChars[] = new char[key.length()];
            key.getChars(0, key.length(), keyChars, 0);
            responseKeys.addElement(keyChars);
            responses.addElement(response);
            responseIndex.addElement(new Integer(-1));
        }
    }


    /**
     *  Description of the Method
     */
    public void clearResponseState() {
        synchronized (waitForSynch) {
            int size = responseIndex.size();
            responseIndex = new Vector();
            for (int counter = 0; counter < size; counter++) {
                responseIndex.addElement(new Integer(-1));
            }
        }
    }


    /**
     *  Description of the Method
     */
    public void removeAllResponses() {
        synchronized (waitForSynch) {
            responseKeys.removeAllElements();
            responseIndex.removeAllElements();
            responses.removeAllElements();
        }
    }


    /**
     *  Description of the Method
     *
     *@param  key  Description of the Parameter
     */
    public void removeResponse(String key) {
        synchronized (waitForSynch) {
            for (int counter = 0; counter < responseKeys.size(); counter++) {
                String k = new String((char[]) responseKeys.elementAt(counter));
                if (k.equals(key)) {

                    responseKeys.removeElementAt(counter);
                    responses.removeElementAt(counter);
                    responseIndex.removeElementAt(counter);
                }
            }
        }
    }


    /**
     *  Description of the Method
     */
    public void clearBackBuffer() {
        synchronized (backBufferSynch) {
            backBuffer = new StringBuffer();
        }

    }


    /**
     *  Description of the Method
     *
     *@param  checkString  Description of the Parameter
     */
    public void handleIncomingData(String checkString) {
        synchronized (waitForSynch) {
            
            synchronized (backBufferSynch) {
                if (!waitingFor && !waitingForLine) {
                    backBuffer.append(checkString);
                }
            }
            

            for (int counter = 0; (waitingFor || waitingForLine) && counter < checkString.length(); counter++) {
                char currentChar = checkString.charAt(counter);

                if (doBuffer && buffer.length() < bufferMax) {
                    buffer.append(currentChar);
                }

                if (currentChar != '\n' && currentChar != '\r') {
                    currentLine.append(currentChar);
                } else if (currentChar == '\n') {

                    prevLine = currentLine;
                    currentLine = new StringBuffer();
                    lineCounter++;

//                        if (counter < checkString.length() - 1) //make sure we're not at the end
//                        {
//                            int nextNewLine = checkString.indexOf('\n', counter + 1);
//                            if (nextNewLine > -1)
//                            {
//                                currentLine.append(checkString.substring(counter + 1, nextNewLine));
//                            }
//                            else
//                            {
//                                currentLine.append(checkString.substring(counter + 1));
//                            }
//                        }



                    if (waitingForLine) {
                        if (prevLine.length() > 0) {
                            waitForSynch.notifyAll();
                            waitingForLine = false;
                        }
                    }
                }

                for (int wcounter = 0; waitingFor && wcounter < watchString.length; wcounter++) {
                //log.info("looking at pos: "+wcounter+" index:"+currentIndex[wcounter]+" curChar:"+currentChar);
                    if (watchString[wcounter] != null && watchString[wcounter].length > currentIndex[wcounter] + 1 && watchString[wcounter][currentIndex[wcounter] + 1] == currentChar) {
                        log.debug("match character '"+currentChar+"' pos:"+currentIndex[wcounter]+ " string:"+watchString[wcounter].length);
                        String str = "Waiting for: \n";
                        for (int x = 0; x< watchString.length; x++) {
                            if (watchString[x] != null) {
                                str += "\t ["+x+"] "+new String(watchString[x])+" \n";
                            } else {
                                str += "\t ["+x+"] null \n";
                            }
                        }
                        log.debug(str);
                        currentIndex[wcounter]++;
                        if (currentIndex[wcounter] + 1 == watchString[wcounter].length) {
                            if (log.isDebugEnabled()) {
                                log.debug("match in handleInput:"+wcounter);
                            }
                            matchNumber = wcounter;
                            waitForSynch.notifyAll();
                            //waitingFor = false;
                            if (currentChar != '\n') {
                                savePoints[matchNumber] = new SavePoint(currentLine, lineCounter);
                            } else {
                                savePoints[matchNumber] = new SavePoint(prevLine, lineCounter);
                            }
                            if (log.isDebugEnabled()) {
                                log.debug("match found in line:"+savePoints[matchNumber].linePartial);
                            }

                            handleResponses(currentChar);
                            if (counter < checkString.length() - 1) {

                                clearBackBuffer();
                                backBuffer.append(checkString.substring(counter + 1));
                            }
                        }
                    } else {
                        currentIndex[wcounter] = -1;
                    }
                }

                if (waitingFor || waitingForLine) {
                    handleResponses(currentChar);
                }
            }

        }
    }


    /**
     *  Description of the Method
     *
     *@param  currentChar  Description of the Parameter
     */
    public void handleResponses(char currentChar) {
        for (int wcounter = 0; wcounter < responseKeys.size(); wcounter++) {
            char keyString[] = (char[]) responseKeys.elementAt(wcounter);
            int cindex = ((Integer) responseIndex.elementAt(wcounter)).intValue();

            if (keyString[cindex + 1] == currentChar) {
                cindex++;
                if (cindex + 1 == keyString.length) {

                    cindex = -1;
                    try {
                        filter.write(((String) responses.elementAt(wcounter)).getBytes("Cp1252"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                cindex = -1;
            }

            responseIndex.removeElementAt(wcounter);
            responseIndex.insertElementAt(new Integer(cindex), wcounter);
        }

    }

    // EXTERNAL API FUNCTIONS
    
    /**
     *  Description of the Method
     *
     *@param  toConvert  Description of the Parameter
     *@return            Description of the Return Value
     */
    public String convertString(String toConvert) {
        //int len = toConvert.length();
        //StringBuffer result = new StringBuffer(toConvert.length());
        
        /*for (int counter = 0; counter < len; counter++) {
            char t = toConvert.charAt(counter);
            //if (t == '^' && counter + 1 < len && (toConvert.charAt(counter + 1) == 'm' || toConvert.charAt(counter + 1) == 'M')) {
            if (t == '*') {
                result.append('\n');
            } else {
                result.append(t);
            }
        }
        */

        //return result.toString();
        return toConvert;
    }

    
    /**
     *  Description of the Method
     *
     *@param  args  Description of the Parameter
     *@return       Description of the Return Value
     */
    public String waitForString(String arg, long timeout, boolean waitForLine) 
            throws InterruptedException {
        long startTime = System.currentTimeMillis();
        String realArg = convertString(arg);
        String result = null;
        int slot = waitFor(new String[]{realArg}, timeout * 1000);
        if (slot == 0) {
            SavePoint sp = savePoints[0];
            if (waitForLine) {
                while (lineCounter <= sp.lineNumber && (System.currentTimeMillis() < startTime+timeout)) {
                    Thread.currentThread().sleep(50);
                }
                result = getLastLine();
            } else {
                result = sp.linePartial;
            }
        }
        return result;
    }
    
    public int waitMux(String[] args, long timeout, boolean waitForLine) 
            throws InterruptedException {
        long startTime = System.currentTimeMillis();
        for (int x=0; x<args.length; x++) {
            args[x] = convertString(args[x]);   
        }
        int result = waitFor(args, timeout * 1000);
        if (result > -1) {
            SavePoint sp = savePoints[result];
            if (waitForLine) {
                while (lineCounter <= sp.lineNumber && (System.currentTimeMillis() < startTime+timeout)) {
                    Thread.currentThread().sleep(50);
                }
            } 
        } 
        return result;
    }
    


    /**
     *  Description of the Method
     *
     *@param  args  Description of the Parameter
     *@return       Description of the Return Value
     */
    public String waitForNextLine() {
        waitForLine();
        return getLastLine();
    }


    /**
     *  Description of the Method
     *
     *@param  args  Description of the Parameter
     *@return       Description of the Return Value
     */
    public void sleep(int seconds) {
        long sleepFor = 200;

        sleepFor = (long) seconds * 1000;

        try {
            Thread.currentThread().sleep(sleepFor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    /**
     *  Description of the Method
     *
     *@param  args  Description of the Parameter
     *@return       Description of the Return Value
     */
    public void waitAndRespond(String key, String response) {
        if (response == null) {
            removeResponse(convertString(key));
        } else {
            addResponse(convertString(key), convertString(response));
        }
    }
    
    private class SavePoint {
        public String linePartial;
        public long lineNumber;
        
        public SavePoint(StringBuffer sb, long lineNumber) {
            linePartial = sb.toString();
            this.lineNumber = lineNumber;
        }
    }

}
