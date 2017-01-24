/*
 * Created on Jun 26, 2006
 */
package epos.model.graph.methods;

import java.util.Arrays;
import java.util.Iterator;

import epos.model.graph.Edge;
import epos.model.graph.Graph;
import epos.model.graph.Vertex;


public class DFS <N extends Vertex<N, E>, E extends Edge<N>> implements Iterable<N>{
    public enum Color{BLACK, WHITE, GRAY};    
    Color[] colors;
    Graph g ;
    Vertex[] pis;
//    int d[];
//    int f[];
    int time = 0;
    
    N nextNode;
    Iterator<N> graphNodes;
    
    public DFS(Graph g){
        this.g = g;
        
        colors = new Color[g != null ? g.getMaxIndex() : 0];
        pis = new Vertex[g != null ? g.getMaxIndex() : 0];
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
        for (E e : n.edges()) {
        	N v = e.getOpposit(n); 
            if(colors[v.getIndex()] == Color.WHITE){
                pis[v.getIndex()] = n;                    
                return v;
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
                    if(nextNode != null) {
		                if(colors[nextNode.getIndex()] != Color.WHITE)
		                    nextNode = null;
                    }
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

    
    public boolean search(Vertex n){
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
            return DFS.this.next();
        }
        public void remove() {
            throw new RuntimeException("Method not supported");            
        }
    }
}
