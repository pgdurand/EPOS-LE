package epos.ui.view.treeview.components;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.QuadCurve2D;

import epos.ui.util.StringUtils;
import epos.ui.view.treeview.TreeView;

public 	class OpenCurveEdgeRenderer extends AbstractEdgeRenderer{
	@Override
	protected void drawEdge(int x1, int y1, int x2, int y2, TreeView view, Graphics2D g) {		
		g.draw(new QuadCurve2D.Double(x1,y1,x1,y2,x2,y2));		
	}

	@Override
	protected void drawLabel(int x1, int y1, int x2, int y2, TreeView view, Graphics2D g) {
		int s = Math.abs(y1-y2)-1;
		if( s< g.getFont().getSize()){
			if(s > 5){
				g.setFont(g.getFont().deriveFont((float)s));
			}else{
				return;
			}
		}
		int l = Math.abs(x2-x1);		
		label = StringUtils.clipStringIfNecessary(view, g.getFontMetrics(), label, l-4);
		int sl = StringUtils.stringWidth(view, g.getFontMetrics(), label);
		if(label.equals("...")) return;
		if (transformer==null){
			g.drawString(label,x1  + l/2 - sl/2, (int) (y1 < y2 ? y1 +s/2 : y1-s/2));
		}
		else{
			GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), label);
			Shape shape = transformer.transform(gv.getOutline(x1  + l/2 - sl/2, y1 < y2 ? y1 +s/2 : y1-s/2));
			g.fill(shape);
		}

	}
	
	public String toString() {return "Open Curve";}
}
