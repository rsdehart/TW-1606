package org.twdata.TW1606.action;

import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.twdata.TW1606.script.flow.*;
import org.twdata.TW1606.script.flow.javascript.*;
import org.twdata.TW1606.signal.*;
import org.mozilla.javascript.*;
import java.util.*;
import java.io.*;

/**
 *  Description of the Class
 *
 */
public class FlowAction extends AbstractScriptAction implements ScriptAction {

    private JavaScriptInterpreter interpreter;
    private Script compiledScript;
    private static final Map scopes = Collections.synchronizedMap(new HashMap());
    private Scriptable scope;
    
    public FlowAction() {
        super();
    }
    
    /**
     *  Sets the beanShell attribute of the BSHAction object
     *
     *@param  js  The new beanShell value
     */
    public void setInterpreter(JavaScriptInterpreter inter) {
        this.interpreter = inter;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    protected Scriptable getScope() {
        if (scope == null) {
            Scriptable sharedScope = null;
            synchronized(scopes) {
                if (scopes.containsKey(path)) {
                    sharedScope = (Scriptable) scopes.get(path);
                } else {
                    try {
                        sharedScope = interpreter.enterContext(null);
                        
                        // read in global script
                        interpreter.eval("global", globalScript, sharedScope);
                        
                        interpreter.exitContext(sharedScope);
                        scopes.put(path, sharedScope);
                    } catch (Exception ex) {
                        log.error("Unable to create scope", ex);
                    }
                }
            }
            try {
                Context context = Context.enter();
                scope = context.newObject(sharedScope);
                scope.setPrototype(null);
                scope.setParentScope(sharedScope);
                
                Scriptable var = context.toObject(this, scope);
                scope.put("action", scope, var);
                
                ((JStw1606)sharedScope.get("tw1606", sharedScope)).setParentScope(scope);
                Context.exit();
            } catch (Exception ex) {
                log.error("Unable to set current action as variable", ex);
            }
        } 
        
        return scope;
    }
    
    /**
     *  Description of the Method
     *
     *@param  frame  Description of the Parameter
     */
    public void invoke() {
        try {
            Scriptable scope = getScope();
            if (compiledScript == null) {
                compiledScript = interpreter.compileScript("action:"+getId(), script);
            }
            long trace = System.currentTimeMillis();
            interpreter.exec(compiledScript, scope, false);
            log.info("Flow action exec time: "+(System.currentTimeMillis() - trace)+" ms");
        }
        catch (Exception e) {
            log.error("Problem executing FlowAction " + id, e);
        }
    }
    
    public void channel(OnlineStatusSignal signal) {
        String command = signal.getCommand();
        if (signal.ONLINE.equals(command)) {
            runScript(signal.ONLINE, onConnect);
        } else {
            runScript(signal.ONLINE, onDisconnect);
        }
    }
    
    public void channel(ScriptStatusSignal signal) {
        String command = signal.getCommand();
        if (signal.START.equals(command)) {
            runScript(signal.START, onScriptStart);
        } else {
            runScript(signal.STOP, onScriptStop);
        }
    }
    
    private void runScript(String name, String code) {
        try {
            Scriptable scope = getScope();
            
            // read in global script
            interpreter.eval(name, code, scope);
            
        } catch (Exception ex) {
            log.error("Unable to run script:"+name, ex);
        }
    }
    
}

