/**
 * 
 */
package org.twdata.TW1606.tw.gui.terminal;

public class Link {
    private String text;
    private int start;
    private int end;

    public Link(String text, int start, int end) {
        this.text = text;
        this.start = start;
        this.end = end;
    }
    public int getEnd() {
        return end;
    }
    public int getStart() {
        return start;
    }
    public String getText() {
        return text;
    }
}