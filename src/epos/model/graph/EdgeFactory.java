/*
 * Created on Jun 27, 2006
 */
package epos.model.graph;



public interface EdgeFactory<E extends Edge<N>, N extends Vertex> {
 
    public E createEdge(N source, N target);
    public EdgeType getEdgeType();
}
