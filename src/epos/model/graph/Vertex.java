/*
 * Created on Jun 27, 2006
 */
package epos.model.graph;



/**
 * A node in a Graph.
 * 
 * @author Thasso
 */
public interface Vertex<T extends Vertex, E extends Edge<T>> extends IndexedElement {
    
	/**
	 * returns the index of this vertex.
	 * @return
	 */
	public int getIndex();
	
	/**
	 * Sets the index of this vertex. This is basically for internal use,
	 * do not modify indices unless you know what you are doing.
	 * 
	 * @param index
	 */
	public void setIndex(int index);
	
    /**
     * Set a node label
     */
    public void setLabel(String label);
    
    /**
     * Returns the nodelabel
     * 
     * @return label
     */
    public String getLabel();
               
    /**
     * Removes all edges adjacent to this node.
     * Should be called when removing a vertex from a graph.
     */
    public void clear();
    
    /**
     * Add a given edge to the list of edges
     * 
     * @param e
     */
    public void addEdge(E e);
    /**
     * Remove a edge from the list of edges
     * 
     * @param e
     */
    public void removeEdge(E e);

    /**
     * Returns true if this node contains the given edge
     * 
     * @param e
     * @return
     */
    public boolean containsEdge(E e);
        
    /**
     * Returns the edge to the opposit node or null if there is no such edge.
     * 
     * @param oppositNode
     * @return
     */
    public E getEdge(Vertex oppositNode);
    
    /**
     * Returns the typed edge to the opposit node or null if there is no such edge.
     * 
     * @param oppositNode
     * @return
     */
    public E getEdge(Vertex oppositNode, EdgeType type);
    
    /**
     * Returns the degree of the node
     * 
     * @return
     */    
    public int degree();
    
    /**
     * Returns the number of connected edges of a given type.
     * 
     * @param type
     * @return
     */
    public int degree(EdgeType type);
    
    /**
     * Returns the indegree of this node. (Number of directed edges connected to
     * this node, where this node is the target vertex of the edge)
     * 
     * @return
     */
    public int indegree();
    
    /**
     * Returns the indegree of this node for a
     * given edge type. (Number of directed edges od given type pointing to this node)
     * 
     * @return
     */
    public int indegree(EdgeType type); 


    /**
     * Iterates over all edges of the node
     * 
     * @return
     */
    public Iterable<E> edges();
    
    /**
     * Iterates over edges of a given type
     * @param type
     * @return
     */
    public Iterable<E> edges(EdgeType type);

    /**
     * Uses the edge iterator ofer edges of the given type but returns the opposit nodes
     * to this node.
     * 
     * @param type
     * @return
     */
    public Iterable<T> edgeNodes(EdgeType type);    
        
    /**
     * Sets the graph that contains this vertex
     * @param g
     */
    public void setGraph(Graph<T,E> g);
    
    /**
     * Returns the graph that contains this vertex
     * @return
     */
    public Graph<T,E> getGraph();
        
}
