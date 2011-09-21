package org.twdata.TW1606U.script.flow.javascript;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.springframework.beans.factory.*;
import org.swixml.SwingEngine;

import javax.swing.*;
import java.awt.event.*;
import org.twdata.TW1606U.*;
import org.mozilla.javascript.*;

public class JSDialog extends ScriptableObject {

    private JDialog dialog;

    public JSDialog() {}
    
    public void init(JDialog dialog, Map comps) {
        this.dialog = dialog;
        
        Context ctx = Context.getCurrentContext();
        Scriptable var;
        
        Map.Entry entry;
        for (Iterator i = comps.entrySet().iterator(); i.hasNext(); ) {
            entry = (Map.Entry)i.next();
            var = ctx.toObject(entry.getValue(), this); 
            put((String)entry.getKey(), this, var);
        }
        
        var = ctx.toObject(dialog, this);
        put("jdialog", this, var);
        
    }


    public static Scriptable jsConstructor(Context cx, Object[] args,
            Function ctorObj,
            boolean inNewExpr)
        throws Exception {
            
        JSKokua kokua = (JSKokua) args[0];
        Scriptable scope = kokua.getParentScope();
        BeanFactory factory = kokua.getBeanFactory();
        JFrame frame = (JFrame) factory.getBean("view-frame");
        ResourceManager rm = (ResourceManager) factory.getBean("resource-manager");
        JavaScriptInterpreter interpreter = (JavaScriptInterpreter) factory.getBean("flow-interpreter");
        
        File file = null;
        args[1] = JSKokua.jsobjectToObject(args[1]);
        if (args[1] instanceof File) {
            file = (File)args[1];
        } else {
            file = rm.getFile((String)args[1]);
        }
       
        Map ids;
        JSDialog jsd = new JSDialog();
        
        SwingEngine engine = new SwingEngine();
        engine.setAppFrame(frame);
        Map.Entry entry;
        JComponent comp;
        JDialog dialog;
        String id;
        String script;
        Script compiledScript;
        InputStream in = new FileInputStream(file);
        if (in != null) {
            Object con = engine.render(new InputStreamReader(in));
            if (con != null && con instanceof JDialog) {
                dialog = (JDialog) con;
                ids = engine.getIdMap();
                for (Iterator i = ids.entrySet().iterator(); i.hasNext(); ) {
                    entry = (Map.Entry) i.next();
                    id = (String) entry.getKey();
                    comp = (JComponent) entry.getValue();
                    script = (String) comp.getClientProperty("onClick");
                    if (script != null) {
                        if (comp instanceof JButton) {
                            compiledScript = interpreter.compileScript(id+"onClick", script);
                            ((JButton) comp).addActionListener(createActionListener(new ScriptEventContext(jsd, comp, compiledScript, scope, interpreter)));
                        } 
                    }
                    script = (String) comp.getClientProperty("onChange");
                    if (script != null) {
                        if (comp instanceof JComboBox) {
                            compiledScript = interpreter.compileScript(id+"onChange", script);
                            ((JComboBox) comp).addItemListener(createItemListener(new ScriptEventContext(jsd, comp, compiledScript, scope, interpreter)));
                        }
                    }
                }
                jsd.init(dialog, ids);
            }
        }
        return jsd;   
    }


    public String getClassName() {
        return "Dialog";
    }

    public void jsFunction_show() {
        dialog.pack();
        dialog.show();
    }
    
    public void jsFunction_dispose() {
        dialog.dispose();
    }
    
    protected static ActionListener createActionListener(final ScriptEventContext ctx) {
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fireEvent(ctx, event);
            }
        };
        return al;
    }
    
    protected static ItemListener createItemListener(final ScriptEventContext ctx) {
        ItemListener il = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==e.SELECTED) {
                    fireEvent(ctx, e);
                }
            }
        };
        return il;
    }

    protected static void fireEvent(ScriptEventContext ctx, Object event) {
        try {
            Context context = Context.enter();
            Scriptable newScope = context.newObject(ctx.scope);
            newScope.setPrototype(null);
            newScope.setParentScope(ctx.scope);
            
            Scriptable var = context.toObject(event, newScope);
            newScope.put("event", newScope, var);
            
            var = context.toObject(ctx.comp, newScope);
            newScope.put("comp", newScope, var);
            
            var = context.toObject(ctx.dialog, newScope);
            newScope.put("dialog", newScope, var);
            
            Context.exit();
            
            ctx.interpreter.exec(ctx.script, newScope, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //Context.exit();
        }
        
    }
    
    protected static class ScriptEventContext {
        public Script script;
        public Scriptable scope;
        public JavaScriptInterpreter interpreter;
        public JComponent comp;
        public JSDialog dialog;
        
        public ScriptEventContext(JSDialog dialog, JComponent comp, Script script, Scriptable scope, JavaScriptInterpreter interpreter) {
            this.script = script;
            this.scope = scope;
            this.comp = comp;
            this.interpreter = interpreter;
            this.dialog = dialog;
        }
    }
}

