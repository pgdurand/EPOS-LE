package epos.model.tree.multitree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import epos.model.graph.DefaultEdge;
import epos.model.graph.Edge;
import epos.model.graph.EdgeFactory;
import epos.model.graph.EdgeType;
import epos.model.graph.FixedIndexList;
import epos.model.graph.Graph;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;

public class MultiTree implements Graph<MultiNode, Edge<MultiNode>>{

	protected FixedIndexList<MultiNode> nodes;
	protected EdgeType[] edgeTypes;
	protected TypedVertexIterable iterable;
	protected double[] treeWeights;
	
	public MultiTree(){ 
		super();
		nodes = new FixedIndexList<MultiNode>();
	}
	
	/**
	 * initializes this MultiTree with the given trees and returns a map that contains
	 * all lables as keys and their corresponding mulitnodes as values.
	 *  
	 * @param trees
	 * @return map with label => multinode mapping
	 */
	public Map<String, MultiNode> initFromTrees(Tree[] trees, String weightProperty){
		this.edgeTypes = new EdgeType[trees.length];
		this.treeWeights = new double[trees.length];
		
		for (int i = 0; i < trees.length; i++) {
			this.edgeTypes[i] = new EdgeType(""+i, true);
			double w = 0;
			Double sw = (Double) trees[i].getProperty(weightProperty);
			if(sw != null){
				w = sw.doubleValue();
			}
		    this.treeWeights[i] = w;
		}
		
		
		HashMap<String, MultiNode> labeledNodes = new HashMap<String, MultiNode>();
		HashMap<TreeNode, MultiNode> all = new HashMap<TreeNode, MultiNode>();
		nodes = new FixedIndexList<MultiNode>();
		int i = 0;
		for (Tree tree : trees) {			
			hangIn(tree.getRoot(), labeledNodes, all, this.edgeTypes[i]);
			i++;
		}	
		return labeledNodes;
	}
		
	public Iterable<MultiNode> vertices(EdgeType type) {
		return new TypedVertexIterable(type);
	}

	private void hangIn(TreeNode n,  HashMap<String, MultiNode> labeledNodes, HashMap<TreeNode,MultiNode> all, EdgeType type) {
		MultiNode m = null;
		if(n.getLabel() != null){
			m = labeledNodes.get(n.getLabel());
		}
		
		if(m == null){
			m = new MultiNode();
			all.put(n, m);
			if(n.getLabel() != null ){
				m.setLabel(n.getLabel());
				labeledNodes.put(n.getLabel(), m);
			}
			addVertex(m);
		}
		
		if(n.getEdgeToParent() != null){
			MultiNode p = all.get(n.getParent());			
			addEdge(p, m, type).setWeight(n.getEdgeToParent().getWeight());
		}
		
		for (TreeNode c : n.children()) {
			hangIn(c, labeledNodes, all, type);
		}
	}

	/**
	 * Adds the edge as outgoing edge to the edges source and as incoming edge to the edges target vertex.
	 * 
	 * @param e the edge
	 * @return the edge
	 */
	public Edge<MultiNode> addEdge(Edge<MultiNode> e) {
		e.getSource().addOutgoingEdge(e);
		e.getTarget().addIncomingEdge(e);
		return e;
	}
	/**
	 * Adds the given vertex to the set of vertices
	 */
	public int addVertex(MultiNode n) {
		nodes.put(n);
		return n.getIndex();
	}
	/**
	 * Returns true if one of the given nodes contains an outgoing edge to the other node.
	 */
	public boolean containsEdge(MultiNode n1, MultiNode n2, EdgeType type) {
		return n1.getEdge(n2, type) != null || n2.getEdge(n1, type) != null;		
	}

