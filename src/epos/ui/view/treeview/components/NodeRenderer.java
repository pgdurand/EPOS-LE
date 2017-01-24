package epos.ui.view.treeview.components;

import java.awt.Graphics;
import java.awt.Graphics2D;

import epos.ui.view.treeview.TreeView;

public interface NodeRenderer {
	public void renderNode(NodeComponent node, TreeView view, Graphics2D g); 
	public void renderNode(NodeComponent node, int x, int y, int width, int height, double angle, TreeView view, Graphics2D g);
	public void setTransformer(ShapeTransformer transformer);
	public ShapeTransformer getTransformer();
	public void hightlightNodes(Object[] nodes, Graphics g); 
}
