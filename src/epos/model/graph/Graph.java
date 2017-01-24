/*
 * Created on Jun 27, 2006
 */
package epos.model.graph;



/**
 * Simple graph interface. A Graph G=(V,E) consists of a set of vertices V and a
 * set of edges E. The Graph consists of generic typs for nodes and edges and
 * the default implementation should only allow one type of node in the graph
 * (at least one supertye).
 * <br>
 * <p>
 * The edges used in this graph are strictly typed. That is, they have to implement
 * at least the {@link Edge} interface, but might be further typed. This allows the 
 * creation of a "mixed" graph, that contains edges of different types.
 * <br>
 * Typed edges are created using a {@link EdgeFactory} to instanciate new edges.
 * </p>
 * <p>
 * The interface does not differentiate between directed and undirected graphs. The actual
 * implementation might further define the type of graph implemented.
 * </p>
 * @author Thasso
 */
public interface Graph<T extends Vertex, E extends Edge<T>> {

	/**
	 * Add an edge factory for a special type of edges. a graph should consist
	 * at least of on edge factory. The first added edge factory is the default
	 * one.
	 * 
	 * @param factory
	 */
	public void addEdgeFactory(EdgeFactory<? extends E, T> factory);

	/**
	 * Add node n to the graph.
	 * 
	 * @param n
	 * @return the index of the added vertex
	 */
	public int addVertex(T n);
	
	/**
	 * Returns the vertex with the given index or null
	 * 
	 * @param index
	 * @return
	 */
	public T getVertex(int index);

	/**
	 * Remove a node from the graph. This also removes all edges connected to
	 * this node.
	 * 
	 * @param n
	 */
	public void removeVertex(T n);
	
	/**
	 * Removes the vertex with the given index.
	 * @param index
	 */
	public void removeVertex(int index);

	/**
	 * use the edgeFactory for the given edgetype, create a new edge between
	 * nodes n1 and n1 and add it to the graph.
	 * 
	 * @param n1
	 * @param n2
	 * @param type
	 * @return newly created edge
	 */
	public E addEdge(T n1, T n2, EdgeType type);

	/**
	 * Use the default edgeFactory to create a new edge between nodes n1 and n2
	 * and add the edge to the graph.
	 * 
	 * @param n1
	 * @param n2
	 * @return newly created edge
	 */
	public E addEdge(T n1, T n2);

	/**
	 * Remove edge of type t between nodes n1 and n2
	 * 
	 * @param n1
	 * @param n2
	 * @param type
	 */
	public void removeEdge(T n1, T n2, EdgeType type);

	/**
	 * Remove edge with default type between n1 and n2.
	 * 
	 * @param n1
	 * @param n2
	 */
	public void removeEdge(T n1, T n2);

	/**
	 * Returns true if the graph contains an edge between n1 and n2
	 * 
	 * @param n1
	 * @param n2
	 * @param type
	 * @return true if edge exists
	 */
	public boolean containsEdge(T n1, T n2, EdgeType type);

	/**
	 * Returns true if the graph contains an edge between n1 and n2
	 * 
	 * @param n1
	 * @param n2
	 * @return true if edge exists
	 */
	public boolean containsEdge(T n1, T n2);
	
	/**
	 * Returns the edge between the two given nodes or null if there is no such edge.
	 * 
	 * @param n1
	 * @param n2
	 * @return Edge between n1 and n2 or null
	 */
	public E getEdge(T n1, T n2);
	
	/**
	 * Returns the edge of given type between the two given nodes or null if there is no such edge.
	 * 
	 * @param n1
	 * @param n2
	 * @param type
	 * @return Edge between n1 and n2 or null
	 */
	public E getEdge(T n1, T n2, EdgeType type);

	
	/**
	 * Iterates over the nodes in this graph.
	 * 
	 * @return
	 */
	public Iterable<T> vertices();
		
	/**
	 * Returns the size of the graph. (number of vertices)
	 * 
	 * @return number of vertices
	 */
	public int vertexCount();
	
	/**
	 * returns the number of all edges in this graph.
	 * 
	 * @return num edges
	 */
	public int edgeCount();
	
	/**
	 * Returns the number of edges of a given type.
	 * 
	 * @return num edges of a given type
	 */
	public int edgeCount(EdgeType type);
	
	/**
	 * Returns the maximal index over all vertices
	 * 
	 * @return
	 */
	public int getMaxIndex();
}
