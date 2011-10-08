package org.twdata.TW1606U.tw.gui;

import org.swixml.SwingEngine;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.*;
import java.awt.Insets;
import org.twdata.TW1606U.gui.SessionFieldEnabler;
import javax.swing.*;
import java.util.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import org.swixml.*;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import java.awt.BorderLayout;
import javax.swing.border.*;
import org.twdata.TW1606U.tw.signal.*;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.data.DaoManager;
import org.twdata.TW1606U.tw.model.*;
import org.twdata.TW1606U.tw.*;
import hypergraph.hyperbolic.*;
import hypergraph.graphApi.*;
import hypergraph.graph.*; 
import hypergraph.visualnet.*;
import javax.swing.text.JTextComponent;
import java.util.*;
import java.util.regex.*;

/**
 *@created    October 18, 2003
 */
public class SectorMap extends JPanel {
    
    public JTextField port;
    public JTextField beacon;
    public JTextField density;
    public JTextField fighters;
    public JTextField mines;
    public JTextArea note;
    public JScrollPane notePane;
    public JList planets;
    
    private JPopupMenu popup;
    private JMenu menuForeground;
    private JMenu menuBackground;
    
    protected static final Logger log = Logger.getLogger(SectorMap.class);

    private SectorGraphPanel gpanel;
    private Graph graph;
    private GraphLayout layout;
    private Game game;
    private DaoManager dm;
    private Sector curSector;
    private boolean curSectorNeedsUpdate = false;
    private HashSet shownWarps = new HashSet();
    
    private SectorDao sectorDao;
    private GameDao gameDao;
    private TWSession session;
    private int radius = 3;
    
    private Node curPopupSector;
    
    private ResourceManager res;
    private SwingEngine swix;
    
    private JComponent detailPanel;
    private List fields = new ArrayList();
    private Pattern attrPtn;
    private SessionFieldEnabler enabler;
    
    public SectorMap() {
        attrPtn = Pattern.compile("\\[([a-zA-Z]+):([ a-zA-Z0-9#]+)\\]");
    }
    
    protected void initGUI() {
        setLayout(new BorderLayout());
        UIManager.put("ModelPanelUI","hypergraph.hyperbolic.ModelPanelUI");

        GraphSystem graphSystem = null;
		try {
			graphSystem = GraphSystemFactory.createGraphSystem("hypergraph.graph.GraphSystemImpl",null); 
		} catch (Exception e) {
			throw new RuntimeException("Unable to load graph system", e);
		}
        
        graph = graphSystem.createGraph();
        gpanel = new SectorGraphPanel(graph);
        //gpanel.getPropertyManager().setProperty("visualnet.GenericMDSLayout.repulsingForce",new Double(0.1));
        //gpanel.getPropertyManager().setProperty("visualnet.GenericMDSLayout.repulsingForceCutOff",new Double(5));
        //gpanel.getPropertyManager().setProperty("visualnet.GenericMDSLayout.connectedDisparity",new Double(0.5));
        gpanel.getPropertyManager().setProperty("visualnet.layout.expandingEnabled", "true");
        gpanel.getPropertyManager().setProperty("hypergraph.visualnet.TreeLayout.mindistance",new Double(.5));
        gpanel.getPropertyManager().setProperty("hypergraph.visualnet.TreeLayout.defaultSize",new Double(.7));
        gpanel.getPropertyManager().setProperty("hypergraph.hyperbolic.text.fontName", "SansSerif");
        gpanel.setEdgeRenderer(new DefaultEdgeRenderer() {
            float[] dashed = new float[] {4f, 4f};
            public void configure(GraphPanel mp, Edge edge) {
                Node src = edge.getSource();
                Node dest = edge.getTarget();
                int dir = -1;
                boolean left = false;
                boolean right = false;
                boolean hovering = false;
                if (src != null && dest != null) {
                    if (shownWarps.contains(src.getName() + ":" + dest.getName())) {
                        dir = DirectedArrowRenderer.RIGHT;
                    }
                    if (shownWarps.contains(dest.getName() + ":" + src.getName())) {
                        if (dir == DirectedArrowRenderer.RIGHT) {
                            dir = DirectedArrowRenderer.BOTH;
                        } else {
                            dir = DirectedArrowRenderer.LEFT;
                        }
                    }
                    if (edge.getSource().equals(mp.getHoverElement()) ||
                        edge.getTarget().equals(mp.getHoverElement()) ||
                        edge.equals(mp.getHoverElement())) {
                        hovering = true;
                    }
                            
                    ((DirectedArrowRenderer)getLineRenderer()).setDirection(dir);
                    ((DirectedArrowRenderer)getLineRenderer()).setHovering(hovering);
                }
                AttributeManager attrMgr = mp.getGraph().getAttributeManager();
                if (!hovering) {
                    attrMgr.setAttribute(GraphPanel.EDGE_STROKE, edge, dashed); 
                } else {
                    attrMgr.setAttribute(GraphPanel.EDGE_STROKE, edge, null);
                }
                super.configure(mp, edge);
                
            }
        });
        ((DefaultEdgeRenderer)gpanel.getEdgeRenderer()).setLineRenderer(new DirectedArrowRenderer());
        
        
        layout = new TreeLayout(graph, gpanel.getModel(), gpanel.getPropertyManager());
        //layout = new GenericMDSLayout();
        gpanel.setGraphLayout(layout);

        add(BorderLayout.CENTER, gpanel);
        
        //gpanel = new GraphPanel(graph);
        //add(BorderLayout.CENTER, gpanel);
    }
    
