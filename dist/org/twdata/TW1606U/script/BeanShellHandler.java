/*
 * BeanShell.java - BeanShell scripting support
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001, 2002 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.twdata.TW1606U.script;

//{{{ Imports
import bsh.*;
import java.io.*;
import java.lang.ref.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import org.apache.log4j.Logger;
import org.twdata.TW1606U.ResourceManager;
import org.springframework.beans.factory.*;
//}}}

/**
 *  BeanShell is the extension language. Adapted from jEdit 4.2pre5 by Slava
 *  Pestov.
 *
 *@author     Don Brown
 *@version    $Id: BeanShellHandler.java,v 1.4 2004/08/02 04:07:49 mrdon Exp $
 */
public class BeanShellHandler implements BeanFactoryAware {//}}}

    //}}}

    //{{{ Static variables
    
    private final Object[] NO_ARGS = new Object[0];
    private Interpreter interpForMethods;
    private NameSpace global;
    private boolean running;
    private static final Logger log = Logger.getLogger(BeanShellHandler.class);
    //}}}
    
    private BeanFactory factory;
    private ResourceManager res;
    
    public void setBeanFactory(BeanFactory factory) {
        this.factory = factory;
        init();
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
    }
    
    //{{{ setupDefaultVariables() method
    /**
     *  Description of the Method
     *
     *@param  namespace          Description of the Parameter
     *@exception  UtilEvalError  Description of the Exception
     */
    private void setupDefaultVariables(NameSpace namespace) {
        // TODO
    } //}}}

    //{{{ runScript() method
    /**
     *  Runs a BeanShell script. Errors are shown in a dialog box.<p>
     *
     *  If the <code>in</code> parameter is non-null, the script is read from
     *  that stream; otherwise it is read from the file identified by <code>path</code>
     *  .<p>
     *
     *  The <code>scriptPath</code> BeanShell variable is set to the path name
     *  of the script.
     *
     *@param  path          The script file's VFS path.
     *@param  in            The reader to read the script from, or <code>null</code>
     *      .
     *@param  ownNamespace  If set to <code>false</code>, methods and variables
     *      defined in the script will be available to all future uses of
     *      BeanShell; if set to <code>true</code>, they will be lost as soon as
     *      the script finishes executing. jEdit uses a value of <code>false</code>
     *      when running startup scripts, and a value of <code>true</code> when
     *      running all other macros.
     *@since                jEdit 4.0pre7
     */
    public void runScript(String path, Reader in,
            boolean ownNamespace) {
        try {
            _runScript(path, in, ownNamespace);
        }
        catch (Throwable e) {
            log.error(e, e);
        }
    }//}}}

    //{{{ runScript() method
    /**
     *  Runs a BeanShell script. Errors are shown in a dialog box.<p>
     *
     *  If the <code>in</code> parameter is non-null, the script is read from
     *  that stream; otherwise it is read from the file identified by <code>path</code>
     *  .<p>
     *
     *  The <code>scriptPath</code> BeanShell variable is set to the path name
     *  of the script.
     *
     *@param  path       The script file's VFS path.
     *@param  in         The reader to read the script from, or <code>null</code>
     *      .
     *@param  namespace  The namespace to run the script in.
     *@since             jEdit 4.2pre5
     */
    public void runScript(String path, Reader in,
            NameSpace namespace) {
        try {
            _runScript(path, in, namespace);
        }
        catch (Throwable e) {
            log.error(e, e);
        }
    }//}}}

    //{{{ _runScript() method
    /**
     *  Runs a BeanShell script. Errors are passed to the caller.<p>
     *
     *  If the <code>in</code> parameter is non-null, the script is read from
     *  that stream; otherwise it is read from the file identified by <code>path</code>
     *  .<p>
     *
     *  The <code>scriptPath</code> BeanShell variable is set to the path name
     *  of the script.
     *
     *@param  path           The script file's VFS path.
     *@param  in             The reader to read the script from, or <code>null</code>
     *      .
     *@param  ownNamespace   If set to <code>false</code>, methods and variables
     *      defined in the script will be available to all future uses of
     *      BeanShell; if set to <code>true</code>, they will be lost as soon as
     *      the script finishes executing. jEdit uses a value of <code>false</code>
     *      when running startup scripts, and a value of <code>true</code> when
     *      running all other macros.
     *@exception  Exception  instances are thrown when various BeanShell errors
     *      occur
     *@since                 jEdit 4.0pre7
     */
    public void _runScript(String path, Reader in,
            boolean ownNamespace)
        throws Exception {
        _runScript(path, in, ownNamespace
                 ? new NameSpace(global, "script namespace")
                 : global);
    }//}}}

