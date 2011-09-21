package de.mud.terminal;

public class BufferEvent {
    public final static int WINDOW_BASE_CHANGED = 1;
    public final static int SCREEN_SIZE_CHANGED = 2;
    public final static int BUFFER_SIZE_CHANGED = 3;
    private int value;
    private int type;
    
    public BufferEvent(int type, int value) {
        this.value = value;
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    public int getValue() {
        return value;
    }
}
