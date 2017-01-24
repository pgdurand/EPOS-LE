package epos.model.graph;

import java.util.HashMap;
import java.util.Map;



public abstract class AbstractGraph<N extends Vertex<N, E>, E extends Edge<N>> implements Graph<N,E>{
	
    protected Map<EdgeType, EdgeFactory<? extends E, N>> edgeFactories;
    protected EdgeFactory<? extends E,N> defaultFactory;
    
	public void addEdgeFactory(EdgeFactory<? extends E, N> factory) {
		if(edgeFactories == null)
			edgeFactories = new HashMap<EdgeType, EdgeFactory<? extends E,N>>();
        edgeFactories.put(factory.getEdgeType(), factory);
        if(defaultFactory == null)
            defaultFactory = factory;
	}
	public abstract int addVertex(N n);		
	
	public abstract void removeVertex(N n);
	
	public E addEdge(N n1, N n2, EdgeType type){
		if(edgeFactories.containsKey(type)){
			return addEdge(edgeFactories.get(type).createEdge(n1, n2));
		}else{
			throw new RuntimeException("Edge Type not supported!");
		}
	}
	
	public E addEdge(N n1, N n2) {		
		if(defaultFactory == null)
			throw new RuntimeException("No factory defined");
		return addEdge(n1, n2, defaultFactory.getEdgeType());
	}
	public abstract E addEdge(E e);	
	public abstract void removeEdge(N n1, N n2, EdgeType type);
	
	public void removeEdge(N n1, N n2) {
        if(defaultFactory == null)
            throw new RuntimeException("No default EdgeFactory set!");
        EdgeType t =  defaultFactory.getEdgeType();
        removeEdge(n1, n2, t);
    }

    public void removeEdge(E e) {
    	removeEdge(e.getSource(), e.getTarget(), e.getType());
    }
    
	public abstract boolean containsEdge(N n1, N n2, EdgeType type);
	
	public boolean containsEdge(N n1, N n2) {
        if(defaultFactory == null)
            throw new RuntimeException("No default EdgeFactory set!");
        EdgeType t =  defaultFactory.getEdgeType();
        return containsEdge(n1, n2, t);
    }
	public abstract Iterable<N> vertices();
	public abstract int vertexCount();

}
