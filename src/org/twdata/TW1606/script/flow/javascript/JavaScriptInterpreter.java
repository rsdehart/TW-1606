/* 

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache tw1606" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
package org.twdata.TW1606.script.flow.javascript;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.*;
import java.util.*;
import java.net.URL;

import org.twdata.TW1606.script.flow.Interpreter;
import org.twdata.TW1606.ResourceManager;
import org.springframework.beans.factory.*;
import org.twdata.TW1606.script.flow.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.apache.log4j.Logger;


/**
 * Interface with the JavaScript interpreter.
 *
 * @author <a href="mailto:ovidiu@apache.org">Ovidiu Predescu</a>
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @since March 25, 2002
 * @version CVS $Id: JavaScriptInterpreter.java,v 1.11 2004/12/02 06:18:22 mrdon Exp $
 */
 public class JavaScriptInterpreter implements BeanFactoryAware {
    /**
     * LAST_EXEC_TIME
     * A long value is stored under this key in each top level JavaScript
     * thread scope object. When you enter a context any scripts whose
     * modification time is later than this value will be recompiled and reexecuted,
     * and this value will be updated to the current time.
     */
    private final static String LAST_EXEC_TIME = "__PRIVATE_LAST_EXEC_TIME__";

    // This is the only optimization level that supports continuations
    // in the Christoper Oliver's Rhino JavaScript implementation
    static int OPTIMIZATION_LEVEL = -2;
    private static final Logger log = Logger.getLogger(JavaScriptInterpreter.class);
    /**
     * Shared global scope for scripts and other immutable objects
     */
    JSGlobal scope;
    
    private BeanFactory factory;
    private ResourceManager resourceMgr;
    private ContinuationsManager continuationMgr;
    private String globalScript;
    private static final String GLOBAL_SCRIPT = "__global__";
    
    private Map scripts = Collections.synchronizedMap(new HashMap());
    private Map activeScripts = Collections.synchronizedMap(new HashMap());
    
    JSErrorReporter errorReporter;
    boolean enableDebugger = false;
    /**
     * JavaScript debugger: there's only one of these: it can debug multiple
     * threads executing JS code.
     */
    static org.mozilla.javascript.tools.debugger.Main debugger;

    static synchronized org.mozilla.javascript.tools.debugger.Main getDebugger() {
        if (debugger == null) {
            final org.mozilla.javascript.tools.debugger.Main db
                = new org.mozilla.javascript.tools.debugger.Main("tw1606 Flow Debugger");
            db.pack();
            java.awt.Dimension size =
                java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            size.width *= 0.75;
            size.height *= 0.75;
            db.setSize(size);
            db.setExitAction(new Runnable() {
                    public void run() {
                        db.setVisible(false);
                    }
                });
            db.setOptimizationLevel(OPTIMIZATION_LEVEL);
            db.setVisible(true);
            debugger = db;
            Context.addContextListener(debugger);
        }
        return debugger;
    }

    public void setErrorReporter(JSErrorReporter er) {
        this.errorReporter = er;
    }
    
    public void setEnableDebugger(boolean debug) {
        enableDebugger = debug;
        if (enableDebugger) {
            if (log.isDebugEnabled()) {
                log.debug("Flow debugger enabled, creating");
            }
            getDebugger().doBreak();
        }
    }
    
    public boolean getEnableDebugger() {
        return enableDebugger;
    }
    
    public void setBeanFactory(BeanFactory factory) {
        this.factory = factory;
    }
    
    public void setContinuationsManager(ContinuationsManager cm) {
        continuationMgr = cm;
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.resourceMgr = rm;
    }
    
    public void setGlobalScript(String globalScript) {
        this.globalScript = globalScript;
    }
    
    public void init() {
        
        Context context = Context.enter();
        context.setOptimizationLevel(OPTIMIZATION_LEVEL);
        context.setCompileFunctionsWithDynamicScope(true);
        context.setGeneratingDebug(true);
        
        try {
            scope = new JSGlobal(context);

            // Register some handy classes with JavaScript, so we can make
            // use of them from the flow layer.

            // Access to the tw1606 log
            ScriptableObject.defineClass(scope, JSLog.class);

            // Access to tw1606 internal objects
            ScriptableObject.defineClass(scope, JStw1606.class);
            
            // Access to Dialog objects
            ScriptableObject.defineClass(scope, JSDialog.class);

            // Wrapper for Continuation
            ScriptableObject.defineClass(scope, JSContinuation.class);

            // Define some functions on the top level scope
            String[] names = { "print", "sleep" };
            try {
                scope.defineFunctionProperties(names, JSGlobal.class,
                                               ScriptableObject.DONTENUM);
            } catch (PropertyException e) {
                throw new Error(e.getMessage());
            }

            // Define some global variables in JavaScript
            Object args[] = {};
            Scriptable slog = context.toObject(log, scope);
            scope.put("log", scope, slog);
            errorReporter.setLogger(log);
        }
        catch (Exception e) {
            log.error(e, e);
        } finally {
            Context.exit();
        }
    }

    /**
     * Returns a new Scriptable object to be used as the global scope
     * when running the JavaScript scripts in the context of a request.
     *
     * <p>If you want to maintain the state of global variables across
     * multiple invocations of <code>&lt;map:call
     * function="..."&gt;</code>, you need to invoke from the JavaScript
     * script <code>tw1606.createSession()</code>. This will place the
     * newly create Scriptable object in the user's session, where it
     * will be retrieved from at the next invocation of {@link #callFunction}.</p>
     *
     * @param environment an <code>Environment</code> value
     * @return a <code>Scriptable</code> value
     * @exception Exception if an error occurs
     */
    public Scriptable enterContext(Scriptable thrScope)
        throws Exception
    {
        Context context = Context.enter();
        context.setOptimizationLevel(OPTIMIZATION_LEVEL);
        context.setGeneratingDebug(true);
        context.setCompileFunctionsWithDynamicScope(true);
        context.setErrorReporter(errorReporter);

        // The tw1606 object exported to JavaScript needs to be setup here
        JStw1606 tw1606;
        boolean newScope = false;
        long lastExecTime = 0;
        if (thrScope == null) {

            newScope = true;

            thrScope = context.newObject(scope);

            thrScope.setPrototype(scope);
            // We want 'thrScope' to be a new top-level scope, so set its
            // parent scope to null. This means that any variables created
            // by assignments will be properties of "thrScope".
            thrScope.setParentScope(null);
            
            // Put in the thread scope the tw1606 object, which gives access
            // to the interpreter object, and some tw1606 objects. See
            // JStw1606 for more details.
            Object args[] = {};
            
            
            
            tw1606 = (JStw1606)context.newObject(thrScope, "tw1606", args);
            tw1606.setInterpreter(this);
            tw1606.setParentScope(thrScope);
            tw1606.setBeanFactory(factory);
            thrScope.put("tw1606", thrScope, tw1606);
            ((ScriptableObject)thrScope).defineProperty(LAST_EXEC_TIME,
                                                        new Long(0),
                                                        ScriptableObject.DONTENUM |
                                                        ScriptableObject.PERMANENT);
            
            
            Script script = (Script) scripts.get(GLOBAL_SCRIPT);
            if (script == null) {
                synchronized(scripts) {
                    Reader reader = new InputStreamReader(resourceMgr.getResource(globalScript));
                    script = compileScript(context, thrScope, reader, GLOBAL_SCRIPT);
                    scripts.put(GLOBAL_SCRIPT, script);
                }
            }
            exec(script, context, thrScope);
        } else {
            // lastExecTime = ((Long)thrScope.get(LAST_EXEC_TIME,
            //                                   thrScope)).longValue();

        }
        return thrScope;
    }

    /**
     * Remove the tw1606 object from the JavaScript thread scope so it
     * can be garbage collected, together with all the objects it
     * contains.
     */
    public void exitContext(Scriptable thrScope)
    {
        Context.exit();
    }
    
    public Script compileScript(String id, String code) throws Exception {
        Scriptable sc = enterContext(null);
        Context context = Context.getCurrentContext();
        Reader reader = new BufferedReader(new StringReader(code));
        Script compiledScript = context.compileReader(sc, reader,
                                                     id,
                                                     1, null);
        return compiledScript;
    }   

    protected Script compileScript(Context cx, Scriptable scope,
                                  Reader src, String id) throws Exception {
        try {
            Reader reader = new BufferedReader(src);
            Script compiledScript = cx.compileReader(scope, reader,
                                                     id,
                                                     1, null);
            return compiledScript;
        } finally {
            src.close();
        }
    }

    public void exec(String id, boolean ownThread) throws Exception {
        exec(id, null, ownThread);
    }
    
    public void exec(URL url, boolean ownThread) throws Exception {
        String id = url.toString();
        //Script sc = (Script) scripts.get(id);
        //if (sc == null) {
            Reader reader = new InputStreamReader(url.openStream());
            synchronized (scripts) {
                Scriptable sc = enterContext(null);
                Context context = Context.getCurrentContext();
                Script compiledScript = compileScript(context, sc, reader, id);
                scripts.put(id, compiledScript);
            }
        //}
        exec(id, ownThread);
    }
    
    public void exec(String id, Scriptable scope, boolean ownThread) throws Exception {
           Script script = (Script) scripts.get(id);
           exec(script, scope, ownThread);
    }
    
    /**
     * Calls a JavaScript function, passing <code>params</code> as its
     * arguments. In addition to this, it makes available the parameters
     * through the <code>tw1606.parameters</code> JavaScript array
     * (indexed by the parameter names).
     *
     * @param funName a <code>String</code> value
     * @param params a <code>List</code> value
     * @param environment an <code>Environment</code> value
     * @exception Exception if an error occurs
     */
     public void exec(final Script script, final Scriptable scope, boolean ownThread) throws Exception
    {
        if (ownThread) {
            Thread t = new Thread() {
                public void run() {
                    Scriptable thrScope = null;
                    try {
                        thrScope = enterContext(scope);
                        Scriptable tw1606 = (JStw1606)thrScope.get("tw1606", thrScope);
                        //activeScripts.put(id, tw1606);
                        final Context context = Context.getCurrentContext();
                        if (enableDebugger) {
                            if (!getDebugger().isVisible()) {
                                // only raise the debugger window if it isn't already visible
                                getDebugger().setVisible(true);
                            }
                        }
                    
                        exec(script, context, thrScope);
                        
                    } catch (Exception ex) {
                        log.error(ex, ex);
                    } finally {
                        activeScripts.remove(script);
                        Context.exit();
                    }
                    //activeScripts.remove(id);
                    
                } 
            };
            t.start();
            activeScripts.put(script, t);
        } else {
            Scriptable thrScope = null;
            try {
                thrScope = enterContext(scope);
                //log.info("tw1606 class:"+thrScope.get("tw1606", thrScope).getClass().getName());
                //Scriptable tw1606 = (JStw1606)thrScope.get("tw1606", thrScope);
                //activeScripts.put(id, tw1606);
                final Context context = Context.getCurrentContext();
                if (enableDebugger) {
                    if (!getDebugger().isVisible()) {
                        // only raise the debugger window if it isn't already visible
                        getDebugger().setVisible(true);
                    }
                }
            
                exec(script, context, thrScope);
            } finally {
                exitContext(thrScope);
            }
            //activeScripts.remove(id);
        }
                
    }
    
    public void stop(URL url) {
        stop(url.toString());
    }
    
    public void stopAll() {
        synchronized(activeScripts) {
            for (Iterator i = activeScripts.values().iterator(); i.hasNext(); ) {
                ((JStw1606)i.next()).stopRequested();
            }
        }
    }
    
    public void stop(String id) {
        Script script = (Script) scripts.get(id);
        Thread t = (Thread)activeScripts.get(script);
        log.warn("script:"+script+" thread:"+t);
        if (t != null) {
            log.debug("Interrupting script");
            t.interrupt();
        }
    }
    
    public Object eval(String name, String code, Scriptable scope) throws Exception {
        Scriptable thrScope = null;
        Object result = null;
        try {
            thrScope = enterContext(scope);
            //log.info("tw1606 class:"+thrScope.get("tw1606", thrScope).getClass().getName());
            //Scriptable tw1606 = (JStw1606)thrScope.get("tw1606", thrScope);
            //activeScripts.put(id, tw1606);
            final Context context = Context.getCurrentContext();
            if (enableDebugger) {
                if (!getDebugger().isVisible()) {
                    // only raise the debugger window if it isn't already visible
                    getDebugger().setVisible(true);
                }
            }
        
            try {
                result = context.evaluateString(thrScope, code, name, 1, null);
            } catch (JavaScriptException ex) {
                EvaluatorException ee =
                    Context.reportRuntimeError(ToolErrorReporter.getMessage("msg.uncaughtJSException",
                                                                            ex.getMessage()));
                Throwable unwrapped = unwrap(ex);
                throw new RuntimeException(ee.getMessage(), unwrapped);
            } catch (EcmaError ee) {
                String msg = ToolErrorReporter.getMessage("msg.uncaughtJSException", ee.toString());
                if (ee.getSourceName() != null) {
                    Context.reportRuntimeError(msg,
                                               ee.getSourceName(),
                                               ee.getLineNumber(),
                                               ee.getLineSource(),
                                               ee.getColumnNumber());
                } else {
                    Context.reportRuntimeError(msg);
                }
                throw new RuntimeException(ee.getMessage(), ee);
            } 
        } finally {
            exitContext(thrScope);
        }   
        return result;
    }
    
    public void exec(Script script, Context ctx, Scriptable thrScope) {
        try {
            script.exec(ctx, thrScope);

        } catch (JavaScriptException ex) {
            EvaluatorException ee =
                Context.reportRuntimeError(ToolErrorReporter.getMessage("msg.uncaughtJSException",
                                                                        ex.getMessage()));
            Throwable unwrapped = unwrap(ex);
            throw new RuntimeException(ee.getMessage(), unwrapped);
        } catch (EcmaError ee) {
            String msg = ToolErrorReporter.getMessage("msg.uncaughtJSException", ee.toString());
            if (ee.getSourceName() != null) {
                Context.reportRuntimeError(msg,
                                           ee.getSourceName(),
                                           ee.getLineNumber(),
                                           ee.getLineSource(),
                                           ee.getColumnNumber());
            } else {
                Context.reportRuntimeError(msg);
            }
            throw new RuntimeException(ee.getMessage(), ee);
        } 
    }
    

    public void handleContinuation(String id, List params)
        throws Exception
    {
        Continuation wk = continuationMgr.lookupContinuation(id);

        if (wk == null) {

            /*
             * Throw an InvalidContinuationException to be handled inside the
             * <map:handle-errors> sitemap element.
             */
            throw new InvalidContinuationException("The continuation ID " + id + " is invalid.");
        }

        Context context = Context.enter();
        context.setOptimizationLevel(OPTIMIZATION_LEVEL);
        context.setGeneratingDebug(true);
        context.setCompileFunctionsWithDynamicScope(true);

        // Obtain the JS continuation object from it, and setup the
        // JStw1606 object associated in the dynamic scope of the saved
        // continuation with the environment and context objects.
        JSContinuation jswk = (JSContinuation)wk.getUserObject();
        JStw1606 tw1606 = jswk.getJStw1606();
        //tw1606.setContext(manager, environment);
        final Scriptable kScope = tw1606.getParentScope();
        if (enableDebugger) {
            getDebugger().setVisible(true);
        }

        // We can now resume the processing from the state saved by the
        // continuation object. Setup the JavaScript Context object.
        Object handleContFunction = kScope.get("handleContinuation", kScope);
        if (handleContFunction == Scriptable.NOT_FOUND) {
            throw new RuntimeException("Cannot find 'handleContinuation' "
                                       + "(system.js not loaded?)");
        }

        Object args[] = { jswk };

        int size = (params != null ? params.size() : 0);
        NativeArray parameters = new NativeArray(size);

        if (size != 0) {
            for (int i = 0; i < size; i++) {
                Interpreter.Argument arg = (Interpreter.Argument)params.get(i);
                parameters.put(arg.name, parameters, arg.value);
            }
        }

        //TODO: do i need this?
        //tw1606.setParameters(parameters);

        try {
            ((Function)handleContFunction).call(context, kScope, kScope, args);
        } catch (JavaScriptException ex) {
            EvaluatorException ee =
                Context.reportRuntimeError(ToolErrorReporter.getMessage("msg.uncaughtJSException",
                                                                        ex.getMessage()));
            Throwable unwrapped = unwrap(ex);

            throw new RuntimeException(ee.getMessage(), unwrapped);
        } catch (EcmaError ee) {
            String msg = ToolErrorReporter.getMessage("msg.uncaughtJSException", ee.toString());
            if (ee.getSourceName() != null) {
                Context.reportRuntimeError(msg,
                                           ee.getSourceName(),
                                           ee.getLineNumber(),
                                           ee.getLineSource(),
                                           ee.getColumnNumber());
            } else {
                Context.reportRuntimeError(msg);
            }
            throw new RuntimeException(ee.getMessage(), ee);
        } finally {
            Context.exit();
        }
    }

    private Throwable unwrap(JavaScriptException e) {
        Object value = e.getValue();
        while (value instanceof Wrapper) {
            value = ((Wrapper)value).unwrap();
        }
        if (value instanceof Throwable) {
            return (Throwable)value;
        }
        return e;
    }

}
