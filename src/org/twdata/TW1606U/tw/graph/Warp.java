package org.twdata.TW1606U.tw.graph;

import org._3pq.jgrapht.graph.*;
import org._3pq.jgrapht.edge.*;
import org._3pq.jgrapht.*;
import org.twdata.TW1606U.tw.model.Sector;

public class Warp extends DirectedEdge {

    public Warp(java.lang.Object sourceVertex, java.lang.Object targetVertex) {
        super(sourceVertex, targetVertex);
    }
    
    public double getWeight() {
        return 1d;
    }
    
    public Sector getSourceSector() {
        return (Sector) getSource();
    }
    
    public Sector getTargetSector() {
        return (Sector) getTarget();
    }
}
