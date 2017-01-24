/*
 * Created on 18.03.2005
 */
package epos.ui.view.treeview.layouts;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.apache.log4j.Logger;

import epos.model.tree.TreeNode;
import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.components.NodeComponent;

/**
 * Layouts a Dendogram
 * 
 * @author Thasso
 */
public class DendogramLayout extends TreeLayouter {
	protected Logger log = Logger.getLogger(getClass());
	/**
	 * use constant distances ?
	 */
	private boolean constant_dist = false;

	/**
	 * expand the leaves to the right ?
	 */
	private boolean expand_leaves = true;
	/**
	 * used to force a relayout of the relative positions
	 */
	protected boolean relayout;
	/**
	 * internal collection of parameters used to compute the layout
	 */
	protected DendoParameter params = new DendoParameter();

	protected double innerRadius = 0d;
	
	protected Point2D viewCenter = new Point2D.Double(0.5,0.5);
	private double magnification = 0.5;
	private double ratio = 0.5;
		
	/**
	 * Create a new Dendogram Layout for a given treePanel.
	 * 
	 */
	public DendogramLayout(TreeView treePanel) {
		super(treePanel);
	}

	public DendogramLayout() {
		super();
	}

	public Dimension preferredLayoutSize(Container arg0) {
		params.init();
		// maximal distance from the root to a leave
		double max_dist = params.max_distance;
		// maximal depth of the tree
		int max_depth = params.max_depth;

		// pixel height for a leave
		int height_per_leave = treePanel.getComponentManager().getLabelFont().getSize();// height / (double)
									// params.getNumberOfLeaves();
		int height = height_per_leave * params.numberOfLeaves;

		// / THIS IS THE SPEED PROBLEM
		// the maximum width of a leave with respect to the labels
		int width = max_depth * 100;
		int max_leave_width = getMaxLeaveWidth(height_per_leave);
		int max_width = width + max_leave_width;
		log.debug("Prefered size " + max_width + " " + height);
		return new Dimension(max_width, height);
	}

	public Dimension getLargestNodeSize() {
		Rectangle2D r = params.max_width_leave.getBounds();
		return new Dimension((int) r.getWidth(), (int) r.getHeight());
	}

	public void oneShotlayout(){
		StepParameter p = new StepParameter();
		p.offsetX = 1.0 / params.max_depth;
		p.offsetY = 1.0 / params.numberOfLeaves;
		p.max_dist = 1.0 / params.max_distance;
		layoutStep(treePanel.getComponentManager().getNodesComponent((TreeNode)tree.getRoot()), p, 0);
	}
	
	protected void layoutStep(NodeComponent node, StepParameter p, double depth) {
		/*
		 * comput x
		 */
		if(constant_dist){
			node.getRelativeBounds().x =depth * p.offsetX; 
		}else if( node.getParent() != null){
			node.getRelativeBounds().x = node.getParent().getRelativeBounds().x + (p.max_dist * node.getNode().getDistanceToParent());
		}

		/*
		 * set height of this node
		 */		
		node.getRelativeBounds().height = p.offsetY;

		/*
		 * compute y and the subtree bounds
		 * also set the nodes width
		 * leaves and collapsed nodes get full width,
		 * others get width set to offset, which is the minimum available width
		 * in fixed case. in the other case we have to check the minimum x position of the nodes children
		 * and ise it as the the width indicator

		 */
		if(node.getNode().isLeaf() || node.isCollapsed()){
			if(expand_leaves && node.getNode().isLeaf()){
				node.getRelativeBounds().x = 1.0;
			}
			node.getRelativeBounds().y = p.offsetY * p.leaves_done;
			//node.getRelativeBounds().y = hyperbolicTransform(0.5, node.getRelativeBounds().y, null).getY(); 
			node.getRelativeBounds().width = relativeleaveLabaleSpace;//1.0 - ((depth-1) * p.offsetX);
			/*
			 * in case of leaves, subtree bounds are equal to the node bounds
			 */
			node.getSubtreeBounds().setFrame(node.getRelativeBounds());
			p.leaves_done++;
		}else{
			

			/*
			 * we have to find min max y bounds over all children
			 */
			double minY = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			double minX = Double.MAX_VALUE;
						
			/*
			 *while layouting the children, accumulate teh subtree bounds of this node.
			 *to do so, initialy set the subtreebounds to the upmost y position and size of 
			 *the relative bounds.  
			 */
			node.getSubtreeBounds().setFrame(node.getRelativeBounds());
			node.getSubtreeBounds().y = p.offsetY * p.leaves_done;
			
			for (NodeComponent c : node.children()) {				
				layoutStep(c, p, depth+1);		
				minY = Math.min(minY, c.getRelativeBounds().y);
				maxY = Math.max(maxY, c.getRelativeBounds().y);
				minX = Math.min(minX, c.getRelativeBounds().x);
				node.getSubtreeBounds().add(c.getSubtreeBounds());
			}
			/*
			 * now center this node between min and max
			 */
			node.getRelativeBounds().y = minY + ((maxY - minY)/2.0);
			
			/*
			 * set the nodes width 
			 */
			node.getRelativeBounds().width = minX - node.getRelativeBounds().x;
		}				
	}
	protected class StepParameter{
		double leaves_done = 0;		
		double offsetX = 0;
		double offsetY = 0;
		double max_dist = 0;
	}

	
	
