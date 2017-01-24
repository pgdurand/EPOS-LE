/*
 * Created on 19.05.2006
 */
package epos.ui.view.treeview.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jdesktop.application.Action;

import epos.ui.view.ppane.AbstractController;
import epos.ui.view.ppane.AbstractRenderer;
import epos.ui.view.ppane.AbstractTool;
import epos.ui.view.ppane.ControlledUnit;
import epos.ui.view.ppane.Controller;
import epos.ui.view.ppane.Renderer;
import epos.ui.view.treeview.TreeContent;
import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.ColorStyle.Colors;
import epos.ui.view.treeview.components.NodeComponent;
import epos.ui.view.treeview.components.ShapeTransformer;

/**
 * renderes a hyperbolic or magnify lens over the tree.
 * 
 * @author Thasso
 */
public class Lens extends AbstractTool<TreeView, TreeContent>{

	public static final int VIEW = 1;

	public static final int HYPERBOLIC = 2;

	public static final int MAGNIFY = 4;

	/**
	 * store click point
	 */
	private Point clickPoint;

	/**
	 * is the mouse inside the parent panel
	 */
	protected boolean mouseIn;

	/**
	 * The lens
	 */
	private Arc2D ellipse = new Arc2D.Double();

	/**
	 * The lens radius
	 */
	private double radius = 100;

	private double maxRadius = 300;

	/**
	 * the style to use
	 */
	private int style;

	/**
	 * the transformer
	 */
	private ShapeTransformer transformer = new MTransform();

	private boolean freeze = false;

	private boolean enabled;

	private LensRenderer renderer;

	private LensController controller;

	/**
	 * Create a new lens for a parent panel.
	 * 
	 * @param parentPanel
	 */
	public Lens() {
		super();
		setStyle(VIEW | HYPERBOLIC);
	}

	/**
	 * @return Returns the viewCenter.
	 */
	protected Point2D getViewCenter() {
		return new Point2D.Double(ellipse.getCenterX(), ellipse.getCenterY());
	}

	/**
	 * 
	 * @return Returns the lens radius
	 */
	protected double getViewRadius() {
		return ellipse.getHeight() / 2;
	}

	/**
	 * 
	 * @return Returns the lens ratio (height/width)
	 */
	protected double getRatio() {
		return ellipse.getHeight() / ellipse.getWidth();
	}

	/**
	 * Returns the result of converting <code>polar</code> to Cartesian
	 * coordinates.
	 */
	protected Point2D polarToCartesian(PolarPoint polar, Point2D returnPoint) {
		return polarToCartesian(polar.getTheta(), polar.getRadius(),
				returnPoint);
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

	/**
	 * Returns the result of converting <code>point</code> to polar
	 * coordinates.
	 */
	protected PolarPoint cartesianToPolar(Point2D point) {
		return cartesianToPolar(point.getX(), point.getY());
	}

	/**
	 * Returns the result of converting <code>(x, y)</code> to polar
	 * coordinates.
	 */
	protected PolarPoint cartesianToPolar(double x, double y) {
		double theta = Math.atan2(y, x);
		double radius = Math.sqrt(x * x + y * y);
		return new PolarPoint(theta, radius);
	}


	private void renderingTraversal(NodeComponent n, Graphics2D g2d) {
		Point2D p = transformer.transform(n.getLocation());
		for (NodeComponent c : n.children()) {			
			if((getStyle() & VIEW) == VIEW){
				parent.getView().getEdgeRenderer().renderEdge(n,c, parent.getView(), g2d, parent.getView().getColorManager().getColor(Colors.edgeColor, c));
			}else{
				/*
				 * only translate the points
				 */
				Point2D np = n.getLocation();
				Point2D cp = c.getLocation();
				np = transformer.transform(np);
				cp = transformer.transform(cp);
				
				parent.getView().getEdgeRenderer().renderEdge(n,c,(int) np.getX(),(int)  np.getY(),(int)  cp.getX(),(int)  cp.getY(),n.getAngle(), c.getAngle(), parent.getView(), g2d, parent.getView().getColorManager().getColor(Colors.edgeColor, c));
			}
			
			/*
			 * 2. do we have to traverse the subtree ? 
			 */
			if(c.getSubtreeShape().intersects(g2d.getClipBounds())){
				renderingTraversal(c, g2d);
			}
		}
		
		if(n.isVisible() && ellipse.intersects(n.getBounds())){
			if((getStyle() & VIEW) == VIEW){
				parent.getView().getNodeRenderer().renderNode(n, n.getX(), n.getY(), n.getWidth(),  n
						.getHeight(),n.getAngle(),  parent.getView(), g2d);
			}else{
				Point2D np = transformer.transform(n.getLocation());
				parent.getView().getNodeRenderer().renderNode(n, (int)np.getX(), (int)np.getY(), n.getWidth(),  n
						.getHeight(), n.getAngle(), parent.getView(), g2d);

			}
		}
	}

	/**
	 * Transform the supplied shape with the overridden transform method so that
	 * the shape is distorted by the hyperbolic transform.
	 * 
	 * @param shape
	 *            a shape to transform
	 * @return a GeneralPath for the transformed shape
	 */

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
		if ((style & HYPERBOLIC) == HYPERBOLIC) {
			transformer = new HTransform();
		} else if ((style & MAGNIFY) == MAGNIFY) {
			transformer = new MTransform();
		} else
			throw new RuntimeException("Lens style not supported");
	}

