/*
 * Created on Jun 26, 2006
 */
package epos.model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Default implementation of the {@link Vertex} interface.
 * 
 * @author thasso
 *
 * @param <N> type of the vertices
 * @param <E> type of the edges allowed
 */
public abstract class DefaultVertex<N extends Vertex, E extends Edge<N>> implements Vertex<N,E> {

	/**
	 * Store the vertex label here
	 */
    protected String label;

    /**
     * store the vertex id
     */
    protected int id = -1;

    /**
     * Map to get the adjacent lists for different edge types
     */
    protected Map<EdgeType, List<E>> adjList;

    /**
     * the parent graph
     */
	protected Graph<N,E> graph;


    public DefaultVertex(){
    	this(null);
    }

    /**
     * Create a new vertex with given label and id
     * 
     * @param label the label
     * @param id the id
     */
    public DefaultVertex(String label) {    	
        setLabel(label);        
        adjList = new HashMap<EdgeType, List<E>>();
    }

    /**
     * Set the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the id.
     */
    public void setIndex(int id) {
        this.id = id;
    }

    /**
     * Returns the id
     */
    public int getIndex() {
        return id;
    }

    /**
     * clears edge lists. nothing done here
     * 
     */
    public void clear() {
    	for (E e : edges()) {
			e.getOpposit((N)this).removeEdge(e);
		}
    	adjList.clear();
    }

    /**
     * Adds an edge to the AdjacentList according to the edges type
     * @param E the edge 
     */
    public void addEdge(E e) {
        List<E> edges = adjList.get(e.getType());
        if (edges == null) {
            edges = new ArrayList<E>();
            adjList.put(e.getType(), edges);
        }        
        edges.add(e);
    }

    /**
     * Remove a given edge
     */
    public void removeEdge(E e) {
        List<E> l = adjList.get(e.getType());
        if (l == null)
            return;
        l.remove(e);
    }

    /**
     * returns true if this node is connected to the given edge
     */
    public boolean containsEdge(E e) {
    	if(e == null) return false;
   		return e.getSource().equals(this) || e.getTarget().equals(this);
    }

    /**
     * returns the overall degree of this vertex. (in+out degree)
     */
    public int degree() {
        int sum = 0;
        for (List l : adjList.values()) {
            sum += l.size();
        }
        return sum;
    }

    /**
     * Returns the degree for a specific edge type (in+out degree)
     */
    public int degree(EdgeType type) {
        List l = adjList.get(type);
        return l==null ? 0: l.size();
    }
    
	public int indegree() {
        int sum = 0;
        for (EdgeType type : adjList.keySet()) {
			if(!type.isDirected())continue;
			List<E> l = adjList.get(type);
			for (E e : l) {
				if(e.getTarget() == this)sum++;
			}
		}
        return sum;
	}

	public int indegree(EdgeType type) {
		if(!type.directed)
			return 0;
		int sum = 0;
		List<E> l = adjList.get(type);
		for (E e : l) {
			if(e.getTarget() == this) sum++;
		}
		return sum;
	}


    /**
     * Iterates over all edges connected to this vertex
     */    
	public Iterable<E> edges() {        
        return new AllEdgeIterator<E>(adjList);
    }

    /**
     * Iterates over all edges of the given type connected to this vertex.
     */
    public Iterable<E> edges(EdgeType type) {
        List<E> l = adjList.get(type);
        if (l == null)
            return new EmptyIterable<E>();
        return l;
    }

    public Iterable<N> edgeNodes(EdgeType type) {
        List<E> l = adjList.get(type);
        if (l == null){
            return new EmptyIterable<N>();
        }
        return new EdgeNodeIterable(l, (N)this);
    }

    public String toString() {
        return label != null ? label : super.toString();
    }

    public boolean equals(Object o) {
        if (o instanceof Vertex) {
            return ((Vertex) o).getIndex() == id;
        }
        return false;
    }

    public int hashCode() {
        return id;
    }
    
	public E getEdge(Vertex oppositNode) {
		for (E e : edges()) {
			if(e.getOpposit((N) this) == oppositNode){
				return e;
			}			 
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public E getEdge(Vertex oppositNode, EdgeType type) {
		List<E> edges = adjList.get(type);
		if(edges != null){
			for (E e : edges) {
				if(e.getOpposit((N) this) == oppositNode){
					return e;
				}
			}
		}
		return null;
	}

	public Graph<N,E> getGraph() {
		return graph;
	}

	public void setGraph(Graph<N,E> g) {
		this.graph = g;
	}
     
}
