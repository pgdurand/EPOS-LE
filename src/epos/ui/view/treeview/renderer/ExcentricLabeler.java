/*
 * Created on 21.04.2005
 */
package epos.ui.view.treeview.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jdesktop.application.Action;

//import epos.ui.annotations.ToolbarAction;
import epos.ui.view.ppane.AbstractContent;
import epos.ui.view.ppane.AbstractController;
import epos.ui.view.ppane.AbstractRenderer;
import epos.ui.view.ppane.AbstractTool;
import epos.ui.view.ppane.ControlledUnit;
import epos.ui.view.ppane.Controller;
import epos.ui.view.ppane.PowerPane;
import epos.ui.view.ppane.Renderer;
import epos.ui.view.treeview.TreeContent;
import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.ColorStyle.Colors;
import epos.ui.view.treeview.components.NodeComponent;

/**
 * @author Thasso
 */
public class ExcentricLabeler extends AbstractTool<TreeView, TreeContent>{
    protected int radius = 20;
    protected Arc2D.Double arc = new Arc2D.Double();
    protected boolean mouseIn = false;
    protected Point mousePoint = new Point(0, 0);
    protected Point clickPoint = null;
    protected Point movePoint = null;
    private int orginalRadius = radius;
    private int tooltipHeight = -1;
    private int tooltipWidth = -1;
    private int alltooltipHeight;
    private boolean labelsOnly = false;
    private boolean labelsSwitched = false;
    protected TooltipRenderer renderer = new TooltipRenderer();
    private boolean enablelabelSwitch = true;
    private Cursor parentCursor;
	private boolean labelerEnabled;
	private ExcentricController controller;
	private ExcentricRenderer excentricRenderer;

    public ExcentricLabeler() {
        super();
    }
    
    protected boolean validComponent(NodeComponent c) {
    	return true;
    }


    @SuppressWarnings("unchecked")
	protected List<NodeComponent> getLabels(Shape arc2, Graphics2D g2d) {
        List<NodeComponent> contains = new ArrayList<NodeComponent>();
        alltooltipHeight = 0;
        tooltipHeight = -1;
        tooltipWidth = -1;

            Object[] nodes = parent.getView().searchNodeComponents(arc2.getBounds());
            for (int i = 0; i < nodes.length; i++) {
                NodeComponent c = (NodeComponent) nodes[i];
                if (!c.isVisible())
                    continue;
                if (labelsOnly && c.getNode().getLabel() == null)
                    continue;
                if(!validComponent(c))
                	continue;
                
                contains.add(c);
                Dimension d = renderer.getPrefferedSize(g2d, c);
                tooltipWidth = d.width > tooltipWidth ? d.width
                        : tooltipWidth;
                tooltipHeight = d.height > tooltipHeight ? d.height
                        : tooltipHeight;
                alltooltipHeight += d.height;
            }
        alltooltipHeight += 2 * contains.size();
        Collections.sort(contains, new CompareY());
        return contains;
    }
    
	public List<NodeComponent> getSelected() {
        List<NodeComponent> contains = new ArrayList<NodeComponent>();
        Object[] nodes = parent.getView().searchNodeComponents(arc.getBounds());
        for (int i = 0; i < nodes.length; i++) {
            NodeComponent c = (NodeComponent) nodes[i];
            if (!c.isVisible())
                continue;
            if (labelsOnly && c.getNode().getLabel() == null)
                continue;
            if(!validComponent(c))
            	continue;                
            contains.add(c);
        }
        Collections.sort(contains, new CompareY());
        return contains;
    }

    
    
    @Action(selectedProperty="labelerEnabled")
    //@ToolbarAction
    public void labeler(){}
    public void setLabelerEnabled(boolean labeler){
    	boolean old = this.labelerEnabled;
    	this.labelerEnabled = labeler;
    	firePropertyChange("labelerEnabled", old, this.labelerEnabled);
    	
    	if(labeler){
            parentCursor = parent.getView().getCursor();
            parent.getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            parent.activateController(getController());
            parent.activateRenderer(getExcentricRenderer());
    	}else{
    	    parent.getView().setCursor(parentCursor);
            parent.deactivateController(getController());
            parent.deactivateRenderer(getExcentricRenderer());
    	}
    }

