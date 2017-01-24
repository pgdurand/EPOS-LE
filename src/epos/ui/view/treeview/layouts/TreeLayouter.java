/*
 * Created on 18.01.2005
 */
package epos.ui.view.treeview.layouts;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.components.NodeComponent;
import epos.ui.view.treeview.components.RectangleEdgeRenderer;


/**
 * Abstract TreeLayouter implements the basic layout operation of a LayoutManager
 * and delegates to implementations of the layoutTree() method to do the layouting.
 * 
 * @author Thasso
 */
public abstract class TreeLayouter implements Cloneable{

	/**
	 * The tree to be layouted
	 */
	protected Tree tree;
	
	/**
	 * The TreePanel containing the tree
	 */
	protected TreeView treePanel;
		
	/**
	 * chache the size of the parent at last layout run
	 */
	protected Dimension lastParentDimension = null;
	
	protected Class defaultEdgeRenderer  = RectangleEdgeRenderer.class;

	protected NodeComponent largetsNode;

	protected int leaves;

	protected int leaveLabaleSpace;
	protected double relativeleaveLabaleSpace;
	
	public Class getDefaultEdgeRenderer() {
		return defaultEdgeRenderer;
	}

	public void setDefaultEdgeRenderer(Class defaultEdgeRenderer) {
		this.defaultEdgeRenderer = defaultEdgeRenderer;
	}

	public TreeLayouter(TreeView panel){
		this();
		setPanel(panel);
	}
	
	public TreeLayouter() {
		super();
	}

	/**
	 * This should do a full tree layout to layout the tree
	 * in the given space
	 *
	 */
    public abstract void layoutTree(int width, int height, int x, int y);
    
    /**
     * Delegates to layoutTree(width, height)
     * @param size
     */
    public void layoutTree(Dimension size, int x, int y){
    		layoutTree(size.width, size.height, x, y);
    }
    
    /**
     * Set the tree
     * 
     * @param tree the tree to layout
     */
    public void setTree(Tree tree){if(this.tree != tree){this.tree = tree; treeStructureChanged();}};
    
    /**
     * Returns the tree
     * 
     * @return tree the layouted tree
     */
    public Tree getTree(){return tree;};
    		
	/**
	 * Checks the size of the parent Container. If 
	 * the size changed and width and heitght > 0
	 * the size is chached and layoutTree is called with
	 * the parents insets taken into account.
	 * 
	 */
	public void layoutContainer(Container parent) {       
		if(tree == null)				
			return;
		if(lastParentDimension == null ||  ! lastParentDimension.equals(parent.getSize())){
			lastParentDimension = parent.getSize();
			Insets insets = parent.getInsets();		
			Insets labelSpace = getInsets();
			double newlabelSpace = (double)leaveLabaleSpace / (double)(parent.getWidth() - insets.left - insets.right);
			// Fixes #101 - if we have changed the leave space trigger a relayout
			// we have to reset lastParentDimension here, because it is nulled by the 
			// call to treeStructureChanged
			if(newlabelSpace != relativeleaveLabaleSpace){
				treeStructureChanged();
				lastParentDimension = parent.getSize();
			}
			relativeleaveLabaleSpace = newlabelSpace;
			Rectangle layoutArea = new Rectangle(
					insets.left, 
					insets.top, 
					lastParentDimension.width - insets.left - insets.right, 
					lastParentDimension.height - insets.top - insets.bottom );
			//move layoutArea to take into account label size
			layoutArea.x += labelSpace.left;
			layoutArea.y += labelSpace.top;
			layoutArea.width -= (labelSpace.left + labelSpace.right);
			layoutArea.height -= labelSpace.bottom;

			//Korilog added: put the layout in a square, and center it on the parent frame
			layoutArea.width = Math.min(layoutArea.width, layoutArea.height);
			layoutArea.height = layoutArea.width;
			layoutArea.x = lastParentDimension.width/2 - layoutArea.width/2;
			layoutArea.y = lastParentDimension.height/2 - layoutArea.height/2;
			//Korilog added: re-center Dendogram on the parent component
			layoutArea.x -= labelSpace.right;
			
			layoutTree(layoutArea.width, layoutArea.height, layoutArea.x, layoutArea.y);
		}
	}

	public void setPanel(TreeView panel) {
		this.treePanel = panel;
        if(panel != null)
            setTree(panel.getTree());
	}
    public TreeView getPanel(){
        return treePanel;
    }
	
	public abstract Object clone();
		       