	/**
	 * detects an edge of given type between the two nodes and 
	 * removes it.
	 */
	public void removeEdge(MultiNode n1, MultiNode n2, EdgeType type) {
		Edge<MultiNode> e = n1.getEdge(n2, type);
		if(e == null) {
			e = n2.getEdge(n1, type);			
		}
		if(e != null) {
			removeEdge(e);
		}
	}
	/**
	 * removes the edge by calling <code> getSource().removeEdge()</code>
	 * and <code>getTarget().removeEdge()</code> on the given edge.
	 * 
	 * @param e
	 */
	protected void removeEdge(Edge<MultiNode> e) {
		e.getSource().removeEdge(e);
		e.getTarget().removeEdge(e);
	}
	/**
	 * Removes a vertex and all its edges
	 */
	public void removeVertex(MultiNode n) {
		if(n == null) return;
		// clear must ensure that all edges of the vertex are removed
		n.clear();
		nodes.remove(n);
	}
	/**
	 * Returns the number of vertices added to this graph
	 */
	public int vertexCount() {
		return nodes.size();
	}
	/**
	 * Count vertices that contain edges of given type
	 * @param type
	 * @return
	 */
	public int vertexCount(EdgeType type) {
		int c = 0;
		for (MultiNode v : vertices()) {
			if(v.degree(type) > 0)
				c++;
		}
		return c;
	}

	/**
	 * Iterates over all vertices
	 */
	public Iterable<MultiNode> vertices() {
		return nodes;
	}
	/**
	 * Returns the number of all edges in this grph (count outgoing edges of all vertices)
	 */
	public int edgeCount() {
		int d = 0;
		for (MultiNode m : vertices()) {
			d += m.outdegree();
		}
		return d;
	}
	/**
	 * Counts the edged of a specific type
	 */
	public int edgeCount(EdgeType type) {
		int d = 0;
		for (MultiNode m : vertices()) {
			d += m.outdegree(type);
		}
		return d;
	}

	public Edge<MultiNode> getEdge(MultiNode n1, MultiNode n2) {
		throw new UnsupportedOperationException("can not return an edge without specifying the type. Use getEdge(MultiNode,MultiNode, EdgeType)");
	}

	public Edge<MultiNode> getEdge(MultiNode n1, MultiNode n2, EdgeType type) {
		return n1.getEdge(n2, type);
	}
	
	public Edge<MultiNode> getEdge(int i, int y, EdgeType type){
		MultiNode n1 = this.getVertex(i);
		MultiNode n2 = this.getVertex(y);
		return getEdge(n1, n2, type);
	}

	public MultiNode getVertex(int index) {
		return nodes.get(index);
	}

	public void removeVertex(int index) {
		removeVertex(nodes.get(index));
	}

	public Edge<MultiNode> addEdge(MultiNode source, MultiNode target, EdgeType type) {
		DefaultEdge<MultiNode> e = new DefaultEdge<MultiNode>(type, source, target);
		source.addOutgoingEdge(e);
		target.addIncomingEdge(e);
		return e;
	}

	public Edge<MultiNode> addEdge(MultiNode n1, MultiNode n2) {
		throw new UnsupportedOperationException("Can not create an edge without an EdgeType");
	}
	
	public Edge<MultiNode> addEdge( int i, int y, EdgeType type ){
		
		MultiNode source = this.getVertex(i);
		MultiNode target = this.getVertex(y);
		
		return addEdge(source, target, type);
	}
	
	

	public void addEdgeFactory(EdgeFactory<? extends Edge<MultiNode>, MultiNode> factory) {
		throw new UnsupportedOperationException("Mutliple edge factories are not allowd.");
	}

	public boolean containsEdge(MultiNode n1, MultiNode n2) {
		throw new UnsupportedOperationException("Can not check for existence of an edge without an specified EdgeType.");
	}

	public void removeEdge(MultiNode n1, MultiNode n2) {
		throw new UnsupportedOperationException("Can not remove an edge with unspecified edge type");
	}
	
	
	public MultiNode getLCAFromPair(MultiNode nodeA, MultiNode nodeB, EdgeType type) {
		/* set nodeA and nodeB to equal level */
		if (nodeA.getLevel(type) < nodeB.getLevel(type)) {
			while (nodeA.getLevel(type) != nodeB.getLevel(type)) {
				nodeB = nodeB.getParent(type);
			}
		} else {			
			while (nodeA.getLevel(type) != nodeB.getLevel(type)) {
				nodeA = nodeA.getParent(type);
			}
		}
		/* set both nodes to their parent, until they are equal */
		while (nodeA != nodeB) {
			nodeA = nodeA.getParent(type);
			nodeB = nodeB.getParent(type);
		}
		return nodeA;
	}
    
