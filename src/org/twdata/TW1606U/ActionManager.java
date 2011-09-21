package org.twdata.TW1606U;

import java.io.*;
import org.twdata.TW1606U.signal.*;
import org.apache.log4j.Logger;
import org.apache.commons.beanutils.*;
import org.springframework.beans.factory.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.xml.sax.ext.*;
import org.twdata.TW1606U.action.*;
import javax.xml.parsers.*;

public class ActionManager implements BeanFactoryAware {
    
    private BeanFactory factory;
    
    private ResourceManager res;
    
    private final Map actions = new HashMap();
    
    private static final Logger log = Logger.getLogger(ActionManager.class);
    
    private String indexPath;
    
    public ActionManager() {
    }
    
    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }
    
    public void setBeanFactory(BeanFactory factory) {
        this.factory = factory;
        init();
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
    }
    
    public void init() {
        log.info("Initializing ActionManager");
        actions.clear();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            log.info("Loading action index");
            parser.parse(res.getResource(indexPath), new IndexParser());
        } catch (Exception ex) {
            log.error("Unable to load action index at path:"+indexPath, ex);
        }
    }
    
    public Action getAction(String id) {
        Action action = (Action) actions.get(id);
        if (action == null) {
            throw new IllegalArgumentException("Unable to locate action "+id);
        }
        return action;
    }
    
    public javax.swing.Action getSwingAction(String id) {
        return getAction(id).getSwingAction();
    }
    
    private class IndexParser extends DefaultHandler {
  
        private static final String CATEGORY_TAG = "category";
        private static final String ACTIONS_TAG = "actions";
        private LinkedList categories = new LinkedList();
        
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (CATEGORY_TAG.equals(qName)) {
                categories.add(attributes.getValue("name"));
            } else if (ACTIONS_TAG.equals(qName)) {
                loadActions(attributes);
            }
        }
        
        public void endElement(String uri, String localName, String qName) throws SAXException{
            if (CATEGORY_TAG.equals(qName)) {
                categories.remove(categories.size() - 1);
            }
        }
        
        private void loadActions(Attributes attrs) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                String path = attrs.getValue("path");
                log.info("loading actions at path: "+path);
                InputStream in = res.getResource("/actions/"+path);
                if (in == null) {
                    throw new IllegalArgumentException("Unable to locate actions path: "+attrs.getValue("path"));
                }
                parser.parse(in, new ScriptActionsParser(attrs.getValue("type"), path));
            } catch (Exception ex) {
                log.error("Unable to load bsf actions", ex);
            }        
        }
    }
    
    private class ScriptActionsParser extends DefaultHandler {
  
        private static final String GLOBAL_TAG = "global";
        private static final String ACTION_TAG = "action";
        private StringBuffer global = null;
        private boolean inGlobal = false;
        private StringBuffer script = null;
        private ScriptAction action = null;
        private String type = null;
        private String path = null;
        
        public ScriptActionsParser(String type, String path) {
            this.type = type;
            this.path = path;
        }
        
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
            try {if (GLOBAL_TAG.equals(qName)) {
                global = new StringBuffer();
                inGlobal = true;
            } else if (ACTION_TAG.equals(qName)) {
                script = new StringBuffer();
                //if (global != null && global.length() > 0) {
                //    script.append(global);
                //}
                log.info("Loading action "+attributes.getValue("id"));
                action = (ScriptAction) ActionManager.this.factory.getBean("action."+type);
                setAttributes(action, attributes);
                action.setPath(path);
                if (log.isDebugEnabled()) {
                    log.debug("adding action:"+action.getId());
                }
                
                ActionManager.this.actions.put(action.getId(), action);
                inGlobal = false;
            }
            } catch (Exception ex) {
                log.error(ex, ex);
                throw new SAXException(ex);
            }
        }
        
        public void endElement(String uri, String localName, String qName) throws SAXException{
            try {if (ACTION_TAG.equals(qName)) {
                action.setScript(global.toString(), script.toString());
                if (log.isDebugEnabled()) {
                    log.debug("adding script text:\n"+script.toString()+"\n");
                }
                action = null;
            }
            } catch (Exception ex) {
                throw new SAXException(ex);
            }
        }
        
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (inGlobal) {
                global.append(ch, start, length);
            } else if (script != null) {
                script.append(ch, start, length);
            }
        }
        
        private void setAttributes(ScriptAction action, Attributes attrs) throws SAXException{
            HashMap props = new HashMap();
            int size = attrs.getLength();
            for (int x=0; x<size; x++) {
                props.put(attrs.getQName(x), attrs.getValue(x));
            }
            try {
                BeanUtils.populate(action, props);
            } catch(Exception e) {
                throw new SAXException("Unable to process attributes for action:"+action.getId());
            }   
        }
        
    }
        
}
