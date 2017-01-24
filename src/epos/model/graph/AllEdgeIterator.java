package epos.model.graph;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AllEdgeIterator<E extends Edge> implements Iterable<E>, Iterator<E>{
	
    Iterator<E> w = null;
    Iterator<EdgeType> types;
    Map<EdgeType, List<E>> adjList;
    
    public AllEdgeIterator(Map<EdgeType, List<E>> adjList){
    	this.adjList = adjList;
        types = adjList.keySet().iterator();
    }
    public Iterator<E> iterator() {
        return this;
    }

    public boolean hasNext() {
        if(w != null && w.hasNext())
            return true;
        if(types.hasNext()){
            w = adjList.get(types.next()).iterator();
            return hasNext();
        }            
        return false;
    }

    public E next() {
        return w.next();
    }

    public void remove() {            
    }
    
}