	/**
	 * This does the positioning of the tree nodes. First the laeves are
	 * positioned and then we start a first run to do a bottom up Layout of the
	 * parents. If thes is done and we want distance based positioning, we do a
	 * second top down run.
	 * 
	 * @param width
	 * @param height
	 */

	
	public void layoutTree(int width, int height, int x, int y) {
		log.debug("Doing layout for " + width + " " + height + " " + x + " " + y); 
		if (width < 0 || height < 0)
			return;
		if (params.max_width_leave == null)
			params.init();
		if(relayout){
			oneShotlayout();
			relayout = false;
		}
		
		x = (int) (x + (width * innerRadius));
		width = (int) (width - (width * innerRadius));		
		for (Iterator iter = treePanel.getComponentManager().getNodeComponentsIterator(); iter.hasNext();) {
			NodeComponent n = (NodeComponent) iter.next();
			/*
			 * distingush between leaves and inner nodes. leaves always get
			 * the space compute beforhand (leaveLableSpace) 
			 * inner nodes get withd according to their relative width.
			 */
			if(n.getNode().isLeaf() || n.isCollapsed()) {
				n.setBounds((int) (x + Math.round((n.getRelativeBounds().x * width))),
						 (int) (y + Math.round((n.getRelativeBounds().y * height))) ,					 						 
						 (int) Math.round(leaveLabaleSpace),
						 (int) Math.round(n.getRelativeBounds().height * height));			
			}else {
				n.setBounds((int) (x + Math.round((n.getRelativeBounds().x * width))),
						 (int) (y + Math.round((n.getRelativeBounds().y * height))) ,					 						 
						 (int) Math.round(n.getRelativeBounds().width * width),
						 (int) Math.round(n.getRelativeBounds().height * height));			
			}
			n.setSubtreeShape(new Rectangle2D.Double(
				 x + (n.getSubtreeBounds().x * width),
				 y + (n.getSubtreeBounds().y * height) , 
				 n.getSubtreeBounds().width * width, 
				 n.getSubtreeBounds().height * height));
					
			
			/*
			 * reset the nodes angle
			 */
			n.setAngle(0);
		}
		
	}

	/**
	 * Helper method collection information about the tree Finds all leaves in
	 * the tree, the maximum depth and the maximum distance fro the root to a
	 * leave.
	 * 
	 * @return
	 */

	class DendoParameter {
		double max_distance = Double.MIN_VALUE;

		int max_depth = 0;

		int numberOfLeaves = 0;

		NodeComponent max_width_leave = null;

		double max_width = 0;

		public void init() {
			max_distance = Double.MIN_VALUE;
			max_depth = 0;
			numberOfLeaves = 0;
			max_width_leave = null;			
			
			traverse(treePanel.getNodesComponent((TreeNode) tree.getRoot()));			
		}

		private void traverse(NodeComponent nc) {
			max_depth = (max_depth < nc.getNode().getLevel()) ? nc.getNode()
					.getLevel() : max_depth;

			if (nc.getNode().isLeaf() || nc.isCollapsed()) {
				numberOfLeaves++;
				Dimension dim = getPrefferedSize(nc);
				if (max_width_leave == null || max_width < dim.width) {
					max_width_leave = nc;
					max_width = dim.getWidth();
				}
				double dist = 0;
				for (TreeNode node = nc.getNode(); node.getParent() != null; node = node
						.getParent()) {
					if (node.getDistanceToParent() != -1)
						dist += node.getDistanceToParent();
				}
				if (max_distance < dist)
					max_distance = dist;
			} else {
				for (NodeComponent c : nc.children()) {
					traverse(c);
				}
			}
		}
	}

	private int getMaxLeaveWidth(double height) {
		if (params == null)
			params = new DendoParameter();
		if (params.max_width_leave == null)
			params.init();
		return getBounds(params.max_width_leave,height).width;
	}

	/**
	 * Switches between a constant layout and a layouting the tree with respect
	 * to the edge weights.
	 * 
	 * @param b
	 */
	public void setUseDistances(boolean b) {
		if(b != constant_dist){
			constant_dist = b;
			treePanel.layoutChanged(this, true, true);
		}
	}

