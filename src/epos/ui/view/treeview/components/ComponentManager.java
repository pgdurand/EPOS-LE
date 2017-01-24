/*
 * Created on 10.06.2005
 */
package epos.ui.view.treeview.components;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import epos.model.graph.FixedIndexList;
import epos.model.tree.TreeNode;
import epos.qnode.properties.Property;
import epos.qnode.properties.PropertySet;
import epos.ui.util.SerializedBasicStroke;

/**
 * This interface defines a factory for NodeComponents. This helps to get
 * different visualizations for {@link TreeNode} objects.
 * <p>
 * Basically the {@link ComponentManager} manages links between {@link TreeNode} in a
 * tree and {@link NodeComponent} objects that visualize these nodes.
 * <p>
 * The factory methods important for object creation are {@link #getNodesComponent(TreeNode)},
 * which returns the instance if {@link NodeComponent} used to visualize the given {@link TreeNode}.
 * {@link #getNodeComponentsIterator()} provides an explicit iterator over all existing {@link NodeComponent}s.
 * The interface also extends {@link Iterable} and can be used in a foreach loop to iterate
 * over all created {@link NodeComponent}s. Remember however, that this does only iterate over
 * already existing {@link NodeComponent}s, so make sure that the complete tree is already
 * represented.
 * <p>
 * Beside the basic factory method, the {@link ComponentManager} provides managing methods 
 * for status and property states of {@link NodeComponent} instances. This is centralized in
 * this interface, because we have to make sure that certain properties are persistently 
 * stored and we want this to happen in a central place. This is the {@link ComponentManager}.
 * <p>
 * Implementations of this interface have to make sure that property change events are fired
 * properly. At least two major properties are important. A property change for the Visual Property
 * has to be fired every time a visual property (colors, strokes, fonts...) is changed. <br>
 * The other important property is covers structural changes. Every time a structural change
 * happens on the tree (collapsing, rotation...), the structural change has to be propagated.
 * This is really important, because the visualization does not have to distinguish between 
 * all sort of properties, but can focus on visual or structural changes.
 *  
 * @author Thasso Griebel (thasso@minet.uni-jena.de)
 *
 */
public class ComponentManager implements Iterable<NodeComponent>{
	
	/**
	 * A property indicating visual changes
	 */
    public static final String PROPERTY_VISUAL_CHANGE = "Visual Change";
    
    /**
     * A property indicating structural changes
     */
    public static final String PROPERTY_STRUCTURAL_CHANGE = "Structural Change";
    
    /**
     * Maps between nodes and their component representations
     */
    //protected HashMap<TreeNode, NodeComponent> nodes2components = new HashMap<TreeNode, NodeComponent>();
    protected FixedIndexList<NodeComponent> nodes2components;
    
    /**
     * used as a helper for the property change event support
     */
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * The minimum font size
	 */
	protected float minimumFontSize = 5f;
	
	/**
	 * The maximum font size 
	 */
	protected float maximumFontSize = 12f;
	
	/**
	 * The default label font
	 */
	protected Font labelFont = new Font("Arial", Font.PLAIN, 12);
	
	/**
	 * Maps components to specific fonts
	 */
	protected HashMap<NodeComponent, Font> labelFonts;
	
	/**
	 * The default node label property
	 */
	protected Property nodeLabelProperty = new Property("Label");

	/**
	 * Maps components to label properties
	 */
	protected HashMap<NodeComponent,Property> nodeLabelProperties;
	/**
	 * The default edge label property
	 */
	protected Property edgeLabelProperty = new Property("Distance to parent");
	/**
	 * Maps components to edge label properties
	 */
	protected HashMap<NodeComponent,Property> edgeLabelProperties;

	/**
	 * The default edge stroke
	 */
	protected Stroke edgeStroke =  new BasicStroke(1f);
	
	/**
	 * Maps components (their index) to strokes 
	 */
	protected HashMap<Integer, SerializedBasicStroke> edgeStrokes;

	/**
	 * Indicates whether dynamic font resizing is enabled
	 */
	protected boolean dynamicFontResizing = true;
	
	/**
	 * Indicates whether label clipping is enabled
	 */
	protected boolean clipNodeLabels = true;
	
	
	/**
	 * Indicates whether edge labels should be rendered.
	 */
	protected boolean drawEdgeLabels = false;
	
	/**
	 * Property set that is used to store properties
	 */
	protected PropertySet props;
    
