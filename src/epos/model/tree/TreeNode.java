/*
 * Created on 05.07.2005
 */
package epos.model.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.jdesktop.application.AbstractBean;

import epos.model.graph.Edge;
import epos.model.graph.EdgeNodeIterable;
import epos.model.graph.EdgeType;
import epos.model.graph.EmptyIterable;
import epos.model.graph.Graph;
import epos.model.graph.Vertex;
import epos.qnode.properties.Property;
import epos.qnode.properties.PropertySet;

/**
 * Tree node used in the EPoS {@link Tree} model. The node supports properties and provides
 * iteration functionality.  
 * 
 * @author Thasso Griebel (thasso@minet.uni-jena.de)
 *
 */
public class TreeNode extends AbstractBean implements Vertex<TreeNode, Edge<TreeNode>>{
	/**
	 * Caches the level
	 */
	protected int level = -1;
	/**
	 * The nodes id
	 */
	protected int id = -1;
	/**
	 * Store the inedge
	 */
	protected Edge<TreeNode> incompingEdge;
	/**
	 * Store out edges
	 */
	protected List<Edge<TreeNode>> edges;
	/**
	 * The nodes label
	 */
	private String label;
	/**
	 * Back-link to the graph containing this node
	 */
	protected Graph<TreeNode, Edge<TreeNode>> graph; 
	
	/**
	 * Property storage
	 */
	protected PropertySet properties;

	/**
	 * Create a new node
	 */
	public TreeNode() {
		this(null);
	}

	/**
	 * Create a new node with given label
	 * 
	 * @param label the label
	 */
	public TreeNode(String label) {
		edges = new ArrayList<Edge<TreeNode>>();
		this.label = label;
	}

	/**
	 * Return the parent of this node.
	 * 
	 * @return parent the parent TreeNode or null
	 */
	public TreeNode getParent() {
		if (incompingEdge == null)
			return null;
		return incompingEdge.getSource();
	}

	/**
	 * Return the edge to the parent node or null.
	 * 
	 * @return edge the Edge to the parent or null
	 */
	public Edge getEdgeToParent() {
		return incompingEdge;
	}

	/**
	 * Enumeration over all children of this node.
	 * 
	 * @return enumeration over all children of this node
	 * @deprecated use {@link #children()}
	 */
	public Enumeration<TreeNode> getChildEnumeration() {
		return new ChildrenEnumeration(this);
	}
	/**
	 * Returns an {@link Iterable} over all children of this node.
	 * <p>
	 * This allow using nodes in foreach loop:
	 * <pre>
	 * for(TreeNode child: node.children()){
	 *    //...do something with the child
	 * }
	 * </pre>
	 * @return iterable over all children
	 */
	public Iterable<TreeNode> children() {
		return new ChildrenEnumeration(this);
	}

	/**
	 * Returns the number of children of this node.
	 * 
	 * @return childCount the number of children
	 */
	public int childCount() {
		return edges.size();
	}

	/**
	 * Returns the child at position i
	 * 
	 * @param i
	 *            the child position
	 * @return node the child at i
	 */
	public TreeNode getChildAt(int i) {
		if (edges == null || edges.size() <= 0 || edges.size() < i)
			return null;
		return edges.get(i).getTarget();
	}

	/**
	 * Returns true if this node is a leaf.
	 * 
	 * @return leaf true if this node is a leaf
	 */
	public boolean isLeaf() {
		return edges == null || edges.size() == 0;
	}

	/**
	 * Returns true if this is not a leaf.
	 * 
	 * @return true is not leaf
	 */
	public boolean isInnerNode() {
		return !isLeaf();
	}

	/**
	 * Lazy and one time computation of the level of this node.
	 * 
	 * @return int level
	 */
	public int getLevel() {
		if (level == -1) {
			TreeNode parent = this;
			level = 0;
			while ((parent = parent.getParent()) != null)
				level++;
		}
		return level;
	}

	/**
	 * Rotates the children of this node.
	 * 
	 * @param clockwise  
	 */
	public void rotateChildren(boolean clockwise) {		
		if (edges != null && edges.size() >= 2) {
			if(clockwise){
				Edge<TreeNode> first = edges.get(0);
				removeEdge(first);
				addEdge(first);
			}else{
				Edge<TreeNode> first = edges.get(edges.size()-1);
				removeEdge(first);				
				edges.add(0, first);
			}
		}
	}


	/**
	 * @return previous sibling of this node
	 */
	public TreeNode getPreviousSibling() {
		TreeNode p = getParent();
		if (p == null)
			return null;
		
		TreeNode prev = null;
		for (Edge<TreeNode> oe : p.edges()) {
			if (oe.getOpposit(p) == this) {
				return prev;
			}
			prev = oe.getOpposit(p);
		}
		return null;
	}
	/**
	 * @return next sibling of this node
	 */
	public TreeNode getNextSibling() {
		TreeNode p = getParent();
		if (p == null)
			return null;
		
		boolean returnNext = false;
		for (Edge<TreeNode> oe : p.edges()) {
			if (returnNext)
				return  oe.getOpposit(p);
			if (oe.getOpposit(p) == this) {
				returnNext = true;
			}
		}
		return null;
	}

