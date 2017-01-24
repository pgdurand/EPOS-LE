/*
 * Created on Jun 26, 2006
 */
package epos.model.graph;

import java.util.HashMap;
import java.util.Map;

public class SimpleGraph<N extends Vertex<N, E>, E extends Edge<N>> implements Graph<N,E> {
    
    protected FixedIndexList<N> nodes;        
    protected Map<EdgeType, EdgeFactory<? extends E, N>> edgeFactories;
    protected EdgeFactory<? extends E,N> defaultFactory;
    protected int size = 0;
   
    
    public SimpleGraph(){
        super();
        nodes =  new FixedIndexList<N>(10); 
        edgeFactories = new HashMap<EdgeType, EdgeFactory<? extends E,N>>();
        //addEdgeFactory((EdgeFactory<? extends E, N>) new DefaultEdgeFactory<N>(EdgeType.UNDIRECTED));
    } 

    public void addEdgeFactory(EdgeFactory<? extends E, N> factory) {
        edgeFactories.put(factory.getEdgeType(), factory);
        if(defaultFactory == null)
            defaultFactory = factory;
    }

    public int addVertex(N n) {
    	nodes.put(n);
    	n.setGraph(this);
    	return n.getIndex();
    }
    
	public void removeVertex(N n) {
        removeVertex(n.getIndex());
    }

    protected E addEdge(N n1, N n2, EdgeFactory<? extends E,N> f){
        E e = f.createEdge(n1, n2);
        return addEdge(e);          
    }
    
    public E addEdge(N n1, N n2, EdgeType type) {
        EdgeFactory<? extends E, N> f = edgeFactories.get(type);
        if(f == null)
            throw new RuntimeException("No EdgeFactory for type " + type );
        return addEdge(n1, n2, f);
    }
    
    public E addEdge(N n1, N n2) {
        if(defaultFactory == null)
            throw new RuntimeException("No default EdgeFactory set!");
        return addEdge(n1, n2, defaultFactory);        
    }

    public E addEdge(E e) {
    	if(e.getSource().getIndex() < 0 || e.getTarget().getIndex() <0){
    		throw new RuntimeException("One of the nodes connected by the new edge has a negative index, which means it is not associated to a graph !!!");
    	}
        e.getSource().addEdge(e);
        e.getTarget().addEdge(e);
        return e;
    }

    public void removeEdge(N n1, N n2, EdgeType type) {
    	E e = n1.getEdge(n2, type);
    	if(e != null){
    		n1.removeEdge(e);
    		n2.removeEdge(e);
    	}
    }

    public void removeEdge(N n1, N n2) {
        if(defaultFactory == null)
            throw new RuntimeException("No default EdgeFactory set!");
        EdgeType t =  defaultFactory.getEdgeType();
        removeEdge(n1, n2, t);
    }

    public void removeEdge(E e) {
    	if(
    	  getVertex(e.getSource().getIndex()) == e.getSource()
    	  && getVertex(e.getTarget().getIndex()) == e.getTarget()){
	        e.getSource().removeEdge(e);
	        e.getTarget().removeEdge(e);
    	}else{
    		throw new RuntimeException("Edge connects Vertices that are not contained in this graph!");
    	}
    }

    public boolean containsEdge(N n1, N n2, EdgeType type) {
        return n1.getEdge(n2, type) != null;
    }

    public boolean containsEdge(N n1, N n2) {
        if(defaultFactory == null)
            throw new RuntimeException("No default EdgeFactory set!");
        EdgeType t =  defaultFactory.getEdgeType();
        return containsEdge(n1, n2, t);
    }

    public Iterable<N> vertices() {
        return nodes;
    }

    public int vertexCount() {
        return nodes.size();
    }

	public int edgeCount() {
		int c = 0;
		for (N n : vertices()) {
			c += n.degree();
		}
		return c/2;
	}

	public int edgeCount(EdgeType type) {
		int c = 0;
		for (N n : vertices()) {
			c += n.degree(type);
		}
		return c/2;
	}

	public E getEdge(N n1, N n2) {
		return n1.getEdge(n2);
	}

	public E getEdge(N n1, N n2, EdgeType type) {
		return n1.getEdge(n2, type);
	}

	public N getVertex(int index) {
		return nodes.get(index);		
	}

	public void removeVertex(int index) {
		N n = nodes.remove(index);
		if(n != null){
			n.clear();
			n.setGraph(null);
			n.setIndex(-1);
		}
	}

	public FixedIndexList<N> getNodes() {
		return nodes;
	}

	public int getMaxIndex() {
		return nodes.getMaximalIndex();
	}

	public void removeEdge(int i, int j) {
		removeEdge(getVertex(i), getVertex(j));
	}

	
}