	/**
	 * Returns the node component for a given tree node. If it does not exist
	 * it gets created by the method.
	 * 
	 * @param node the associated tree node
	 * @return component the {@link NodeComponent} representing the given node
	 */
	public NodeComponent getNodesComponent(TreeNode node){
		if (node == null) {
			return null;
		}	
		
		int index = node.getIndex();
		if(nodes2components == null){			
			nodes2components = new FixedIndexList<NodeComponent>();
		}
					
		NodeComponent component = nodes2components.get(index);
		if (component == null) {
			/*
			 * make sure that the index fits !
			 */
			component = new NodeComponent(node, this);
			component.setIndex(index);
			nodes2components.put(component);
		}
		return component;
	}

    /**
     * Returns an iterator over the {@link NodeComponent} collection of this manager.
     * The iterator only iterates over already created {@link NodeComponent} instances.
     * 
     * @return iterator over all created {@link NodeComponent} instances
     */
    public Iterator<NodeComponent> getNodeComponentsIterator(){    	
    	return nodes2components.iterator();
    }
    
    /**
     * Clears the manager and remove all nodes.
     * <p>
     * This has to make sure that the persistent storage is cleared and
     * all nodes are removed from the component cache.
     * 
     */ 
    public void clear(){
    	nodes2components = null;
    	//TODO: make sure the backend is cleared as well
    }
	//
    //
    /////// end provider methods
	
	
    /////// property manager methods
    //
    //
    /**
     * Add a {@link PropertyChangeListener}.
     * 
     * @param listener the listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener){
    	propertyChangeSupport.addPropertyChangeListener(listener);
    }
        
    /**
     * Remove a {@link PropertyChangeListener}.
     * 
     * @param listener the listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener){
    	propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
	/**
	 * Adds a {@link PropertyChangeListener} for a specific property.
	 * 
	 * @param propertyName the properties name
	 * @param listener the listener
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Removes a specific {@link PropertyChangeListener}.
	 * 
	 * @param propertyName the property name
	 * @param listener the listener
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener){
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
     * Fires a {@link PropertyChangeEvent}.
     * 
     * @param event the event
     */
    public void firePropertyChange(PropertyChangeEvent event){
    	propertyChangeSupport.firePropertyChange(event);
    }

	/**
	 * Fires a specific property change.
	 * <p>
	 * No Event is fired if oldValue and newValue are equal.
	 * 
	 * @param propertyName the property name
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}


    /**
     * Collapse/expands a given node.
     * <p>
     * Changes the nodes status by delegating to the nodes {@link NodeComponent#setCollapsed(boolean)}
     * method and fires a structural change.
     * 
     * @param node the node to collapse or expand
     * @param collapsed if true, the node gets collapsed, otherwise it gets expanded
     */
    public void setNodeCollapsed(NodeComponent node, boolean collapsed){
		node.setCollapsed(collapsed);
		firePropertyChange(PROPERTY_STRUCTURAL_CHANGE, -1, 1);
    }
    
    /**
     * Set the collapsed/expand status of a set of nodes.
     * <p>
     * For all nodes in the given collection, their collapse/expand status
     * is set to the given value.
     * <p>
     * This method delegates to the nodes {@link NodeComponent#setCollapsed(boolean)} method
     * and fire ONE structural change after iterating over all given nodes.
     * Always use this method if you have to switch state of more than one node.  
     * 
     * @param nodes set of nodes whose collapse/expand status gets switched
     */
    public void setCollapsed(Collection<NodeComponent> nodes, boolean collapsed){
		for (NodeComponent n : nodes) {
			n.setCollapsed(collapsed);
		}
		firePropertyChange(PROPERTY_STRUCTURAL_CHANGE, -1, 1);
    }
    
    /**
     * Switch the collapsed/expand status of a set of nodes.
     * <p>
     * For all nodes in the given collection, their collapse/expand status
     * is switched. A collapsed node gets expanded, a expanded node gets collapsed.
     * <p>
     * This method delegates to the nodes {@link NodeComponent#setCollapsed(boolean)} method
     * and fire ONE structural change after iterating over all given nodes.
     * Always use this method if you have to switch state of more than one node.  
     * 
     * @param nodes set of nodes whose collapse/expand status gets switched
     */
    public void setCollapsed(Collection<NodeComponent> nodes){
		for (NodeComponent n : nodes) {
			n.setCollapsed(!n.isCollapsed());
		}
		firePropertyChange(PROPERTY_STRUCTURAL_CHANGE, -1, 1);
    }
    	    
