package org.twdata.TW1606U.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class RadialMenu extends JComponent implements MouseListener,
        MouseMotionListener {

    protected List menuItems; // the important list of menu choices
    protected int diameter; // diameter of the radial menu
    protected int offset; // allows the radial menu center to be shifted from

    // the upper left corner
    protected boolean labelEdges = true; // add labels to the edges of the

    // menu
    protected int wedgeSeparation = 2; // number of degrees between wedges of

    // the radial menu
    protected float backgroundTransparency = .5f; // how transparent the

    // radial menu's background
    // is
    protected boolean hideOnExit = false;
    protected int menuHighlighted = -1; // which menu item is highlighted,

    // negative numbers mean no item is
    // highlighted
    protected Color highlightColor = Color.WHITE; // highlight wedge color
    protected List menuListeners; // list of objects to be notified of menu
    protected int rotation = 0;

    // events
    public RadialMenu(int diameter, int offset) {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        menuListeners = new ArrayList();
        menuItems = new ArrayList();
        this.diameter = diameter;
        this.offset = offset;
    }

    public void addMenuItem(Object o) {
        menuItems.add(o);
    }

    public void addMenuListener(MenuListener ml) {
        menuListeners.add(ml);
    }

    public void removeMenuListener(MenuListener ml) {
        menuListeners.remove(ml);
    }

    protected Point getCoordOnCircle(int radius, double angle) {
        int x, y;
        double cos = Math.cos(Math.toRadians((int) (angle) % 90)) * radius;
        double sin = Math.sin(Math.toRadians((int) (angle) % 90)) * radius;
        if (angle < 90) {
            x = (int) (diameter / 2 + cos);
            y = (int) (diameter / 2 - sin);
        } else if (angle < 180) {
            x = (int) (diameter / 2 - sin);
            y = (int) (diameter / 2 - cos);
        } else if (angle < 270) {
            x = (int) (diameter / 2 - cos);
            y = (int) (diameter / 2 + sin);
        } else {
            x = (int) (diameter / 2 + sin);
            y = (int) (diameter / 2 + cos);
        }
        return new Point(x, y);
    }

    protected Point getCoordOnCircle(double angle) {
        return this.getCoordOnCircle(diameter / 2, angle);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Composite oldcomp = g2.getComposite();
        Composite fade = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                1.0f - backgroundTransparency);
        g2.setComposite(fade);
        double angle = 360.0 / this.menuItems.size();
        for (int i = 0; i < this.menuItems.size(); i++) {
            drawWedge(g2, angle, fade, i);
            if (menuItems.get(i) instanceof RadialMenuItem) {
                RadialMenuItem rmi = (RadialMenuItem) menuItems.get(i);
                if (rmi.getIcon() != null) {
                    drawIconOnWedge(g2, (angle * i), rmi, oldcomp, fade);
                }
            }
        }
        g2.setComposite(oldcomp);
        drawBorder(g2);
        labelWedges(g2, angle);
        labelEdges(g2, angle);
    }

    protected void drawIconOnWedge(Graphics2D g2, double angle,
            RadialMenuItem rmi, Composite iconcomposite,
            Composite othercomposite) {
        Point iconpoint = this.getCoordOnCircle(diameter * 3 / 10, angle);
        ImageIcon icon = rmi.getIcon();
        int iconwidth = icon.getImage().getWidth(this);
        int iconheight = icon.getImage().getHeight(this) / 2;
        g2.setComposite(iconcomposite);
        g2.drawImage(icon.getImage(),
                (int) (iconpoint.getX() - (iconwidth / 2)) + offset,
                ((int) iconpoint.getY() - (iconheight / 2)) + offset, this);
        g2.setComposite(othercomposite);
    }

    protected void labelWedges(Graphics2D g2, double angle) {
        FontMetrics fm = this.getFontMetrics(this.getFont());
        for (int i = 0; i < this.menuItems.size(); i++) {
            if (menuItems.get(i) instanceof String) {
                String s = (String) menuItems.get(i);
                s = trimStringToFit(g2, s, 40);
                double stringwidth = fm.getStringBounds(s, g2).getWidth();
                double stringheight = fm.getStringBounds(s, g2).getHeight();
                if (i * angle > 90 && i * angle < 270) {
                    g2.transform(AffineTransform.getRotateInstance(Math
                            .toRadians((180 - i * angle)), (diameter / 2)
                            + offset, (diameter / 2) + offset));
                    g2.drawString(s, (int) ((diameter * 1 / 4)
                            - (stringwidth / 2) + offset), (int) (diameter / 2
                            + stringheight / 4 + offset));
                    g2.transform(AffineTransform.getRotateInstance(Math
                            .toRadians((180 + i * angle)), (diameter / 2)
                            + offset, (diameter / 2) + offset));
                } else {
                    g2.transform(AffineTransform.getRotateInstance(Math
                            .toRadians((-i * angle)), (diameter / 2) + offset,
                            (diameter / 2) + offset));
                    g2.drawString(s, (int) ((diameter * 3 / 4)
                            - (stringwidth / 2) + offset), (int) (diameter / 2
                            + stringheight / 4 + offset));
                    g2.transform(AffineTransform.getRotateInstance(Math
                            .toRadians((+i * angle)), (diameter / 2) + offset,
                            (diameter / 2) + offset));
                }
            }
        }
        g2.transform(AffineTransform.getRotateInstance(Math.toRadians(90),
                (diameter / 2) + offset, (diameter / 2) + offset));
    }

    protected void labelEdges(Graphics2D g2, double angle) {
        for (int i = 0; i < this.menuItems.size(); i++) {
            if (labelEdges) {
                String s = null;
                Object o = this.menuItems.get(i);
                if (o instanceof String) {
                    s = (String) o;
                } else if (o instanceof RadialMenuItem) {
                    RadialMenuItem rmi = (RadialMenuItem) o;
                    s = rmi.getText();
                }
                FontMetrics fm = this.getFontMetrics(this.getFont());
                s = trimStringToFit(g2, s, 50);
                double stringwidth = fm.getStringBounds(s, g2).getWidth();
                g2.drawString(s, (int) (diameter / 2 - stringwidth / 2)
                        + offset, 12 + offset);
                g2.transform(AffineTransform.getRotateInstance(-Math
                        .toRadians(angle), (diameter / 2) + offset,
                        (diameter / 2) + offset));
            }
        }
        g2.transform(AffineTransform.getRotateInstance(Math
                .toRadians((angle / 2) - 180), (diameter / 2) + offset,
                (diameter / 2) + offset));
    }

    protected String trimStringToFit(Graphics2D g2, String s, int offset) {
        FontMetrics fm = this.getFontMetrics(this.getFont());
        double stringwidth = fm.getStringBounds(s, g2).getWidth();
        boolean trimmed = false;
        while (stringwidth > (diameter / 2 - offset)) {
            s = s.substring(0, s.length() - 1);
            stringwidth = fm.getStringBounds(s, g2).getWidth();
            trimmed = true;
        }
        if (trimmed) {
            s = s + ">";
        }
        return s;
    }

    protected void drawBorder(Graphics2D g2) {
        g2.setColor(this.getForeground());
        g2.drawOval(0 + offset, 0 + offset, diameter, diameter);
        g2.drawOval(0 + offset + (diameter * 3 / 40), 0 + offset
                + (diameter * 3 / 40), diameter * 17 / 20, diameter * 17 / 20);
    }

    protected void drawWedge(Graphics2D g2, double angle, Composite fade,
            int wedgenum) {
        g2.setComposite(fade);
        g2.setColor(this.getBackground());
        if (menuHighlighted == wedgenum) {
            g2.setColor(this.highlightColor);
        }
        g2.fillArc(offset, offset, (int) (diameter), (int) (diameter),
                (int) ((wedgenum * angle) - (angle / 2) + wedgeSeparation),
                (int) (angle - wedgeSeparation));
        g2.setColor(this.getBackground());
    }

    public static void main(String args[]) {
        final JFrame f = new JFrame("Radial Menu Test");
        f.setSize(300, 300);
        final RadialMenu rm = new RadialMenu(200, 30);
        ImageIcon importicon = new ImageIcon(rm.getClass().getResource(
                "/new.gif"));
        final JLabel label = new JLabel("Text goes here", importicon,
                SwingConstants.CENTER);
        ImageIcon openicon = new ImageIcon(rm.getClass().getResource(
                "/copy.gif"));
        rm.addMenuItem("One");
        rm.addMenuItem(new RadialMenuItem("open", openicon));
        rm.addMenuItem("Three");
        rm.addMenuItem("Four");
        // rm.addMenuItem("Five");
        // rm.addMenuItem("Six");
        // rm.addMenuItem("Seven");
        // rm.addMenuItem(new RadialMenuItem("import", importicon));
        rm.setBackground(Color.WHITE);
        rm.setForeground(Color.RED);
        rm.setFont(new Font("Arial", Font.PLAIN, 12));
        rm.setHighlightColor(Color.BLUE);

        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                f.getLayeredPane().remove(rm);
                rm.setBounds(me.getX() - 130, me.getY() - 130, 261, 261);
                rm.setVisible(true);
                f.getLayeredPane().add(rm,
                        new Integer(JLayeredPane.POPUP_LAYER));
                f.validate();

                System.out.println("mouse click complete");
            }
        });
        f.add(label);
        f.setVisible(true);
    }

    public void mouseClicked(MouseEvent e) {
        checkMenuItemClick(e.getX(), e.getY());
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    protected void checkMenuItemClick(int x, int y) {
        int menuchosen = determineSlice(x, y);
        if (menuchosen < this.menuItems.size()) {
            System.out.println(this.menuItems.get(menuchosen) + " clicked");
            Iterator i = menuListeners.iterator();
            while (i.hasNext()) {
                MenuListener ml = (MenuListener) i.next();
                ml.menuSelected(new MenuEvent(menuItems.get(menuchosen)));
            }
        }
        setVisible(false);

    }

    public void mouseExited(MouseEvent e) {
        cancel();
    }

    private void cancel() {
        this.setVisible(false);
        Iterator i = menuListeners.iterator();
        while (i.hasNext()) {
            MenuListener ml = (MenuListener) i.next();
            ml.menuCanceled(new MenuEvent(this));
        }
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int menuhighlight = determineSlice(x, y);
        if (menuhighlight != menuHighlighted) {
            menuHighlighted = menuhighlight;
            this.repaint();
        }
    }

    private int determineSlice(int x, int y) {
        int betax = Math.abs(x - offset - diameter / 2);
        int betay = Math.abs(y - offset - diameter / 2);
        double segment = Math.sqrt(betax * betax + betay * betay);
        if (hideOnExit) {
            if (segment > (diameter / 2)) {
                cancel();
            }
        }
        double cosineA = ((segment * segment) + (betax * betax) - (betay * betay))
                / (2 * betax * segment);
        double angle = Math.toDegrees(Math.acos(cosineA));

        if ((x - offset) < diameter / 2) {
            if ((y - offset) < diameter / 2) {
                angle = 180 - angle;
            } else {
                angle = 180 + angle;
            }
        } else if ((x - offset) >= diameter / 2 && (y - offset) >= diameter / 2) {
            angle = 360 - angle;
        }
        angle += (360.0 / menuItems.size()) / 2;
        if (angle > 360)
            angle -= 360.0;

        int menuhighlight = (int) (angle / (360.0 / menuItems.size()));
        return menuhighlight;
    }

    public float getBackgroundTransparency() {
        return backgroundTransparency;
    }

    public void setBackgroundTransparency(float backgroundTransparency) {
        this.backgroundTransparency = backgroundTransparency;
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    public int getMenuHighlighted() {
        return menuHighlighted;
    }

    public void setMenuHighlighted(int menuHighlighted) {
        this.menuHighlighted = menuHighlighted;
    }

    public int getWedgeSeparation() {
        return wedgeSeparation;
    }

    public void setWedgeSeparation(int wedgeSeparation) {
        this.wedgeSeparation = wedgeSeparation;
    }

    public boolean isHideOnExit() {
        return hideOnExit;
    }

    public void setHideOnExit(boolean hideOnExit) {
        this.hideOnExit = hideOnExit;
    }

}
