package epos.ui.view.treeview.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.ColorStyle.Colors;

public abstract class AbstractEdgeRenderer implements EdgeRenderer{ 
	protected ShapeTransformer transformer;
	
	protected NumberFormat formatter = DecimalFormat.getNumberInstance(Locale.ENGLISH);
	{		
		formatter.setMaximumFractionDigits(4);
	}
	protected String label = "";
	public void renderEdge(NodeComponent parent, NodeComponent child, TreeView view, Graphics2D g, Color color) {
		renderEdge(parent, child, parent.getX(), parent.getY(), child.getX(), child.getY(), parent.getAngle(), child.getAngle(), view, g, color);
	}
	public void renderEdge(NodeComponent p, NodeComponent c, int px, int py, int cx, int cy,double pa, double ca,  TreeView view, Graphics2D g, Color color) {		
		
		Color oldColor = g.getColor();		
		Stroke oldStroke = g.getStroke();

		if(view.getSelectionManager().isNodeSelected(c)){
			
			g.setColor(view.getColorManager().getColor(Colors.edgeSelection, c));
			
			BasicStroke st = (BasicStroke) view.getComponentManager().getEdgeStroke(c);
			g.setStroke(new BasicStroke(st.getLineWidth()+2));
			//drawSelection(px, py, cx, cy, view, g);
			drawEdge(px,py,cx,cy, view, g);
		}

		
		if(c == null){
			g.setColor(view.getColorManager().getColor(Colors.edgeColor, c));
		}else{
			g.setColor(color);
		}			
		g.setStroke(view.getComponentManager().getEdgeStroke(c));
		drawEdge(px,py,cx,cy, view, g);
		
		if(view.getComponentManager().isDrawEdgeLabels()){
			label = formatter.format(c.getNode().getDistanceToParent());
			g.setFont(Font.decode("Arial-12"));
			drawLabel(px,py,cx,cy, view, g);
		}
		
		
		g.setColor(oldColor);
		g.setStroke(oldStroke);

	}

	
	protected abstract void drawEdge(int x1, int y1, int x2, int y2, TreeView view, Graphics2D g);
	protected abstract void drawLabel(int x1, int y1, int x2, int y2, TreeView view, Graphics2D g);
	protected void drawSelection(int x1, int y1, int x2, int y2, TreeView view, Graphics2D g){
		drawEdge(x1+1, y1+1, x2+1, y2+1, view, g);
		drawEdge(x1-1, y1-1, x2-1, y2-1, view, g);
	}
	
	public void setTransformer(ShapeTransformer transformer){
		this.transformer = transformer;
	}
	public ShapeTransformer getTransformer(){
		return transformer;
	}

}
