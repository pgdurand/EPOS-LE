package epos.ui.view.treeview;

import java.awt.Color;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import epos.ui.view.treeview.components.NodeComponent;

public class ColorStyle implements Serializable{
	private String name;
			
	public static enum Colors{
			background("Background"), 
			foreground("Foreground"),
			edgeColor("Edge Color"),
			nodeColor("Node Color"),
			fontColor("Font Color"),
			edgeSelection("Edge Selection"),
			nodeSelection("Node Selection");

			
			private String name;
			
			Colors(String name){
				this.name = name;
			}
			public String getName(){
				return name;
			}	
	};
	private Map<Colors, Color> defaults;
	private Map<Integer, Map<Colors, Color>> nodeColors; 
		
	private boolean immutable = false;
	public ColorStyle() {
		super();
	}
	
	public ColorStyle(String name, boolean immutable) {
		this();
		this.name = name;
		this.immutable = immutable;
		defaults = new HashMap<Colors, Color>();
		nodeColors = new HashMap<Integer, Map<Colors,Color>>();
	}	

	public ColorStyle(String name) {
		this(name, false);	
	}

	public ColorStyle(String name, Color background, Color foreground, Color edgeColor,
			Color nodeColor, Color fontColor, Color edgeSelection,
			Color nodeSelection, boolean immutable) {
		this(name);
		try{
			setColor(Colors.background, background);
			setColor(Colors.foreground, foreground);
			setColor(Colors.edgeColor, edgeColor);
			setColor(Colors.nodeColor, nodeColor);
			setColor(Colors.fontColor, fontColor);
			setColor(Colors.edgeSelection, edgeSelection);
			setColor(Colors.nodeSelection, nodeSelection);
		}catch (ImmutableException e) {}
		this.immutable = immutable; 
	}

	public ColorStyle(String name, Color background, Color foreground, Color edgeColor,
			Color nodeColor, Color fontColor, Color edgeSelection,
			Color nodeSelection) {
		this(name, background, foreground, edgeColor, nodeColor, fontColor, edgeSelection, nodeSelection, false);
	}
	
	public void setColor(Colors type, Color color) throws ImmutableException{
		if(isImmutable()){
			throw new ImmutableException("Color Style " + name + " is immutable. Can not change " + type.getName());
		}
		defaults.put(type, color);
	}
	
	public Color getColor(Colors type){
		return defaults.get(type);
	}
	
	public void setColor(Colors type, Color color, NodeComponent component) throws ImmutableException{
		if(isImmutable()){			
			throw new ImmutableException("Color Style " + name + " is immutable. Can not change " + type.getName());
		}
		if(component == null){
			return;
		}
			
		/*
		 * if this switches back to the default color, remove the specific entry and/or map
		 */
		if(color.equals(getColor(type))){
			Map<Colors, Color> nc = nodeColors.get(component.getNode().getIndex());
			if(nc != null){
				nc.remove(type);
				/*
				 * check for size, if 0, also remove the color map for this node
				 */
				if(nc.size() <= 0){
					nodeColors.remove(component.getNode().getIndex());
				}
			}
		}else{
			/*
			 * ok, its not the default so, we first get the map of custom colors for the given node
			 */
			Map<Colors, Color> nc = nodeColors.get(component.getNode().getIndex());
			if(nc == null){
				/*
				 * create a new list
				 */
				nc = new HashMap<Colors, Color>();
				nodeColors.put(component.getNode().getIndex(), nc); 
			}
			/*
			 * add the custom color
			 */
			nc.put(type, color);
		}
	}
	
	public Color getColor(Colors type, NodeComponent component){
		/*
		 * check if the colors are defined
		 */
		Map<Colors, Color> nc = nodeColors.get(component.getNode().getIndex());
		Color color = null;
		if(nc == null){
			/*
			 * return the default
			 */
			color = defaults.get(type);			
		}else{
			/*
			 * get the custom color if their is one, else return the default
			 */
			color = nc.get(type);
			if(color == null){
				color = defaults.get(type);
			}				
		}		
		return color;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if(immutable){
			throw new RuntimeException("Colorstyle " + name + " is immutable, can not change color!");			
		}

		this.name = name;
	}

	/**
	 * @return the immutable
	 */
	public boolean isImmutable() {
		return immutable;
	}	
	
	/**
	 * copy the given color style to a new mutable colorstyle with teh same
	 * base colors. Specific node color attributes are not set.
	 * 
	 * @param new_name
	 * @param toCopy
	 * @return
	 */
	public static ColorStyle createMutableCopy(String new_name, ColorStyle toCopy){
		ColorStyle ns = new ColorStyle(new_name, false);
		EnumSet<Colors> all = EnumSet.allOf(Colors.class);
		try{
			for (Colors c : all) {
				ns.setColor(c, toCopy.getColor(c));
			}
		}catch(ImmutableException e){}
		return ns;
	}
	
	public String toString(){
		return getName();
	}

	public void setImmutable(boolean immutable) {
		this.immutable = immutable;
	}
	
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof ColorStyle)) return false;
		ColorStyle s = (ColorStyle) o;
		return getName().equals(s.getName());
	}
	
	public int hashcode() {
		return name == null ? super.hashCode() : name.hashCode();
	}
}