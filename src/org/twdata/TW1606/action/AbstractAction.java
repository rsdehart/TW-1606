package org.twdata.TW1606.action;

import javax.swing.JFrame;

public abstract class AbstractAction implements Action {
    
    protected String id;
    protected String label;
    protected String mouseOverText;
    protected String category;
    protected boolean enabled;
    protected javax.swing.Action swingAction;
    
    public AbstractAction() {
        id = null;
        label = null;
        mouseOverText = null;
        category = null;
        enabled = true;
        swingAction = new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                org.twdata.TW1606.action.AbstractAction.this.invoke();
            }
        };
    }
    
    public javax.swing.Action getSwingAction() {
        return swingAction;    
    }
    public void setLabel(String label) {
        this.label = label;
        swingAction.putValue(javax.swing.Action.NAME, label);
    }
    public void setMouseOverText(String mouseOverText) {
        this.mouseOverText = mouseOverText;
        swingAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, mouseOverText);
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        swingAction.setEnabled(enabled);
    }
    
    
    public boolean isEnabled() {
        return enabled;
    }
    public String getCategory() {
        return category;
    }
    public String getMouseOverText() {
        return mouseOverText;
    }
    public String getLabel() {
        return label;
    }
    public String getId() {
        return id;
    }

 
    public abstract void invoke();
    
    public boolean isToggle() {
        return false;
    }
    
    public boolean isSelected() {
        return false;
    }
    
    public boolean noRememberLast() {
        return false;
    }
    
    public boolean noRecord() {
        return false;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Action:").append(getClass().getName());
        return sb.toString();
    }
}
