package org.twdata.TW1606U.tw.gui.terminal;

import org.twdata.TW1606U.action.FlowAction;


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
