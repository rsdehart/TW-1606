package org.twdata.TW1606U.gui;

import javax.swing.ImageIcon;

import org.twdata.TW1606U.action.Action;

public class RadialMenuItem {
    private ImageIcon icon;

    private Action action;
    private String text;

    public RadialMenuItem(String t, ImageIcon i) {
        icon = i;
        text = t;
    }
    
    public RadialMenuItem(Action action) {
        this.action = action;
        this.text = action.getLabel();
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}