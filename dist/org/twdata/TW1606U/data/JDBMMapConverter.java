package org.twdata.TW1606U.data;

import com.thoughtworks.xstream.converters.collections.*;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.*;

/**
 * Converts a java.util.Map to XML, specifying an 'entry'
 * element with 'key' and 'value' children.
 * <p/>
 * <p>Supports java.util.HashMap, java.util.Hashtable and
 * java.util.LinkedHashMap.</p>
 *
 * @author Joe Walnes
 */
public class JDBMMapConverter extends MapConverter {

    private Map map;
    private DaoManager dm;
    private boolean debug = false;
    
    public JDBMMapConverter(ClassMapper classMapper, String classAttributeIdentifier, DaoManager dm) {
        super(classMapper, classAttributeIdentifier);
        this.dm = dm;
    }
    
    public void setMap(Map map) {
        this.map = map;
    }
    
    public boolean canConvert(Class type) {
        return type.equals(map.getClass());
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        populateMap(reader, context, map);
        return map;
    }
    
    public void setDebug(boolean val) {
        this.debug = val;
    }

    protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            reader.moveDown();
            Object key = readItem(reader, context, map);
            reader.moveUp();

            reader.moveDown();
            Object value = readItem(reader, context, map);
            reader.moveUp();

            if (value instanceof DaoAwareModel) {
                    ((DaoAwareModel)value).setDaoManager(dm);
            }

            map.put(key, value);
            
            reader.moveUp();
        }
    }

}
