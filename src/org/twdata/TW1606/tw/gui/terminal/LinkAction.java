package org.twdata.TW1606.tw.gui.terminal;

import org.twdata.TW1606.action.FlowAction;


public class LinkAction extends FlowAction {
    
    private Link link;

    public Link getLink() {
        return link;
    }

    public LinkAction setLink(Link link) {
        this.link = link;
        return this;
    }
    
}
