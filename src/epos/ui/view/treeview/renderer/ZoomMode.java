/*
 * Created on 21.04.2005
 */
package epos.ui.view.treeview.renderer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.application.Action;

import epos.model.tree.TreeNode;
//import epos.ui.annotations.ToolbarAction;
import epos.ui.util.MouseHelper;
import epos.ui.view.ppane.AbstractController;
import epos.ui.view.ppane.AbstractRenderer;
import epos.ui.view.ppane.AbstractTool;
import epos.ui.view.ppane.ControlledUnit;
import epos.ui.view.ppane.Renderer;
import epos.ui.view.treeview.TreeContent;
import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.ViewportChangeListener;


/**
 * @author Thasso
 */
public class ZoomMode extends AbstractTool<TreeView, TreeContent>{
	public static final Logger log = Logger.getLogger(ZoomMode.class);
		
    public static final double DEFAULT_ZOOM_AMOUNT = 0.3;


    /**
     * the click point
     */
    protected Point clickPoint = null;
    /**
     * the last mouse position
     */    
    protected Point movePoint = null;
    
    /**
     * the parents viewport
     */
    private JViewport viewPort;
        
    private ViewportChangeListener viewListener;
    
    private Rectangle2D.Double selectionRect = new Rectangle2D.Double();
    
    private Mover mover;
   
    //Korilog added
    private boolean animate = false;
    
	private AbstractController controller;
	private Renderer<TreeView, TreeContent> renderer;
	
	protected boolean zoomModeEnabled = false;
    
    /**
     * Craete a new ZoomMode RC.
     * This also detects a vieport as parent of the given tree panel.
     * 
     * @param parentPanel
     */
    public ZoomMode() {
        super();                      
    }
    
    @Action
    //@ToolbarAction
    public void fullscreen(){
    	moveViewport(1.0, 1.0, new Point(0,0));
    }
    @Action
    //@ToolbarAction
    public void zoomIn(){
        double sx = parent.getView().getScaleX() + ZoomMode.DEFAULT_ZOOM_AMOUNT;
        double sy = parent.getView().getScaleY() + ZoomMode.DEFAULT_ZOOM_AMOUNT;
	    moveViewport(sx,sy, null);
    }
    @Action 
    //@ToolbarAction
    public void zoomOut(){
        double sx = parent.getView().getScaleX() - ZoomMode.DEFAULT_ZOOM_AMOUNT;
        double sy = parent.getView().getScaleY() - ZoomMode.DEFAULT_ZOOM_AMOUNT;
	    moveViewport(sx,sy, null);	    
    }
    
    @Action(selectedProperty="zoomModeEnabled")
    //@ToolbarAction
    public void zoomModeRenderer(ActionEvent e){}
        
