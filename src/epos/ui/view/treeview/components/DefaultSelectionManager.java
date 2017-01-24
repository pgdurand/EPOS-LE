package epos.ui.view.treeview.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import epos.ui.view.treeview.TreeView;

public class DefaultSelectionManager extends AbstractSelectionManager{
	protected Collection<NodeComponent> selectedNodes;
	protected Collection<NodeComponent> tempNodes;
	public DefaultSelectionManager(){
		super();
		selectedNodes = new ArrayList<NodeComponent>();
	}
	public DefaultSelectionManager(TreeView view){
		this();
		setPanel(view);
	}

	public Collection<NodeComponent> getSelectedNodes() {
		return selectedNodes;
	}
	public void addToSelection(Collection<NodeComponent> nodes, EnumSet<SelectionType> type) {
		if(nodes == null || nodes.size() == 0) return;
		if(type == null || type.contains(SelectionType.NODE)){
			selectedNodes.addAll(nodes);
		}
		if(type != null){
			if(type.contains(SelectionType.PATH_TO_ROOT)){
				ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
				for (NodeComponent p : nodes) {
					if(p == null) continue;
					p = p.getParent();
					while(p != null){
						newAdds.add(p);
						p = p.getParent();
					}					
				}
				selectedNodes.addAll(newAdds);
			}
			if(type.contains(SelectionType.SUBTREE)){
				ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
				for (NodeComponent p : nodes) {
					for (NodeComponent c : p.depthFirstIterator()) {
						newAdds.add(c);
					}
				}
				selectedNodes.addAll(newAdds);
			}
		}
		fireSelectionEvent(null);
	}

	public void removeFromSelection(Collection<NodeComponent> nodes, EnumSet<SelectionType> type) {
		if(nodes == null || nodes.size() == 0) return;
		if(type == null || type.contains(SelectionType.NODE)){
			selectedNodes.removeAll(nodes);
		}
		if(type != null){
			if(type.contains(SelectionType.PATH_TO_ROOT)){
				ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
				for (NodeComponent p : nodes) {
					p = p.getParent();
					while(p != null){
						newAdds.add(p);
						p = p.getParent();
					}					
				}
				selectedNodes.removeAll(newAdds);
			}
			if(type.contains(SelectionType.SUBTREE)){
				ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
				for (NodeComponent p : nodes) {
					for (NodeComponent c : p.depthFirstIterator()) {
						newAdds.add(c);
					}
				}
				selectedNodes.removeAll(newAdds);
			}
		}

		fireSelectionEvent(null);
	}
	public void addToSelection(NodeComponent node, EnumSet<SelectionType> type) {
		if(node == null )return;
		if(type == null || type.contains(SelectionType.NODE)){
			selectedNodes.add(node);
		}
		if(type != null){
			if(type.contains(SelectionType.PATH_TO_ROOT)){
				ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
				node = node.getParent();
				while(node != null){
					newAdds.add(node);
					node = node.getParent();
				}					
				selectedNodes.addAll(newAdds);
			}
			if(type.contains(SelectionType.SUBTREE)){
				ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
				for (NodeComponent c : node.depthFirstIterator()) {
					newAdds.add(c);
				}
				selectedNodes.addAll(newAdds);
			}
		}

		fireSelectionEvent(null);
	}
	public void removeFromSelection(NodeComponent node, EnumSet<SelectionType> type) {
		if(node == null )return;
		if(type == null || type.contains(SelectionType.NODE)){
			selectedNodes.remove(node);
		}
		if(type != null){
			if(type.contains(SelectionType.PATH_TO_ROOT)){
				ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
				node = node.getParent();
				while(node != null){
					newAdds.add(node);
					node = node.getParent();
				}					
				selectedNodes.removeAll(newAdds);
			}
			if(type.contains(SelectionType.SUBTREE)){
				ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
				for (NodeComponent c : node.depthFirstIterator()) {
					newAdds.add(c);
				}
				selectedNodes.removeAll(newAdds);
			}
		}
		fireSelectionEvent(null);
		
	}

	public void setSelection(Collection<NodeComponent> selectedNodes) {
		this.selectedNodes.clear();
		if(selectedNodes != null){
			this.selectedNodes.addAll(selectedNodes);
		}
		fireSelectionEvent(null);
	}
	
	public boolean isNodeSelected(NodeComponent node) {
		return selectedNodes.contains(node);
	}
	public Collection<NodeComponent> getTemporarySelection() {
		Collection<NodeComponent> ret = tempNodes;
		return ret;
	}
	public void setTemoprarySelection(Collection<NodeComponent> selection) {
		this.tempNodes = selection;
		fireSelectionEvent(null);
	}	
}
