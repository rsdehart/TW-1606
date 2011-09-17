package org.twdata.TW1606.tw.graph;

import org.twdata.TW1606.tw.data.*;
import org.twdata.TW1606.tw.model.*;
import org.twdata.TW1606.tw.signal.*;
import org.twdata.TW1606.signal.*;
import org.twdata.TW1606.data.*;
import java.util.*;
import org._3pq.jgrapht.graph.*;
import org._3pq.jgrapht.*;
import org._3pq.jgrapht.alg.DijkstraShortestPath;
import org.apache.log4j.Logger;



public class SectorGraph {
    
    private static final Logger log = Logger.getLogger(SectorGraph.class);
    
    private DefaultDirectedGraph graph;
    private SectorDao sectorDao;
    
    public SectorGraph() {
        graph = new DefaultDirectedGraph(new EdgeFactory() {
            public Edge createEdge(java.lang.Object sourceVertex, java.lang.Object targetVertex) {
                return new Warp(sourceVertex, targetVertex);
            }
        });
    }
    
    public void setSectorDao(SectorDao sd) {
        sectorDao = sd;
    }
    
    public void setMessageBus(MessageBus bus) {
        bus.plug(this);
    }
    
    public void channel(SessionStatusSignal signal) {
        if (signal.START.equals(signal.getStatus())) {
            if (log.isDebugEnabled()) {
                log.debug("Initializing sector graph");
            }
            addAllSectors(sectorDao.getAll());
        }
    }
    
    protected void addAllSectors(Collection sectors) {
        synchronized(graph) {
            Sector s;
            Sector[] kids;
            for (Iterator i = sectors.iterator(); i.hasNext(); ) {
                s = (Sector)i.next();
                graph.addVertex(s);
                kids = s.getWarps();
                for (int x=0; x<kids.length; x++) {
                    graph.addVertex(kids[x]);
                    graph.addEdge(s, kids[x]);
                }
            }
        }
    }
    
    protected void addSector(Sector s) {
        synchronized(graph) {
            graph.addVertex(s);
            Sector[] kids = s.getWarps();
            for (int x=0; x<kids.length; x++) {
                graph.addVertex(kids[x]);
                graph.addEdge(s, kids[x]);
            }
        }
    }
    
    public void channel(SectorStatusSignal signal) {
        addSector(signal.getSector());
    }
    
    public List shortestPath(Sector start, Sector end) {
        synchronized(graph) {
            return DijkstraShortestPath.findPathBetween(graph, start, end);
        }
    }
    
    public ClosestWithinRangeIterator getSectorsWithinRange(Sector start, int range) {
        synchronized(graph) {
            return new ClosestWithinRangeIterator(graph, start, range);
        }
    }
    
}
