package epos.ui.view.treeview.components;

import java.util.Collection;
import java.util.EnumSet;

import epos.ui.view.treeview.TreeView;

public interface SelectionManager {
	public static enum SelectionType{NODE, PATH_TO_ROOT, SUBTREE}
	/**
	 * Sets the treeview that is managed by this selection manager
	 * @param view
	 */
	public void setPanel(TreeView view);
	
	/**
	 * Returns a collection of the selected nodes
	 * 
	 * @return
	 */
	public Collection<NodeComponent> getSelectedNodes();
	
	/**
	 * removes the given collection of nodes from the set of already selected nodes.
	 * 
	 * @param nodes
	 */
	public void removeFromSelection(Collection<NodeComponent> nodes);
	
	/**
	 * Adds the given collection of nodes to the collection of selected nodes 
	 * @param nodes
	 */
	public void addToSelection(Collection<NodeComponent> nodes);
	
	/**
	 * Sets the collection of selected nodes to the given collection.
	 * A given null value shuld result in deselecting all nodes.
	 * 
	 * @param selectedNodes
	 */
	public void setSelection(Collection<NodeComponent> selectedNodes);
	
	/**
	 * Adds a seletion listener to this selection manager
	 * @param listener
	 */
	public void addSelectionListener(SelectionListener listener);
	
	/**
	 * remove a selection listener
	 * @param listener
	 */
	public void removeSelectionListener(SelectionListener listener);
	
	/**
	 * Delegate a selection event to all listening selectionlisteners
	 * @param e
	 */ 
	public void fireSelectionEvent(SelectionEvent e);
	
	/**
	 * Adds a single node to the collection of selected nodes
	 * @param node
	 */
	public void addToSelection(NodeComponent node);
	
	/**
	 * Removes a single nodefrom the collection of selected nodes
	 * @param node
	 */
	public void removeFromSelection(NodeComponent node);
	

	/**
	 * Adds a single node to the collection of selected nodes
	 * @param node
	 */
	public void addToSelection(NodeComponent node, EnumSet<SelectionType> type);
	
	/**
	 * Removes a single nodefrom the collection of selected nodes
	 * @param node
	 */
	public void removeFromSelection(NodeComponent node, EnumSet<SelectionType> type);

	/**
	 * removes the given collection of nodes from the set of already selected nodes.
	 * 
	 * @param nodes
	 */
	public void removeFromSelection(Collection<NodeComponent> nodes, EnumSet<SelectionType> type);
	
	/**
	 * Adds the given collection of nodes to the collection of selected nodes 
	 * @param nodes
	 */
	public void addToSelection(Collection<NodeComponent> nodes, EnumSet<SelectionType> type);

	
	
	/**
	 * Returns true if the given node is selected
	 * @param node
	 * @return
	 */
	public boolean isNodeSelected(NodeComponent node);

	/**
	 * Returns a Collection of temporary selected nodes. This collection will be 
	 * cleared after the first call to this method.
	 * 
	 * @return Collection of temporary selected nodes
	 */
	public Collection<NodeComponent> getTemporarySelection();
	
	/**
	 * Sets the temporary selection.
	 * 
	 * @param selection
	 */
	public void setTemoprarySelection(Collection<NodeComponent> selection);
	
}
