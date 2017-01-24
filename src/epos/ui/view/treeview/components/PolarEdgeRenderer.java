package epos.ui.view.treeview.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import epos.model.tree.TreeNode;
import epos.ui.util.StringUtils;
import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.ColorStyle.Colors;

public class PolarEdgeRenderer implements EdgeRenderer{
	protected Arc2D.Double arc = new Arc2D.Double();
	protected ShapeTransformer transformer; 
	protected NumberFormat formatter = DecimalFormat.getNumberInstance(Locale.ENGLISH);
	{		
		formatter.setMaximumFractionDigits(3);
	}
	protected String label = "";

	
	public void renderEdge(NodeComponent parent, NodeComponent child, TreeView view, Graphics2D g, Color color) {
		renderEdge(parent, child, parent.getX(), parent.getY(), child.getX(), child.getY(), parent.getAngle(), child.getAngle(), view, g, color);
	}
	public void renderEdge(NodeComponent p, NodeComponent c, int px, int py, int cx, int cy, double pa, double ca, TreeView view, Graphics2D g, Color color) {
		Color oldColor = g.getColor();
		Stroke oldStroke = g.getStroke();
		
		if(c == null){
			g.setColor(view.getColorManager().getColor(Colors.edgeColor, c));
		}else{
			g.setColor(color);
		}
		
		g.setStroke(view.getComponentManager().getEdgeStroke(c));
		
		if(view.getSelectionManager().isNodeSelected(c)){
			g.setColor(view.getColorManager().getColor(Colors.edgeSelection, c));
			g.setStroke(new BasicStroke(2f));
		}

		
		
		Point mid = view.getComponentManager().getNodesComponent((TreeNode) view.getTree().getRoot()).getLocation();
		double r = mid.distance(px,py);

		double ex = r * Math.cos(ca);
		double ey = r * Math.sin(ca);		

		pa = (2.0*Math.PI) - pa;
		ca = (2.0*Math.PI) - ca;
		
		arc.setArc(mid.x-r,mid.y-r,2*r, 2*r, Math.toDegrees(pa),-Math.toDegrees(pa-ca), Arc2D.OPEN);
		
		if(transformer == null){
			g.draw(arc);
			g.drawLine( (int)(mid.x + ex + 0.5), (int)(mid.y + ey + 0.5), cx, cy);
		}else{
            GeneralPath path = new GeneralPath();
            float x1 = (float) (mid.x + ex + 0.5);
            float y1 = (float) (mid.y + ey + 0.5);
            float x2 = (float) cx;
            float y2 = (float) cy;
            
            double dist = Point2D.distance(x1,y1, x2,y2);
            
            float split = (float) (dist/10f);
            float sy = (y2-y1) / split;
            float sx = (x2-x1) / split;
            
            path.moveTo(x1,y1);
            for(float i=0f;i<split; i++)
            	path.lineTo(x1 + (sx *i), y1 + (sy*i) );                
            path.lineTo(x2,y2);
            g.draw(transformer.transform(arc));
            g.draw(transformer.transform(path));

		}
		if(view.getComponentManager().isDrawEdgeLabels()){
			label = formatter.format(c.getNode().getDistanceToParent());
			g.setFont(Font.decode("Arial-12"));
			drawLabel( (int)(mid.x + ex + 0.5), (int)(mid.y + ey + 0.5), cx, cy, view, g);
		}

		// draw label		
		g.setColor(oldColor);
		g.setStroke(oldStroke);

	}
	
	protected void drawLabel(int x1, int y1, int x2, int y2, TreeView view, Graphics2D g){
		int s = (int) Point.distance(x1, y1, x2, y2);
		if( s< g.getFont().getSize()){
			if(s > 5){
				g.setFont(g.getFont().deriveFont((float)s));
			}else{
				return;
			}
		}
		int l = s;//Math.abs(x2-x1);		
		label = StringUtils.clipStringIfNecessary(view, g.getFontMetrics(), label, l-4);
		//int sl = StringUtils.stringWidth(view, g.getFontMetrics(), label);
		if(label.equals("...")) return;
		
		if (transformer==null){
			g.drawString(label,x1, y1);
		}
		else{
			GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), label);
			Shape shape = transformer.transform(gv.getOutline(x1, y1));
			g.fill(shape);
		}
	}
	public ShapeTransformer getTransformer() {
		return transformer;
	}
	public void setTransformer(ShapeTransformer transformer) {
		this.transformer = transformer;
	}

	
	public String toString() {return "Polar Edges";}
}