	public boolean isLabelerEnabled(){
    	return this.labelerEnabled;
    }

    public Controller getController() {
		if(controller == null) controller = new ExcentricController(this);
		return controller;
	}
    
    public void setController(ExcentricController controller){
		if(controller == null) this.controller = new ExcentricController(this);
		else this.controller = controller;    	
    }
    
    protected Renderer<TreeView, TreeContent> getExcentricRenderer(){
    	if(excentricRenderer == null) excentricRenderer = new ExcentricRenderer();
    	return excentricRenderer;
    }
    
	public boolean isEnablelabelSwitch() {
		return enablelabelSwitch;
	}

	public void setEnableLabelSwitch(boolean enablelabelSwitch) {
		this.enablelabelSwitch = enablelabelSwitch;
	}

	public TooltipRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(TooltipRenderer renderer) {
		if(renderer == null ) this.renderer = new TooltipRenderer();
		else this.renderer = renderer;
	}
	
	public static class ExcentricController extends AbstractController<TreeView, TreeContent>{
        private Timer timer;
		protected ExcentricLabeler labeler;

		public ExcentricController(ExcentricLabeler labeler) {
			super(labeler.getParentPP());
			this.labeler = labeler;
		}

		@Override
		public void disable() {
			labeler.setLabelerEnabled(false);
		}

		@Override
		public int getMouseDisableActions() {
			return BUTTON1 | MOVE;
		}
            public void mouseMoved(MouseEvent e) {
            	labeler.mousePoint.setLocation(e.getX(), e.getY());
            	labeler.parent.getView().repaint();
                e.consume();
            }

            public void mouseDragged(MouseEvent e) {
                if (labeler.clickPoint != null) {
                    int dist = labeler.clickPoint.y - e.getY();
                    labeler.radius = (labeler.orginalRadius + dist) > 5 ? (labeler.orginalRadius + dist)
                            : 5;
                    labeler.movePoint = e.getPoint();
                    labeler.parent.getView().repaint();
                }
            }
			
            public void mouseEntered(MouseEvent e) {
            	labeler.mouseIn = true;
            	labeler.parent.getView().repaint();
            }

            public void mouseExited(MouseEvent e) {
                Component c = SwingUtilities.getDeepestComponentAt(labeler.parent.getView(),
                        e.getX(), e.getY());
                if (c == null) {
                	labeler.mouseIn = false;
                	labeler.parent.getView().repaint();
                }
            }

            public void mousePressed(MouseEvent e) {
            	labeler.clickPoint = e.getPoint();
            	labeler.movePoint = e.getPoint();
            	labeler.orginalRadius = labeler.radius;
            	labeler.parent.getView().repaint();
            }