	/**
	 * Returns true if the given node is sibling of this node.
	 * 
	 * @param node the other node
	 * @return true if the other node is sibling of this node
	 */
	public boolean isNodeSibling(TreeNode node) {
		TreeNode p = getParent();
		if (p == null)
			return false;

		for (Edge<TreeNode> oe : p.edges()) {
			if (oe.getOpposit(p) == this)
				return true;
		}

		return false;
	}

	/**
	 * Clones this node.
	 * 
	 * @return node the new node
	 */
	public TreeNode cloneNode() {
		TreeNode n = new TreeNode();
		n.setLabel(getLabel());
		return n;
	}
	/**
	 * Clones the node
	 * @see #cloneNode()
	 */
	public Object clone() {
		return cloneNode();
	}

	/**
	 * Creates and returns the {@link Partition} of this node. Partitions are not 
	 * cached and recomputed for each call. 
	 * 
	 * @return partition of this node
	 */
	public Partition getPartition() {
		Partition partition = new Partition();
		for (TreeNode n : depthFirstIterator()) {
			if (n.isLeaf()){
				partition.add(n);
			}
		}
		return partition;
	}

	/**
	 * Returns the leaves under this node.
	 * 
	 * @return leaves under this node
	 */
	public TreeNode[] getLeaves() {
		if (isLeaf())
			return new TreeNode[] { this };

		List<TreeNode> l = new ArrayList<TreeNode>();
		for (TreeNode c : depthFirstIterator()) {
			if(c.isLeaf()){
				l.add(c);
			}
		}
		TreeNode[] n = new TreeNode[l.size()];
		l.toArray(n);
		return n;
	}
	/**
	 * Returns the distance to the parent node.
	 * If the node has no parent (root node ) -1 is returned.
	 * @return distance to parent or -1 (if there is no parent)
	 */
	public double getDistanceToParent() {
		if (getEdgeToParent() == null)
			return -1;// null;
		return getEdgeToParent().getWeight();
	}
	/**
	 * Returns a depth first {@link Iterable}. This enables iterating 
	 * the subtree rooted at this node in post order within a foreach loop:
	 * <pre>
	 * for(TreeNode next:node.depthFirstIterator()){
	 *    //...do something with next node
	 * }
	 * </pre>
	 * 
	 * @return depthFirstIterable 
	 * @see PostorderIterator
	 */
	public Iterable<TreeNode> depthFirstIterator() {
		return new PostorderIterator(this);
	}
	/**
	 * Traverses the tree rooted at this node and counts leaves.
	 * 
	 * @return leaves under this node
	 */
	public int leafCount() {
		if (isLeaf()) {
			return 1;
		}
		int i = 0;
		for (TreeNode n : children()) {
			i += n.leafCount();
		}
		return i;
	}

	/**
	 * Add an edge to this node.
	 */
	@SuppressWarnings("unchecked")
	public void addEdge(Edge e) {
		if(e.getSource() == this){
			if(getEdge(e.getTarget()) == null)
				edges.add(e);
		}else{
			incompingEdge = e;
			level = -1;
		}
	}
	/**
	 * Clears the node. Removes all edges and null references.
	 */
	@SuppressWarnings("unchecked")
	public void clear() {
		if(incompingEdge != null) {
			incompingEdge.getSource().removeEdge(incompingEdge);
			incompingEdge = null;
		}
		
		if (edges != null) {
			for (Edge out : edges) {
				out.getTarget().removeEdge(out);
			}
			edges.clear();
		}
	}
	/**
	 * Returns true if this Node contains the given edge
	 */
	@SuppressWarnings("unchecked")
	public boolean containsEdge(Edge e) {
		if (incompingEdge != null)
			if (incompingEdge.equals(e))
				return true;
		if (edges != null) {
			for (Edge edge : edges) {
				if (e.equals(edge))
					return true;
			}
		}
		return false;
	}
	
	public int degree() {
		int i = incompingEdge != null ? 1 : 0;
		if (edges != null)
			return edges.size() + i;
		return i;
	}

	public int getIndex() {
		return id;
	}
	/**
	 * The label of this node. If the label is not set, this looks for a label property
	 * {@link TreeNodeProperties#PROPERTY_LABEL}
	 * 
	 * 
	 */
	public String getLabel() {	
		if(label == null){
			label = (String) getProperty(TreeNodeProperties.PROPERTY_LABEL);
		}
		return label;
	}

	public void setIndex(int index) {
		this.id = index;
	}