    public void setSessionFieldEnabler(SessionFieldEnabler enabler) {
        this.enabler = enabler;
    }
    
    public void setSession(TWSession session) {
        this.session = session;
    }
    
    public void setResourceManager(ResourceManager rm) {
        this.res = rm;
    }
    
    public void setDaoManager(DaoManager dm) {
        this.dm = dm;
        sectorDao = (SectorDao)dm.getDao("sector");
        gameDao = (GameDao)dm.getDao("game");
    }
    
    public void setMessageBus(MessageBus bus) {
        bus.plug(this);
    }
    
    public void setRadius(int rad) {
        radius = rad;
    }
    
    public void setSrc(String xml) {
        initGUI();
        try {
            long start = System.currentTimeMillis();
            swix = new SwingEngine(this);
            detailPanel = (JComponent)swix.render(res.getResourceAsReader(xml));
            add(BorderLayout.SOUTH, detailPanel);
            
            if (planets != null) {
                planets.setModel(new DefaultListModel());
                planets.setBorder(port.getBorder());
            }
            if (note != null) {
                if (notePane != null) {
                    notePane.setBorder(port.getBorder());
                } else {
                    note.setBorder(port.getBorder());
                }
                note.addFocusListener(new FocusListener() {
                    public void focusGained(FocusEvent e) {}

                    public void focusLost(FocusEvent e) {
                        curSector.setNote(note.getText().trim());
                        sectorDao.update(curSector);
                    }
                });
            }
            
            if (log.isInfoEnabled()) {
                log.info("Loaded DetailPanel - "+(System.currentTimeMillis() - start)+"ms");
            }
            
            popup = (JPopupMenu)swix.find("popup");
            menuForeground = (JMenu)swix.find("menuForeground");
            menuBackground = (JMenu)swix.find("menuBackground");
            
            addAttributeMenuActions(menuForeground, "color");
            addAttributeMenuActions(menuBackground, "background");
            
            JMenuItem mi = (JMenuItem)swix.find("showSector");
            if (mi != null) {
                mi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String id = JOptionPane.showInputDialog(gpanel, "Enter sector to display in the sector map",
                            "Enter Sector", JOptionPane.QUESTION_MESSAGE);
                        if (id != null) {
                            try {
                                int x = Integer.parseInt(id);
                                Sector s = sectorDao.get(x);
                                if (s == null) {
                                    JOptionPane.showMessageDialog(gpanel, "The sector you entered, "+
                                        id+", has not been explored yet.", "Unknown Sector",  JOptionPane.WARNING_MESSAGE);
                                } else {
                                    createGraphForSector(s);
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(gpanel, "Invalid sector: "+id, "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
            }
            
            mi = (JMenuItem)swix.find("refreshSector");
            if (mi != null) {
                mi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        createGraphForSector(curSector);
                    }
                });
            }
            if (port != null) enabler.addField(port);
            if (beacon != null) enabler.addField(beacon);
            if (density != null) enabler.addField(density);
            if (fighters != null) enabler.addField(fighters);
            if (mines != null) enabler.addField(mines);
            if (planets != null) enabler.addField(planets);
            if (note != null) enabler.addField(note);
            if (notePane != null) enabler.addField(notePane);
            if (popup != null) enabler.addField(popup);
        } catch (Exception e) {
            log.error(e, e);
        }
        
    }
    
    private void addAttributeMenuActions(JMenu menu, final String attributeName) {
        if (menu != null) {
            for (int x=0; x<menu.getItemCount(); x++) {
                final JMenuItem mi = menu.getItem(x);
                mi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        if (curPopupSector != null) {
                            try {
                                changeAttribute(curPopupSector.getLabel(), attributeName, mi.getText());
                            } catch (GraphException e) {
                                log.error("Unable to update sector attribute", e);
                            }
                        }
                    }
                });
            }
        }
    }
    
    public void channel(SessionStatusSignal signal) {
        if (SessionStatusSignal.STOP.equals(signal.getStatus())) {
            clearGraph();
        } 
    }
    
    public void channel(ChangedSessionSignal signal) throws GraphException {
        if (ChangedSessionSignal.SECTOR == signal.getType()) {
            synchronized(this) {
                Sector s = session.getSector();
                if (s.getWarps().length == 0) {
                    curSectorNeedsUpdate = true;
                }
                createGraphForSector(session.getSector());
            }
        } 
    }
    
    public void channel(SectorStatusSignal signal) {
        final Sector s = signal.getSector();
        synchronized(this) {
            if (curSectorNeedsUpdate && curSector == s && s.getWarps().length > 0) {
                createGraphForSector(s);
                curSectorNeedsUpdate = false;
            }
        }
    }
    
    
    void createGraphForSector(final Sector s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    synchronized(graph) {
                        //clearGraph();
                        refreshSectorRing(s);
                        //gpanel.center(s.getId());
                        updateDetailPanel(s);
                    }
                } catch (GraphException ex) {
                    log.error("Unable to update graph for sector "+s.getId(), ex);
                }
            }
        });
    }
    
    private void clearGraph() {
        
        
        List lst = new ArrayList();
        for (Iterator i = graph.getEdges().iterator(); i.hasNext(); ) {
            lst.add(i.next());
        }
        for (Iterator i = graph.getNodes().iterator(); i.hasNext(); ) {
            lst.add(i.next());
        }
        for (Iterator i = lst.iterator(); i.hasNext(); ) {
            graph.removeElement((Element)i.next());
        }   
        shownWarps.clear();
    }
    
    private void updateDetailPanel(final Sector s) throws GraphException {
        // s is null when disconnected
        if (s != null) {
            if (log.isDebugEnabled()) {
                log.debug("updating detail panel for "+s.getId());
            }
            Border border = detailPanel.getBorder();
            if (border instanceof TitledBorder) {
                ((TitledBorder)border).setTitle("Sector "+s.getId());
                detailPanel.repaint();
            }
            Port p = s.getPort();
            if (port != null) {
                if (p != null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Class ").append(String.valueOf(p.getPortClass()));
                    sb.append(" (").append(p.getPortClassName()).append(")");
                    port.setText(sb.toString());
                    port.setCaretPosition(0);
                } else {
                    port.setText("None");
                }
            } 
            if (beacon != null) {
                if (s.getBeacon() != null) {
                    beacon.setText(s.getBeacon());
                    beacon.setCaretPosition(0);
                } else {
                    beacon.setText("");
                }
            }
            if (density != null) {
                density.setText(String.valueOf(s.getDensity()));
            }
            if (fighters != null) {
                fighters.setText(String.valueOf(s.getFighters()));
            }
            if (mines != null) {
                mines.setText(String.valueOf(s.getMines(s.ARMID)));
            }
            if (note != null) {
                if (s != curSector && curSector != null && 
                        !note.getText().trim().equals(curSector.getNote())) {
                    curSector.setNote(note.getText().trim());
                    sectorDao.update(curSector);
                    refreshSectorRing(session.getSector());
                }
                note.setText(s.getNote()==null?"":s.getNote());
                note.setCaretPosition(0);
            }    
            if (planets != null) {
                Planet[] pList = s.getPlanets();
                DefaultListModel model = (DefaultListModel)planets.getModel();
                model.clear();
                if (pList != null && pList.length > 0) {
                    for (int x=0; x<pList.length; x++) {
                        model.addElement(pList[x].getTWId()+": "+pList[x].getName());
                    }
                } 
            }  
            curSector = s;
        }
    }
    
    protected void changeAttribute(String id, String key, String val) throws GraphException {
        Sector s = sectorDao.get(Integer.parseInt(findSectorIdFromLabel(id)));
        String note = s.getNote();
        String newNote = null;
        if (note != null) {
            Matcher m = attrPtn.matcher(note);
            while (m.find()) {
                String name = m.group(1);
                if (name.equals(key)) {
                    newNote = note.substring(0, m.start(2))+val+note.substring(m.end(2));
                }
            }
        }
        
        if (newNote == null) {
            newNote = "["+key+":"+val+"]\n"+(note == null ? "" : note);
        }
        
        s.setNote(newNote);
        if (s.equals(curSector)) {
            updateDetailPanel(s);
        }
        sectorDao.update(s);
        refreshSectorRing(session.getSector());
    }
    
    private void refreshSectorRing(Sector s) throws GraphException {
        synchronized (graph) {
            clearGraph();
            //Set<String> oldShownWarps = new HashSet<String>(shownWarps);
            //shownWarps.clear();
            buildGraph(s, radius);
            /*
            System.out.println("shown warps: "+shownWarps);
            List<String> toRemoveList = new ArrayList<String>();
            // Clear out old nodes
            oldShownWarps.removeAll(shownWarps);
            for (String id : oldShownWarps) {
                toRemoveList.add(id);
            }
            System.out.println("Removing warps: "+toRemoveList);
            for (String id : toRemoveList) {
                graph.removeElement(graph.getElement(id));
            }
            toRemoveList.clear();
            for (Node n : (Collection<Node>)graph.getNodes()) {
                if (graph.getEdges(n).size() == 0) {
                    toRemoveList.add(n.getName());
                }
            }
            System.out.println("Removing sectors: "+toRemoveList);
            for (String id : toRemoveList) {
                graph.removeElement(graph.getElement(id));
            }
            */
            Node root = findNode(s.getId());
            if (root == null) {
                throw new IllegalStateException("Can't have a null root");
            }
            ((TreeLayout)layout).setRoot(root);
            graph.getAttributeManager().setAttribute(AttributeManager.GRAPH_ROOT, graph.getSpanningTree(), root);
            layout.layout();
            gpanel.centerNode(root);
            
            
            //layout.invalidate();
            //layout.layout();
             
             
            
        }
        //graph.structureChanged(new GraphEvent());
    }
    
    private void buildGraph(Sector c, int ring) throws GraphException {
        
        if (ring == 0) {
            return;
        }
        
        for (Sector inWarp : c.getIncomingWarps()) {
            addWarp(inWarp, c);
            buildGraph(inWarp, ring - 1);
        }
        
        for (Sector warp : c.getWarps()) {
            addWarp(c, warp);
            buildGraph(warp, ring - 1);
        }
        setNodeProperties(c, findNode(c));
    }
    
    private void addWarp(Sector source, Sector warp) throws GraphException {
        
        Node sourceNode = findNode(source);
        Node nwarp = findNode(warp);
        String warpId = source.getId() + ":" + warp.getId();
        if (graph.getElement(warpId) == null) {
            graph.createEdge(warpId, sourceNode, nwarp);
        }
        shownWarps.add(warpId);
    }
    
    private Node findNode(Object id) {
        if (id != null) {
            String label = findSectorIdFromLabel(id);
            return (Node) graph.getElement(label);
        }
        return null;
    }

    private String findSectorIdFromLabel(Object id) {
        String label = id.toString();
        int pos = label.indexOf(' ');
        if (pos > -1) {
            label = label.substring(0, pos);
        }
        return label;
    }
    
    private Node findNode(Sector s) throws GraphException {
        Node node = findNode(s.getId());
        if (node == null) {
            node = createNode(s);
        }
        
        
        return node;
    }
    
    private Node setNodeProperties(Sector s, Node node) {
        AttributeManager attrMgr = graph.getAttributeManager();
        Color color = Color.BLACK;
        Color bgColor = Color.WHITE;
        
        if ("FedSpace, FedLaw Enforced".equals(s.getBeacon())) {
            color = Color.RED;
        } 
        if (s.getNote() != null && s.getNote().trim().length() > 0) {
            Matcher m = attrPtn.matcher(s.getNote());
            while (m.find()) {
                String name = m.group(1);
                String val = m.group(2);
                if ("color".equals(name)) {
                    color = matchColor(val);
                } else if ("background".equals(name)) {
                    bgColor = matchColor(val);
                }    
            }
            if (bgColor == Color.WHITE && color == Color.BLACK) {
                bgColor = Color.YELLOW;
            }
        }
        
        
        
        if (s.getPort() != null) {
            node.setLabel(s.getId()+" ("+s.getPort().getPortClassName()+")");
        }
        
        attrMgr.setAttribute(GraphPanel.NODE_FOREGROUND,node,color);  
        attrMgr.setAttribute(GraphPanel.NODE_BACKGROUND,node,bgColor);
        return node;
    }
    
    private Node createNode(Sector s) throws GraphException {
        Node node = graph.createNode(String.valueOf(s.getId()));
        return setNodeProperties(s, node);
    }
    
    private Color matchColor(String str) {
        return ("black".equalsIgnoreCase(str) ? Color.BLACK : 
                "red".equalsIgnoreCase(str) ? Color.RED :
                "blue".equalsIgnoreCase(str) ? Color.BLUE :
                "cyan".equalsIgnoreCase(str) ? Color.CYAN :
                "darkGray".equalsIgnoreCase(str) ? Color.DARK_GRAY :
                "gray".equalsIgnoreCase(str) ? Color.GRAY :
                "green".equalsIgnoreCase(str) ? Color.GREEN :
                "lightGray".equalsIgnoreCase(str) ? Color.LIGHT_GRAY :
                "magenta".equalsIgnoreCase(str) ? Color.MAGENTA :
                "orange".equalsIgnoreCase(str) ? Color.ORANGE :
                "pink".equalsIgnoreCase(str) ? Color.PINK :
                "white".equalsIgnoreCase(str) ? Color.WHITE : 
                "yellow".equalsIgnoreCase(str) ? Color.YELLOW : Color.BLACK);
    }
    
    private class SectorGraphPanel extends GraphPanel {
        
        public SectorGraphPanel(Graph graph) {
            super(graph);
        }
        
        protected void center(int id) {
            Node root = findNode(id);
            ((TreeLayout)layout).setRoot(root);
            centerNode(root);
            setHoverElement(root, true);
        }
        
        public void setHoverElement(Element node,boolean repaint) {
            if (getHoverElement() != node) {
                if (node == null) {
                    setToolTipText(null);
                } else {
                    if (node.getElementType() == Element.NODE_ELEMENT) {
                        String id = node.getName();
                        Sector s = sectorDao.get(Integer.parseInt(id));
                        try {
                            updateDetailPanel(s);
                        } catch (GraphException e) {
                            log.error("Unable to update panel", e);
                        }
                            
                        /*StringBuffer sb = new StringBuffer("<html>");
                        sb.append("Sector ").append("<b>").append(id).append("</b><br>");
                        sb.append("&nbsp;Port : ").append("<b>");
                        if (s.getPort() != null) {
                            sb.append("Class ").append(String.valueOf(s.getPort().getPortClassName())).append("</b><br>");
                        } else {
                            sb.append("None").append("<br>");
                        }
                        sb.append("&nbsp;NavHaz : ").append("<b>").append(String.valueOf(s.getNavHaz())).append("</b><br>");
                        sb.append("&nbsp;Figs : ").append("<b>").append(String.valueOf(s.getFighters()));
                        sb.append("</html>");
                        
                        //setToolTipText(sb.toString());
                        */
                    }
                }
            }
            super.setHoverElement(node,repaint);
        }
        
        public void mouseClicked(MouseEvent me) {
            if (SwingUtilities.isRightMouseButton(me)) {
                if (popup != null && popup.isEnabled()) {
                   Element e = getElement(me.getPoint());
                   Node node = null;
                   if (e != null && e.getElementType() == Element.NODE_ELEMENT) {
                       node = (Node)e;
                   }
                   if (node == null && false) {
                       menuForeground.setEnabled(false);
                       menuBackground.setEnabled(false);
                       curPopupSector = null;
                   } else {
                       menuForeground.setEnabled(true);
                       menuBackground.setEnabled(true);
                       curPopupSector = node;
                   }
                   popup.show(this, me.getX(), me.getY());
                } else {
                    super.mouseClicked(me);
                }
           } else {
               super.mouseClicked(me);
           }
        }
        
        public synchronized void nodeClicked(int clickCount,Node node) {
//          Update inner ring of graph elements
            if (node != ((TreeLayout)layout).getRoot()) {
                String id = node.getName();
                Sector s = sectorDao.get(Integer.parseInt(id));
                createGraphForSector(s);
            } else {
                super.nodeClicked(clickCount,node);
            }

        }
        
    }
    
