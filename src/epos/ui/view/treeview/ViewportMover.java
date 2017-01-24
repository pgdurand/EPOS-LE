/*
 * Created on 06.02.2006
 */
package epos.ui.view.treeview;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;
import javax.swing.Timer;

import org.jdesktop.application.Action;

//import epos.ui.annotations.ToolbarAction;
import epos.ui.util.MouseHelper;
//import epos.ui.util.ResourceManager;
import epos.ui.view.ppane.AbstractController;
import epos.ui.view.ppane.AbstractRenderer;
import epos.ui.view.ppane.AbstractTool;
import epos.ui.view.ppane.ControlledUnit;
import epos.ui.view.ppane.PowerPane;
import epos.ui.view.ppane.Renderer;

public class ViewportMover extends AbstractTool<TreeView, TreeContent>
		implements ActionListener {

	private static final double max_rotation = 0.3;
	private static final double max_move = 100;

	/*protected final Image moverImage = ResourceManager
			.getImage("/icons/mover.png", this);
	protected final Image rotaterImage = ResourceManager
			.getImage("/icons/rotater.png", this);*/

	protected static final double WHEEL_ZOOM_AMOUNT = 0.1;

	/**
	 * the click point
	 */
	protected Point clickPoint = null;
	/**
	 * the last mouse position
	 */
	protected Point movePoint = null;
	/**
	 * Viewport start position
	 */
	private Point startPosition;

	protected double yDiff = 0.0;
	protected double xDiff = 0.0;

	protected Timer timer;

	private boolean rotate = false;
	private boolean move = false;
	private epos.ui.view.treeview.ViewportMover.Controller controller;
	private ViewportMoverRenderer renderer;
	private boolean enabled;

	/**
	 * Craete a new ZoomMode RC. This also detects a vieport as parent of the
	 * given tree panel.
	 * 
	 * @param parentPanel
	 */
	public ViewportMover() {
		super();
		timer = new Timer(50, this);
	}

	@Action(selectedProperty = "moverEnabled")
	//@ToolbarAction
	public void mover() {
	}

	public void actionPerformed(ActionEvent e) {
		if (move) {
			double mod_x = Math.abs(xDiff) / 500;
			double mod_y = Math.abs(yDiff) / 500;

			int xdiff = (int) (max_move * mod_x);
			int ydiff = (int) (max_move * mod_y);
			if (xDiff > 0)
				xdiff = -xdiff;
			if (yDiff > 0)
				ydiff = -ydiff;

			Point vp = parent.getView().getViewPosition();

			int nx = vp.x + xdiff;
			if (nx < 0) {
				nx = 0;
			} else if (nx > parent.getView().getViewSize().width
					- parent.getView().getVisibleRect().width) {
				nx = parent.getView().getViewSize().width
						- parent.getView().getVisibleRect().width;
			}

			int ny = vp.y + ydiff;
			if (ny < 0) {
				ny = 0;
			} else if (ny > parent.getView().getViewSize().height
					- parent.getView().getVisibleRect().height) {
				ny = parent.getView().getViewSize().height
						- parent.getView().getVisibleRect().height;
			}

			clickPoint.x += nx - vp.getX();
			clickPoint.y += ny - vp.getY();
			movePoint.x += nx - vp.getX();
			movePoint.y += ny - vp.getY();

			Point npos = new Point(nx, ny);
			parent.getView().setViewPosition(npos);

		}
	}



	public void setMoverEnabled(boolean enabled) {
		boolean old = this.enabled;
		this.enabled = enabled;
		firePropertyChange("moverEnabled", old, this.enabled);
	
		if(enabled){
			parent.activateController(getController());
			parent.activateRenderer(getRenderer());
			parent.getView().setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}else{
			parent.deactivateController(getController());
			parent.deactivateRenderer(getRenderer());
			parent.getView().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));			
		}
	}
	public boolean isMoverEnabled() {
		return enabled;
	}


	private Renderer<TreeView, TreeContent> getRenderer() {
		if(renderer == null) renderer = new ViewportMoverRenderer();
		return renderer;
	}

	public AbstractController getController() {
		if (controller == null) {
			controller = new Controller();
		}
		return controller;
	}
	
	class ViewportMoverRenderer extends AbstractRenderer<TreeView, TreeContent> {
		public ViewportMoverRenderer() {
			super(getParentPP());
		}
		public void render(Graphics g) {
			if (clickPoint != null && movePoint != null) {
				/*if (move)
					g.drawImage(moverImage,
									(int) (clickPoint.x - (moverImage
											.getWidth(null) / 2.0)),
									(int) (clickPoint.y - (moverImage
											.getHeight(null) / 2.0)), null);*/
			}
		}

		public void disable() {
			setMoverEnabled(false);
		}

		@Override
		public boolean conflicts(ControlledUnit<TreeView, TreeContent> otherUnit) {
			return true;
		}
		public List<KeyStroke> getKeyStrokes() {
			return null;
		}

	}

	class Controller extends AbstractController<TreeView, TreeContent> {

		private List<KeyStroke> strokes;

		public Controller() {
			super(getParentPP());
		}

		public void mouseDragged(MouseEvent e) {
			if (clickPoint != null) {
				movePoint = e.getPoint();
				yDiff = clickPoint.getY() - e.getPoint().getY();
				xDiff = clickPoint.getX() - e.getPoint().getX();
			}
		}

		public void mousePressed(MouseEvent e) {
			clickPoint = e.getPoint();
			startPosition = parent.getView().getViewPosition();
			movePoint = e.getPoint();
			if (MouseHelper.isLeftClick(e)) {
				rotate = false;
				move = true;
			} else {
				rotate = true;
				move = false;
			}

			parent.getView().repaint();
			timer.start();
		}

		public void mouseReleased(MouseEvent e) {
			clickPoint = null;
			movePoint = null;
			move = false;
			rotate = false;
			timer.stop();
			parent.getView().repaint();
		}
		public void mouseWheelMoved(MouseWheelEvent e) {
			int clicks = e.getWheelRotation();

			double nxscale = parent.getView().getScaleX()
					+ (-clicks * WHEEL_ZOOM_AMOUNT);
			double nyscale = parent.getView().getScaleY()
					+ (-clicks * WHEEL_ZOOM_AMOUNT);

			parent.getView().setScaling(nxscale, nyscale, null);
		}

		@Override
		public List<KeyStroke> getKeyStrokes() {
			if(strokes ==null){
				strokes = new ArrayList<KeyStroke>();
				strokes.add(KeyStroke.getKeyStroke("meta pressed META"));
			}
			return strokes;
		}
		
		@Override
		public int getMouseDisableActions() {
			return BUTTON1 | PRESSED | RELEASED | WHEEL;
		}

		@Override
		public void disable() {
			setMoverEnabled(false);
		}
	}
}
