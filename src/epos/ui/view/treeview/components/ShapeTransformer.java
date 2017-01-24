/*
 * Created on 22.05.2006
 */
package epos.ui.view.treeview.components;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface ShapeTransformer {

    public Shape transform(Shape shape);
    
    public Shape transform(Shape shape, float flatness);
    
    public Rectangle2D transform(Point2D location, double width, double height);
    
    public void setTransform(AffineTransform af);

    public Point2D transform(Point2D location);   
        
    public double getMaxMagnification();
    public double getMagnification();
    public void setMagnification(double magnification);
    
}