	/**
	 * Returns true if the is layouted with respect to the edge weights.
	 * 
	 * @return true if distance are used to layout the tree
	 */
	public boolean isUseDistances() {
		return constant_dist;
	}


	/**
	 * @return Returns the expand_leaves.
	 */
	public boolean isExpandLeaves() {
		return expand_leaves;
	}

	/**
	 * @param expand_leaves
	 *            The expand_leaves to set.
	 */
	public void setExpandLeaves(boolean expand_leaves) {
		if (expand_leaves != this.expand_leaves) {
			this.expand_leaves = expand_leaves;
			treePanel.layoutChanged(this, true, true);
		}
		
	}

	public Object clone() {
		DendogramLayout l = new DendogramLayout();
		l.setExpandLeaves(isExpandLeaves());
		l.setUseDistances(isUseDistances());
		return l;
	}

	public void treeStructureChanged() {
		super.treeStructureChanged();
		if(params != null){
			params.max_width_leave = null;			
		}
		relayout = true;
	}
	
	public double getInnerRadius() {
		return innerRadius;
	}

	public void setInnerRadius(double innerRadius) {
		if(innerRadius >= 0.0 && innerRadius <= 1.0 && innerRadius != this.innerRadius){
			this.innerRadius = innerRadius;
			treePanel.layoutChanged(this, true);
		}
	}
	
	
	public Point2D hyperbolicTransform(double x, double y,
			Point2D returnPoint) {
		if (returnPoint == null)
			returnPoint = new Point2D.Double(x, y);

		//viewCenter = new Point2D.Double(0.5, 0.5);
		double viewRadius = 0.5;
		//double ratio = 0.5;
		//double magnification = 0.5;

		// calculate point from center
		double dx = x - viewCenter.getX();
		double dy = y - viewCenter.getY();

		// factor out ellipse
		dx *= ratio;
		Point2D pointFromCenter = new Point2D.Double(dx, dy);

		Point2D polar = cartesianToPolar(pointFromCenter.getX(), pointFromCenter.getY());
		double theta = polar.getX();
		double radius = polar.getY();
		if (radius > viewRadius) {
			returnPoint.setLocation(x, y);
			return returnPoint;
		}

		double mag = Math.tan(Math.PI / 2 * magnification);
		radius *= mag;

		radius = Math.min(radius, viewRadius);
		radius /= viewRadius;
		radius *= Math.PI / 2;
		radius = Math.abs(Math.atan(radius));
		radius *= viewRadius;
		Point2D projectedPoint = new Point2D.Double();
		polarToCartesian(theta, radius, projectedPoint);
		projectedPoint.setLocation(projectedPoint.getX() / ratio,
				projectedPoint.getY());
		returnPoint.setLocation(projectedPoint.getX() + viewCenter.getX(),
				projectedPoint.getY() + viewCenter.getY());
		return returnPoint;
		// Point2D translatedBack = new
		// Point2D.Double(projectedPoint.getX()+viewCenter.getX(),
		// projectedPoint.getY()+viewCenter.getY());
		// return translatedBack;
	}
	
	/**
	 * Returns the result of converting <code>(x, y)</code> to polar
	 * coordinates.
	 */
	protected Point2D cartesianToPolar(double x, double y) {
		double theta = Math.atan2(y, x);
		double radius = Math.sqrt(x * x + y * y);
		return new Point2D.Double(theta, radius);
	}
	
	/**
	 * Returns the result of converting <code>(theta, radius)</code> to
	 * Cartesian coordinates.
	 */
	protected Point2D polarToCartesian(double theta, double radius,
			Point2D returnPoint) {
		returnPoint.setLocation(radius * Math.cos(theta), radius
				* Math.sin(theta));
		return returnPoint;
	}

	public Point2D getViewCenter() {
		return viewCenter;
	}

	public void setViewCenter(Point2D viewCenter) {
		this.viewCenter = viewCenter;
	}

	public void setMagnification(double magnification) {
		this.magnification = magnification; 
	}

	public double getMagnification() {
		return magnification;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	//Korilog added
	public Insets getInsets() {
		Insets s = super.getInsets();
		int r = s.right;
		//'r/2': see TreeLayouter.layoutContainer(): we do this to correctly
		//resize and center the layoutArea
		return new Insets(0,r/2,0,r/2);
	}
	//Korilog added
	public int getEstimatedFontSize(){
		//estimate the size of the font as follows: height of the viewer frame divided
		//by the number of leaves
		Insets pinsets = getPanel().getInsets();
		return (getPanel().getHeight() - pinsets.top -pinsets.bottom)/ leaves;
	}

}