    public void setZoomModeEnabled(boolean zoomModeEnabled){
    	boolean old = this.zoomModeEnabled;
    	this.zoomModeEnabled = zoomModeEnabled;
    	firePropertyChange("zoomModeEnabled", old, this.zoomModeEnabled);
    	
    	if(zoomModeEnabled){
			if(viewPort == null){
				viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, getParentPP().getView());
				viewListener = new ViewportChangeListener(viewPort);       
			}
			viewPort.addChangeListener(viewListener);			
	    	getParentPP().activateController(getController());
	    	getParentPP().activateRenderer(getRenderer());
    	}else{
    		viewPort.removeChangeListener(viewListener);
	    	getParentPP().deactivateController(getController());
	    	getParentPP().deactivateRenderer(getRenderer());    		
    	}
    }
    
    @Action
    //@ToolbarAction
	public void fitVertical(ActionEvent e) {
        int minHeight = (int) getParentPP().getView().getComponentManager().getLabelFont().getSize2D() + 2;        
        int nodes = 0;
        Iterator iter = getParentPP().getView().getTree().vertices().iterator();
        while (iter.hasNext()) {
            TreeNode n = (TreeNode) iter.next();
            if(n.isLeaf())
                nodes++;
        }                    
        int height = (nodes * minHeight);
        double scale_y = (double)height / getParentPP().getView().getHeight();        
       	moveViewport(1.0, scale_y, null);
	}
    
    
    private Renderer<TreeView, TreeContent> getRenderer() {
    	if(renderer == null) renderer = new ZommModeRenderer();
		return renderer;
	}

	public boolean isZoomModeEnabled(){
    	return zoomModeEnabled;
    }
	//Korilog added
	public void enableAnimation(boolean animate){
		this.animate = animate;
		mover = null;
	}
	
    public void moveViewport(double scalingx, double scalingy, Point newPos) {
    	if(mover == null){
    		//Korilog updated
    		mover = animate ? new AnimatedMover() : new SimpleMover();
    	}
    	if(newPos == null){    		
    		Rectangle v = parent.getView().getVisibleRect();    		    		
    		double ratio_x = v.getCenterX() / parent.getView().getWidth() ;
    		double ratio_y = v.getCenterY() / parent.getView().getHeight() ;
    		int	nx = (int) ((v.width * scalingx * ratio_x) - (v.width/2.0));
    		int	ny = (int) ((v.height * scalingy * ratio_y) - (v.height/2.0));
    		newPos = new Point(nx, ny);
    	}
    	mover.setTargetPosition(newPos);
    	mover.setTargetScaleX(scalingx);
    	mover.setTargetScaleY(scalingy);    	
    	mover.move(getParentPP().getView());           
    }    
        
	public AbstractController getController() {
		if(controller == null) controller = new MoverController();
		return controller;
	}
	
	class MoverController extends AbstractController<TreeView, TreeContent>{

		public MoverController() {
			super(getParentPP());		
		}
		@Override
		public int getMouseDisableActions() {
			return BUTTON1 | BUTTON3 | DRAG | PRESSED | CLICKED | RELEASED | WHEEL;
		}		
        @Override
		public void mouseWheelMoved(MouseWheelEvent e) {
		}
			public void mouseDragged(MouseEvent e){
				if(MouseHelper.isLeftClick(e)){    
                	if(clickPoint != null){
    	                movePoint = e.getPoint();	                
    	                getParentPP().getView().repaint();	                               
                	}
                }
            }
	        public void mousePressed(MouseEvent e){                
                if(MouseHelper.isLeftClick(e)){
                	clickPoint = e.getPoint();
                	movePoint = e.getPoint();            	
                	getParentPP().getView().repaint();
                }
            }
            public void mouseClicked(MouseEvent e) {
                //super.mouseClicked(e);
                if(e.getButton() == MouseEvent.BUTTON3){
                    double sx = getParentPP().getView().getScaleX() - 0.1;
                    double sy = getParentPP().getView().getScaleY() - 0.1;
                    getParentPP().getView().setScaling(sx,sy);                    
                }

            }
            public void mouseReleased(MouseEvent e){    
            	if(MouseHelper.isLeftClick(e)){
                    if(viewPort != null){                    
                        double x = selectionRect.x;
                        double y = selectionRect.y;
                        double w = selectionRect.width;
                        double h = selectionRect.height;
                        if(w > 5 && h > 5){                           
                            Rectangle actualView = viewPort.getViewRect();
                            
                            double newScalingX = 1.0;
                            double newScalingY = 1.0;
                            if(w > h){
                                newScalingX = actualView.width/w;
                                newScalingY = newScalingX;
                            }else{
                            	newScalingY = actualView.height/h;
                                newScalingX = newScalingY;
                                                            	
                            }
                            
                            newScalingX *= getParentPP().getView().getScaleX();
                            newScalingY *= getParentPP().getView().getScaleY();
    
                            double px = x/getParentPP().getView().getSize().width;
                            double py = y/getParentPP().getView().getSize().height;
                                                    
                            double nx = actualView.width * newScalingX * px;
                            double ny = actualView.height * newScalingY * py;

                            //getParentPP().getView().setScaling(newScaling, new Point((int)nx, (int)ny));
                            moveViewport(newScalingX, newScalingY, new Point((int)nx,(int)ny));
                        }
                    }                
                    clickPoint = null;
                    movePoint = null;
                }else{
                    double sx = getParentPP().getView().getScaleX() - DEFAULT_ZOOM_AMOUNT;
                    double sy = getParentPP().getView().getScaleY() - DEFAULT_ZOOM_AMOUNT;
                    moveViewport(sx,sy, null);
                }
            }
			@Override
			public void disable() {		
				setZoomModeEnabled(false);
			}
	}
	class ZommModeRenderer extends AbstractRenderer<TreeView, TreeContent>{
		public ZommModeRenderer() {
			super(getParentPP());
		}

		@Override
		public void render(Graphics g) {
	        Rectangle p = getParentPP().getView().getVisibleRect();	        
	        if(clickPoint != null){        	
	            Graphics2D g2d = (Graphics2D)g;
	            Shape oldClip = g2d.getClip();
	            g2d.setClip(p);
	            
	            int x = clickPoint.x > movePoint.x ? movePoint.x : clickPoint.x;
	            int y = clickPoint.y > movePoint.y ? movePoint.y : clickPoint.y;
	            
	            int h = Math.abs(clickPoint.y - movePoint.y);
	            int w = Math.abs(clickPoint.x - movePoint.x);
	            
	            selectionRect.setFrame(x,y,w,h);
	            if(w > 5 && h > 5){
	            	g2d.setColor(getParentPP().getView().getForeground());
	                g2d.drawRect(x,y, w, h);
	            }
	            g2d.setClip(oldClip);
	        }
		}

		public void disable() {
			setZoomModeEnabled(false);			
		}

		@Override
		public boolean conflicts(ControlledUnit<TreeView, TreeContent> otherUnit) {
			return false;
		}		
	}


    //
    //
    ////// end RC Stuff	
}

