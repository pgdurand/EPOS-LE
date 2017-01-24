package epos.model.tree.multitree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import epos.model.graph.Edge;
import epos.model.graph.EdgeNodeIterable;
import epos.model.graph.EdgeType;
import epos.model.graph.EmptyIterable;
import epos.model.graph.Graph;
import epos.model.graph.Vertex;

public class MultiNode implements Vertex<MultiNode, Edge<MultiNode>> {
	
	protected int index = -1;
	protected HashMap<EdgeType, Edge<MultiNode>> incomingEdges = new HashMap<EdgeType, Edge<MultiNode>>();
	protected HashMap<EdgeType, ArrayList<Edge<MultiNode>>> outgoingEdges = new HashMap<EdgeType, ArrayList<Edge<MultiNode>>>();
	private String label;
	private HashMap<EdgeType, Integer> levels;
	
	public void addIncomingEdge(Edge<MultiNode> e) {
		incomingEdges.put(e.getType(), e);
	}
	public void addOutgoingEdge(Edge<MultiNode> e) {
		ArrayList<Edge<MultiNode>> l  = outgoingEdges.get(e.getType());
		if(l == null){
			l = new ArrayList<Edge<MultiNode>>();
			outgoingEdges.put(e.getType(), l);
		}
		l.add(e);		
	}

	public void addEdge(Edge<MultiNode> e) {
		throw new UnsupportedOperationException("use addIncoming or adOuting Edge methods");
	}
	/**
	 * Clears all edges and esures that edges are also removed from source or target nodes.
	 */
	public void clear() {
		// remove incoming edges
		for (Edge<MultiNode> e : incomingEdges.values()) {
			e.getSource().removeOutgoingEdge(e);
		}
		// remove outgoing Edges
		for (List<Edge<MultiNode>> l : outgoingEdges.values()) {
			for (Edge<MultiNode> edge : l) {
				edge.getTarget().removeIncomingEdge(edge);
			}
		}
		incomingEdges.clear();
		outgoingEdges.clear();
	}
	/**
	 * Helper method to directly remove an outgoing edge
	 * @param e
	 */
	protected void removeOutgoingEdge(Edge<MultiNode> e) {
		if(outgoingEdges.containsKey(e.getType()))
			outgoingEdges.get(e.getType()).remove(e);
	}
	/**
	 * Helper method to directly remove an incoming edge
	 * @param e
	 */
	protected void removeIncomingEdge(Edge<MultiNode> e) {		
		if(incomingEdges.get(e.getType()) == e) incomingEdges.remove(e.getType());
	}
	/**
	 * Check the edges source/target for this node and returns true if this 
	 * node contains the given edge as incoming or outgoing edge.
	 */
	public boolean containsEdge(Edge<MultiNode> e) {
		if(e.getSource() == this){
			Edge e1 = incomingEdges.get(e.getType());
			return (e1 != null && e1 == e);			
		}else{
			ArrayList<Edge<MultiNode>> l = outgoingEdges.get(e.getType());
			if(l != null){
				return l.contains(e);
			}
		}
		return false;
	}
	/**
	 * Returns the full degree of the vertex, this is the number of all edges (in and out) od all edge types.
	 */
	public int degree() {
		int d = 0;
		for (List<Edge<MultiNode>> l : outgoingEdges.values()) {
			d += l.size();
		}
		d += incomingEdges.size();
		return d;
	}
	/**
	 * Returns the number of edges of specified type (in and out)
	 */
	public int degree(EdgeType type) {
		List<Edge<MultiNode>> l = outgoingEdges.get(type);
		int d = 0;
		if(l != null) d+= l.size();
		if(incomingEdges.containsKey(type)) d++;
		return d; 
	}
	/**
	 * Iterates over all outgoing edges of given type and returns the target vertices
	 */
	public Iterable<MultiNode> edgeNodes(EdgeType type) {
        ArrayList<Edge<MultiNode>> l = outgoingEdges.get(type);
        if (l == null){
            return new EmptyIterable<MultiNode>();
        }
        return new EdgeNodeIterable<MultiNode, Edge<MultiNode>>(l, this);
	}

	/**
	 * Iterates over all outgoing edges of al types
	 */
	public Iterable<Edge<MultiNode>> edges() {
		return new MultiNodeEdgeIterable(this);
	}
	/**
	 * Iterates over all outgoing edges of given type
	 */
	public Iterable<Edge<MultiNode>> edges(EdgeType type) {
		ArrayList<Edge<MultiNode>> l = outgoingEdges.get(type);
		if(l == null)
			return new EmptyIterable<Edge<MultiNode>>();
        return l;
	}
	
