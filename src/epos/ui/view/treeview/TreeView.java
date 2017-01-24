/*
 * Created on 18.01.2005
 */
package epos.ui.view.treeview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.qnode.properties.Property;
import epos.ui.view.ppane.AbstractView;
import epos.ui.view.ppane.PowerPane;
import epos.ui.view.treeview.ColorStyle.Colors;
import epos.ui.view.treeview.components.CloseCurveEdgeRenderer;
import epos.ui.view.treeview.components.ComponentManager;
import epos.ui.view.treeview.components.DefaultNodeRenderer;
import epos.ui.view.treeview.components.DefaultSelectionManager;
import epos.ui.view.treeview.components.EdgeRenderer;
import epos.ui.view.treeview.components.LineEdgeRenderer;
import epos.ui.view.treeview.components.NodeComponent;
import epos.ui.view.treeview.components.NodeRenderer;
import epos.ui.view.treeview.components.OpenCurveEdgeRenderer;
import epos.ui.view.treeview.components.PolarEdgeRenderer;
import epos.ui.view.treeview.components.RectangleEdgeRenderer;
import epos.ui.view.treeview.components.SelectionListener;
import epos.ui.view.treeview.components.SelectionManager;
import epos.ui.view.treeview.layouts.DendogramLayout;
import epos.ui.view.treeview.layouts.TreeLayouter;

/**
 * This class implements the view interface to be the view of the TreePP It uses
 * the TreePanel to render and display the trees.
 * 
 * @author Thasso
 */
public class TreeView extends AbstractView<TreeView, TreeContent> {

	private static final String PROPERTY_KEY = "ViewProperties";

	private static final String PROPERTY_INSETS = "insets";

	protected JScrollPane scrollPane;

	/**
	 * The Tree
	 */
	protected Tree tree;

	/**
	 * the node component manager
	 */
	protected ComponentManager componentManager;

	/**
	 * the selection manager
	 */
	protected SelectionManager selectionManager;

	/**
	 * scale in x direction
	 */
	protected double scaleX = 1.0;

	/**
	 * scale in Y direction
	 */
	protected double scaleY = 1.0;

	/**
	 * The LayoutManager.
	 */
	protected TreeLayouter layout;

	/**
	 * The ColorManager
	 */
	protected ColorManager colorManager;

	/**
	 * Detect component in the panel
	 */
	protected ComponentDetector ncFinder;

	/**
	 * the default edge renderer
	 */
	protected EdgeRenderer edgeRenderer;

	/**
	 * the default node renderer
	 */
	protected NodeRenderer nodeRenderer;

	/**
	 * animation fraction
	 */
	protected float fraction = 1.0f;

	/**
	 * animation controller
	 */
	private Mover mover = new Mover();

	/**
	 * animator
	 */
	private Animator animator = new Animator(250, mover);

	/**
	 * use animation ?
	 */
	private boolean animate = false;

	/**
	 * indicator when to draw subtrees
	 */
	protected double edgeCutThreshold = 1.0;

	/**
	 * global insets for this view
	 */
	protected Insets insets;
	
	/**
	 * store persistent properties here
	 */
	protected epos.qnode.properties.PropertySet properties;
	
	/**
	 * used to force a repaint after selection changes
	 */
	private SelectionListener selectionListener = new SelectionListener() {
		public void selectionChanged(SelectionManager manager) {
			repaint();
		}
	};

	/**
	 * list of available edge renderer TODO: make edge renderer pluggable
	 */
	protected ArrayList<EdgeRenderer> availableEdgeRenderer;

	/**
	 * true while animation is running
	 */
	protected boolean animating = false;
	
	{
		availableEdgeRenderer = new ArrayList<EdgeRenderer>();
		availableEdgeRenderer.add(new RectangleEdgeRenderer());
		availableEdgeRenderer.add(new LineEdgeRenderer());
		availableEdgeRenderer.add(new OpenCurveEdgeRenderer());
		availableEdgeRenderer.add(new CloseCurveEdgeRenderer());
		availableEdgeRenderer.add(new PolarEdgeRenderer());
	}