    //{{{ _runScript() method
    /**
     *  Runs a BeanShell script. Errors are passed to the caller.<p>
     *
     *  If the <code>in</code> parameter is non-null, the script is read from
     *  that stream; otherwise it is read from the file identified by <code>path</code>
     *  .<p>
     *
     *  The <code>scriptPath</code> BeanShell variable is set to the path name
     *  of the script.
     *
     *@param  path           The script file's VFS path.
     *@param  in             The reader to read the script from, or <code>null</code>
     *      .
     *@param  namespace      The namespace to run the script in.
     *@exception  Exception  instances are thrown when various BeanShell errors
     *      occur
     *@since                 jEdit 4.2pre5
     */
    public void _runScript(String path, Reader in,
            NameSpace namespace)
        throws Exception {
        log.info("Running script " + path);

        Interpreter interp = createInterpreter(namespace);

        try {
            if (in == null) {
                in = res.getResourceAsReader(path);
            }

            setupDefaultVariables(namespace);
            interp.set("scriptPath", path);

            running = true;

            interp.eval(in, namespace, path);
        }
        catch (Exception e) {
            unwrapException(e);
        }
        finally {
            running = false;

            try {
                // no need to do this for macros!
                if (namespace == global) {
                    resetDefaultVariables(namespace);
                    interp.unset("scriptPath");
                }
            }
            catch (EvalError e) {
                // do nothing
            }
        }
    }//}}}

    //{{{ eval() method
    /**
     *  Evaluates the specified BeanShell expression. Errors are reported in a
     *  dialog box.
     *
     *@param  namespace  The namespace
     *@param  command    The expression
     *@return            Description of the Return Value
     *@since             jEdit 4.0pre8
     */
    public Object eval(NameSpace namespace, String command) {
        try {
            return _eval(namespace, command);
        }
        catch (Throwable e) {
            log.error(e, e);
        }

        return null;
    }//}}}

    //{{{ _eval() method
    /**
     *  Evaluates the specified BeanShell expression. Unlike <code>eval()</code>
     *  , this method passes any exceptions to the caller.
     *
     *@param  namespace      The namespace
     *@param  command        The expression
     *@return                Description of the Return Value
     *@exception  Exception  instances are thrown when various BeanShell errors
     *      occur
     *@since                 jEdit 3.2pre7
     */
    public Object _eval(NameSpace namespace, String command)
        throws Exception {
        Interpreter interp = createInterpreter(namespace);

        try {
            setupDefaultVariables(namespace);
            if (log.isDebugEnabled()) {
                log.debug("Running script: " + command);
            }
            return interp.eval(command);
        }
        catch (Exception e) {
            unwrapException(e);
            // never called
            return null;
        }
        finally {
            //try {
                resetDefaultVariables(namespace);
            //}
            //catch (UtilEvalError e) {
                // do nothing
            //}
        }
    }//}}}

    //{{{ cacheBlock() method
    /**
     *  Caches a block of code, returning a handle that can be passed to
     *  runCachedBlock().
     *
     *@param  id             An identifier. If null, a unique identifier is
     *      generated
     *@param  code           The code
     *@param  namespace      If true, the namespace will be set
     *@return                Description of the Return Value
     *@exception  Exception  instances are thrown when various BeanShell errors
     *      occur
     *@since                 jEdit 4.1pre1
     */
    public BshMethod cacheBlock(String id, String code, boolean namespace)
        throws Exception {
        String name = "__internal_" + id;

        // evaluate a method declaration
        if (namespace) {
            _eval(global, name + "(ns) {\nthis.callstack.set(0,ns);\n" + code + "\n}");
            return global.getMethod(name, new Class[]{NameSpace.class});
        }
        else {
            _eval(global, name + "() {\n" + code + "\n}");
            return global.getMethod(name, new Class[0]);
        }
    }//}}}

