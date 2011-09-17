package org.twdata.TW1606.action;

import javax.swing.JFrame;

public interface Action {
 
    public String getId();
    
    public String getLabel();
    
    public String getMouseOverText();
    
    public String getCategory();
    
    public void invoke();
    
    public boolean isToggle();
    
    public boolean isSelected();
    
    public boolean noRememberLast();
    
    public boolean noRecord();
    
    public boolean isEnabled();
    
    public void setEnabled(boolean enabled);
    
    public String toString();
    
    public javax.swing.Action getSwingAction();
}
