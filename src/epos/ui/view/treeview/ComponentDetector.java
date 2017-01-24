package epos.ui.view.treeview;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import epos.ui.view.treeview.components.NodeComponent;

public class ComponentDetector {
	/**
	 * KD-Tree for Reange search
	 */
	protected KDTree nodeTree = null;

	/**
	 * List of visible nodes
	 */
	protected List<NodeComponent> visibleNodes = new ArrayList<NodeComponent>();

	protected Rectangle lastVisibleNodeSearch;

	protected Rectangle lastSearch;

	protected Object[] lastResult;

	protected TreeView view;
	
	public ComponentDetector(TreeView view){
		super();
		this.view = view; 
	}

	public Object[] searchNodeComponents(Rectangle r) {
		if (lastSearch != null && lastSearch.equals(r))
			return lastResult;

		checkSearchTree();
		try {
			lastResult = nodeTree.range(new double[] { r.x, r.y },
					new double[] { r.x + r.width, r.y + r.height });
		} catch (KeySizeException e) {
			e.printStackTrace();
		}
		lastSearch = r;
		return lastResult;
	}

	public List<NodeComponent> getVisibleNodes(Rectangle r) {
		if (lastVisibleNodeSearch != null
				&& lastVisibleNodeSearch.equals(r))
			return visibleNodes;

		Object[] nodes = searchNodeComponents(r);
		visibleNodes.clear();
		for (int i = 0; i < nodes.length; i++) {
			if (((NodeComponent) nodes[i]).isVisible())
				visibleNodes.add((NodeComponent) nodes[i]);
		}
		lastVisibleNodeSearch = r;
		return visibleNodes;
	}

	public List<NodeComponent> getVisibleNodes() {
		return visibleNodes;
	}

	protected NodeComponent getNodeNextTo(Point p, int w, int h) {
		NodeComponent highlightNode = null;
		// double hdist = -1;

		Object[] o = searchNodeComponents(new Rectangle(p.x - (w / 2), p.y
				- (h / 2), 1 + w, 1 + h));
		if (o == null || o.length == 0) {
			return null;
		}
		// if(o.length > 1){
		// reset old highlighted node
		// Point2D comparePoint = new Point2D.Double(-1, -1);
		for (int i = 0; i < o.length; i++) {
			NodeComponent n = (NodeComponent) o[i];
//			if (n.contains(mousePoint)) {
//				return n;
//			}

			// if (n.getBounds().contains(mousePoint)) {
			// if (highlightNode == null) {
			// highlightNode = n;
			// hdist = mousePoint.distance(n.getX(), n.getY());
			// } else {
			// comparePoint.setLocation(n.getX(), n.getY());
			// if (hdist > comparePoint.distance(mousePoint)) {
			// highlightNode = n;
			// hdist = mousePoint.distance(n.getX(), n.getY());
			// }
			// }
			// }
		}

		// }else{
		// highlightNode = (NodeComponent) o[0];
		// }
		return highlightNode;
	}

	protected void checkSearchTree() {
		if (nodeTree == null) {
			nodeTree = new KDTree(2);
			Iterator it = view.getComponentManager().getNodeComponentsIterator();
			while (it.hasNext()) {
				NodeComponent n = (NodeComponent) it.next();

				if (!n.isVisible())
					continue;
				try {
					nodeTree.insert(new double[] { n.getX(), n.getY() }, n);
					// nodeTree.insert(new double[] { x+w,y}, n);
					// nodeTree.insert(new double[] { x+w,y+h}, n);
					// nodeTree.insert(new double[] { x,y+h}, n);

				} catch (KeySizeException e) {
					// e.printStackTrace();
				} catch (KeyDuplicateException e) {
					// e.printStackTrace();
				}
			}
		}
	}

	public void prepareUpdate() {
		nodeTree = null;
		lastSearch = null;
		lastVisibleNodeSearch = null;
	}

}
