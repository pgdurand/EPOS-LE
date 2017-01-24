/*
 * Created on 05.07.2005
 */
package epos.model.tree;

import java.io.Serializable;

import org.jdesktop.application.AbstractBean;

import epos.model.graph.DefaultEdgeFactory;
import epos.model.graph.Edge;
import epos.model.graph.EdgeFactory;
import epos.model.graph.EdgeType;
import epos.model.graph.FixedIndexList;
import epos.model.graph.Graph;
import epos.qnode.properties.PropertySet;

/**
 * This is the base class for rooted trees in EPoS. The tree extends {@link Graph} and consists
 * of {@link TreeNode}s and {@link Edge}s.
 * <p>
 * The tree implementation uses a {@link FixedIndexList} to store its vertices. This enables indexed
 * based access to tree nodes. The structure ensures that a nodes index is fixed after its initial setup.
 * For example, if you create a Tree and add a new node:
 * <pre>
 *  Tree t = new Tree();
 *  TreeNode newNode = new TreeNode();
 *  int index = t.addVertex(newNode);
 * </pre>
 * The returned index is the unique index of newNode and newNode will be the node returned by {@link #getVertex(int)}
 * until you delete the node. The index is fixed.
 * <p>
 * To create trees, we provide a little helper method {@link Tree#fromNewick(String)} that takes a
 * tree string in Newick format (i.e. ((A,B),C); ) and returns the corresponding tree. 
 *  
 * <p>
 * The tree is rooted and you can access the trees root node using {@link #getRoot()}. Edges are
 * directed and the implementation does not distinguish between {@link EdgeType}s. 
 * <p>
 * To create copies if a tree use {@link #cloneTree()} or {@link #getSubtree(TreeNode)} if you want
 * to get only a subtree of this tree.
 * <p>
 * The tree supports least common ancestor detection using {@link #findLeastCommonAncestor(TreeNode, TreeNode)} and
 * {@link #findLeastCommonAncestor(TreeNode...)}. 
 *
 *  
 * @author Thasso Griebel (thasso@minet.uni-jena.de)
 *
 */