interface Mover{    	
	public void setTargetPosition(Point targetPoint);
	public void setTargetScaleX(double scalex );
	public void setTargetScaleY(double scaley );
	public void move(TreeView view);
}

class SimpleMover implements Mover{
	protected Point startPoint;
	protected Point targetPoint;
	protected double startScaleX;
	protected double startScaleY;
	protected double targetScaleX;
	protected double targetScaleY;
	protected TreeView view;
	
	public void move(TreeView view) {
		this.view = view;
		this.startScaleX = view.getScaleX();
		this.startScaleY = view.getScaleY();
		this.startPoint = view.getViewPosition();
		view.setScaling(targetScaleX, targetScaleY, targetPoint);
	}

	public void setTargetPosition(Point targetPoint) {
		this.targetPoint = targetPoint;
	}

	public void setTargetScaleX(double scalex) {
		this.targetScaleX = scalex;
	}

	public void setTargetScaleY(double scaley) {
		this.targetScaleY = scaley;
	}
}

class AnimatedMover extends SimpleMover implements TimingTarget{
	protected int dist_x, dist_y;
	protected double dist_sx, dist_sy;
	protected Point p = new Point();
	protected float fraction;
	protected Animator animator;
	public void begin() {}
	public void end() {}
	public void repeat() {}
	public void timingEvent(float f) {
		int px, py;
		double sx, sy;
		this.fraction = f;
		px = (int) (startPoint.x + (dist_x * f));
		py = (int) (startPoint.y + (dist_y * f));
		sx = startScaleX + (dist_sx * f);
		sy = startScaleY + (dist_sy * f);
		p.setLocation(px, py);
		view.setScaling(sx, sy, p);
	}	
	public void setView(TreeView view){
	}
	public void move(TreeView view) {
		if(animator == null){
			animator = new Animator(350, this);
			//animator.setAcceleration(0.2f);
			animator.setDeceleration(0.5f);
		}

		if(animator != null && animator.isRunning()){
			animator.stop();
		}
		this.view = view;
		this.startScaleX = view.getScaleX();
		this.startScaleY = view.getScaleY();
		this.startPoint = view.getViewPosition();

		dist_x = targetPoint.x - startPoint.x ;
		dist_y = targetPoint.y - startPoint.y;
		dist_sx = targetScaleX - startScaleX;
		dist_sy = targetScaleY - startScaleY;

		animator.start();
	}
}



