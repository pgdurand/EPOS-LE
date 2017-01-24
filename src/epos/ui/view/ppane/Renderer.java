/*
 * Created on 22.05.2006
 */
package epos.ui.view.ppane;

import java.awt.Graphics;

public interface Renderer<V extends View<V,C>, C extends Content> extends ControlledUnit<V,C>{
    
    /**
     * render this RR on the Graphics Object of the parent view
     * 
     * @param g
     */
    public void render(Graphics g);
                
    /**
     * @return returns the z axis level of this renderer
     */
    public int getLayerLevel();
    
    /**
     * Sets the layer level of this renderer
     * @param level
     */
    public void setLayerLevel(int level);
}
