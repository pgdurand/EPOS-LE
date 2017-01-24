package epos.ui.view.treeview.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.GlyphVector;

import epos.ui.util.StringUtils;
import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.ColorStyle.Colors;

public class DefaultNodeRenderer implements NodeRenderer{
	protected ShapeTransformer transformer;
	//Korilog added
	private Polygon _poly = new Polygon();
 
	public void renderNode(NodeComponent node, TreeView view, Graphics2D g) {
		renderNode(node, node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getAngle(), view, g);
	}

	public void renderNode(NodeComponent node, final int x, final int y, final int width, final int height, final double angle,  TreeView view, Graphics2D g) {
		Color oldColor = g.getColor();
		//g.setColor(Color.blue);
		//g.draw(node.getBoundingShape());

		if(angle!= 0){
			g.rotate(angle, x, y);
		}

		//Korilog added: draw little circle on each node
		g.setColor(Color.black);
		if (node.getNode().isLeaf()){
			_poly.reset();
			_poly.addPoint(x, y);
			_poly.addPoint(x+5, y-3);
			_poly.addPoint(x+5, y+3);
			_poly.addPoint(x, y);
			g.fillPolygon(_poly);
		}
		else{
			g.fillOval(x-2, y-2, 5, 5);
			
		}

		int cr = 0, decal = 0;
		if(node.isCollapsed()){
			g.setColor(view.getColorManager().getEdgeSelectionColor());
			cr = height > 12 ? 12 : height;
			g.fillPolygon(new int[]{x,x+cr,x+cr,x}, new int[]{y,y-(cr/2), y+(cr/2),y}, 4);
			g.setColor(view.getColorManager().getColor(Colors.nodeColor, node));
			g.drawPolygon(new int[]{x,x+cr,x+cr,x}, new int[]{y,y-(cr/2), y+(cr/2),y}, 4);
		}
		
		g.setColor(view.getColorManager().getColor(Colors.nodeColor, node));
		
		
		/*
		 * draw the label
		 */
		String s = node.getLabel();
		
	
		if(s != null && s.length() > 0 && node.getNode().isLeaf()){			
			g.setFont(view.getComponentManager().getLabelFont());
			if(view.getComponentManager().isClipNodeLabels()){
				/*
				 *  cut string if the label contains at least 3 characters
				 *  This solves Ticket #11 and makes sure that short labels 
				 *  are not replaces with "..." 
				 */					
				if(s.length() > 2){
					s = StringUtils.clipStringIfNecessary(null, g.getFontMetrics(), node.getLabel(), width-2+cr);
				}
			}
			int y2 = (int) (y + (g.getFont().getSize2D() / 2.0));
			/*Korilog: replace fontColor by nodeColor to apply a particular color on a node. Otherwise
			 * it's always black (default).*/
			Color clr = view.getColorManager().getColor(Colors.nodeColor, node);
			if (clr==null)
				clr = view.getColorManager().getColor(Colors.fontColor, node);
			g.setColor(clr);
			//Korilog: rotate label for circular layout so that they are human readable
			if (angle>Math.PI/2.0 && angle<3.0*Math.PI/2.0){
				g.rotate(Math.PI, x, y);
				decal = g.getFontMetrics().stringWidth(s)+17;
			}
			else{
				decal = 0;
			}
			//Korilog: position set to 'x+5' instead of 'x+2'
			if(transformer == null){
				g.drawString(s, x+8+cr-decal, y2-1);
			}else{
				GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), s);
				Shape shape = transformer.transform(gv.getOutline(x+8+cr-decal, y2-1));
				g.fill(shape);
			}
			if (angle>Math.PI/2.0 && angle<3.0*Math.PI/2.0){
				g.rotate(-Math.PI, x, y);
			}
		}
		if(node.getAngle() != 0){
			g.rotate(-angle, x, y);
		}
		g.setColor(oldColor);

	}

	public ShapeTransformer getTransformer() {
		return transformer;
	}

	public void setTransformer(ShapeTransformer transformer) {
		this.transformer = transformer;
	}

	public void hightlightNodes(Object[] nodes, Graphics g) {
		g.setColor(Color.BLUE);
		for (Object object : nodes) {
			NodeComponent c = (NodeComponent) object;
			g.drawArc(c.getX()-5, c.getY()-5, 10, 10, 0, 360);
		}
	}
	
}
