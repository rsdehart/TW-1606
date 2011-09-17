package org.twdata.TW1606;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

public class LexerRunner implements Runnable, BeanFactoryAware {
    
    protected Logger log = Logger.getLogger(getClass());
    
    protected Class lexClass;
    protected Lexer lex;
    protected boolean lexerGo = true;
    private int lastState = -1;
    protected StreamReader reader;
    private AutowireCapableBeanFactory factory;
    
    public void setBeanFactory(BeanFactory factory) {
        this.factory = (AutowireCapableBeanFactory)factory;
    }
    
    public void setReader(StreamReader reader) {
        this.reader = reader;
    }
    
    public void setLexerClass(String name) {
        try {
            lexClass = Class.forName(name);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to locate lexer class "+name, ex);
        }
    }
    
    public Lexer getLexer() {
        return lex;
    }
    
    
    /**
     *  Description of the Method
     *
     *@param  in  Description of the Parameter
     */
    public void init(InputStream in) {
        
        try {
            init(new InputStreamReader(in));
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create new lexer", ex);
        }
    }
    
    /**
     *  Description of the Method
     *
     *@param  in  Description of the Parameter
     */
    public void init(Reader in) {
        
        try {
            BufferedReader br = new BufferedReader(in, 16384);
            Constructor c = lexClass.getConstructor(new Class[] {Reader.class});
            if (c != null) {
                lex = (Lexer)c.newInstance(new Object[] {br});
            } else {
                throw new RuntimeException("Constructor for lexer cannot be found");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create new lexer", ex);
        }
        factory.autowireBeanProperties(lex, factory.AUTOWIRE_BY_TYPE, true);
        lex.init();
        if (lastState > -1) {
            if (log.isDebugEnabled()) {
                log.debug("Restarting lexer at state "+lastState);
            }    
            lex.setState(lastState);
        }    
        lexerGo = true;
    }
    
    /**
     *  Main processing method for the TWLexer object
     */
    public void run() {
        
        if (log.isDebugEnabled()) {
            log.debug("Started lexer");
        }
        while (lexerGo) {
            int state;
            try {
                state = lex.yylex();
                if (log.isDebugEnabled()) {
                    log.debug("Captured state:"+state);
                }
            } catch (InterruptedIOException ex) {
                log.debug("Lexer interrupted");
                lexerGo = false;
            } catch (IOException ex) {
                log.debug("Stopping lexer due to IO problem", ex);
                lexerGo = false;
            } catch (Exception e) {
                log.error("There was a problem lexing stream:",e);
                lexerGo = false;
            }
        }
        lastState = lex.getState();
        if (log.isDebugEnabled()) {
            log.debug("Lexer exited, last state:"+lastState);
        }
    }
    
    /**
     *  Description of the Method
     */
    public void shutdown() {
        lexerGo = false;
    }

    public int getLastState() {
        return lastState;
    }    
        
}
