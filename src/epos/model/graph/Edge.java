/*
 * Created on Jun 27, 2006
 */
package epos.model.graph;
 



/**
 * The Edge interface describes a simple edge in a graph.
 * The Edge might be typed with the type of vertices connected
 * by this edge.
 * 
 * @author thasso
 *
 * @param <T>
 */
public interface Edge<T extends Vertex> {
    
	/**
	 * Return the {@link EdgeType} of this edge.
	 * @return
	 */
    public EdgeType getType();
    /**
     * Return the source vertex
     * 
     * @return source vertex
     */
    public T getSource();
    /**
     * Return the target vertex
     * 
     * @return target vertex
     */
    public T getTarget();
    /**
     * Returns true if this is an directed edge.
     * 
     * @return true if directed
     */
    public boolean isDirected();
    
    /**
     * If set, the weight of this edge is returned
     * 
     * @return
     */
    public double getWeight();
    /**
     * Sets the weigh of this edge if the implementation 
     * supports weighted edges.
     * 
     * @param weight
     */
    public void setWeight(double weight);
    
    /**
     * Returns the opposit of source or target if the given vertex itself is 
     * source or target.
     *  
     * @param node source or target vertex
     * @return opposit vertex of given vertex
     */
    public T getOpposit(T vertex);
}