	class HTransform implements ShapeTransformer {

		AffineTransform af;

		Point2D projectedPoint = new Point2D.Double();

		Point2D ul = new Point2D.Double();

		Point2D ur = new Point2D.Double();

		Point2D ll = new Point2D.Double();

		Point2D lr = new Point2D.Double();

		/**
		 * Magnification factor
		 */
		private double magnification = 0.75;

		public Shape transform(Shape shape) {
			shape = af != null ? af.createTransformedShape(shape) : shape;
			Shape s = hyperbolicTransform(shape);
			return s;
		}

		public Shape transform(Shape shape, float flatness) {
			Shape s = hyperbolicTransform(shape, flatness);
			return af != null ? af.createTransformedShape(s) : s;
		}

		public void setTransform(AffineTransform af) {
			this.af = af;
		}

		public Point2D hyperbolicTransform(Point2D viewPoint,
				Point2D returnPoint) {
			if (viewPoint == null)
				return null;
			return hyperbolicTransform(viewPoint.getX(), viewPoint.getY(),
					returnPoint);
		}

		/**
		 * Do a hyperbolic transformation for a point
		 * 
		 * @param graphPoint
		 *            the point
		 * @return transformaed point
		 */
		public Point2D hyperbolicTransform(double x, double y,
				Point2D returnPoint) {
			if (returnPoint == null)
				returnPoint = new Point2D.Double(x, y);

			Point2D viewCenter = getViewCenter();
			double viewRadius = getViewRadius();
			double ratio = getRatio();

			// calculate point from center
			double dx = x - viewCenter.getX();
			double dy = y - viewCenter.getY();

			// factor out ellipse
			dx *= ratio;
			Point2D pointFromCenter = new Point2D.Double(dx, dy);

			PolarPoint polar = cartesianToPolar(pointFromCenter);
			double theta = polar.getTheta();
			double radius = polar.getRadius();
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

		public Rectangle2D transform(Point2D location, double width,
				double height) {
			hyperbolicTransform(location, ul);
			hyperbolicTransform(new Point2D.Double(location.getX() + width,
					location.getY()), ur);
			hyperbolicTransform(new Point2D.Double(location.getX(), location
					.getY()
					+ height), ll);
			hyperbolicTransform(new Point2D.Double(location.getX() + width,
					location.getY() + height), lr);

			double w = Math.min(Math.abs(ul.getX() - ur.getX()), Math.abs(ll
					.getX()
					- lr.getX()));
			double h = Math.min(Math.abs(ul.getY() - ll.getY()), Math.abs(ur
					.getY()
					- lr.getY()));

			Rectangle2D.Double r = new Rectangle2D.Double(ul.getX(), ul.getY(),
					w, h);
			// Math.abs(ul.getX() - ur.getX()) , Math.abs(ul.getY()
			// - ll.getY()) );
			return r;
		}

		public Shape hyperbolicTransform(Shape shape) {
			return hyperbolicTransform(shape, 0);
		}

		public Shape hyperbolicTransform(Shape shape, float flatness) {
			GeneralPath newPath = new GeneralPath();
			double[] coords = new double[6];
			PathIterator iterator = null;
			if (flatness == 0) {
				iterator = shape.getPathIterator(null);
			} else {
				iterator = shape.getPathIterator(null, flatness);
			}
			Point2D.Double p = new Point2D.Double();
			Point2D.Double q = new Point2D.Double();
			Point2D.Double r = new Point2D.Double();

			for (; iterator.isDone() == false; iterator.next()) {
				int type = iterator.currentSegment(coords);
				switch (type) {

				case PathIterator.SEG_MOVETO:
					hyperbolicTransform(coords[0], coords[1], p);
					newPath.moveTo((float) p.getX(), (float) p.getY());
					break;

				case PathIterator.SEG_LINETO:
					hyperbolicTransform(coords[0], coords[1], p);
					newPath.lineTo((float) p.getX(), (float) p.getY());
					break;

				case PathIterator.SEG_QUADTO:
					hyperbolicTransform(coords[0], coords[1], p);
					hyperbolicTransform(coords[2], coords[3], q);
					newPath.quadTo((float) p.getX(), (float) p.getY(),
							(float) q.getX(), (float) q.getY());
					break;

				case PathIterator.SEG_CUBICTO:
					hyperbolicTransform(coords[0], coords[1], p);
					hyperbolicTransform(coords[2], coords[3], q);
					hyperbolicTransform(coords[4], coords[5], r);
					newPath.curveTo((float) p.getX(), (float) p.getY(),
							(float) q.getX(), (float) q.getY(), (float) r
									.getX(), (float) r.getY());
					break;

				case PathIterator.SEG_CLOSE:
					newPath.closePath();
					break;

				}
			}
			return newPath;
		}

		public Point2D transform(Point2D location) {
			return hyperbolicTransform(location, null);
		}

		public double getMaxMagnification() {
			return 0.5;
		}

		public double getMagnification() {
			return magnification;
		}

		public void setMagnification(double magnification) {
			this.magnification = 2.5 + (getMaxMagnification() * magnification);
		}

	}