    /**
     * Rotates the children of the given node clock or anti clockwise.
     * <p>
     * The direct change happens via the {@link TreeNode#rotateChildren(boolean)} method
     * and a structural change event.
     * 
     * 
     * 
     * @param node the node to rotate
     * @param clockwise if true rotation is done clockwise
     */
    @SuppressWarnings("unchecked")
	public void rotateNode(NodeComponent node, boolean clockwise){
		TreeNode p = node.getNode();
		p.rotateChildren(clockwise);
		firePropertyChange(PROPERTY_STRUCTURAL_CHANGE, -1, 1);
		
		// get the rotation map
		HashMap<Integer, Integer> rotationMap = (HashMap<Integer, Integer>) loadProperty("rotateNode");
		if(rotationMap == null) rotationMap = new HashMap<Integer, Integer>();
		Integer c = rotationMap.get(p.getIndex());
		if(c != null){
			if(c+1 == p.childCount()){
				rotationMap.remove(p);
			}else{
				rotationMap.put(p.getIndex(), c+1);
			}
		}else{
			if(p.childCount() > 1){
				rotationMap.put(p.getIndex(), 1);
			}
		}
		//saveProperty("rotateNode", rotationMap);
    }
	    
    /**
     * Returns the minimal font size supported by this manager.
     * 
     * @return minFontSize the minimal fontsize
     */
	public float getMinimumFontSize(){
		return minimumFontSize;
	}
	
	/**
	 * Sets the minimal font size.
	 * <p>
	 * If the minimum font size is changed, a visual change event is propagated.
	 * 
	 * @param size the minimal font size
	 */
	public void setMinimumFontSize(float size){
		float old = this.minimumFontSize;
		this.minimumFontSize = size;
		firePropertyChange("minimumFontSize", old, this.minimumFontSize);
		firePropertyChange(PROPERTY_VISUAL_CHANGE, old, this.minimumFontSize);
	}
	
	/**
	 * Returns the maximal font size supported by this manager.
	 * 
	 * @return maxFontSize the maximum font size
	 */
	public float getMaximumFontSize(){
		return maximumFontSize;
	}
	
	/**
	 * Sets the maximal font size.
	 * <p>
	 * If the maximum font size is changed, a visual change event is propagated.
	 * 
	 * @param size the maximal font size
	 */
	public void setMaximumFontSize(float size){
		float old = this.maximumFontSize;
		this.maximumFontSize = size;
		firePropertyChange("maximumFontSize", old, this.maximumFontSize);
		firePropertyChange(PROPERTY_VISUAL_CHANGE, old, this.maximumFontSize);		
	}
	
	/**
	 * Returns the default font for vertices.
	 * 
	 * @return defaultFont the default font
	 */
	public Font getLabelFont(){
		return labelFont;
	}
	
	/**
	 * Returns a font specific to a node component. 
	 * <p>
	 * If no special font is defined, the default font
	 * should be returned via {@link #getLabelFont()}.
	 * 
	 * @param node the node
	 * @return font the nodes font
	 */
	public Font getLabelFont(NodeComponent node){
		if(node == null) return null;
		if (labelFonts == null)return getLabelFont();
		Font f = labelFonts.get(node);
		return f == null ? getLabelFont() : f;

	}
	
	/**
	 * Sets the default font
	 * 
	 * @param font the default font
	 */
	public void setLabelFont(Font font){
		Font old = this.labelFont;
		this.labelFont = font;
		firePropertyChange("labelFont", old, this.labelFont);
		firePropertyChange(PROPERTY_VISUAL_CHANGE, -1, 1);		
		//saveProperty("labelFont", this.labelFont);		
	}
	
	/**
	 * Sets a specific font for a given node.
	 * <p>
	 * If the given font is null, the stored value 
	 * for the node is set back to the default font.
	 * 
	 * @param font the Font
	 * @param node the node
	 */
	public void setLabelFont(Font font, NodeComponent node){
		if (font == null) {
			if (labelFonts == null)
				return;
			labelFonts.remove(node);
		} else {
			if (labelFonts == null)
				labelFonts = new HashMap<NodeComponent, Font>();
			labelFonts.put(node, font);
		}
		firePropertyChange(PROPERTY_VISUAL_CHANGE, -1, 1);
	}
	
    /**
     * Returns the default property that should be renderer as the node's label.
     * 
     * @return property used as nodes label
     */
    public Property getNodeLabelProperty(){
    	return nodeLabelProperty;
    }
    
