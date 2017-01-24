/*
 * Created on 21.07.2005
 */
package epos.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;


/**
 * @author Thasso
 */
public class ColorChooserPanel extends AbstractColorChooserPanel{
    SwatchPanel swatchPanel;
    RecentSwatchPanel recentSwatchPanel;
    MouseListener mainSwatchListener;
    MouseListener recentSwatchListener;

    private static String recentStr = UIManager.getString("ColorChooser.swatchesRecentText");

    public ColorChooserPanel() {
        super();
    }

    public String getDisplayName() {
        return UIManager.getString("ColorChooser.swatchesNameText");
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }

    /**
     * The background color, foreground color, and font are already set to the
     * defaults from the defaults table before this method is called.
     */									
    public void installChooserPanel(JColorChooser enclosingChooser) {
        super.installChooserPanel(enclosingChooser);
    }

    protected void buildChooser() {
      
        JPanel superHolder = new JPanel(new BorderLayout());

        swatchPanel =  new MainSwatchPanel();
	swatchPanel.getAccessibleContext().setAccessibleName(getDisplayName());

	recentSwatchPanel = new RecentSwatchPanel();
	recentSwatchPanel.getAccessibleContext().setAccessibleName(recentStr);

	mainSwatchListener = new MainSwatchListener();
	swatchPanel.addMouseListener(mainSwatchListener);
	recentSwatchListener = new RecentSwatchListener();
	recentSwatchPanel.addMouseListener(recentSwatchListener);


	JPanel mainHolder = new JPanel(new BorderLayout());
	Border border = new CompoundBorder( new LineBorder(Color.black),
					    new LineBorder(Color.white) );
	mainHolder.setBorder(border);
	mainHolder.add(swatchPanel, BorderLayout.CENTER);
        superHolder.add( mainHolder, BorderLayout.CENTER );

	JPanel recentHolder = new JPanel( new BorderLayout() );
	recentSwatchPanel.addMouseListener(recentSwatchListener);
	recentHolder.setBorder(border);
	recentHolder.add(recentSwatchPanel, BorderLayout.CENTER);
	JPanel recentLabelHolder = new JPanel(new BorderLayout());
	recentLabelHolder.add(recentHolder, BorderLayout.CENTER);
	JLabel l = new JLabel(recentStr);
	l.setLabelFor(recentSwatchPanel);
//	recentLabelHolder.add(l, BorderLayout.NORTH);
//	JPanel recentHolderHolder = new JPanel(new CenterLayout());
//        if (this.getComponentOrientation().isLeftToRight()) {
//	    recentHolderHolder.setBorder(new EmptyBorder(2,10,2,2));
//        } else {
//	    recentHolderHolder.setBorder(new EmptyBorder(2,2,2,10));
//        }
//	recentHolderHolder.add(recentLabelHolder);
//	superHolder.add( recentHolderHolder, BorderLayout.AFTER_LINE_ENDS );	

	add(superHolder);
	
    }

    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        super.uninstallChooserPanel(enclosingChooser);
	swatchPanel.removeMouseListener(mainSwatchListener);
	recentSwatchPanel.removeMouseListener(recentSwatchListener);
	swatchPanel = null;
	recentSwatchPanel = null;
	mainSwatchListener = null;
	recentSwatchListener = null;
	removeAll();  // strip out all the sub-components
    }

    public void updateChooser() {

    }


    class RecentSwatchListener extends MouseAdapter implements Serializable {
        public void mousePressed(MouseEvent e) {
	    Color color = recentSwatchPanel.getColorForLocation(e.getX(), e.getY());
	    getColorSelectionModel().setSelectedColor(color);

	}
    }

    class MainSwatchListener extends MouseAdapter implements Serializable {
        public void mousePressed(MouseEvent e) {
	    Color color = swatchPanel.getColorForLocation(e.getX(), e.getY());
	    getColorSelectionModel().setSelectedColor(color);
	    recentSwatchPanel.setMostRecentColor(color);

	}
    }


    class SwatchPanel extends JPanel {

        protected Color[] colors;
        protected Dimension swatchSize;
        protected Dimension numSwatches;
        protected Dimension gap;

        public SwatchPanel() {
            initValues();
            initColors();
    	setToolTipText(""); // register for events
    	setOpaque(true);
    	setBackground(Color.white);
    	setRequestFocusEnabled(false);
        }

        public boolean isFocusTraversable() {
            return false;
        }

        protected void initValues() {

        }

        public void paintComponent(Graphics g) {
             g.setColor(getBackground());
             g.fillRect(0,0,getWidth(), getHeight());
    	 for (int row = 0; row < numSwatches.height; row++) {
    	    for (int column = 0; column < numSwatches.width; column++) {

    	      g.setColor( getColorForCell(column, row) ); 
    		int x;
    		if ((!this.getComponentOrientation().isLeftToRight()) &&
    		    (this instanceof RecentSwatchPanel)) {
    		    x = (numSwatches.width - column - 1) * (swatchSize.width + gap.width);
    		} else {
    		    x = column * (swatchSize.width + gap.width);
    		}
    		int y = row * (swatchSize.height + gap.height);
    	        g.fillRect( x, y, swatchSize.width, swatchSize.height);
    		g.setColor(Color.black);
    		g.drawLine( x+swatchSize.width-1, y, x+swatchSize.width-1, y+swatchSize.height-1);
    		g.drawLine( x, y+swatchSize.height-1, x+swatchSize.width-1, y+swatchSize.height-1);
    	    }
    	 }
        }

        public Dimension getPreferredSize() {
            int x = numSwatches.width * (swatchSize.width + gap.width) -1;
    	int y = numSwatches.height * (swatchSize.height + gap.height) -1;
            return new Dimension( x, y );
        }

        protected void initColors() {

          
        }

        public String getToolTipText(MouseEvent e) {
            Color color = getColorForLocation(e.getX(), e.getY());
            return color.getRed()+", "+ color.getGreen() + ", " + color.getBlue();
        }

        public Color getColorForLocation( int x, int y ) {
            int column;
            if ((!this.getComponentOrientation().isLeftToRight()) &&
                (this instanceof RecentSwatchPanel)) {
                column = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
            } else {
                column = x / (swatchSize.width + gap.width);
            }
            int row = y / (swatchSize.height + gap.height);
    	return getColorForCell(column, row);
        }

        private Color getColorForCell( int column, int row) {
    	return colors[ (row * numSwatches.width) + column ]; // (STEVE) - change data orientation here
        }




    }

    class RecentSwatchPanel extends SwatchPanel {
        protected void initValues() {
            swatchSize = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize");
    	numSwatches = new Dimension( 5, 7 );
            gap = new Dimension(1, 1);
        }


        protected void initColors() {
            Color defaultRecentColor = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor");
            int numColors = numSwatches.width * numSwatches.height;
    	
    	colors = new Color[numColors];
    	for (int i = 0; i < numColors ; i++) {
    	    colors[i] = defaultRecentColor;
    	}
        }

        public void setMostRecentColor(Color c) {

    	System.arraycopy( colors, 0, colors, 1, colors.length-1);
            colors[0] = c;
    	repaint();
        }

    }

    class MainSwatchPanel extends SwatchPanel {


        protected void initValues() {
            swatchSize = UIManager.getDimension("ColorChooser.swatchesSwatchSize");
    	numSwatches = new Dimension( 31, 9 );
            gap = new Dimension(1, 1);
        }

        protected void initColors() {
            int[] rawValues = initRawValues();
            int numColors = rawValues.length / 3;
    	
    	colors = new Color[numColors];
    	for (int i = 0; i < numColors ; i++) {
    	    colors[i] = new Color( rawValues[(i*3)], rawValues[(i*3)+1], rawValues[(i*3)+2] );
    	}
        }

        private int[] initRawValues() {

            int[] rawValues = {     
    255, 255, 255, // first row.
    204, 255, 255,
    204, 204, 255,
    204, 204, 255,
    204, 204, 255,
    204, 204, 255,
    204, 204, 255,
    204, 204, 255,
    204, 204, 255,
    204, 204, 255,
    204, 204, 255,
    255, 204, 255,
    255, 204, 204,
    255, 204, 204,
    255, 204, 204,
    255, 204, 204,
    255, 204, 204,
    255, 204, 204,
    255, 204, 204,
    255, 204, 204,
    255, 204, 204,
    255, 255, 204,
    204, 255, 204,
    204, 255, 204,
    204, 255, 204,
    204, 255, 204,
    204, 255, 204,
    204, 255, 204,
    204, 255, 204,
    204, 255, 204,
    204, 255, 204,
    204, 204, 204,  // second row.
    153, 255, 255,
    153, 204, 255,
    153, 153, 255,
    153, 153, 255,
    153, 153, 255,
    153, 153, 255,
    153, 153, 255,
    153, 153, 255,
    153, 153, 255,
    204, 153, 255,
    255, 153, 255,
    255, 153, 204,
    255, 153, 153,
    255, 153, 153,
    255, 153, 153,
    255, 153, 153,
    255, 153, 153,
    255, 153, 153,
    255, 153, 153,
    255, 204, 153,
    255, 255, 153,
    204, 255, 153,
    153, 255, 153,
    153, 255, 153,
    153, 255, 153,
    153, 255, 153,
    153, 255, 153,
    153, 255, 153,
    153, 255, 153,
    153, 255, 204,
    204, 204, 204,  // third row
    102, 255, 255,
    102, 204, 255,
    102, 153, 255,
    102, 102, 255,
    102, 102, 255,
    102, 102, 255,
    102, 102, 255,
    102, 102, 255,
    153, 102, 255,
    204, 102, 255,
    255, 102, 255,
    255, 102, 204,
    255, 102, 153,
    255, 102, 102,
    255, 102, 102,
    255, 102, 102,
    255, 102, 102,
    255, 102, 102,
    255, 153, 102,
    255, 204, 102,
    255, 255, 102,
    204, 255, 102,
    153, 255, 102,
    102, 255, 102,
    102, 255, 102,
    102, 255, 102,
    102, 255, 102,
    102, 255, 102,
    102, 255, 153,
    102, 255, 204,
    153, 153, 153, // fourth row
    51, 255, 255,
    51, 204, 255,
    51, 153, 255,
    51, 102, 255,
    51, 51, 255,
    51, 51, 255,
    51, 51, 255,
    102, 51, 255,
    153, 51, 255,
    204, 51, 255,
    255, 51, 255,
    255, 51, 204,
    255, 51, 153,
    255, 51, 102,
    255, 51, 51,
    255, 51, 51,
    255, 51, 51,
    255, 102, 51,
    255, 153, 51,
    255, 204, 51,
    255, 255, 51,
    204, 255, 51,
    153, 244, 51,
    102, 255, 51,
    51, 255, 51,
    51, 255, 51,
    51, 255, 51,
    51, 255, 102,
    51, 255, 153,
    51, 255, 204,
    153, 153, 153, // Fifth row
    0, 255, 255,
    0, 204, 255,
    0, 153, 255,
    0, 102, 255,
    0, 51, 255,
    0, 0, 255,
    51, 0, 255,
    102, 0, 255,
    153, 0, 255,
    204, 0, 255,
    255, 0, 255,
    255, 0, 204,
    255, 0, 153,
    255, 0, 102,
    255, 0, 51,
    255, 0 , 0,
    255, 51, 0,
    255, 102, 0,
    255, 153, 0,
    255, 204, 0,
    255, 255, 0,
    204, 255, 0,
    153, 255, 0,
    102, 255, 0,
    51, 255, 0,
    0, 255, 0,
    0, 255, 51,
    0, 255, 102,
    0, 255, 153,
    0, 255, 204,
    102, 102, 102, // sixth row
    0, 204, 204,
    0, 204, 204,
    0, 153, 204,
    0, 102, 204,
    0, 51, 204,
    0, 0, 204,
    51, 0, 204,
    102, 0, 204,
    153, 0, 204,
    204, 0, 204,
    204, 0, 204,
    204, 0, 204,
    204, 0, 153,
    204, 0, 102,
    204, 0, 51,
    204, 0, 0,
    204, 51, 0,
    204, 102, 0,
    204, 153, 0,
    204, 204, 0,
    204, 204, 0,
    204, 204, 0,
    153, 204, 0,
    102, 204, 0,
    51, 204, 0,
    0, 204, 0,
    0, 204, 51,
    0, 204, 102,
    0, 204, 153,
    0, 204, 204, 
    102, 102, 102, // seventh row
    0, 153, 153,
    0, 153, 153,
    0, 153, 153,
    0, 102, 153,
    0, 51, 153,
    0, 0, 153,
    51, 0, 153,
    102, 0, 153,
    153, 0, 153,
    153, 0, 153,
    153, 0, 153,
    153, 0, 153,
    153, 0, 153,
    153, 0, 102,
    153, 0, 51,
    153, 0, 0,
    153, 51, 0,
    153, 102, 0,
    153, 153, 0,
    153, 153, 0,
    153, 153, 0,
    153, 153, 0,
    153, 153, 0,
    102, 153, 0,
    51, 153, 0,
    0, 153, 0,
    0, 153, 51,
    0, 153, 102,
    0, 153, 153,
    0, 153, 153,
    51, 51, 51, // eigth row
    0, 102, 102,
    0, 102, 102,
    0, 102, 102,
    0, 102, 102,
    0, 51, 102,
    0, 0, 102,
    51, 0, 102,
    102, 0, 102,
    102, 0, 102,
    102, 0, 102,
    102, 0, 102,
    102, 0, 102,
    102, 0, 102,
    102, 0, 102,
    102, 0, 51,
    102, 0, 0,
    102, 51, 0,
    102, 102, 0,
    102, 102, 0,
    102, 102, 0,
    102, 102, 0,
    102, 102, 0,
    102, 102, 0,
    102, 102, 0,
    51, 102, 0,
    0, 102, 0,
    0, 102, 51,
    0, 102, 102,
    0, 102, 102,
    0, 102, 102,
    0, 0, 0, // ninth row
    0, 51, 51,
    0, 51, 51,
    0, 51, 51,
    0, 51, 51,
    0, 51, 51,
    0, 0, 51,
    51, 0, 51,
    51, 0, 51,
    51, 0, 51,
    51, 0, 51,
    51, 0, 51,
    51, 0, 51,
    51, 0, 51,
    51, 0, 51,
    51, 0, 51,
    51, 0, 0,
    51, 51, 0,
    51, 51, 0,
    51, 51, 0,
    51, 51, 0,
    51, 51, 0,
    51, 51, 0,
    51, 51, 0,
    51, 51, 0,
    0, 51, 0,
    0, 51, 51,
    0, 51, 51,
    0, 51, 51,
    0, 51, 51,
    51, 51, 51 };
    	return rawValues;
        }
    }
}