	class MTransform implements ShapeTransformer {

		AffineTransform af;

		Point2D projectedPoint = new Point2D.Double();

		Point2D ul = new Point2D.Double();

		Point2D ur = new Point2D.Double();

		Point2D ll = new Point2D.Double();

		/**
		 * Magnification factor
		 */
		private double magnification = 3;

		public Shape transform(Shape shape) {
			Shape s = magnifyTransform(shape);
			return af != null ? af.createTransformedShape(s) : s;
		}

		public Shape transform(Shape shape, float flatness) {
			Shape s = magnifyTransform(shape, flatness);
			return af != null ? af.createTransformedShape(s) : s;
		}

		public void setTransform(AffineTransform af) {
			this.af = af;
		}

		/**
		 * do a magnify transformation on the point.
		 * 
		 * @param viewPoint
		 * @return transformed point
		 */
		public Point2D magnifyTransform(Point2D viewPoint, Point2D returnPoint) {
			if (viewPoint == null)
				return null;
			return magnifyTransform(viewPoint.getX(), viewPoint.getY(),
					returnPoint);
		}

		public Point2D magnifyTransform(double x, double y, Point2D returnPoint) {
			if (returnPoint == null)
				returnPoint = new Point2D.Double(x, y);

			Point2D viewCenter = getViewCenter();
			double viewRadius = getViewRadius();
			double ratio = getRatio();

			// calculate point from center
			double dx = x - viewCenter.getX();
			double dy = y - viewCenter.getY();
			// factor out ellipse
			dx *= ratio;
			Point2D pointFromCenter = new Point2D.Double(dx, dy);

			PolarPoint polar = cartesianToPolar(pointFromCenter);
			double theta = polar.getTheta();
			double radius = polar.getRadius();
			if (radius > viewRadius) {
				returnPoint.setLocation(x, y);
				return returnPoint;
			}

			double mag = magnification;
			radius *= mag;

			radius = Math.min(radius, viewRadius);
			polarToCartesian(theta, radius, projectedPoint);
			projectedPoint.setLocation(projectedPoint.getX() / ratio,
					projectedPoint.getY());
			returnPoint.setLocation(projectedPoint.getX() + viewCenter.getX(),
					projectedPoint.getY() + viewCenter.getY());
			return returnPoint;

			// Point2D translatedBack = new Point2D.Double(projectedPoint.getX()
			// + viewCenter.getX(), projectedPoint.getY()
			// + viewCenter.getY());
			// return translatedBack;
		}

		public Rectangle2D transform(Point2D location, double width,
				double height) {
			magnifyTransform(location, ul);
			magnifyTransform(new Point2D.Double(location.getX() + width,
					location.getY()), ur);
			magnifyTransform(new Point2D.Double(location.getX(), location
					.getY()
					+ height), ll);
			Rectangle2D.Double r = new Rectangle2D.Double(ul.getX(), ul.getY(),
					Math.abs(ul.getX() - ur.getX()), Math.abs(ul.getY()
							- ll.getY()));
			return r;
		}

		public Shape magnifyTransform(Shape shape) {
			return magnifyTransform(shape, 0);
		}

