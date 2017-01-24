package epos.ui.view.treeview.components;

import java.awt.Color;
import java.awt.Graphics2D;

import epos.ui.view.treeview.TreeView;

public interface EdgeRenderer {
	public void renderEdge(NodeComponent parent, NodeComponent child, TreeView view, Graphics2D g, Color c);
	public void renderEdge(NodeComponent parent, NodeComponent child, int px, int py, int cx, int cy, double pa, double ca, TreeView view, Graphics2D g, Color c);
	public void setTransformer(ShapeTransformer f);
	public ShapeTransformer getTransformer();
}