    /**
     * Returns the property that should be renderer as the label of the given node.
     * <p>
     * If no node specific property is set, this should return the default property.
     * 
     * @param node the node
     * @return property used to be rendered as the nodes label
     */
    public Property getNodeLabelProperty(NodeComponent node){
		if (nodeLabelProperties == null)
			return getNodeLabelProperty();
		Property p = nodeLabelProperties.get(node);
		return p == null ? getNodeLabelProperty() : p;
    }
        
    /**
     * Sets the default property that is used as label for nodes.
     * <p>
     * Null is not permitted as default property
     * 
     * @param property the default label property
     */
    public void setNodeLabelProperty(Property property){
    	if(property == null) return;
    	Property old = this.nodeLabelProperty;
    	this.nodeLabelProperty = property;
    	firePropertyChange(PROPERTY_VISUAL_CHANGE, old, this.nodeLabelProperty);
    	firePropertyChange("nodeLabelProperty", old, this.nodeLabelProperty);
    }
    
    /**
     * Sets a specific node label property that is used as a label for the specified node.
     * <p>
     * A null value for the property resets the nodes property back to its default value.
     * 
     * @param property the new nodes label property
     * @param node the node
     */
    public void setNodeLabelProperty(Property property, NodeComponent node){
		if (property == null) {
			if (nodeLabelProperties == null)
				return;
			nodeLabelProperties.remove(node);
		} else {
			if (nodeLabelProperties == null) {
				nodeLabelProperties = new HashMap<NodeComponent, Property>();
			}
			nodeLabelProperties.put(node, property);
		}
		firePropertyChange(PROPERTY_VISUAL_CHANGE, -1, 1);
    }
    
    /**
     * Returns the default property that should be renderer as the Edge's label.
     * 
     * @return property used as edge label
     */
    public Property getEdgeLabelProperty(){
    	return edgeLabelProperty;
    }
    
    /**
     * Returns the property that should be renderer as the label of the edge to the parent of the given node.
     * 
     * @param node the node
     * @return property the edge label property for the given node
     */
    public Property getEdgeLabelProperty(NodeComponent node){
		if (edgeLabelProperties == null)
			return getEdgeLabelProperty();
		Property p = edgeLabelProperties.get(node);
		return p == null ? getEdgeLabelProperty() : p;
    }
        
    /**
     * Sets the default property that is used as label for Edges.
     * <p>
     * Null is not permitted as default property.
     * 
     * @param property the default edge label property
     */
    public void setEdgeLabelProperty(Property property){
    	if(property == null) return;
    	Property old = this.edgeLabelProperty;
    	this.edgeLabelProperty = property;
    	firePropertyChange(PROPERTY_VISUAL_CHANGE, old, this.edgeLabelProperty);
    	firePropertyChange("edgeLabelProperty", old, this.edgeLabelProperty);
    }
    
    /**
     * Sets a specific Edge label property that is used as a label for the specified Edge.
     * 
     * @param property the edge label property
     * @param Edge the edge
     */
    public void setEdgeLabelProperty(Property property, NodeComponent node){
		if (property == null) {
			if (edgeLabelProperties == null)
				return;
			edgeLabelProperties.remove(node);
		} else {
			if (edgeLabelProperties == null)
				edgeLabelProperties = new HashMap<NodeComponent, Property>();
			edgeLabelProperties.put(node, property);
		}
		firePropertyChange(PROPERTY_VISUAL_CHANGE, -1, 1);
    }
    
    /**
     * Returns the default edge stroke.
     * 
     * @return stroke the default edge stroke
     */
    public Stroke getEdgeStroke(){
    	return edgeStroke;
    }
    
    /**
     * Sets the default edge stroke.
     * <p>
     * Null is not permitted as default edge stroke
     * 
     * @param stroke the new default edge stroke
     */
    public void setEdgeStroke(Stroke stroke){
		if (stroke == null)
			return;
		Stroke old = this.edgeStroke;
		this.edgeStroke = stroke;
		firePropertyChange(PROPERTY_VISUAL_CHANGE, old, this.edgeStroke);
		firePropertyChange("edgeStroke", old, this.edgeStroke);
		//saveProperty("edgeStroke", new SerializedBasicStroke((BasicStroke) stroke));
    }
    
    /**
     * Returns the edge stroke for the edge to the parent of the given node.
     * 
     * @param node the node
     * @return stroke the edge stroke
     */
    public Stroke getEdgeStroke(NodeComponent node){
		if (edgeStrokes == null) {
			return getEdgeStroke();
		}
		SerializedBasicStroke bs = edgeStrokes.get(node.getNode().getIndex()); 
		if (bs == null) {
			return getEdgeStroke();
		}
		return bs.getStroke();
    }
    