/**
 * Special line renderer that also shows an arrow.
 * Showing the arrow can be switched on and off
 */
public class ArrowLineRenderer extends DefaultLineRenderer {

	/** Used to switch rendering of the arrow on and off. */
	private boolean _showArrows = true;

	/** Set this property to <code>true</code> to show an arrow on each line.
	 * @param showArrows
	 */
	public void setShowArrows(boolean showArrows) {
		_showArrows = showArrows;
	}

	/** Indicates whether the renderer shows an arrow.
	 * @return <code>True</code>, if the renderer shows arrows.
	 */
	public boolean isShowArrows() {
		return _showArrows;
	}

	public void paint(Graphics g) {

		if (start == null || end == null)
			return;

		g.setColor(getColor());

		ModelPanelUI ui = (ModelPanelUI) panel.getUI();
		Point[] lineSegments = panel.getProjector().getLineSegments(start, end, panel);
		int[] xPoints = new int[lineSegments.length];
		int[] yPoints = new int[lineSegments.length];
		for (int i = 0; i < lineSegments.length; i++) {
			xPoints[i] = lineSegments[i].x;
			yPoints[i] = lineSegments[i].y;
		}
		g.drawPolyline(xPoints, yPoints, lineSegments.length);

		if (_showArrows) {
			int fromIndex = lineSegments.length / 2;
			int toIndex = fromIndex + 1;
			if (toIndex >= lineSegments.length)
				return;
			ModelPoint toModelPoint = panel.unProject(lineSegments[toIndex]);
			drawArrow(g,
					xPoints[fromIndex], yPoints[fromIndex],
					xPoints[toIndex], yPoints[toIndex],
					panel.getScale(toModelPoint).getX(),
					panel.getScale(toModelPoint).getY());
		}
    }