            public void mouseReleased(MouseEvent e) {
            	labeler.clickPoint = null;
            	labeler.movePoint = null;
            	labeler.parent.getView().repaint();
            }

            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(labeler.enablelabelSwitch) {
                	labeler.labelsSwitched = true;
                	labeler.labelsOnly = !labeler.labelsOnly;
                	labeler.parent.getView().repaint();
	
	                if (timer == null) {
	                    timer = new Timer(2000, new ActionListener() {
	                        public void actionPerformed(ActionEvent e) {
	                        	labeler.labelsSwitched = false;
	                        	labeler.parent.getView().repaint();
	                            timer.stop();
	                        }
	                    });
	                }
	                timer.start();
                }
            }
	}

	class ExcentricRenderer extends AbstractRenderer<TreeView, TreeContent>{
	    public ExcentricRenderer() {
			super(getParentPP());
		}

		public void render(Graphics parentGraphics) {
	        Rectangle p = parent.getView().getVisibleRect();
	        Graphics2D g2d = (Graphics2D) parentGraphics;        
	        Font oldFont = g2d.getFont();
	        Stroke oldStroke = g2d.getStroke();
	        Shape oldClip = g2d.getClip();
	        
	        // move arc around mouse point
	        arc.setArcByCenter(mousePoint.x, mousePoint.y, radius, 0, 360,
	                Arc2D.CHORD);
	        // draw arc and resizeline
	        if (mouseIn) {
	            g2d.setColor(new Color(255,100,20, 100));
	            g2d.fill(arc);
	            g2d.setColor(parent.getView().getColorManager().getColor(Colors.foreground));
	            g2d.draw(arc);
	        }
	        if (clickPoint != null && movePoint != null) {
	            g2d.setColor(parent.getView().getColorManager().getColor(Colors.foreground));
	            g2d.drawLine(movePoint.x, movePoint.y, clickPoint.x, clickPoint.y);
	        }

	        // set font and stroke
	        g2d.setFont(g2d.getFont().deriveFont(12.0f));        
	        Stroke dashStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
	                BasicStroke.JOIN_ROUND, 5f, new float[] { 5f }, 10.0f);

	        // get nodes under arc and the tooltip max width/height
	        List l = getLabels(arc, g2d);

	        // this radius is the minimum distance from a label to the nodes center
	        // point/the mouse position
	        int radius = ExcentricLabeler.this.radius + 10;

	        if (l != null && mouseIn) {

	            /*
	             * compute space ! we need to no the space on the left and on the
	             * right of the arc to arrage the labels
	             */
	            int leftSpace = (mousePoint.x - p.x) - radius;
	            int rightSpace = ((p.x + p.width) - mousePoint.x) - radius;
	            leftSpace = (leftSpace > tooltipWidth ? p.height : 0);
	            rightSpace = (rightSpace > tooltipWidth ? p.height : 0);
	            int space = leftSpace + rightSpace;

	            int consumedSpace = 0;
	            int drawnElements = 0;
	            String notEnoughSpace = null;
	            if (alltooltipHeight > space) {
	                // / draw "not enough space" indicator
	                notEnoughSpace = "";
	            }
	            int rightY = 0;
	            int leftY = 0;
	            int s = alltooltipHeight > space ? space : alltooltipHeight;
	            leftY = (int) (mousePoint.y - (s / 2.0));
	            if (leftY < p.y)
	                leftY = p.y;
	            else if (leftY + (s - rightSpace) > p.y + p.height) {
	                leftY -= ((leftY + (s - rightSpace)) - (p.y + p.height));
	            }
	            rightY = (int) (mousePoint.y - (s / 2.0));

	            if (rightY < p.y)
	                rightY = p.y;
	            else if (rightY + (s > rightSpace ? rightSpace : s) > p.y
	                    + p.height) {
	                rightY -= ((rightY + rightSpace) - (p.y + p.height));
	            }

	            Stroke stroke= new BasicStroke(1f);
	            for (Iterator iter = l.iterator(); iter.hasNext();) {
	                // stop if no space left...
	                if (consumedSpace >= space)
	                    break;

	                NodeComponent comp = (NodeComponent) iter.next();
	                //continue with next if not valid
//	                if(! validComponent(comp)) {
//	                	continue;
//	                }

	                Dimension dim = renderer.getPrefferedSize(g2d, comp);
	                if(dim.height == 0 && dim.width == 0) continue;
	                
	                int x = 0, y = 0;
	                if (rightSpace - dim.height > 0) {
	                    x = mousePoint.x + radius;
	                    y = rightY + p.height - rightSpace;
	                    rightSpace -= (dim.height + 2);
	                    g2d.setColor(parent.getView().getColorManager().getColor(Colors.foreground));
	                    g2d.setStroke(dashStroke);
	                    g2d.drawLine((int) comp.getX(),
	                            (int) comp.getY(), x,
	                            (int) (y + (dim.height / 2.0)));
	                    g2d.setStroke(stroke);
	                } else if (leftSpace - dim.height > 0) {
	                    x = mousePoint.x - radius - tooltipWidth;
	                    y = leftY + p.height - leftSpace;
	                    leftSpace -= (dim.height + 2);
	                    g2d.setColor(parent.getView().getColorManager().getColor(Colors.foreground));
	                    g2d.setStroke(dashStroke);
	                    g2d.drawLine((int) comp.getX(),
	                            (int) comp.getY(), x + dim.width,
	                            (int) (y + (dim.height / 2.0)));
	                    g2d.setStroke(stroke);
	                } else {
	                    break;
	                }
	                consumedSpace += dim.height;
	                drawnElements++;
	                g2d.setClip(x, y, tooltipWidth, dim.height);                
	                renderer.paintComponent(g2d, comp);
	                g2d.setClip(oldClip);
	            }
	            

	            if (notEnoughSpace != null) {
	                // draw the not enough space label
	                notEnoughSpace = "And " + (l.size() - drawnElements)
	                        + " more...";
	                g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD));
	                int width = SwingUtilities.computeStringWidth(g2d
	                        .getFontMetrics(), notEnoughSpace);

	                g2d.setColor(new Color(255, 130, 0, 200));
	                g2d.fillRect((int) (mousePoint.x - (width / 2.0)) - 2,
	                        mousePoint.y - 15, width + 2, 14);
	                g2d.setColor(Color.BLACK);
	                g2d.drawRect((int) (mousePoint.x - (width / 2.0)) - 2,
	                        mousePoint.y - 15, width + 2, 14);
	                g2d.drawString(notEnoughSpace,
	                        (int) (mousePoint.x - (width / 2.0)), mousePoint.y - 2);
	            }
	            if (labelsSwitched) {
	                String label = "";
	                if (labelsOnly)
	                    label = "Only labeled nodes";
	                else
	                    label = "All nodes";
	                g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD));
	                int width = SwingUtilities.computeStringWidth(g2d
	                        .getFontMetrics(), label);

	                g2d.setColor(new Color(255, 130, 0, 200));
	                g2d.fillRect((int) (mousePoint.x - (width / 2.0)) - 2,
	                        mousePoint.y + 15, width + 2, 14);
	                g2d.setColor(Color.BLACK);
	                g2d.drawRect((int) (mousePoint.x - (width / 2.0)) - 2,
	                        mousePoint.y + 15, width + 2, 14);
	                g2d.drawString(label, (int) (mousePoint.x - (width / 2.0)),
	                        mousePoint.y - 2 + 29);
	            }

	        }
	        g2d.setFont(oldFont);
	        g2d.setStroke(oldStroke);
	        g2d.setClip(oldClip);
	    }

		@Override
		public boolean conflicts(ControlledUnit<TreeView, TreeContent> otherUnit) {
			return true;
		}

		@Override
		public void disable() {
			setLabelerEnabled(false);
		}

	}

}