	public void treeStructureChanged() {
		lastParentDimension = null;
		largetsNode = null;
	}
	
	public Insets getInsetsOld() {
		/*
		 * first find longest node depending on node label
		 */
		if(largetsNode == null){
			largetsNode = getPanel().getNodesComponent((TreeNode) tree.getRoot());
			leaves = 0;
			for (NodeComponent n : largetsNode.depthFirstIterator()) {
				if (n.getNode().isLeaf() && n.isVisible()) {
					if ((largetsNode == null || largetsNode.getLabel() == null)
							|| (n.getLabel() != null && n.getLabel().length() > largetsNode
									.getLabel().trim().length())) {
						largetsNode = n;
					}
					leaves++;
				}
			}
		}
		Insets pinsets = getPanel().getInsets();
		
		double node_height = (getPanel().getHeight() - pinsets.top -pinsets.bottom)/ (double) leaves;
		Insets insets = new Insets(0,0,0,0);
		if (largetsNode != null/*
				&& node_height + 0.5 >= getPanel().getComponentManager().getMinimumFontSize()*/) {
			float fontSize = (float) Math.min(node_height, getPanel().getComponentManager().getMaximumFontSize());
			Font f = getPanel().getComponentManager().getLabelFont(/*largetsNode*/).deriveFont(fontSize+2.f);
			getPanel().getComponentManager().setLabelFont(f);
			if (largetsNode.getLabel() != null) {
				insets.right = SwingUtilities.computeStringWidth(
						getPanel().getFontMetrics(f), largetsNode.getLabel().trim());
				leaveLabaleSpace=insets.right;
			}
			insets.top = (int) ((node_height/2.0));
		}
		return insets;
	}
	public Insets getInsets() {
		/*
		 * first find longest node depending on node label
		 */
		if(largetsNode == null){
			largetsNode = getPanel().getNodesComponent((TreeNode) tree.getRoot());
			leaves = 0;
			for (NodeComponent n : largetsNode.depthFirstIterator()) {
				if (n.getNode().isLeaf() && n.isVisible()) {
					if ((largetsNode == null || largetsNode.getLabel() == null)
							|| (n.getLabel() != null && n.getLabel().length() > largetsNode
									.getLabel().trim().length())) {
						largetsNode = n;
					}
					leaves++;
				}
			}
		}
		Insets pinsets = getPanel().getInsets(); 
		Insets insets = new Insets(0,0,0,0);
		int    curSize, estSize, area;

		//Korilog added:
		//by default, the most larger label cannot be greater than 1/4 of
		//the viewer height (unit is pixel)
		area = (getPanel().getHeight() - pinsets.top -pinsets.bottom)/4;
		
		//estimate the font size
		estSize = getEstimatedFontSize();

		//retrieve the current font
		Font f = getPanel().getComponentManager().getLabelFont();
		curSize = (int) getPanel().getComponentManager().getMaximumFontSize();
		//try to find the font that enables to put the larger label within
		//'area'
		while(curSize>1){
			f = f.deriveFont((float) curSize);
			insets.right = SwingUtilities.computeStringWidth(
					getPanel().getFontMetrics(f), largetsNode.getLabel().trim());
			leaveLabaleSpace=insets.right;
			if (insets.right<=area){
				break;
			}
			curSize -= 1;
		}
		//the best font size is: min(estSize, curSize)
		f = f.deriveFont((float) Math.min(estSize, curSize));
		//since the previous line can modify the font, we recompute the following
		insets.right = SwingUtilities.computeStringWidth(
				getPanel().getFontMetrics(f), largetsNode.getLabel().trim());
		leaveLabaleSpace=insets.right;
		//set the font here for reuse within DefaultNodeRenderer
		getPanel().getComponentManager().setLabelFont(f);
		return insets;
	}
	public abstract int getEstimatedFontSize();
	
	public Dimension getBounds(NodeComponent node, double height){
		int w = 5;
		if(node != null){
			String label = node.getLabel();
			if(label != null && treePanel != null && treePanel.getGraphics() != null){
				Font f = treePanel.getComponentManager().getLabelFont(node);
				w= SwingUtilities.computeStringWidth(treePanel.getGraphics().getFontMetrics(f), label);
			}
		}
		return new Dimension(w, (int) (2+height));
	}
	public Dimension getPrefferedSize(NodeComponent node){
		return getBounds(node, treePanel.getComponentManager().getLabelFont(node).getSize());
	}	
}