	protected void drawArrow(
			Graphics g, int fromX, int fromY, int toX, int toY, double scaleX, double scaleY) {
		int width = (int) Math.round(15 * scaleX);
		int height = (int) Math.round(15 * scaleY);
		Color headColor = getColor();
		Color currentcolor = getColor();
		int startAngle;
		int angle = 60;
		g.drawLine(fromX, fromY, toX, toY);
		double lengthX = toX - fromX;
		double lengthY = toY - fromY;
		double lineLength = Math.sqrt(lengthX * lengthX + lengthY * lengthY);
		startAngle = (int) Math.toDegrees(Math.asin(lengthY / lineLength));
		if (lengthX > 0) {
			startAngle = (180 - startAngle - 30);
		} else {
			startAngle = (startAngle - 30);
		}
		// Draw the arrow head
		g.setColor(headColor);
		g.fillArc(
			toX - (width / 2), toY - (height / 2) + 1,
			width, height,
			startAngle, angle);
		g.setColor(currentcolor);
	}
}

private class DirectedArrowRenderer extends ArrowLineRenderer {
        private int dir;
        public static final int LEFT = 0;
        public static final int RIGHT = 1;
        public static final int BOTH = 2; 
        
        private boolean hovering = false;
        
        public void setHovering(boolean hovering) {
            this.hovering = hovering;
        }
        
