/*
 * Created on Jun 27, 2006
 */
package epos.model.graph;



public class DefaultEdgeFactory<N extends Vertex> implements EdgeFactory<DefaultEdge<N>, N>{

    protected EdgeType type;

    public DefaultEdgeFactory(EdgeType type){
        this.type = type;
    }
    public EdgeType getEdgeType() {
        return type;
    }
    public DefaultEdge<N> createEdge(N source, N target) {        
        return new DefaultEdge<N>(type, source, target);
    }

}