	public void setLabel(String label) {
		setProperty(TreeNodeProperties.PROPERTY_LABEL, label);
		this.label = label;
	}


	public int degree(EdgeType type) {
		return degree();
	}

	public Iterable<TreeNode> edgeNodes(EdgeType type) {        
        if (edges == null){
            return new EmptyIterable<TreeNode>();
        }
        return new EdgeNodeIterable<TreeNode, Edge<TreeNode>>(edges, this);
	}

	public Iterable<Edge<TreeNode>> edges() {
		return edges;
	}

	public Iterable<Edge<TreeNode>> edges(EdgeType type) {
		return edges;
	}

	@SuppressWarnings("unchecked")
	public Edge<TreeNode> getEdge(Vertex oppositNode) {
		if(edges == null){
			return null;
		}
		for (Edge<TreeNode> e : edges) {
			if(e.getOpposit(this) == oppositNode) return e;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Edge<TreeNode> getEdge(Vertex oppositNode, EdgeType type) {
		return getEdge(oppositNode);
	}

	public Graph<TreeNode, Edge<TreeNode>> getGraph() {
		return graph;
	}

	public int indegree() {
		return incompingEdge == null ? 0 : 1;
	}

	public int indegree(EdgeType type) {
		return indegree();
	}

	public void setGraph(Graph<TreeNode, Edge<TreeNode>> g) {
		this.graph = g;
	}

	public void removeEdge(Edge<TreeNode> e) {
		if(incompingEdge != null && e == incompingEdge) {			
			incompingEdge = null;
			level = -1;
		}else {
			edges.remove(e);
		}
	}
	

	
	

	/**
	 *@deprecated 
	 */
	class ChildrenEnumeration implements Enumeration<TreeNode>,
			Iterator<TreeNode>, Iterable<TreeNode> {
	
		private Object sourceNode = null;
	
		private Iterator<Edge<TreeNode>> outEdges;
		private Iterator<Edge<TreeNode>> edges;
	
		public ChildrenEnumeration(TreeNode node) {
			this.sourceNode = node;
			outEdges = edges().iterator();
			edges = outEdges;//.iterator();
		}
	
		public boolean hasMoreElements() {
			return outEdges.hasNext();
		}
	
		public TreeNode nextElement() {
			Edge e = (Edge) edges.next();
			return (TreeNode) e.getTarget();
		}
	
		public boolean hasNext() {
			return edges.hasNext();
		}
	
		public TreeNode next() {
			Edge e = edges.next();
			return (TreeNode) e.getTarget();
		}
	
		public void remove() {
			throw new RuntimeException("Method not supported");
		}
	
		public Iterator<TreeNode> iterator() {
			return this;
		}
	}
	/**
	 * Helper that enables post order traversals.
	 * 
	 * @author Thasso Griebel (thasso@minet.uni-jena.de)
	 *
	 */
	class PostorderIterator implements Iterable<TreeNode>, Iterator<TreeNode> {
		protected TreeNode root;
	
		protected Iterator<TreeNode> children;
	
		protected Iterator<TreeNode> subtree;
	
		public PostorderIterator(TreeNode node) {
			root = node;
			children = node.children().iterator();
			subtree = new EmptyIterable<TreeNode>();
		}
	
		public Iterator<TreeNode> iterator() {
			return this;
		}
	
		public boolean hasNext() {
			return root != null;
		}
	
		public TreeNode next() {
			TreeNode retval;
	
			if (subtree.hasNext()) {
				retval = subtree.next();
			} else if (children.hasNext()) {
				subtree = new PostorderIterator(children.next());
				retval = subtree.next();
			} else {
				retval = root;
				root = null;
			}
			return retval;
		}
	
		public void remove() {
		}
	
	}

	public String toString(){
		return getLabel() == null ? "":getLabel();
	}
	
	/**Compares this {@link TreeNode} with a given object.
	 * If the nodes are in the same Tree, then their labels are compared.
	 * If they are in different trees, the labels are compared.
	 * If both have labels, they are compared. If both have no labels, the super class method is used.
	 * If one has a label and the other not, they can't be equal.
	 * @param an object, that has to be compared with this one.
	 * @return true, if one of the conditions above holds, false otherwise*/
	public boolean equals(Object o){
		//check if both objects are TreeNodes
		if(o instanceof TreeNode){
			TreeNode object = (TreeNode)o;
			//check if both TreeNodes are in the same graph
			if(this.graph != null && this.graph == object.graph){
				//compare their indices
				if(this.getIndex() == object.getIndex()){
					return true;
				}else{
					return false;
				}
			}else{
			//if they are in a different graph
				//compare their labels
				if(this.getLabel() != null && object.getLabel() != null){
				//both have labels
					if(this.getLabel().equals(object.getLabel())){
						return true;
					}else{
						return false;
					}
				}else if(this.getLabel() == null && object.getLabel() == null){
				//both have no labels
					//use the super class method
					return super.equals(object); 
				}
				else{
				//one has a label and the other one not
					return false;
				}
			}
			
		}else{
			return false;
		}
	}
	
	/**Override the hashCode() method.
	 * It returns the hashcode value of its label, if this node has one.
	 * Otherwise the former hashcode value is returned.
	 * @return the hashcode of the label, if there is one, the former hashcode value otherwise*/
	public int hashCode(){
		if(this.getLabel() != null){
			return this.getLabel().hashCode();
		}else{
			return super.hashCode();
		}
	}
	
	/**Indicates, if this node is a child of the given node.
	 * @param node: the node, that may be parent of this node
	 * @return true, if this node is a child of the given node, false otherwise*/
	public boolean isChildOf(TreeNode node){
		if(this.getParent().equals(node)){
			return true;
		}else{
			return false;
		}
	}
	
	/**Get a list of all children of this node.
	 * It is helpful if one wants to iterate over all children and delete some children meanwhile.
	 * @return a list of all children of this node (may be empty if the node is a leaf)*/
	public ArrayList<TreeNode> getChildren(){
		ArrayList<TreeNode> children = new ArrayList<TreeNode>();
		for(TreeNode child: this.children()){
			children.add(child);
		}
		return children;
	}
	
	/**Computes the number of the given child for this node.
	 * This number is 0 <= childNum < 'number of children', if the child is a real child of this node.
	 * It is childNum = 'number of children', if the given child is parent of this node.
	 * @param child: the child, for that its number has to be computed
	 * @return the child number for the  given node*/
	public int getChildNumber(TreeNode child){
		if(this.getParent().equals(child)){
			return childCount();
		}else if(child.isChildOf(this)){
			int num = 0;
			for(TreeNode nodesChild: this.children()){
				if(!nodesChild.equals(child)){
					num++;
				}else{
					break;
				}
			}
			return num;
		}else{
			throw new RuntimeException("Given child is no child of this node!");
		}
	}
	
	/**Compute a list of ALL children of a {@link TreeNode}.
	 * ALL means all nodes, that are adjacent to this node. That are all direct children 
	 * and the parent (if there is one).
	 * 
	 * @param node: the node, for that all children have to be computed
	 * 
	 * Runtime: O(d)
	 * */
	public ArrayList<TreeNode> getAllChildren(){
		ArrayList<TreeNode> children = new ArrayList<TreeNode>();
		//add all children
		for(TreeNode child: this.children()){
			children.add(child);
		}
		/**Alternative: children.addAll(node.getChildren()); => but needs maybe O(d^2)*/
		//add the parent
		if(this.getParent() != null){
			children.add(this.getParent());
		}		
		return children;
	}
	
	
	/**Compute a list of ALL edges of a given {@link TreeNode}.
	 * ALL means all adjacent edges to the node. That are all edges to children and the edge to 
	 * the parent (if there is one).
	 * 
	 * @param node: the node, for that all adjacent edges have to be computed
	 * 
	 * Runtime: O(d)
	 * */
	public ArrayList<Edge<TreeNode>> getAllEdges(){
		ArrayList<Edge<TreeNode>> edges = new ArrayList<Edge<TreeNode>>();
		for(Edge<TreeNode> edge: this.edges()){
			edges.add(edge);
		}
		if(!(this.getEdgeToParent() == null)){
			edges.add(this.getEdgeToParent());
		}
		return edges;
	}
	
	//
	//
	// //// END VERTEX IMPLEMENTATION
	
	/**
	 * Returns the {@link PropertySet} associated with this node
	 * @return properties of this node
	 */
	public PropertySet getProperties() {
		if(properties == null) properties = new PropertySet();
		return properties;
	}
	
	/**
	 * Sets the property set associated with this node
	 * @param properties
	 */
	public void setProperties(PropertySet properties) {
		this.properties = properties;
	}
	/**
	 * Returns the value of the property with the given name. If the {@link Property}
	 * does not exist, this returns the given default value.
	 * 
	 * @param name of the property
	 * @param defaultValue returned if property does not exist
	 * @return value of the property or null
	 */
	public Serializable getProperty(String name, Serializable defaultValue) {
		return getProperties().get(name, defaultValue);
	}
	/**
	 * Returns the value of a property with the given name or null if no such {@link Property} exists.
	 *
	 * @param name of the property
	 * @return value of the property or null
	 */
	public Serializable getProperty(String name) {
		return getProperties().get(name);
	}
	/**
	 * Sets the value of a {@link Property}
	 * 
	 * @param name of the {@link Property}
	 * @param value of the {@link Property}
	 * @return true if successful
	 */
	public boolean setProperty(String name, Serializable value) {
		return getProperties().set(name, value);
	}

}
