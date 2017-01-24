/*
 * Created on Aug 23, 2003
 */
package epos.ui.view.treeview;

import java.util.EventObject;

import epos.ui.view.treeview.layouts.TreeLayouter;


/**
 * LayoutChange Event is fired if the Layoutmanager of a Component changes.
 * 
 * @author Thasso
  */
public class LayoutChangedEvent extends EventObject {

	/**
	 * The Layoutmanager
	 */
	private TreeLayouter layout;
	/**
	 * The Panel.
	 */
	private TreeView panel;
	
	/**
	 * This Event is called if a LayoutManager changes.
	 * 
	 * @param source
	 */	
	public LayoutChangedEvent(Object source, TreeView panel, TreeLayouter layout) {
		super(source);
		this.layout = layout;
		this.panel = panel;
	}

	
	/**
	 * Returns the new Layoutmanager.
	 * 
	 * @return LayoutManager
	 */
	public TreeLayouter getLayout() {
		return layout;
	}

	/**
	 * Returns the Panel, where the Layout changed.
	 * 
	 * @return
	 */
	public TreeView getPanel() {
		return panel;
	}
}