		public Shape magnifyTransform(Shape shape, float flatness) {
			GeneralPath newPath = new GeneralPath();
			double[] coords = new double[6];
			PathIterator iterator = null;
			if (flatness == 0) {
				iterator = shape.getPathIterator(null);
			} else {
				iterator = shape.getPathIterator(null, flatness);
			}
			Point2D.Double p = new Point2D.Double();
			Point2D.Double q = new Point2D.Double();
			Point2D.Double r = new Point2D.Double();

			for (; iterator.isDone() == false; iterator.next()) {
				int type = iterator.currentSegment(coords);
				switch (type) {
				case PathIterator.SEG_MOVETO:
					magnifyTransform(coords[0], coords[1], p);
					newPath.moveTo((float) p.getX(), (float) p.getY());
					break;

				case PathIterator.SEG_LINETO:
					magnifyTransform(coords[0], coords[1], p);
					newPath.lineTo((float) p.getX(), (float) p.getY());
					break;

				case PathIterator.SEG_QUADTO:
					magnifyTransform(coords[0], coords[1], p);
					magnifyTransform(coords[2], coords[3], q);
					newPath.quadTo((float) p.getX(), (float) p.getY(),
							(float) q.getX(), (float) q.getY());
					break;

				case PathIterator.SEG_CUBICTO:
					magnifyTransform(coords[0], coords[1], p);
					magnifyTransform(coords[2], coords[3], q);
					magnifyTransform(coords[4], coords[5], r);
					newPath.curveTo((float) p.getX(), (float) p.getY(),
							(float) q.getX(), (float) q.getY(), (float) r
									.getX(), (float) r.getY());
					break;

				case PathIterator.SEG_CLOSE:
					newPath.closePath();
					break;

				}
			}
			return newPath;
		}

		public Point2D transform(Point2D location) {
			return magnifyTransform(location, null);
		}

		public double getMaxMagnification() {
			return 5.0;
		}

		public double getMagnification() {
			return magnification;
		}

		public void setMagnification(double magnification) {
			this.magnification = 1.0 + (getMaxMagnification() * magnification);
		}

	}


	public void setRadius(double d) {
		radius = maxRadius * d;
		parent.getView().repaint();
	}

	/**
	 * @return the transformer
	 */
	public ShapeTransformer getTransformer() {
		return transformer;
	}

	class LensController extends AbstractController<TreeView, TreeContent>{
			public LensController() {
				super(getParentPP());				
			}

			public void mouseMoved(MouseEvent e) {
				if (!freeze) {
					clickPoint = e.getPoint();
					parent.getView().repaint();
				}
			}

			public void mouseDragged(MouseEvent e) {
				if (!freeze) {
					clickPoint = e.getPoint();
					parent.getView().repaint();
				}
			}
			public void mouseEntered(MouseEvent e) {
				mouseIn = true;
				if (!freeze)
					clickPoint = e.getPoint();
				parent.getView().repaint();
			}

			public void mouseExited(MouseEvent e) {
				if (!freeze)
					mouseIn = false;
				parent.getView().repaint();
			}

			public void mousePressed(MouseEvent e) {
				clickPoint = e.getPoint();
				freeze = !freeze;
				parent.getView().repaint();

			}

			@Override
			public void disable() {			
				setLensEnabled(false);
			}

			@Override
			public int getMouseDisableActions() {
				return BUTTON1 | MOVE | DRAG | PRESSED;
			}
			
			
			
	}
	
	class LensRenderer extends AbstractRenderer<TreeView, TreeContent>{
		public LensRenderer() {
			super(getParentPP());
		}

