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

 4. The names "Apache TW1606u" and  "Apache Software Foundation" must  not  be
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
package org.twdata.TW1606U.script.flow.javascript;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;
import org.springframework.beans.factory.BeanFactory;
import org.twdata.TW1606U.ResourceManager;
import org.twdata.TW1606U.StreamFilter;
import org.twdata.TW1606U.TextEventGenerator;

/**
 * JavaScript interface to various TW1606u abstractions.
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @since March 16, 2002
 * @version CVS $Id: JSTW1606u.java,v 1.10 2007/06/03 12:43:39 mrdon Exp $
 */
public class JSTW1606u extends ScriptableObject
{
    protected static String OBJECT_SOURCE_RESOLVER = "source-resolver";
    protected JavaScriptInterpreter interpreter;
    protected Scriptable parentScope;
    protected BeanFactory factory;
    protected TextEventGenerator textGen;
    protected StreamFilter streamFilter;
    protected ResourceManager rm;
    protected boolean stopRequested;
    
    public JSTW1606u() {}

    public String getClassName()
    {
        return "TW1606u";
    }


    public void setInterpreter(JavaScriptInterpreter interpreter)
    {
        this.interpreter = interpreter;
    }

    public JavaScriptInterpreter jsGet_interpreter()
    {
        return interpreter;
    }
    
    public TextEventGenerator jsGet_textEvents()
    {
        return textGen;
    }
    
    public boolean jsGet_stopRequested() {
        return stopRequested;
    }
    
    public void stopRequested() {
        this.stopRequested = true;
    }
    
    public void setParentScope(Scriptable scope) {
        this.parentScope = scope;
    }
    
    public Scriptable getParentScope() {
        return parentScope;
    }
    
    public void setBeanFactory(BeanFactory factory) {
        this.factory = factory;
        textGen = (TextEventGenerator)factory.getBean("text-event-generator");
        streamFilter = (StreamFilter)factory.getBean("telnet");
        rm = (ResourceManager)factory.getBean("resource-manager");
    }
    
    public BeanFactory getBeanFactory() {
        return factory;
    }
    
    public void jsFunction_send(String txt) throws IOException {
        txt = txt.replace("*", "\r\n");
        streamFilter.write(textGen.convertString(txt).getBytes("Cp1252"));
    }
    
    public Object jsFunction_getBean(String name) {
        return factory.getBean(name);
    }
        

    /**
     * Load the script file specified as argument.
     *
     * @param filename a <code>String</code> value
     * @return an <code>Object</code> value
     * @exception JavaScriptException if an error occurs
     */
    public void jsFunction_load(String filename) throws WrappedException
    {
        org.mozilla.javascript.Context cx =
            org.mozilla.javascript.Context.getCurrentContext();
        try {
            Scriptable scope = getParentScope();
            InputStream in = rm.getResource(filename);
            Script script = cx.compileReader(new InputStreamReader(in), filename, 1, null);
            interpreter.exec(script, scope, false);
            in.close();
        } catch (WrappedException e) {
            throw e;
        } catch (Exception e) {
            throw new WrappedException(e);
        }
    }

    public String jsFunction_toString()
    {
        return "[object " + toString() + "]";
    }
    
    public Map jsFunction_jsobjectToMap(Scriptable jsobject) {
        return jsobjectToMap(jsobject);
    }

    public static Map jsobjectToMap(Scriptable jsobject)
    {
        HashMap hash = new HashMap();
        Object[] ids = jsobject.getIds();
        for (int i = 0; i < ids.length; i++) {
            String key = ScriptRuntime.toString(ids[i]);
            Object value = jsobject.get(key, jsobject);
            if (value == Undefined.instance)
                value = null;
            else
                value = jsobjectToObject(value);
            hash.put(key, value);
        }
        return hash;
    }

    public static Object jsobjectToObject(Object obj)
    {
        // unwrap Scriptable wrappers of real Java objects
        if (obj instanceof Wrapper) {
            obj = ((Wrapper) obj).unwrap();
        } else if (obj == Undefined.instance) {
            obj = null;
        }
        return obj;
    }

}
