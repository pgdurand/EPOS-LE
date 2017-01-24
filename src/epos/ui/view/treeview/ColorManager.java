package epos.ui.view.treeview;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import epos.qnode.properties.Property;
import epos.qnode.properties.PropertySet;
import epos.ui.view.treeview.ColorStyle.Colors;
import epos.ui.view.treeview.components.NodeComponent;


/**
 * Manage Colors of the tree
 * 
 * @author thasso
 * 
 */
public class ColorManager {
	public static final String PROPERTY_STYLE = "ColorStyleChange";
	public static final String PROPERTY_STYLEADD = "ColorStyleAdded";
	public static final String PROPERTY_STYLEREMOVE = "ColorStyleRemove";


	public final static ColorStyle BLACK_ON_WHITE = new ColorStyle("Black on White", Color.white, Color.black, Color.black, Color.cyan, Color.black, Color.red, Color.green, true);
    public final static ColorStyle WHITE_ON_BLACK = new ColorStyle("White on Black", Color.black, Color.white, Color.white, Color.cyan, Color.white, Color.red, Color.green, true);
    
    public static enum NodeColorizations{NODE, PARENT, CHILDREN, NODES_TO_ROOT, NODES_TO_LEAVES };
    public static enum EdgeColorizations{IN_EDGE, OUT_EDGES, EDGES_TO_ROOT, EDGES_TO_LEAVES};
    
    private List<ColorStyle> styles;
    private ColorStyle style;
    private PropertyChangeSupport propertyChange;
    private PropertySet backend;
    
    public ColorManager() {
        super();
        styles = new ArrayList<ColorStyle>();
        styles.add(BLACK_ON_WHITE);
        styles.add(WHITE_ON_BLACK);
        style = BLACK_ON_WHITE;
        
        propertyChange = new PropertyChangeSupport(this);
    }

    public void setColorStyle(ColorStyle s) {
    	setColorStyle(s, true);
    }
    public void setColorStyle(ColorStyle s, boolean store) {
    	if(s != style){	    	
	    	propertyChange.firePropertyChange(PROPERTY_STYLE, this.style, s);
	    	if(store && ! styles.contains(s)){
	    		styles.add(s);
	    	}
	    	this.style = s;
	    	
	    	
    	}
    }
    
    public ColorStyle getColorStyle(){
    	return style;
    }


    public Color getEdgeSelectionColor() {
        return Color.RED;
    }
    
    public void setNodeColor(NodeComponent node, Color color, EnumSet<NodeColorizations> colorizeStyle) throws ImmutableException{
    	if (colorizeStyle.contains(NodeColorizations.NODE)) {
            this.style.setColor(Colors.nodeColor, color, node);
        }
        if (colorizeStyle.contains(NodeColorizations.PARENT)){
        	this.style.setColor(Colors.nodeColor, color, node.getParent());
        }
        if (colorizeStyle.contains(NodeColorizations.CHILDREN)){
            for (NodeComponent n : node.children()) {
            	this.style.setColor(Colors.nodeColor, color, n);            
            }
        }
        if (colorizeStyle.contains(NodeColorizations.NODES_TO_ROOT)){
            NodeComponent p = node;
            while (p != null) {
            	this.style.setColor(Colors.nodeColor, color, p);
                p = p.getParent();
            }
        }
        if (colorizeStyle.contains(NodeColorizations.NODES_TO_LEAVES)){
            for (NodeComponent n : node.depthFirstIterator()) {
            	this.style.setColor(Colors.nodeColor, color, n);
            }
        }
        
        // store property        
        storeColorSet(this.style);        
    }
    
	public void setEdgeColor(NodeComponent node, Color color, EnumSet<EdgeColorizations> colorizeStyle) throws ImmutableException{			
    	if (colorizeStyle.contains(EdgeColorizations.IN_EDGE)) {
            this.style.setColor(Colors.edgeColor, color, node);
        }
        if (colorizeStyle.contains(EdgeColorizations.OUT_EDGES)){
            for (NodeComponent n : node.children()) {
            	this.style.setColor(Colors.edgeColor, color, n);            
            }
        }
        if (colorizeStyle.contains(EdgeColorizations.EDGES_TO_ROOT)){
            NodeComponent p = node;
            while (p != null) {
            	this.style.setColor(Colors.edgeColor, color, p);
                p = p.getParent();
            }
        }
        if (colorizeStyle.contains(EdgeColorizations.EDGES_TO_LEAVES)){
            for (NodeComponent n : node.depthFirstIterator()) {
            	this.style.setColor(Colors.edgeColor, color, n);
            }
        }
    }

	
    public void storeColorSet(ColorStyle style) {
    	if(backend == null) return;
    	Property p = this.backend.getProperty(style.getName());
    	if(p == null) {
    		p = new Property(style.getName());    	
    	}
    	p.setValue(style);
    	this.backend.set(p);		
	}

	/**
	 * @param type
	 * @param component
	 * @return
	 * @see epos.ui.view.treeview.ColorStyle#getColor(epos.ui.view.treeview.ColorStyle.Colors, epos.ui.view.treeview.components.NodeComponent)
	 */
	public Color getColor(Colors type, NodeComponent component) {
		return style.getColor(type, component);
	}

	/**
	 * @param type
	 * @return
	 * @see epos.ui.view.treeview.ColorStyle#getColor(epos.ui.view.treeview.ColorStyle.Colors)
	 */
	public Color getColor(Colors type) {
		return style.getColor(type);
	}

	/**
	 * @param type
	 * @param color
	 * @param component
	 * @throws ImmutableException
	 * @see epos.ui.view.treeview.ColorStyle#setColor(epos.ui.view.treeview.ColorStyle.Colors, java.awt.Color, epos.ui.view.treeview.components.NodeComponent)
	 */
	public void setColor(Colors type, Color color, NodeComponent component) throws ImmutableException {
		style.setColor(type, color, component);
		storeColorSet(style);
	}

	/**
	 * @param type
	 * @param color
	 * @throws ImmutableException
	 * @see epos.ui.view.treeview.ColorStyle#setColor(epos.ui.view.treeview.ColorStyle.Colors, java.awt.Color)
	 */
	public void setColor(Colors type, Color color) throws ImmutableException {
		style.setColor(type, color);
		storeColorSet(style);
	}
	
	/**
	 * Add a color style to the collection of available styles
	 * @param style
	 */
	public void addColorStyle(ColorStyle style){
		styles.add(style);
		propertyChange.firePropertyChange(PROPERTY_STYLEADD, -1, 1);
	}
	/**
	 * remove color style from the collection of available styles
	 * @param style
	 */
	public void removeColorStyle(ColorStyle style){
		styles.remove(style);
		propertyChange.firePropertyChange(PROPERTY_STYLEREMOVE, -1, 1);
	}
	/**
	 * return the number of available styles
	 * @return
	 */
	public int size(){
		return styles.size();
	}
	/**
	 * return colorstyle i from the collection of available styles
	 * @param i
	 * @return
	 */
	public ColorStyle getColorStyle(int i){
		if(i >=0 && i < styles.size()) return styles.get(i);
		return null;
	}


	public void setColorStyle(String string) {
		for (ColorStyle s : styles) {
			if(s.getName().equals(string)) {
				setColorStyle(s);
				return;
			}
		}
		ColorStyle s= ColorStyle.createMutableCopy(string, style);
		setColorStyle(s);		
	}
	
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChange.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChange.removePropertyChangeListener(listener);		
	}
	public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
		propertyChange.addPropertyChangeListener(property, listener);
	}
	public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
		propertyChange.removePropertyChangeListener(property, listener);		
	}
	
	
}
