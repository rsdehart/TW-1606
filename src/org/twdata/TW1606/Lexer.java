package org.twdata.TW1606;

public interface Lexer {
    
    public void init();

    public void setState(int state);

    public int getState();
    public int yylex() throws java.io.IOException;
}