    //{{{ runCachedBlock() method
    /**
     *  Runs a cached block of code in the specified namespace. Faster than
     *  evaluating the block each time.
     *
     *@param  method         The method instance returned by cacheBlock()
     *@param  namespace      The namespace to run the code in
     *@return                Description of the Return Value
     *@exception  Exception  instances are thrown when various BeanShell errors
     *      occur
     *@since                 jEdit 4.1pre1
     */
    public Object runCachedBlock(BshMethod method,
            NameSpace namespace)
        throws Exception {
        boolean useNamespace;
        if (namespace == null) {
            useNamespace = false;
            namespace = global;
        }
        else {
            useNamespace = true;
        }

        try {
            setupDefaultVariables(namespace);

            Object retVal = method.invoke(useNamespace
                     ? new Object[]{namespace}
                     : NO_ARGS,
                    interpForMethods, new CallStack());
            if (retVal instanceof Primitive) {
                if (retVal == Primitive.VOID) {
                    return null;
                }
                else {
                    return ((Primitive) retVal).getValue();
                }
            }
            else {
                return retVal;
            }
        }
        catch (Exception e) {
            unwrapException(e);
            // never called
            return null;
        }
        finally {
            resetDefaultVariables(namespace);
        }
    }//}}}

    //{{{ isScriptRunning() method
    /**
     *  Returns if a BeanShell script or macro is currently running.
     *
     *@return    The scriptRunning value
     *@since     jEdit 2.7pre2
     */
    public boolean isScriptRunning() {
        return running;
    }//}}}

    //{{{ getNameSpace() method
    /**
     *  Returns the global namespace.
     *
     *@return    The nameSpace value
     *@since     jEdit 3.2pre5
     */
    public NameSpace getNameSpace() {
        return global;
    }//}}}

    //{{{ Package-private members

    //{{{ init() method
    /**  Description of the Method */
    public void init() {

        global = new NameSpace(new BshClassManager(), 
                "Embedded BeanShell interpreter");
        //global.importPackage("org.gjt.sp.util");
	global.importPackage("org.twdata.TW1606U.gui");
	global.importPackage("org.twdata.TW1606U.signal");
	global.importPackage("org.twdata.TW1606U.action");
	global.importPackage("org.twdata.TW1606U.tw");
	global.importPackage("org.twdata.TW1606U.tw.data");
	global.importPackage("org.twdata.TW1606U.data");
	global.importPackage("org.twdata.TW1606U.tw.model");
	global.importPackage("org.twdata.TW1606U.tw.signal");
	global.importPackage("org.werx.framework.bus");
        interpForMethods = createInterpreter(global);
    }//}}}

    //{{{ resetDefaultVariables() method
    /**
     *  Description of the Method
     *
     *@param  namespace          Description of the Parameter
     *@exception  UtilEvalError  Description of the Exception
     */
    private void resetDefaultVariables(NameSpace namespace)
        //throws UtilEvalError {
		{
			try {
				namespace.setVariable("reg", factory, false);
			} catch (UtilEvalError ex) {
				log.error(ex, ex);
			}
			
    }//}}}

    //{{{ unwrapException() method
    /**
     *  This extracts an exception from a 'wrapping' exception, as BeanShell
     *  sometimes throws. This gives the user a more accurate error traceback
     *
     *@param  e              Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    private void unwrapException(Exception e)
        throws Exception {
        if (e instanceof TargetError) {
            Throwable t = ((TargetError) e).getTarget();
            if (t instanceof Exception) {
                throw (Exception) t;
            }
            else if (t instanceof Error) {
                throw (Error) t;
            }
        }

        if (e instanceof InvocationTargetException) {
            Throwable t = ((InvocationTargetException) e).getTargetException();
            if (t instanceof Exception) {
                throw (Exception) t;
            }
            else if (t instanceof Error) {
                throw (Error) t;
            }
        }

        throw e;
    }//}}}

    //{{{ createInterpreter() method
    /**
     *  Description of the Method
     *
     *@param  nameSpace  Description of the Parameter
     *@return            Description of the Return Value
     */
    private Interpreter createInterpreter(NameSpace nameSpace) {
        return new Interpreter(null, System.out, System.err, false, nameSpace);
    }//}}}

    //}}}
}