        public void setDirection(int dir) {
            this.dir = dir;
        }
        
        public void paint(Graphics g) {
            if (start == null || end == null)
                return;
    
            g.setColor(getColor());
    
            ModelPanelUI ui = (ModelPanelUI) panel.getUI();
            Point[] lineSegments = panel.getProjector().getLineSegments(start, end, panel);
            int[] xPoints = new int[lineSegments.length];
            int[] yPoints = new int[lineSegments.length];
            for (int i = 0; i < lineSegments.length; i++) {
                xPoints[i] = lineSegments[i].x;
                yPoints[i] = lineSegments[i].y;
            }
            g.drawPolyline(xPoints, yPoints, lineSegments.length);
            
            Color curColor = getColor();
            setColor(getColor().darker());
    
            if (hovering) {
                if (dir == RIGHT || dir == BOTH) {
                    
                    int fromIndex = lineSegments.length / 2 ;
                    int toIndex = fromIndex + 2;
                    if (toIndex >= lineSegments.length)
                        return;
                    ModelPoint toModelPoint = panel.unProject(lineSegments[toIndex]);
                    drawArrow(g,
                            xPoints[fromIndex], yPoints[fromIndex],
                            xPoints[toIndex], yPoints[toIndex],
                            panel.getScale(toModelPoint).getX(),
                            panel.getScale(toModelPoint).getY());
                } 
                if (dir == LEFT || dir == BOTH) {
                    
                    int toIndex = lineSegments.length / 2 - 2;
                    int fromIndex = toIndex + 2;
                    if (fromIndex <= -1)
                        return;
                    ModelPoint toModelPoint = panel.unProject(lineSegments[toIndex]);
    
                    drawArrow(g,
                            xPoints[fromIndex], yPoints[fromIndex],
                            xPoints[toIndex], yPoints[toIndex],
                            panel.getScale(toModelPoint).getX(),
                            panel.getScale(toModelPoint).getY());
                }
            }
            setColor(curColor);
        }
    }
   
}