	/**
	 * Create a new empty treeView
	 * 
	 */
	public TreeView() {
		this(new ComponentManager());
	}

	/**
	 * @param factory
	 */
	public TreeView(ComponentManager manager) {
		this(null, new DendogramLayout(), manager);
	}

	/**
	 * Creates a new TreePanel with default dendogram layout and a default node
	 * and edge representation.
	 * 
	 * @param tree
	 */
	public TreeView(Tree tree) {
		this(tree, new DendogramLayout());

	}

	/**
	 * Creates a new TreePanel using the given layout manager and the default
	 * node and edge representation.
	 * 
	 * @param tree
	 * @param layout
	 */
	public TreeView(Tree tree, TreeLayouter layout) {
		this(tree, layout, new ComponentManager());
	}

	/**
	 * Create a new TreePanel using the given layout and the given nodeManager
	 * to create and manage nodeComponents. the edgeManager will be the default
	 * one.
	 * 
	 * @param tree
	 * @param layout
	 * @param factory
	 */
	public TreeView(Tree tree, TreeLayouter layout, ComponentManager factory) {
		super();
		setFocusable(true);
		setAutoscrolls(true);

		this.tree = tree;
		this.componentManager = factory;
		ncFinder = new ComponentDetector(this);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(layout);
		setColorManger(new ColorManager());
		factory.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(
						ComponentManager.PROPERTY_STRUCTURAL_CHANGE)) {
					TreeView.this.layout.treeStructureChanged();
					log.debug("Structural change : doing relayout");
					//Korilog commented
					//animate = true;
					doLayout();
				}
				// / implicit PROPERTY_VISUAL_CHANGE
				setForceBufferRefresh(true);
				parent.getContent().setTreeChanged(true);
				repaint();
			}
		});
		nodeRenderer = new DefaultNodeRenderer();
		edgeRenderer = new RectangleEdgeRenderer();
		setSelectionManager(new DefaultSelectionManager());

		/*
		 * enables mouse draged movement of the scrollpane, i.e. while selecting
		 * nodes
		 */
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
				scrollRectToVisible(r);
			}
		});
	}
	
	/**
	 * @see stree.gui.powerPane.RegistrableComponent#getComponent()
	 */
	public JComponent getComponent() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(super.getComponent(), 
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setFocusable(false);
		}
		return scrollPane;
	}
	public void enableAnimation(boolean animate){
		this.animate = animate;
	}
	/**
	 * Sets the TreeLayouter. the panel is set as teh layouts panel and new
	 * drawingInformations are initialized in the nodeComponents.
	 * 
	 * @param manager
	 */
	public void setLayout(TreeLayouter manager) {
		if (this.layout == manager) {
			return;
		}
		this.layout = manager;
		this.layout.setPanel(this);

		super.setLayout(null);
		this.fireLayoutChangedEvent(this, layout);
		layoutChanged(this.layout, true, false);
	}

	/**
	 * Adds a LayoutChangedListener.
	 * 
	 * @param aListener
	 */
	public void addLayoutChangedListener(LayoutChangedListener aListener) {
		listenerList.add(LayoutChangedListener.class, aListener);
	}

	/**
	 * Removes a LayoutChangeListener
	 * 
	 * @param aListener
	 */
	public void removeLayoutChangedListener(LayoutChangedListener aListener) {
		listenerList.remove(LayoutChangedListener.class, aListener);
	}

	/**
	 * Fires a LayoutChange Event.
	 * 
	 * @param panel
	 * @param layout
	 */
	public void fireLayoutChangedEvent(TreeView panel, TreeLayouter layout) {
		Object[] listeners = listenerList.getListenerList();
		LayoutChangedEvent event = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == LayoutChangedListener.class) {
				if (event == null) {
					event = new LayoutChangedEvent(this, this, layout);
				}
				((LayoutChangedListener) listeners[i + 1]).layoutChanged(event);
			}
		}
	}

	/**
	 * Returns the tree.
	 * 
	 * @return Returns the tree.
	 */
	public Tree getTree() {
		return tree;
	}

	/**
	 * Sets the Tree.
	 * 
	 * @param tree
	 *            The tree to set.
	 */
	public void setTree(Tree tree) {
		if (this.tree != tree) {
			this.tree = tree;
			componentManager.clear();
			if (tree != null) {
				if (layout == null)
					setLayout(new DendogramLayout());
				else
					layout.setTree(tree);
				// layout.treeStructureChanged();
				revalidate();
			}
		}
	}

	/**
	 * Set rectangular scaling factor, this is scaleX = scaleY = scale
	 * 
	 * @param scale
	 */
	public void setScaling(double scalex, double scaley) {
		setScaling(scalex, scaley, null);
	}

	/**
	 * sets scaling to 1.0
	 * 
	 * @param b
	 */
	public void setFullScreen(boolean b) {
		setScaling(1.0, 1.0, new Point(0, 0));
	}

	public void setScaling(double scalex, double scaley, Point newPos) {
		if (scalex < 1.0)
			scalex = 1.0;// 0.0000001;
		if (scaley < 1.0)
			scaley = 1.0;// 0.0000001;

		if (scalex == scaleX && scaley == scaleY)
			return;

		this.scaleX = scalex;
		this.scaleY = scaley;

		if (newPos == null) {
			Rectangle v = getVisibleRect();
			double ratio_x = v.getCenterX() / getWidth();
			double ratio_y = v.getCenterY() / getHeight();
			int nx = (int) ((v.width * scaleX * ratio_x) - (v.width / 2.0));
			int ny = (int) ((v.height * scaleY * ratio_y) - (v.height / 2.0));
			newPos = new Point(nx, ny);
		}

		Rectangle vis = getVisibleRect();
		int w = (int) (vis.width * scaleX);
		int h = (int) (vis.height * scaleY);

		if (scaleX == 1.0 && scaleY == 1.0) {
			w = 0;
			h = 0;
			setPreferredSize(new Dimension(0, 0));
			revalidate();
		} else {
			setPreferredSize(new Dimension(w, h));
			setSize(w, h);
		}
		setViewPosition(newPos);

	}

	public Point getViewPosition() {
		if (scrollPane != null) {
			return scrollPane.getViewport().getViewPosition();
		}
		return new Point(0, 0);
	}

	public void setViewPosition(Point newPos) {
		if (scrollPane != null) {
			scrollPane.getViewport().setViewPosition(newPos);
		}
	}

	/**
	 * @return Returns the nodeManager.
	 */
	public ComponentManager getComponentManager() {
		return componentManager;
	}

	/**
	 * Wrapps around the nodeManger and returns the compoennt for a given node.
	 * 
	 * @param node
	 * @return nodeComponent the component
	 */
	public NodeComponent getNodesComponent(TreeNode node) {
		return componentManager.getNodesComponent(node);
	}

	protected void renderBackground(Graphics g) {
//		/*
//		 * make shure that the background is rendererd appropriatly. this is
//		 * basically done to avoid conflicts with exporting the rendered image.
//		 * Normaly, this mehtod is called by the exporter, but, as
//		 * paintComponent is not called, drawing the background has to be
//		 * handled manualy and we do not want an extra method for export
//		 * handling at the moment.
//		 */
		Rectangle r = g.getClipBounds();
		if (r == null) {
			r = getVisibleRect();
		}
		g.setColor(colorManager.getColor(Colors.background));
		((Graphics2D) g).fill(r);

	}

	public void renderView(Graphics g) {
		Rectangle r = g.getClipBounds();
		if (r == null) {
			r = getVisibleRect();
		}

//		/*
//		 * make shure that the background is rendererd appropriatly. this is
//		 * basically done to avoid conflicts with exporting the rendered image.
//		 * Normaly, this mehtod is called by the exporter, but, as
//		 * paintComponent is not called, drawing the background has to be
//		 * handled manualy and we do not want an extra method for export
//		 * handling at the moment.
//		 */
//		g.setColor(colorManager.getColor(Colors.background));
//		((Graphics2D) g).fill(r);

		/*
		 * now render the tree.
		 */
		renderTree(g, r);
	}

	public void renderTree(Graphics g, Rectangle clip) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setClip(clip.x, clip.y, clip.width, clip.height);		
		if (tree != null) {
			TreeNode root = (TreeNode) tree.getRoot();
			NodeComponent rn = componentManager.getNodesComponent(root);
			renderingtraversal(rn, clip, g2d);
		}
	}

	private void renderingtraversal(NodeComponent n, Rectangle clip,
			Graphics2D g2d) {
		boolean cutSubtree = n.getSubtreeShape().getBounds2D().getHeight() < edgeCutThreshold
				|| n.getSubtreeShape().getBounds2D().getWidth() < edgeCutThreshold;
		n.setFraction(fraction);
		if (cutSubtree || !n.isVisible() || n.isCollapsed()) {
			if (cutSubtree) {
				NodeComponent d = n.getDeepestSuccessor();
				d.setFraction(fraction);
				edgeRenderer.renderEdge(n, d, n.getX(), n.getY(), d.getX(), d
						.getY(), n.getAngle(), d.getAngle(), this, g2d,
						colorManager.getColor(Colors.edgeColor, d));
				// edgeRenderer.renderEdge(n, d,n.getX(fraction),
				// n.getY(fraction), d.getX(fraction),
				// d.getY(fraction),n.getAngle(fraction), d.getAngle(fraction),
				// this, g2d, Color.BLUE);
			}
		} else {
			/*
			 * now check two things: 1. do we have th render the edged top the
			 * children ? 2. do we have to traverse the children ?
			 */
			for (NodeComponent c : n.children()) {
				c.setFraction(fraction);
				/*
				 * 1. check the edge
				 */
				edgeRenderer.renderEdge(n, c, n.getX(), n.getY(), c.getX(), c
						.getY(), n.getAngle(), c.getAngle(), this, g2d,
						colorManager.getColor(Colors.edgeColor, c));

				/*
				 * 2. do we have to traverse the subtree ?
				 */
				if (c.getSubtreeShape().intersects(clip)
						|| c.getSubtreeShape().getBounds2D().getHeight() == 0.0) {
					renderingtraversal(c, clip, g2d);
				}
			}
		}
		/*
		 * the node itself is visible, so render it this is done last, so nodes
		 * overlay edges
		 */
		if (n.getBoundingShape().intersects(clip)) {
			nodeRenderer.renderNode(n, n.getX(), n.getY(), n.getWidth(), n
					.getHeight(), n.getAngle(), this, g2d);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		TreeView clone = new TreeView(tree, (TreeLayouter) layout.clone());
		clone.setScaling(getScaleX(), getScaleY());
		return clone;
	}

	public void doLayout() {
		// super.doLayout();
		layout.layoutContainer(this);

		if (animate) {
			fraction = 0f;
			animating = true;
			if (animator.isRunning()) {
				animator.stop();
			}
			animator.start();
			animate = false;
		} else {
			fraction = 1f;
			ncFinder.prepareUpdate();
		}
	}

	/**
	 * Return a list of object that intersect the given rectangle
	 * 
	 * @param r
	 *            the search are
	 * @return Objects in the search area
	 */
	public Object[] searchNodeComponents(Rectangle r) {
		return ncFinder.searchNodeComponents(r);
	}

	/**
	 * Set the colormanager
	 * 
	 * @param colorManager
	 */
	public void setColorManger(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	/**
	 * Returns the color manager
	 * 
	 * @return
	 */
	public ColorManager getColorManager() {
		return colorManager;
	}

	/**
	 * Returns the background color bound the the color manager
	 */
	public Color getBackground() {
		if (colorManager == null)
			return super.getBackground();
		else {
			return colorManager.getColor(Colors.background);
		}
	}

	/**
	 * Returns the foreground color bound the the ColorManager
	 */
	public Color getForeground() {
		if (colorManager == null)
			return super.getForeground();
		else {
			return colorManager.getColor(Colors.foreground);
		}
	}

	/**
	 * @return the scaleX
	 */
	public double getScaleX() {
		return scaleX;
	}

	/**
	 * @param scaleX
	 *            the scaleX to set
	 */
	public void setScaleX(double scaleX) {
		setScaling(scaleX, scaleY);
	}

	/**
	 * @return the scaleY
	 */
	public double getScaleY() {
		return scaleY;
	}

	/**
	 * @param scaleY
	 *            the scaleY to set
	 */
	public void setScaleY(double scaleY) {
		setScaling(scaleX, scaleY);
	}

	/**
	 * @return the edgeRenderer
	 */
	public EdgeRenderer getEdgeRenderer() {
		return edgeRenderer;
	}

	/**
	 * @param edgeRenderer
	 *            the edgeRenderer to set
	 */
	public void setEdgeRenderer(EdgeRenderer edgeRenderer) {
		if (this.edgeRenderer != edgeRenderer) {
			EdgeRenderer old = this.edgeRenderer;
			this.edgeRenderer = edgeRenderer;
			firePropertyChange("edgeRenderer", old, this.edgeRenderer);
			repaint();
		}
	}

	/**
	 * @return the nodeRenderer
	 */
	public NodeRenderer getNodeRenderer() {
		return nodeRenderer;
	}

	/**
	 * @param nodeRenderer
	 *            the nodeRenderer to set
	 */
	public void setNodeRenderer(NodeRenderer nodeRenderer) {
		this.nodeRenderer = nodeRenderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epos.ui.view.ppane.AbstractView#setParentPane(epos.ui.view.ppane.PowerPane)
	 */
	@Override
	public void setParentPane(PowerPane<TreeView, TreeContent> parent) {
		super.setParentPane(parent);
		// get the tree from the content
		if (parent!=null)//Korilog: fix a bug when this method is called from PowerPane.setView()
			setTree(parent.getContent().getTree());
	}

	/**
	 * @return the selectionManager
	 */
	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

	/**
	 * @param selectionManager
	 *            the selectionManager to set
	 */
	public void setSelectionManager(SelectionManager selectionManager) {
		if (this.selectionManager != selectionManager) {
			if (this.selectionManager != null) {
				this.selectionManager
						.removeSelectionListener(selectionListener);
			}
			this.selectionManager = selectionManager;
			this.selectionManager.addSelectionListener(selectionListener);
			this.selectionManager.setPanel(this);
		}
	}

	public Dimension getViewSize() {
		return scrollPane.getViewport().getViewSize();
	}

	public Rectangle getViewRect() {
		return scrollPane.getViewport().getViewRect();
	}

	/**
	 * Search for the deepest subtree that contains the given rectngle. Starts
	 * searching at given parent. FIXME: improve this !
	 * 
	 * @param r
	 * @return
	 */
	public NodeComponent getRootForRectangle(NodeComponent parent, Rectangle r) {
		if (r == null)
			return null;
		if (parent == null)
			return null;
		if (parent.getSubtreeShape().contains(r)) {
			for (NodeComponent child : parent.children()) {
				if (child.getSubtreeShape().contains(r)) {
					return getRootForRectangle(child, r);
				}
			}
		}
		return parent;
	}

	/**
	 * Calls {@link #getRootForRectangle(NodeComponent, Rectangle)} with the
	 * trees rot as starting point.
	 * 
	 * @param r
	 * @return
	 */
	public NodeComponent getRootForRectangle(Rectangle r) {
		if (tree == null)
			return null;
		return getRootForRectangle(componentManager
				.getNodesComponent((TreeNode) tree.getRoot()), r);
	}

	/**
	 * Returns the tree layout
	 * 
	 * @return
	 */
	public TreeLayouter getTreeLayout() {
		return layout;
	}

	/**
	 * propagate a layout change
	 * 
	 * @param layout
	 */
	public void layoutChanged(TreeLayouter layout) {
		layoutChanged(layout, true);
	}

	public void layoutChanged(TreeLayouter layout, boolean structureChange) {
		layoutChanged(layout, structureChange, false);
	}

	public void layoutChanged(TreeLayouter layout, boolean structureChange,
			boolean animated) {
		if (structureChange) {
			layout.treeStructureChanged();
		}
		//Korilog added
		boolean oldAnimate = animate;
		animate = animated;

		Class edgeRendererClass = layout.getDefaultEdgeRenderer();
		EdgeRenderer r = null;
		for (EdgeRenderer renderer : availableEdgeRenderer) {
			if (renderer.getClass().equals(edgeRendererClass)) {
				r = renderer;
				break;
			}
		}
		if (r != null) {
			setEdgeRenderer(r);
		}
		revalidate();
		repaint();

		fireLayoutChangedEvent(this, layout);
		//Korilog added
		animate = oldAnimate;
	}

	class Mover implements TimingTarget {

		public void begin() {
			fraction = 0f;
			setAnimating(true);
		}

		public void end() {
			fraction = 1f;
			ncFinder.prepareUpdate();
			setAnimating(false);
		}

		public void repeat() {
		}

		public void timingEvent(float f) {
			fraction = f;
			repaint();
		}

	}

	public double getEdgeCutThreshold() {
		return edgeCutThreshold;
	}

	public void setEdgeCutThreshold(double edgeCutThreshold) {
		this.edgeCutThreshold = edgeCutThreshold;
	}

	/**
	 * take a default border + node sizes dependent onn the view size and
	 * scaling
	 */
	public Insets getInsets() {
		if(insets == null){
			insets = new Insets(20, 20, 20, 20);
		}
		if (1 == 1)
			return insets;

		/*
		 * first find longest node depending on node label
		 */
		NodeComponent largest = getNodesComponent((TreeNode) tree.getRoot());
		int leaves = 0;
		for (NodeComponent n : largest.depthFirstIterator()) {
			if (n.getNode().isLeaf() && n.isVisible()) {
				if ((largest == null || largest.getLabel() == null)
						|| (n.getLabel() != null && n.getLabel().length() > largest
								.getLabel().length())) {
					largest = n;
				}
				leaves++;
			}
		}
		double node_height = (getHeight() - insets.top - insets.bottom)
				/ (double) leaves;
		if (largest != null
				&& node_height + 0.5 >= getComponentManager()
						.getMinimumFontSize()) {
			Font f = getComponentManager().getLabelFont(largest).deriveFont(
					(float) node_height);
			if (largest.getLabel() != null) {
				insets.right += SwingUtilities.computeStringWidth(
						getFontMetrics(f), largest.getLabel());
			}
			insets.top += (int) (f.getSize2D() / 2.0);
			insets.bottom += (int) (f.getSize2D() / 2.0);
		}

		return insets;
	}

	public ArrayList<EdgeRenderer> getAvailableEdgeRenderer() {
		return availableEdgeRenderer;
	}

	/*public void setBackendAvailable(boolean backend) {
		if (backend && parent.getBackend() != null) {
			getColorManager().updateFromBackend(parent.getBackend());
			componentManager.setBackend(parent.getBackend(), parent.getContent().getTree());
			
			// read config from backend
			Property o = getProperties().getProperty(PROPERTY_INSETS);
			if(o != null){
				insets = (Insets) o.getValue();
			}
			if(layout != null){
				layout.treeStructureChanged();
				revalidate();
				repaint();
			}

		}
	}*/

	public boolean isAnimating() {
		return animating;
	}

	public void setAnimating(boolean animating) {
		if(this.animating != animating){
			this.animating = animating;
			firePropertyChange("animating", !this.animating, this.animating);
		}
		
		
	}

	public void setInsets(Insets insets) {
		this.insets = insets;
		/*if( parent.getBackend() != null){
			// store insets		
			getProperties().set(PROPERTY_INSETS, insets);			
		}*/
		layout.treeStructureChanged();
		revalidate();
		repaint();
	}

	protected epos.qnode.properties.PropertySet getProperties() {
		/*if(properties == null && parent.getBackend() != null){
			properties = parent.getBackend().getPropertySet(PROPERTY_KEY);
			if(properties == null){
				properties = new epos.qnode.properties.PropertySet(PROPERTY_KEY);
				parent.getBackend().addPropertySet(properties);
			}
		}else*/ if(properties == null){
			properties = new epos.qnode.properties.PropertySet();
		}
		return properties;
	}

}