	public Edge<MultiNode> getEdge(Vertex oppositNode) {
		throw new UnsupportedOperationException("Can not find an edge without a specified type, use getEdge(Vertex, EdgeType)");
	}

	@SuppressWarnings("unchecked")
	public Edge<MultiNode> getEdge(Vertex oppositNode, EdgeType type) {
		ArrayList<Edge<MultiNode>> l = outgoingEdges.get(type);
		if(l != null){
			for (Edge edge : l) {
				if(edge.getOpposit(this) == oppositNode) return edge;
			}
		}
		// check for incoming edge
		Edge in = incomingEdges.get(type);
		if(in != null && in.getOpposit(oppositNode) == this) return in;
		// no edge found
		return null;
	}

	public Graph<MultiNode, Edge<MultiNode>> getGraph() {
		throw new UnsupportedOperationException("The node does not store the underlying graph");
	}

	public int getIndex() {
		return index;
	}

	public String getLabel() {
		return label;
	}

	public int indegree() {
		return incomingEdges.size();
	}

	public int indegree(EdgeType type) {
		return incomingEdges.get(type) != null ? 1 : 0;
	}

	public void removeEdge(Edge<MultiNode> e) {
		if(incomingEdges.get(e.getType()) == e) incomingEdges.remove(e.getType());
		ArrayList<Edge<MultiNode>> l = outgoingEdges.get(e.getType());
		if(l != null) {
			l.remove(e);
		}
	}

	public void setGraph(Graph<MultiNode, Edge<MultiNode>> g) {
		throw new UnsupportedOperationException("Storing the graph is not supported");
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * The number of outgoing edges of this node
	 * @return
	 */
	public int outdegree() {		
		int d = 0;
		for (List<Edge<MultiNode>> l : outgoingEdges.values()) {
			d += l.size();
		}
		return d;
	}
	/**
	 * Returns the number of outgoing edges of given type
	 * @param type
	 * @return
	 */
	public int outdegree(EdgeType type) {
		List l = outgoingEdges.get(type);		
		return l == null ? 0 : l.size();
	}
	
	/**
	 * Returns the parent connected with specified type or null
	 * @param type
	 * @return parent of given type or null
	 */
	public MultiNode getParent(EdgeType type) {
		Edge<MultiNode> e =getEdgeToParent(type);		
		return e != null ? e.getSource() : null;
	}
	/**
	 * Returns the edge to parent or null
	 * @param type
	 * @return
	 */
	public Edge<MultiNode> getEdgeToParent(EdgeType type){
		return incomingEdges.get(type);
	}


	
	
	/**
	 * Cmpute the level of the node. this is the number of edges between this node 
	 * and the root of the tree identified by the given edge type.
	 * 
	 * @param type
	 * @return
	 */
	public int getLevel(EdgeType type) {
		if(levels == null) {
			levels = new HashMap<EdgeType, Integer>();
		}
		
		Integer l = levels.get(type);
		
		if(l == null || l.intValue() == -1) {
			int level = 0;
			MultiNode p = this;
			while(p.getParent(type) != null) {
				p = p.getParent(type);
				level++;
			}
			levels.put(type, level);
			return level;
		}else {
			return l;
		}
		
	}
	/**
	 * Iterates over all children of given type
	 * 
	 * @param type
	 * @return
	 */
	public Iterable<MultiNode> children(EdgeType type) {
		return new MultiNodeChildIterable(type);
	}
	
	
	class MultiNodeChildIterable implements Iterable<MultiNode>, Iterator<MultiNode>{
		Iterator<Edge<MultiNode>> it;
		public MultiNodeChildIterable(EdgeType type) {
			it = edges(type).iterator();
		}

		public Iterator<MultiNode> iterator() {
			return this;
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public MultiNode next() {
			return it.next().getTarget();
		}

		public void remove() {
			throw new UnsupportedOperationException("Removal not supported");
		}
		
	}
	class MultiNodeEdgeIterable implements Iterable<Edge<MultiNode>>, Iterator<Edge<MultiNode>>{
		
		private List<Edge<MultiNode>> list;
		private Iterator<ArrayList<Edge<MultiNode>>> lists;
		int c = 0;
		public MultiNodeEdgeIterable(MultiNode node) {		
			lists = node.outgoingEdges.values().iterator();
		}
		public Iterator<Edge<MultiNode>> iterator() {
			return this;
		}

		public boolean hasNext() {
			if(list == null && lists.hasNext()) {
				list = lists.next();
				c = 0;
			}else if(list == null && !lists.hasNext()) {
				return false;
			}
			return c < list.size();
		}

		public Edge<MultiNode> next() {
			if(hasNext()) return list.get(c++);
			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException("removal not supported");
		}		
	}

}
