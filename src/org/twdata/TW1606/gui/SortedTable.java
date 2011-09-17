package org.twdata.TW1606.gui;

import javax.swing.*;
import java.awt.Component;
import java.util.*;
import javax.swing.table.*;

public class SortedTable extends JTable {
    
    private Object[] titles;
    
    public SortedTable(Object[][] data, Object[] titles) {
        super(data, titles);
    }
    
    public SortedTable(Object... titles) {
        super(new Object[0][titles.length], titles);
    }
    
    public SortedTable(final Object[][] data, final Object[] titles, final int sortColumn) {
        super();
        
        this.titles = titles;
        setData(data, sortColumn);
        
        final DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);
        
        final DefaultTableCellRenderer rest = new DefaultTableCellRenderer();
        rest.setHorizontalAlignment(SwingConstants.RIGHT);
        
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (column == 0) {
                    return left.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                } else {
                    return rest.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            }
        });
            
    }
    
    public void setData(final Object[][] data, final int sortColumn) {
        Arrays.sort(data, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object val1 = ((Object[])o1)[sortColumn];
                Object val2 = ((Object[])o2)[sortColumn];
                
                if (val1 instanceof String) {
                    return ((String)val1).compareTo(val2.toString());
                } else if (val1 instanceof Number && val2 instanceof Number) {
                    double d1 = ((Number)val1).doubleValue();
                    double d2 = ((Number)val2).doubleValue();
                    return (d1 < d2) ? -1 :
                           (d1 > d2) ? 1 : 0;
                } else {
                    return -1;
                }
            }
            
            public boolean equals(Object o1, Object o2) {
                return o1.equals(o2);
            }
        });
        
        setModel(new DefaultTableModel(data, titles) {
            public Object getValueAt(int row, int column) {
                Object val = super.getValueAt(row, column);
                if (val instanceof Number) {
                    return new Integer(((Number)val).intValue());
                } 
                return val;
            }
        });
    }
}
