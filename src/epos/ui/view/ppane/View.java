/*
 * Created on 17.08.2004
 *
 */
package epos.ui.view.ppane;

import java.awt.Graphics;
import java.awt.Image;
import java.util.EnumSet;

import javax.swing.JComponent;



/**
 * The Basic View of a PowerPane
 * 
 * @author Thasso
 */
public interface View<V extends View<V,C>, C extends Content> extends Cloneable{
	public enum Antialiasing{AA_ON, AA_OFF, AA_TEXT_ON, AA_TEXT_OFF}
    /**
     * The views default rendering level
     */    
	public static final int DEFAULT_VIEW_LEVEL = 0;
	
    /**
     * Activate a renderer.
     * 
     * @param renderer
     */
    public void enableRenderer(Renderer<V, C> renderer);
   
    /**
     * Disable a given renderer
     * 
     * @param renderer
     */
    public void disableRenderer(Renderer<V, C> renderer);
    
    /**
     * Render the view on the given graphics object
     * @param g
     */
    public void render(Graphics g);
    
    /**
     * Returns an Image of the actual visible area of the view.
     * Comes in handy for absolute renderers that need the view in the background.
     * 
     * @return
     */
    public Image getBufferedImage();
	
    /**
     * 
     * @return Returns true if the buffered image must be validated
     */
    public boolean isForceBufferRefresh();

    /**
     * If set to true, the buffered image will be validated for the next call of 
     * {@link #getBufferedImage()}
     * 
     * @param forceBufferRefresh
     */
	public void setForceBufferRefresh(boolean forceBufferRefresh);
	
	/**
	 * Returns the actual component that contains this view. This can be the view itself 
	 * or a surrounding component such as a ScrollPane.
	 * 
	 * @return component
	 * @see #getViewComponent()
	 */
	public JComponent getComponent();
	
	/**
	 * Returns the reals view component. this is used to split the view component
	 * from a component taht is returned by {@link #getComponent()}. For example, {@link #getComponent()} might
	 * return a ScrollPane that contains the Jomponent returned by {@link #getViewComponent()}.
	 * 
	 * @return component
	 * @see #getComponent()
	 */
	public JComponent getViewComponent();
		
	/**
	 * returns the powerpane embeding this view
	 * @return
	 */
	public PowerPane<V,C> getParentPane();
	
	/**
	 * Sets this views parent pane.
	 * 
	 * @param parent
	 */
	public void setParentPane(PowerPane<V,C> parent);
	
	/**
	 * Retusn the antialiasing settings
	 * @return
	 */
	public EnumSet<Antialiasing> getAntialiasing();
	
	/**
	 * Sets the antialiasing settings
	 * @param aa
	 */
	public void setAntialiasing(EnumSet<Antialiasing> aa);
	
	/**
	 * Set backend status for this view
	 * @param backend
	 */
	public void setBackendAvailable(boolean backend);
	
}
