package epos.ui.view.treeview.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import epos.ui.view.treeview.components.NodeComponent;


/**
 * Custom multiline tooltip for a node component
 * 
 * @author Thasso
 */
public class TooltipRenderer{
   
    protected static final Color DEFAULT_BACKGROUND = new Color(200,200,200,220);

    /**
     * Default row height
     */
    protected float vertical_row_height = 10;
    
    /**
     * Create a new label for a given node
     * @param node
     */
    public TooltipRenderer(){
        super();
    }    
    
    /**
     * Paint the label
     * 
     * @param g
     */
    public void paintComponent(Graphics g, NodeComponent node){
        Graphics2D g2d = (Graphics2D)g;          
        g2d.setColor(DEFAULT_BACKGROUND);
                
        Rectangle b = g2d.getClipBounds();
        g2d.fill(b);
        
        g2d.setColor(Color.black);
        g2d.drawRect(b.x,b.y,b.width -1, b.height -1);
        
        int elements = 0;        
        Font font = g2d.getFont();
        if(node.getLabel() != null){
            elements++;
            g2d.setFont(font.deriveFont(Font.BOLD));
            g2d.drawString(node.getLabel(),b.x+2,2+b.y+vertical_row_height*elements-4);
            g2d.setFont(font);
        }
        if(node.getNode().getDistanceToParent() >=0){
            elements++;            
            String d = String.format("%10.4f", node.getNode().getDistanceToParent());
            g2d.drawString(d,b.x+2,2+b.y+vertical_row_height*elements-4);
        }           
    }
    /**
     * Returns the size of the label
     * 
     * @param g2d
     * @return
     */
    public Dimension getPrefferedSize(Graphics2D g2d, NodeComponent node){
        int width = 0;
        vertical_row_height = g2d.getFont().getSize2D() + 4;
        int elements = 0;
        
        if(node.getLabel() != null){
            int labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), node.getLabel());
            width = labelWidth > width ? labelWidth : width;
            elements++;
        }
        if(node.getNode().getDistanceToParent() >=0){
            String d = String.format("%+10.4f", node.getNode().getDistanceToParent());            
            int distWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), d);
            width = distWidth > width ? distWidth : width;
            elements++;
        }           
        int height = (int)(elements * vertical_row_height);
        return new Dimension(width+4,height+4);
    }    
}