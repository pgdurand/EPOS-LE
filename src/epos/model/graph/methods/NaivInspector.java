package epos.model.graph.methods;

import epos.model.graph.Graph;
import epos.model.graph.Vertex;

public class NaivInspector implements ConnectivityInspector{
    protected Graph g;
    
    public NaivInspector(Graph g){
        this.g = g;
    }
    public void delete(Vertex n1, Vertex n2) {
    }

    public void insert(Vertex n1, Vertex n2) {
    }

    public boolean isConnected(Vertex a, Vertex b) {
        DFS dfs  = new DFS(g);        
        return dfs.search(a, b);
    }

}