class CompareX implements Comparator {
    public boolean equals(Object obj) {
        return false;
    }

    public int compare(Object o1, Object o2) {
        NodeComponent n1 = (NodeComponent) o1;
        NodeComponent n2 = (NodeComponent) o2;

        if (n1.getX() > n2.getX())
            return 1;
        else if (n1.getX() < n2.getX())
            return -1;
        return 0;
    }
}

class CompareY implements Comparator {
    public boolean equals(Object obj) {
        return false;
    }

    public int compare(Object o1, Object o2) {
        NodeComponent n1 = (NodeComponent) o1;
        NodeComponent n2 = (NodeComponent) o2;

        if (n1.getY() > n2.getY())
            return 1;
        else if (n1.getY() < n2.getY())
            return -1;
        return 0;
    }
}

class CompareXDouble implements Comparator {
    public boolean equals(Object obj) {
        return false;
    }

    public int compare(Object o1, Object o2) {
        NodeComponent n1 = (NodeComponent) o1;
        double n2 = ((Double) o2).doubleValue();
        if (n1.getX() > n2) {
            return 1;
        } else if (n1.getX() < n2) {
            return -1;
        }
        return 0;
    }
}

class CompareYDouble implements Comparator {
    public boolean equals(Object obj) {
        return false;
    }

    public int compare(Object o1, Object o2) {
        NodeComponent n1 = (NodeComponent) o1;
        double n2 = ((Double) o2).doubleValue();
        if (n1.getY() > n2) {
            return 1;
        } else if (n1.getY() < n2) {
            return -1;
        }
        return 0;
    }
}