	 /**
	  * This method returns the LCA of a list of nodes.
	  *  @param list of nodes
	   * @return least common ancestor
	   */
	  public MultiNode findLCA(MultiNode[] nodes, EdgeType type) {
		
		  MultiNode retVal = getLCAFromPair(nodes[0], nodes[1], type);
		
		for (int i = 2; i < nodes.length; i++) {
		  retVal = getLCAFromPair(retVal, nodes[i], type);
		}
		return retVal;	
	  }

	public EdgeType[] getEdgeTypes() {
		return edgeTypes;
	}

	public int numberTrees(){
		return edgeTypes.length;
	}
	
	
	
	class TypedVertexIterable implements Iterable<MultiNode>, Iterator<MultiNode>{
		private EdgeType type;
		Iterator<MultiNode> it;
		MultiNode next;
		
		public TypedVertexIterable(EdgeType type) {
			this.type = type;
			it = nodes.iterator();
			if(it.hasNext()) {
				while(it.hasNext()) {
					next = it.next();					
					if(next.degree(type) > 0) break;
				}				
				if(next.degree(type) <= 0) next = null;
			}else {
				next = null;
			}

		}
		
		public Iterator<MultiNode> iterator() {
			return this;
		}

		public boolean hasNext() {		
			return next != null;
		}

		public MultiNode next() {
			MultiNode n = next;
			if(it.hasNext()) {
				while(it.hasNext()) {
					next = it.next();					
					if(next.degree(type) > 0) break;
				}				
				if(next.degree(type) <= 0) next = null;
			}else {
				next = null;
			}
			return n;
		}

		public void remove() {
			throw new UnsupportedOperationException("vertex removal not supported");
		}
		
	}

	
	
	public MultiTree copy(){
		
		//System.out.println(" in copy " + this.edgeCount());
		
		MultiTree copy = new MultiTree();
		
		copy.edgeTypes = edgeTypes;
		copy.treeWeights = treeWeights;
		
		//add all vertices to the copy 
		Iterator nodeIter = this.vertices().iterator();
		while(nodeIter.hasNext()){
			MultiNode node = (MultiNode) nodeIter.next();
			MultiNode copy_n = new MultiNode();						
		    copy_n.setIndex(node.getIndex());
		    copy_n.setLabel(node.getLabel());		  
			copy.addVertex(copy_n);
			
		}
		
	 for (EdgeType type : edgeTypes) {		 
		 Iterator nodeIter2 = this.vertices().iterator();
         while(nodeIter2.hasNext()){
        	 MultiNode curr = (MultiNode) nodeIter2.next();
        	 //ArrayList<Edge> edges = new ArrayList<Edge>();
        	 for (MultiNode opp : curr.edgeNodes(type)) {
        		 copy.addEdge(curr.getIndex(), opp.getIndex(), type);
        		 //edges.add(getEdge(curr, opp, type));
        	 }
//        	 for (Edge edge : edges) {
//				copy.addEdge(edge.getSource().getIndex(), edge.getTarget().getIndex(), edge.getType());
//			}
//        	 curr.e
//        	 ArrayList<Edge<MultiNode>> edgeIter = (ArrayList<Edge<MultiNode>>) curr.edges(type);
//        	 
//        	for (Edge<MultiNode> edge : edgeIter) {
//			      MultiNode opp = (MultiNode) edge.getOpposit(curr);
//        	      
//        	      copy.addEdge(curr.getIndex(), opp.getIndex(), type);
//        	 }
         }
	}  	
		return copy;
	}


	public int getMaxIndex() {
		return nodes.getMaximalIndex();
	}
	
}