public class Tree extends AbstractBean implements Graph<TreeNode, Edge<TreeNode>>, Cloneable
		 {
	/**
	 * The root node
	 */
	private TreeNode root;
	/**
	 * Stores the nodes
	 */
	private FixedIndexList<TreeNode> nodes;
	/**
	 * The trees property set
	 */
	private PropertySet propertySet = new PropertySet();
	
	/**
	 * Create edges
	 */
	private EdgeFactory<? extends Edge<TreeNode>, TreeNode> edgefactory;
	/**
	 * The trees name
	 */
	private String name;
		
	/**
	 * Create a new empty tree with a {@link DefaultEdgeFactory} and directed edges. 
	 */
	public Tree() {
		super();
		addEdgeFactory(new DefaultEdgeFactory<TreeNode>(EdgeType.DIRECTED));
		nodes = new FixedIndexList<TreeNode>();
	}
	/**
	 * Returns the root node of this tree. If the root was not set explicitly, this
	 * searches the tree and returns the first node with in-degree equal to 0.
	 * <p>
	 * The search is only done once and the found root node is cached, so make sure to call
	 * that after the tree is complete (all nodes and edges inserted) or call {@link #setRoot(TreeNode)} 
	 * with null to reset the root node cache.
	 *   
	 * @return root node of the tree
	 */
	public TreeNode getRoot() {
		if (root == null) {
			// find root, do a search for the node
			// with inDegree 0
			for (TreeNode v : vertices()) {
				if (v.getParent() == null) {
					setRoot(v);
				}
			}
		}
		return (TreeNode) root;
	}
	/**
	 * Sets the root node. This explicitly sets a root node. The node is returned by
	 * {@link #getRoot()} but this does NOT rearrange the tree. 
	 * You have to be careful what you set here.
	 * <p>
	 * Null is permitted and resets the root cache. The next call to {@link #getRoot()} will
	 * search for a root node.
	 * 
	 * @param root the root
	 */
	public void setRoot(TreeNode root) {
		this.root = root;
	}
	
	/**
	 * Create a new {@link Tree} that represents the subtree of this tree rooted at the given
	 * node. The returned tree is a new tree that contains clones of the {@link TreeNode}s of this tree.
	 * The node copies are created using {@link TreeNode#cloneNode()} and are
	 * new instances, so you will not have object equality ( n != n.cloneNode() ). Node equality using 
	 * {@link TreeNode#equals(Object)} will work at least for labeled nodes (because the labels are compared), but
	 * for performance reasons we do not do deep checks.  
	 * <p>
	 * If this tree does not contain the given node, null is returned. 
	 * 
	 * @param innerNode the root node of the subtree
	 * @return subtree rooted at the given node or null
	 */
	public Tree getSubtree(TreeNode n) {
		if(n == null) throw new NullPointerException();
		if (nodes.get(n.getIndex()) != n){
			return null;
		}
		Tree r = new Tree();
		TreeNode root = n.cloneNode();
		r.addVertex(root);
		hangIn(root, n, r);
		return r;
	}
	
	/**
	 * Clones the complete tree. This calls {@link #getSubtree(TreeNode)} using the root
	 * node of this tree.
	 * 
	 * @return tree clone of this tree
	 */
	public Tree cloneTree(){
		return getSubtree(getRoot());		
	}
	/**
	 * Clones the tree located at the given node. Calls {@link #getSubtree(TreeNode)} with the
	 * given node.
	 * 
	 * @param node root of the cloned subtree
	 * @return tree clone of the tree rooted at given node
	 */
	public Tree cloneTree(TreeNode node){
		return getSubtree(node);
	}
	
	/*
	 * Helper used in getSubtree() to do recursive cloning
	 */
	@SuppressWarnings("unchecked")
	private void hangIn(TreeNode newParent, TreeNode oldParent, Tree tree) {
		for (TreeNode n : oldParent.children()) {
			TreeNode newNode = n.cloneNode();
			tree.addVertex(newNode);
			Edge edge = tree.addEdge(newParent, newNode);
			edge.setWeight(n.getDistanceToParent());
			hangIn(newNode, n, tree);			
		}
	}
	
	/**
	 * Returns the number leaves of the tree. 
	 * (Leaves are all vertices with outdegree == 0).
	 * <br> THIS DOES NOT CACHE and always recounts.
	 *  
	 * @return number of leaves
	 */
	public int getNumTaxa() {
		int nTaxa = 0;
		for (TreeNode v : vertices()) {
			if (v.isLeaf())
				nTaxa++;
		}
		return nTaxa;
	}

	/**
	 * Adds an edge from n1 to n2. This {@link EdgeType} is ignored as rooted
	 * trees use directed edges.
	 * 
	 */
	public Edge<TreeNode> addEdge(TreeNode n1, TreeNode n2, EdgeType type) {
		return addEdge(n1, n2);
	}
	/**
	 * Uses the trees {@link EdgeFactory} to create an edge from parent to child.
	 */
	@SuppressWarnings("unchecked")
	public Edge<TreeNode> addEdge(TreeNode parent, TreeNode child) {
		Edge e = edgefactory.createEdge(parent, child);
		parent.addEdge(e);
		child.addEdge(e);
		return e;
	}
	/**
	 * Sets the edge factory. The tree implementation uses only one {@link EdgeFactory}
	 * and this can be used to override the default edge factory. 
	 */
	public void addEdgeFactory(
			EdgeFactory<? extends Edge<TreeNode>, TreeNode> factory) {
		this.edgefactory = factory;
	}
	/**
	 * Adds a node to the tree.
	 */
	public int addVertex(TreeNode n) {
		n.setGraph(this);
		nodes.put(n);
		return n.getIndex();
	}
	/**
	 * Returns true if here is an edge connecting n1 and n2 (directed from n1 to n2).
	 * The {@link Graph} interface froces an {@link EdgeType} here, but it is ignored by
	 * the tree implementation.
	 */
	public boolean containsEdge(TreeNode n1, TreeNode n2, EdgeType type) {
		return containsEdge(n1, n2);
	}
	/**
	 * Returns true if there exists an edge from n1 to n2. 
	 */
	public boolean containsEdge(TreeNode n1, TreeNode n2) {
		if(n1 == null || n2 == null) return false;
		return n1.getEdge(n2) != null;
	}
	/**
	 * Returns the number of edges. This assumes that the tree is connected and returns
	 * {@link #vertexCount()}-1 instead of really counting the edges
	 */
	public int edgeCount() {
		return vertexCount() - 1;
	}
	/**
	 * Directly delegates to {@link #edgeCount()}
	 */
	public int edgeCount(EdgeType type) {
		return edgeCount();
	}
	/**
	 * Returns the {@link Edge} connecting n1 to n2 or null.
	 * 
	 */
	public Edge<TreeNode> getEdge(TreeNode n1, TreeNode n2) {
		if(n1 == null || n2 == null) return null;
		return n1.getEdge(n2);
	}
	/**
	 * Delegates to {@link #getEdge(TreeNode, TreeNode)}
	 */
	public Edge<TreeNode> getEdge(TreeNode n1, TreeNode n2, EdgeType type) {
		return getEdge(n1, n2);
	}
	/**
	 * Returns the {@link TreeNode} at given index or null of no such node exists.
	 */
	public TreeNode getVertex(int index) {
		return nodes.get(index);
	}
	/**
	 * Delegates to {@link #removeEdge(TreeNode, TreeNode)}
	 */
	public void removeEdge(TreeNode n1, TreeNode n2, EdgeType type) {
		removeEdge(n1, n2);
	}
	/**
	 * Removes the edge from n1 to n2 from the tree (if such edge exists)
	 */
	public void removeEdge(TreeNode n1, TreeNode n2) {
		Edge<TreeNode> e = n1.getEdge(n2);
		if (e != null) {
			n1.removeEdge(e);
			n2.removeEdge(e);
		}
	}
	/**
	 * Removes given {@link TreeNode} from this tree.
	 */
	public void removeVertex(TreeNode n) {
		if(n == null) return;
		if(nodes.remove(n)){
			n.clear();
		}		
	}
	/**
	 * Removes {@link TreeNode} with given index.
	 */
	public void removeVertex(int index) {
		TreeNode n = nodes.remove(index);
		if (n != null)
			n.clear();
	}
	/**
	 * Returns the number of vertices 
	 */
	public int vertexCount() {
		return nodes.size();
	}
	/**
	 * Provides an {@link Iterable} over all vertices
	 */
	public Iterable<TreeNode> vertices() {
		return nodes;
	}

	/**
	 * This method returns the last common ancestor of two nodes of a tree.
	 * <p>
	 * This eventually returns null if one of the given nodes is null or the given nodes
	 * are not in this tree.
	 * 
	 * @param nodeA
	 * @param nodeB
	 * @return the LCA of nodeA and nodeB or null if no LCA exists
	 */	
	public TreeNode findLeastCommonAncestor(TreeNode nodeA, TreeNode nodeB) {
		if(nodeA == null || nodeB == null) return null;
		if(nodeA.getGraph() != this || nodeB.getGraph() != this) return null;
		
		/* set nodeA and nodeB to equal level */
		if (nodeA.getLevel() < nodeB.getLevel()) {
			while (nodeA.getLevel() != nodeB.getLevel()) {
				nodeB = (TreeNode) nodeB.getParent();

			}
		} else {			
			while (nodeA.getLevel() != nodeB.getLevel()) {
				nodeA = (TreeNode) nodeA.getParent();
			}
		}
		/* set both nodes to their parent, until they are equal */
		while (nodeA != nodeB) {
			nodeA = (TreeNode) nodeA.getParent();
			nodeB = (TreeNode) nodeB.getParent();
		}
		return (nodeA);
	}
    
	 /**
	  * This method returns the LCA of a list of nodes.
	  *  @param list of nodes
	   * @return least common ancestor
	   */
	  public TreeNode findLeastCommonAncestor(TreeNode... nodes) {
		if(nodes == null) return null;
		if(nodes.length <=1) return null;
		TreeNode retVal = findLeastCommonAncestor(nodes[0], nodes[1]);
		
		for (int i = 2; i < nodes.length; i++) {
		  retVal = findLeastCommonAncestor(retVal, nodes[i]);
		}
		return (retVal);	
	  }
	
	/**
	 * Returns true if this tree contains the given node
	 * 
	 * @param node the node
	 * @return true if tree contains node
	 */
	public boolean containsVertex(TreeNode node) {
		if(node == null) return false;
		return nodes.get(node.getIndex())!= null && nodes.get(node.getIndex()) == node;
	}
	/**
	 * Returns the name of this tree
	 * @return the name of this tree
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name of this tree
	 * 
	 * @param name the name of this tree
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Returns the maximal index over all nodes
	 */
	public int getMaxIndex() {
		return nodes.getMaximalIndex();
	}
	/**
	 * Returns the value of the property with the given name or the 
	 * given default value if no such property exists.
	 *  
	 * @param name of the property
	 * @param defaultValue returned in case property does not exist
	 * @return value of the property
	 */
	public Serializable getProperty(String name, Serializable defaultValue) {
		Serializable s = propertySet.get(name);
		return s==null?defaultValue : s;
	}
	/**
	 * Returns the value of the property with the given name or null
	 * 
	 * @param name of the property
	 * @return value of the property or null
	 */
	public Serializable getProperty(String name) {
		return propertySet.get(name);
	}
	/**
	 * Set a property
	 * 
	 * @param name of the property
	 * @param value of the property
	 * @return true if stored successful
	 */
	public boolean setProperty(String name, Serializable value) {
		propertySet.set(name, value);
		return true;
	}
	/**
	 * Set the property set used to store properties
	 * 
	 * @param propertySet
	 */
	public void setPropertySet(PropertySet propertySet) {
		this.propertySet = propertySet;
	}

	
	
	//// helper methods

	/**
	 * Creates a new Tree from a given newick ( i.e. ((A,B)C); ) String
	 * 
	 * @param newick string
	 * @see epos.model.tree.io.Newick#getTreeFromString(String)
	 */
	/*public static Tree fromNewick(String newickString){
		return Newick.getTreeFromString(newickString);
	}*/
	/**
	 * Creates a newick representation of the given tree.
	 * 
	 * @param tree
	 * @return
	 */
	/*public static String toNewick(Tree tree){
		return Newick.getStringFromTree(tree);
	}*/

}