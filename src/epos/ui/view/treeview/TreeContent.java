/*
 * Created on 05.04.2005
 */
package epos.ui.view.treeview;

import javax.swing.event.EventListenerList;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.ui.view.ppane.AbstractContent;


/**
 * @author Thasso
 *
 */
public class TreeContent extends AbstractContent{
	
	protected EventListenerList listenerList;
    protected Tree tree;
		
    public TreeContent(Tree tree){
        this.tree = tree;
    }
	
	/**
	 * this returns the tree if the TreeQNode contais one.
	 * If not, its starts the conputation.
	 * @return
	 */
	public synchronized Tree getTree() {
        return tree;
	}
	public void setTreeChanged(boolean changed){
	}
	public void updateTreeNode(TreeNode node) {
	}
}
