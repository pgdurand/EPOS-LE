package epos.ui.view.treeview.components;

import java.util.Collection;

import javax.swing.event.EventListenerList;

import epos.ui.view.treeview.TreeView;

public abstract class AbstractSelectionManager implements SelectionManager{

	protected TreeView parent;
	protected EventListenerList listenerList = new EventListenerList();
	
	public void addSelectionListener(SelectionListener listener) {
		listenerList.add(SelectionListener.class, listener);
	}
	public void removeSelectionListener(SelectionListener listener) {
		listenerList.remove(SelectionListener.class, listener);
	}
	public void fireSelectionEvent(SelectionEvent e) {
		if(listenerList.getListenerCount() > 0){		     
		     Object[] listeners = listenerList.getListenerList();
		     for (int i = listeners.length-2; i>=0; i-=2) {
		         if (listeners[i]==SelectionListener.class) {
		             // Lazily create the event:
		             if (e == null)
		                 e = new SelectionEvent(this);
		             ((SelectionListener)listeners[i+1]).selectionChanged(this);
		         }
		     }
		}
	}

	public void setPanel(TreeView view) {
		this.parent = view;
	}
	
	/**
	 * Adds a single node to the collection of selected nodes
	 * @param node
	 */
	public void addToSelection(NodeComponent node){
		addToSelection(node, null);
	}
	
	/**
	 * Removes a single nodefrom the collection of selected nodes
	 * @param node
	 */
	public void removeFromSelection(NodeComponent node){
		removeFromSelection(node, null);		 
	}

	/**
	 * removes the given collection of nodes from the set of already selected nodes.
	 * 
	 * @param nodes
	 */
	public void removeFromSelection(Collection<NodeComponent> nodes){
		removeFromSelection(nodes, null);
	}
	
	/**
	 * Adds the given collection of nodes to the collection of selected nodes 
	 * @param nodes
	 */
	public void addToSelection(Collection<NodeComponent> nodes){
		addToSelection(nodes, null);
	}

}