		public void render(Graphics g) {				
			if (clickPoint == null || !mouseIn) {
				//parent.getView().renderView(g);
				return;
			}

			// update the lens
			ellipse.setArcByCenter(clickPoint.x, clickPoint.y, radius, 0, 360,
					Arc2D.CHORD);

			Graphics2D g2d = (Graphics2D) g;
			Color oldColor = g2d.getColor();
			Shape oldclip = g2d.getClip();
			
			g2d.setColor(parent.getView().getBackground());
			Rectangle vrect = parent.getView().getVisibleRect();
			
//			g2d.fill(clip);
//			g2d.setClip(clip.x, clip.y, clip.width, clip.height);
			
	        g2d.setClip(vrect.x, vrect.y, vrect.width, vrect.height);



			
			/*
			 * Minimize rendering affords
			 * 
			 * 1. render background image
			 * 
			 * 2. fill background of the ellipse area
			 * 
			 * 3. rerender tree only in the ellipse area
			 */
			
//			g2d.drawImage(parent.getView().getBufferedImage(), vrect.x, vrect.y, parent.getView());
			Rectangle clip = ellipse.getBounds();
			g2d.fill(ellipse);
			clip.grow(1,1);
			//g2d.setClip(clip);
	        
	        g2d.setClip(ellipse);
	        g2d.clipRect(vrect.x, vrect.y, vrect.width, vrect.height);
			
			
			NodeComponent nr = parent.getView().getRootForRectangle(clip);		
//			Object[] allNodes;
//			allNodes = parent.getView().searchNodeComponents(clip);

//			HashMap nds = new HashMap();
			//long time = System.currentTimeMillis();
//			for (int i = 0; i < allNodes.length; i++) {
			
			ShapeTransformer oldEdgeTransformer =parent.getView().getEdgeRenderer().getTransformer();
			ShapeTransformer oldNodeTransformer =parent.getView().getNodeRenderer().getTransformer();
			
			if((getStyle() & VIEW) == VIEW){
				parent.getView().getEdgeRenderer().setTransformer(transformer);
				parent.getView().getNodeRenderer().setTransformer(transformer);
			}
			renderingTraversal(nr, g2d);
			if((getStyle() & VIEW) == VIEW){
				parent.getView().getEdgeRenderer().setTransformer(oldEdgeTransformer);
				parent.getView().getNodeRenderer().setTransformer(oldNodeTransformer);
			}
//			Iterator it = parent.getView().getEdgeManger().getEdgeComponentsIterator();
//			boolean view = (style & VIEW) == VIEW;
//			while (it.hasNext()) {
//				EdgeComponent ec = (EdgeComponent) it.next();// parent.getView().getEdgeManger()
//				if (!ec.isVisible()) {
//					continue;
//				}
//				if (!clip.intersectsLine(ec.getStartX(), ec.getStartY(), ec
//						.getEndX(), ec.getEndY()))
//					continue;
	//
//				Point2D cp = (Point2D) nds.get(ec.getEdge().getTarget());
//				if (cp == null || view) {
//					cp = parent.getView().getComponentManager().getNodesComponent(
//							(TreeNode) ec.getEdge().getTarget()).getLocation();
//				}
	//
//				p = (Point2D) nds.get(ec.getEdge().getSource());
//				if (p == null || view) {
//					p = parent.getView().getComponentManager().getNodesComponent(
//							(TreeNode) ec.getEdge().getSource()).getLocation();
//				}
	//
//				if (view) {
//					ec.render(g2d, transformer, p, cp, true);
//				} else {
//					ec.render(g2d, null, p, cp, true);
//				}
//			}
			//System.out.println("Done in " + (System.currentTimeMillis() - time));
			// draw the lens
			g2d.setClip(vrect.x, vrect.y, vrect.width, vrect.height);
			g2d.setColor(parent.getView().getForeground());
			g2d.draw(ellipse);
			
			g2d.setColor(oldColor);
			g2d.setClip(oldclip);
			//g2d.dispose();
		}

		@Override
		public boolean conflicts(ControlledUnit<TreeView, TreeContent> otherUnit) {
			return true;
		}

		@Override
		public void disable() {
			setLensEnabled(false);			
		}
	}
	@Action(selectedProperty="lensEnabled")
	public void lens(ActionEvent e){System.out.println("lens action...");}
	public void setLensEnabled(boolean enabled) {
		System.out.println("Switch lens..."  + enabled);
		boolean old = this.enabled;
		this.enabled = enabled;
		firePropertyChange("lensEnabled", old, this.enabled);
		
		if(enabled){
			getParentPP().activateController(getController());
			getParentPP().activateRenderer(getRenderer());
		}else{
			getParentPP().deactivateController(getController());
			getParentPP().deactivateRenderer(getRenderer());
			
		}
	}
	
	public boolean isLensEnabled(){
		return enabled;
	}

	private Renderer<TreeView, TreeContent> getRenderer() {
		if(renderer == null) renderer = new LensRenderer();
		return renderer;
	}

	private Controller<TreeView, TreeContent> getController() {
		if(controller == null) controller = new LensController();
		return controller;
	}


}
/**
 * Polar point representation Wraps around x/y of a {@link Point2D} and
 * creates the methods for<br>
 * 
 * <pre>
 *      Theta =&gt; X
 *      Radius =&gt; Y
 * </pre>
 * 
 * @author Thasso
 */
class PolarPoint extends Point2D.Double {
	public PolarPoint(double theta, double radius) {
		super(theta, radius);
	}

	public double getTheta() {
		return getX();
	}

	public double getRadius() {
		return getY();
	}

	public void setTheta(double theta) {
		setLocation(theta, getRadius());
	}

	public void setRadius(double radius) {
		setLocation(getTheta(), radius);
	}
}
