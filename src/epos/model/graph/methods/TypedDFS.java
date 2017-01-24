/*
 * Created on Jun 26, 2006
 */
package epos.model.graph.methods;

import java.util.Arrays;
import java.util.Iterator;

import epos.model.graph.Edge;
import epos.model.graph.EdgeType;
import epos.model.graph.Graph;
import epos.model.graph.Vertex;


public class TypedDFS<N extends Vertex<N, E>, E extends Edge<N>> implements Iterable<N>{
    public enum Color{BLACK, WHITE, GRAY};
    
    Color[] colors;
    Graph<N, E> g ;
    Vertex[] pis;
//    int d[];
//    int f[];
    int time = 0;
    EdgeType[] types;
    
    N nextNode;
    Iterator<N> graphNodes;
    
    public TypedDFS(Graph<N,E> g, EdgeType[] types){
        this.g = g;
        this.types = types;
        colors = new Color[g.vertexCount()];
        pis = new Vertex[g.vertexCount()];
//        d = new int[g.getSize()];
//        f = new int[g.getSize()];
        
    }

    public void reinit(){
        Arrays.fill(colors,Color.WHITE);
        Arrays.fill(pis,null);
    }
        
    private N nextTovisit(N n) {
        if(n == null){
            return null;
        }        
        colors[n.getIndex()] = Color.GRAY;
        time++;
//        d[n.index] = time;
        for (EdgeType type : types) {
            for (N v : n.edgeNodes(type)) {
                if(colors[v.getIndex()] == Color.WHITE){
                    pis[v.getIndex()] = n;                    
                    return v;
                }
            }
        }
        colors[n.getIndex()] = Color.BLACK;
//        f[n.index] = time = time + 1;
        return n;
    }
        
    private N next(){
        while(colors[nextNode.getIndex()] != Color.BLACK){
            nextNode = nextTovisit(nextNode);            
        }                
        N ret = nextNode;
        nextNode = nextTovisit((N)pis[nextNode.getIndex()]);
        
        if(nextNode == null){  
            if(graphNodes != null){                
                    while( graphNodes.hasNext() ){
                        nextNode = graphNodes.next();
                        if(colors[nextNode.getIndex()] == Color.WHITE)
                            break;
                    }
                    if(colors[nextNode.getIndex()] != Color.WHITE)
                        nextNode = null;                    
            }
        }        
        return ret;
    }
    
    public Iterator<N> iterator() {
        reinit();
        return new NodeIterator();
    }
    
    public Iterator<N> iterator(N start) {
        reinit();
        return new NodeIterator(start);
    }

    
    public boolean search(N n){
        for (Vertex node : this) {
            if(node.equals(n))
                return true;
        }
        return false;               
    }
    
    public boolean search(N start, N target){
        reinit();
        Iterator<N> it = new NodeIterator(start);
        while (it.hasNext()) {
            Vertex n = (Vertex) it.next();
            if(n.equals(target))
                return true;
        }
        return false;               
    }    

    
    class NodeIterator implements Iterator<N>{
        public NodeIterator(){
            graphNodes = g.vertices().iterator();
            nextNode = graphNodes.hasNext() ? graphNodes.next() : null;
        }
        public NodeIterator(N start){
            graphNodes = null;
            nextNode = start;
        }

        public boolean hasNext() {
            return nextNode != null;
        }
        public N next() {
            return TypedDFS.this.next();
        }
        public void remove() {
            throw new RuntimeException("Method not supported");            
        }
    }
}