    /**
     * Set the edge stroke for the edge to parent of the given node.
     * <p>
     * A null stroke resets the component value to the default
     * 
     * @param stroke
     * @param node
     */
    public void setEdgeStroke(Stroke stroke, NodeComponent node){
		if (stroke == null || stroke.equals(edgeStroke)) {
			if (edgeStrokes == null)
				return;
			edgeStrokes.remove(node.getNode().getIndex());
		} else {
			if (edgeStrokes == null)
				edgeStrokes = new HashMap<Integer, SerializedBasicStroke>();
			edgeStrokes.put(node.getNode().getIndex(), new SerializedBasicStroke(stroke));
		}		
		firePropertyChange(PROPERTY_VISUAL_CHANGE, -1, 1);
		//saveProperty("edgeStrokes", edgeStrokes);
    }
    
	/**
	 * Update the stroke for a set of nodes
	 * @param stroke
	 * @param nodes
	 */
	public void setEdgeStroke(BasicStroke stroke, Collection<NodeComponent> nodes){
		for (NodeComponent node : nodes) {
			if (stroke == null || stroke.equals(edgeStroke)) {
				if (edgeStrokes == null)
					return;
				edgeStrokes.remove(node.getNode().getIndex());
			} else {
				if (edgeStrokes == null)
					edgeStrokes = new HashMap<Integer, SerializedBasicStroke>();
				edgeStrokes.put(node.getNode().getIndex(), new SerializedBasicStroke(stroke));
			}
		}
		firePropertyChange(PROPERTY_VISUAL_CHANGE, -1, 1);
		//saveProperty("edgeStrokes", edgeStrokes);
	}
        
	/**
	 * Enable edge labels drawing
	 * @param drawEdgeLabels
	 */
	public void setDrawEdgeLabels(boolean drawEdgeLabels){
		boolean old = this.drawEdgeLabels;
		this.drawEdgeLabels = drawEdgeLabels;
		firePropertyChange("drawEdgeLabels", old, this.drawEdgeLabels);
		firePropertyChange(PROPERTY_VISUAL_CHANGE, old, this.drawEdgeLabels);
		//saveProperty("drawEdgeLabels", drawEdgeLabels);
	}
	/**
	 * 
	 * @return true if edge labels are painted
	 */
	public boolean isDrawEdgeLabels(){
		return drawEdgeLabels;
	}

    
    /**
     * Returns true if font sizes should be automatically adjusted by
     * the renderer.
     * 
     * @return
     */
    public boolean isDynamicFontResizing(){
    	return dynamicFontResizing;
    }
    /**
     * Enables or disabled dynamic font resizing.
     * 
     * @param resizeFonts
     */
    public void setDynamicFontResizing(boolean resizeFonts){
    	boolean old = this.dynamicFontResizing;
    	this.dynamicFontResizing = resizeFonts;
    	firePropertyChange(PROPERTY_VISUAL_CHANGE, old, this.dynamicFontResizing);
    	firePropertyChange("dynamicFontResizing", old, this.dynamicFontResizing);
    	//saveProperty("dynamicFontResizing", this.dynamicFontResizing);
    }
    /**
     * Returns true if labels should be clipped to fit a given width, 
     * otherwise the width should be ignored
     * 
     * @return clipping true if node label clipping is enabled
     */
    public boolean isClipNodeLabels(){
    	return clipNodeLabels;
    }
    
    /**
     * Set clipping of labels.
     *  
     * @param clipLabels
     */
    public void setClipNodeLabels(boolean clipLabels){
    	boolean old = this.clipNodeLabels;
    	this.clipNodeLabels = clipLabels;
    	firePropertyChange("clipNodeLabels", old, this.clipNodeLabels);
    	firePropertyChange(PROPERTY_VISUAL_CHANGE, old, this.clipNodeLabels);
    	//saveProperty("clipNodeLabels", this.clipNodeLabels);
    }

	/**
	 * Delegates calls to {@link #getNodeComponentsIterator()}
	 */
	public Iterator<NodeComponent> iterator() {
		return getNodeComponentsIterator();
	}    

	/**
	 * Loads a property value from the property set
	 * 
	 * @param name the name of the property
	 * @return value of the property or null
	 */
	protected Serializable loadProperty(String name) {
		if (props != null) {
			Property p = props.getProperty(name);
			if (p != null) {
				return p.getValue();
			}
		}
		return null;
	}
	


}


