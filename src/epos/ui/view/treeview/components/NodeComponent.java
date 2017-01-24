/*
 * Created on Jul 2, 2003
 */
package epos.ui.view.treeview.components;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Iterator;

import epos.model.graph.IndexedElement;
import epos.model.tree.TreeNode;

/**
 * This is the graphical Unit of a Node. It draws the Node on the Position
 * specified by the Drawing Algorithm. This depands on the LayoutManeger. It
 * also contains (will contain) the Listeners for Mouse or Key Events on the
 * Node.
 * 
 * @author Thasso
 * 
 */
public class NodeComponent implements IndexedElement {

    public static final String PROPERTY_COLLAPSED = "Node collapsed";    
    public static final String PROPERTY_DESCRIPTION = "Description";
    public static final String PROPERTY_VISIBLE = "Visible";
    /**
     * the BaseNode of this NC.
     */
    protected TreeNode node = null;

    /**
     * The nodes angle relative to the midpoint
     */
    protected double angle = 0.0;
    protected double lastAngle = 0.0;

    /**
     * Bounds of this node. xy represent the ancor points (the points where
     * edges reach the node.
     */
    protected Rectangle bounds = new Rectangle();
    protected Rectangle lastBounds = new Rectangle();
    
    protected Shape boundingShape = new Rectangle();
    protected Shape subtreeShape = new Rectangle();
    
//    protected Rectangle subtreeBoundsAbs = new Rectangle();
    
    protected Rectangle2D.Double subtreeBounds = new Rectangle2D.Double();
    
    protected Rectangle2D.Double relativeBounds = new Rectangle2D.Double();

    /**
     * The manager of this nc
     */
    protected ComponentManager manager;

    private boolean visible = true;

    private boolean collapsed = false;

    private boolean selected = false;

	private NodeComponent deepest;

	private double fraction;

	private int index;
    

    /**
     * Constructs a new Node with the give BaseNode created by a given manager.
     * 
     * @param node
     *            The BaseNode of this Component.
     * @param node
     *            The BaseNode of this Component.
     */
    public NodeComponent(TreeNode node, ComponentManager manager) {
        super();
        if(node == null){
        	throw new RuntimeException("Can not create NodeComponent without backend node !");
        }
        this.node = node;
        this.manager = manager;        
    }

    /**
     * @return Returns the manager.
     */
    public ComponentManager getManager() {
        return manager;
    }

    /**
     * Returns the x coordinate of this component. X is the ancorpoint's x, the
     * point where edges connect to this node, NOT the upper left corner.
     * 
     * @return x the x coordinate
     */
    public int getX() {
    	if(fraction < 1.0) {
    		return (int) (lastBounds.x + ((bounds.x - lastBounds.x) * fraction));
    	}
        return bounds.x;
    }

    /**
     * Set the X Coordinate of the Component. The X coordinate is the x of the
     * ancor point, NOT the upper left corner. The Ancorpoint represents the
     * location where edges connect to this node.
     * 
     * @param x
     *            the ancor x
     */
    public void setX(int x) {
    	lastBounds.x = bounds.x;
        bounds.x = x;
    }

    /**
     * Returns the y coordinate of this component.
     * @return y the y coordinate
     */
    public int getY() {
    	if(fraction < 1.0) {
    		return (int) (lastBounds.y + ((bounds.y - lastBounds.y) * fraction));
    	}
        return bounds.y;
    }

    /**
     * Set the Y Coordinate of the Component. 
     * @param y
     *            the ancor y
     */
    public void setY(int y) {
    	lastBounds.y = bounds.y;
        bounds.y = y;
    }

    /**
     * Returns the width of this node.
     * 
     * @return width of this node
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Returns the location of this component.
     * 
     * @return point
     */
    public Point getLocation() {
        return new Point(bounds.x, bounds.y);
    }

    /**
     * Returns the height of this node.
     * 
     * @return height of this node
     */
    public int getHeight() {
        return bounds.height;
    }

    /**
     * Returns the bounds of this node. This is represented as a rectangle but
     * remeber, x/y points are the ancor points, not the upper left corner !!!
     * 
     * @return bounds the bounds of this node
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(bounds.x, bounds.y, bounds.width,
                bounds.height);
    }    

    /**
     * Set the bounds of this node. Remember x/y must be the ancor points, not
     * the upper left corner !
     * 
     * @param x
     *            the ancor x
     * @param y
     *            the ancor y
     * @param width
     * @param height
     */
    public void setBounds(int x, int y, int width, int height) {
    	lastBounds.setFrame(bounds);
        bounds.x = x;
        bounds.y = y;
        bounds.width = width;
        bounds.height = height;
        setBoundingShape(bounds);
    }

