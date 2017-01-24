package epos.ui.view.treeview.layouts;

import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import epos.model.tree.TreeNode;
import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.components.NodeComponent;
import epos.ui.view.treeview.components.PolarEdgeRenderer;

public class CircularLayout extends DendogramLayout{
	/**
	 * the available angel space
	 */
	protected double maximalAngle = Math.PI*2.0;
	protected double rotation = 0.0;
	
	
	public CircularLayout(TreeView treePanel) {
		super(treePanel);
		setDefaultEdgeRenderer(PolarEdgeRenderer.class);
	}

	public CircularLayout() {
		super();
		setDefaultEdgeRenderer(PolarEdgeRenderer.class);
	}
	
	public void layoutTree(int width, int height, int x, int y) {
		if (width < 0 || height < 0)
			return;
		if (params.max_width_leave == null)
			params.init();
		if(relayout){
			oneShotlayout();
			relayout = false;
		}
		
		/*
		 * use this to keep the 1-1 aspect ratio of width and height
		 * an elliptical layout looks strange ;)
		 */
		int way = (int) (Math.min(width, height) / 2.0);				
		int w2 = (int) (width/2.0);
		int h2 = (int) (height/2.0);
		Rectangle2D.Double bounds = new Rectangle2D.Double();
		for (NodeComponent n : treePanel.getComponentManager().getNodesComponent( (TreeNode) tree.getRoot() ).depthFirstIterator()  ) {
				
			/*
			 * move coords from cartesian to polar-cartesian. treat the relative x as radius and the relative y
			 * of a node as the relative angle
			 */			
			double radius = 0;
			if(tree.getRoot() != n.getNode()){
				radius = n.getRelativeBounds().x + innerRadius;
				if(radius > 1.0) radius = 1.0;				
			}else{
				radius = n.getRelativeBounds().x;
			}
			double rel_angle = n.getRelativeBounds().y;
			
			/*
			 * we multiply with width/2 and height /2
			 */
			double nx = (radius * Math.cos( (rel_angle * maximalAngle) -rotation  ) * way);
			double ny = (radius * Math.sin( (rel_angle * maximalAngle)  -rotation ) * way);
			
			n.setAngle((rel_angle * maximalAngle)-rotation);
			
			if(n.getNode().isLeaf() || n.isCollapsed()) {
			bounds.setFrame(
					(w2 + x + nx),
				 	(h2 + y + ny), 
				    leaveLabaleSpace,
				    (2*n.getRelativeBounds().height) * h2);
			}else {
				bounds.setFrame(
						(w2 + x + nx),
					 	(h2 + y + ny),
					    n.getRelativeBounds().width * w2,
					    (2*n.getRelativeBounds().height) * h2);			
			}
			n.setBounds((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height);			
			AffineTransform rot = AffineTransform.getRotateInstance(n.getAngle(), bounds.getX(), bounds.getY());			
			n.setBoundingShape(rot.createTransformedShape(bounds));
			
			if(n.getNode().isLeaf() || n.isCollapsed()){
				n.setSubtreeShape(n.getBoundingShape());				
			}else{
				Rectangle2D b = n.getBoundingShape().getBounds2D();
				for (NodeComponent child : n.children()) {
					b.add(child.getSubtreeShape().getBounds2D());
				}
				n.setSubtreeShape(b);
			}
		}
		
	}
	
	public double getMaximalAngle() {
		return maximalAngle;
	}

	public void setMaximalAngle(double maximalAngle) {
		if(this.maximalAngle != maximalAngle){
			this.maximalAngle = maximalAngle;
			treePanel.layoutChanged(this);
		}
	}

	public void setRotation(double rotation) {
		if(this.rotation != rotation){
			this.rotation = rotation;			
			treePanel.layoutChanged(this, true);
		}
	}
	public double getRotation(){
		return rotation;
	}

	//Korilog added
	public int getEstimatedFontSize(){
		//estimate the size of the font as follows: perimeter of the circle divided
		//by the number of leaves
		Insets pinsets = getPanel().getInsets();
		int    perimeter = (int)((double)(getPanel().getHeight() - pinsets.top -pinsets.bottom) * Math.PI); 
		/*return Math.min( (getPanel().getHeight() - pinsets.top -pinsets.bottom)/4, perimeter/ leaves);*/
		return perimeter/ leaves;
	}
	//Korilog added
	public Insets getInsets() {
		Insets s = super.getInsets();
		//'2*...': see TreeLayouter.layoutContainer(): we do this to correctly
		//resize and center the layoutArea
		int r = 2*s.right;
		return new Insets(r,r,2*r,0);
	}


}
