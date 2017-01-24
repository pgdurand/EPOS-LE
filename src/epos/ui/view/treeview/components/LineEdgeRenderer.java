package epos.ui.view.treeview.components;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import epos.ui.util.StringUtils;
import epos.ui.view.treeview.TreeView;

public 	class LineEdgeRenderer extends AbstractEdgeRenderer{
	@Override
	protected void drawEdge(int x1, int y1, int x2, int y2, TreeView view, Graphics2D g) {
		if(transformer == null){
			g.drawLine(x1,y1,x2,y2);
		}else{
            GeneralPath p = new GeneralPath();
            double dist = Point2D.distance(x1,y1, x2,y2);
            
            float split = (float) (dist/10f);
            float sy = (y2-y1) / split;
            float sx = (x2-x1) / split;
            
            p.moveTo(x1,y1);
            for(float i=0f;i<split; i++)
                p.lineTo(x1 + (sx*i), y1 + (sy*i) );                
            p.lineTo(x2,y2);                
            g.draw(transformer.transform(p));
		}
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
	
	public String toString() {return "Lines";}
}