    /**
     * Set the bounds of this node. Remember x/y must be the ancor points, not
     * the upper left corner !
     * 
     * @param bounds
     */
    public void setBounds(Rectangle bounds) {
        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /**
     * Returns the angle (in radians) of this node with respect to the midpoint.
     * 
     * @return angle
     */
    public double getAngle() {
    	if(fraction < 1.0) {
    		return lastAngle + ((angle - lastAngle) * fraction);
    	}
        return angle;
    }

    /**
     * Sets the angle (in radians) for this node with respect to the given
     * midpoint.
     * 
     * @param angle
     *            in radians
     * @param midPoint
     */
    public void setAngle(double angle) {
    	this.lastAngle = this.angle;
        this.angle = angle;        
    }

    /**
     * Returns the BaseNode of this Component.
     * 
     * @return base node
     */
    public TreeNode getNode() {
        return this.node;
    }

    /**
     * Sets nodes visibility.
     * 
     * @param visible
     */
    public void setVisible(boolean visible) {
   		this.visible = visible;
   		setProperty(PROPERTY_VISIBLE, this.visible);
    }

    /**
     * Returns true if this node should visible. This does NOT check the actual
     * visibility of a node in its
     * {@link qalign2.gui.powerPane.treeView.TreePanel}'s clippiong area. Just
     * the global visibility flag of this node is returned.
     * 
     * @return true if node should be visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Returns true if this Node is shrunk.
     * 
     * @return boolean True if this Node is shrunk.
     */
    public boolean isCollapsed() {
        return collapsed;
    }

    /**
     * Collapse this Node and set the description to
     * {@link NodeLabel#getCommonString(String[])}
     * 
     * @param b
     */
    public void setCollapsed(boolean collapse) {
        if (getNode().isLeaf() || collapse == collapsed) {
            return;
        }
        collapsed = collapse;
        shrinkChildren(collapsed, this);

        if (collapsed) {
            // automatic anotation
        	// TODO set automatic annotation for collapsed nodes   using the description     	
        } else {
            //setDescription(null);
        }
        setProperty(PROPERTY_COLLAPSED, collapse);
    }

    /**
     * Helper for collapsing a node. Shrinks this Node and eventualy the
     * children of this node.
     * 
     * @param shrinkMe
     */
    private void shrinkChildren(boolean collapse, NodeComponent collapsedNode) {
        for (TreeNode n : node.children()) {
            NodeComponent nc = manager.getNodesComponent(n);
            nc.setX(collapsedNode.getX());
            nc.setY(collapsedNode.getY());
            if (collapse) {
                nc.setVisible(false);                
                nc.shrinkChildren(collapse, collapsedNode);
            } else {
                nc.setVisible(true);
                if (!nc.isCollapsed()) {
                    nc.shrinkChildren(collapse, collapsedNode);
                }
            }
        }
    }

    /**
     * Sets the description of this node. This is not a property of the base
     * node, but could be some custom label i.e for a collapsed node the number
     * of leaves under that node.
     * 
     * @param description
     *            the description
     */
    public void setDescription(String description) {
        setProperty(PROPERTY_DESCRIPTION, description);
    }

    /**
     * Returns the Description of the node.
     * 
     * @return description
     */
    public String getDescription() {
        return (String) getProperty(PROPERTY_DESCRIPTION);
    }

    /**
     * Select/deselect this node. 
     * 
     * @param selected
     * @param markPathToRoot
     * @param selectionColor
     */
    public void setSelected(boolean selected) {
    	this.selected = selected;
    }
    /**
     * returns true if the node is selected
     * @return
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Reinitialize the node's component
     */
    public void reinit() {
        bounds.x = 0;
        bounds.y = 0;
        bounds.width = 1;
        bounds.height = 1;
        angle = 0;
    }

    /**
     * get Parnet
     * @return
     */
    public NodeComponent getParent() {
        return manager.getNodesComponent(node.getParent());
    }

    /**
     * Use the manager to get the current label property and 
     * translates its value to a string.
     * 
     * @return
     */
    public String getLabel(){
    	epos.qnode.properties.Property labelProperty = manager.getNodeLabelProperty(this);		
    	if (labelProperty.getName() == "Label") {
			String l = getNode().getLabel();
			if(l != null){
				return l;
			}
			if(isCollapsed())
				return ""+getNode().getLeaves().length +" nodes";
			return null;
		}		
		Object value = getProperty(labelProperty.getName());
		if (value != null) {
			return labelProperty.toString();
		}
    	return null;//manager.getNodeLabel(this);
    }
    
    /**
     * Iterate over the children
     * @return
     */
    public Iterable<NodeComponent> children() {
        return new ChildIterable();
    }

    /**
     * returns a new PostorderIterator
     * 
     * @return Iterable
     */
    public Iterable<NodeComponent> depthFirstIterator() {
        return new PostorderIterator(this);
    }
    
    public void setProperty(String name, Serializable value) {
        node.setProperty(name, value);
        if(name.equals(PROPERTY_COLLAPSED)){
            setCollapsed((Boolean)value);
        }
    }

    public Object getProperty(String name) {
        return node.getProperty(name);
    }

    /**
     * Iteration helper
     */
    private static final Iterator<NodeComponent> EMPTY_ITERABLE = new Iterator<NodeComponent>() {
        public boolean hasNext() {
            return false;
        }

        public NodeComponent next() {
            return null;
        }

        public void remove() {
        }
    };

    /**
     * PostOrder or DepthFirst iteration over the subtree at this nodeComponent
     * 
     * @author Thasso
     */
    class PostorderIterator implements Iterable<NodeComponent>,
            Iterator<NodeComponent> {
        protected NodeComponent root;

        protected Iterator<NodeComponent> children;

        protected Iterator<NodeComponent> subtree;

        public PostorderIterator(NodeComponent node) {
            root = node;
            children = node.children().iterator();
            subtree = EMPTY_ITERABLE;
        }

        public Iterator<NodeComponent> iterator() {
            return this;
        }

        public boolean hasNext() {
            return root != null;
        }

        public NodeComponent next() {
            NodeComponent retval;
            if (subtree.hasNext()) {
                retval = subtree.next();
            } else if (children.hasNext()) {
                subtree = new PostorderIterator(children.next());
                retval = subtree.next();
            } else {
                retval = root;
                root = null;
            }
            return retval;
        }

        public void remove() {
        }

    }

    /**
     * Iterate over the children
     * @author Thasso
     */
    class ChildIterable implements Iterator<NodeComponent>,
            Iterable<NodeComponent> {
        Iterator<TreeNode> it;

        public ChildIterable() {
            it = node.children().iterator();
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public NodeComponent next() {
            return manager.getNodesComponent(it.next());
        }

        public void remove() {
            throw new RuntimeException("Method not supported");
        }

        public Iterator<NodeComponent> iterator() {
            return this;
        }

    }
    public boolean contains(Point p) {
        if(getAngle() == 0.0){
            return bounds.contains(p);
        }else{
            return bounds.contains(AffineTransform.getRotateInstance(-getAngle(), bounds.x, bounds.y).transform(p, null));
        }
    }

	/**
	 * @return the subtreeBounds
	 */
	public Rectangle2D.Double getSubtreeBounds() {
		return subtreeBounds;
	}

	/**
	 * @param subtreeBounds the subtreeBounds to set
	 */
	public void setSubtreeBounds(Rectangle2D.Double subtreeBounds) {
		this.subtreeBounds = subtreeBounds;
	}

	/**
	 * @return the relativeBounds
	 */
	public Rectangle2D.Double getRelativeBounds() {
		return relativeBounds;
	}

	/**
	 * @param relativeBounds the relativeBounds to set
	 */
	public void setRelativeBounds(Rectangle2D.Double relativeBounds) {
		this.relativeBounds = relativeBounds;
	}

	public Shape getBoundingShape() {
		return boundingShape;
	}

	public void setBoundingShape(Shape boundingShape) {
		this.boundingShape = boundingShape;
	}

	public Shape getSubtreeShape() {
		return subtreeShape;
	}

	public void setSubtreeShape(Shape subtreeShape) {
		this.subtreeShape = subtreeShape;
	}

	public Rectangle getLastBounds() {
		return lastBounds;
	}
//	public int getX(float fraction){
//		return (int) (lastBounds.x + ((bounds.x - lastBounds.x) * fraction));
//	}
//	public int getY(float fraction){
//		return (int) (lastBounds.y + ((bounds.y - lastBounds.y) * fraction));
//	}
//	public double getAngle(float fraction){
//		return lastAngle + ((angle - lastAngle) * fraction);
//	}

	public NodeComponent getDeepestSuccessor(){
		if(deepest == null){					
			double dist = 0;
			double d;		
			for (NodeComponent c : this.depthFirstIterator()) {
				if(c.getNode().isLeaf()){
					d = c.getLocation().distance(getX(), getY());
					if(d >= dist){
						deepest = c;
					}
				}
			}
		}
		return deepest;
	}

	public double getFraction() {
		return fraction;
	}

	public void setFraction(double fraction) {
		this.fraction = fraction;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}



/*
 
    public JPopupMenu getPopupMenu(String title) {
        final JPopupMenu pop = new JPopupMenu(title);
        JMenuItem collapse = new JMenuItem(new CollapseAction(this));        
        JMenuItem rotate = new JMenuItem(new RotateAction(this));
        JMenu marker = new JMenu("Marker");
        final JColorChooser col = new JColorChooser();
        col.setPreviewPanel(new JPanel());
        col.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {                
                getManager().setNodeColor(NodeComponent.this, col.getSelectionModel().getSelectedColor(),null, ColorManager.EDGE_TO_LEAVES);                               
                pop.setVisible(false);
            }
        });
        col
                .setChooserPanels(new AbstractColorChooserPanel[] { new ColorChooserPanel() });

        marker.add(col);

        if (getNode().isLeaf()) {
            collapse.setEnabled(false);
            rotate.setEnabled(false);
            marker.setEnabled(false);
        } else {
            collapse.setEnabled(true);
            marker.setEnabled(true);
        }
        pop.add(collapse);
        pop.add(rotate);
        pop.add(marker);
        return pop;
    }
    */

