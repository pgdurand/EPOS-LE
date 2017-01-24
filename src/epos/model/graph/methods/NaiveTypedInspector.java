/*
 * Created on Jun 26, 2006
 */
package epos.model.graph.methods;

import epos.model.graph.EdgeType;
import epos.model.graph.Graph;
import epos.model.graph.Vertex;


public class NaiveTypedInspector implements TypedConnectivityInspector{

    Graph g;
    
    public NaiveTypedInspector(Graph g){
        this.g = g;
    }
    public void delete(Vertex n1, Vertex n2, EdgeType type) {
    }

    public void insert(Vertex n1, Vertex n2, EdgeType type) {
    }

    public boolean isConnected(Vertex a, Vertex b, EdgeType[] types) {
        TypedDFS dfs  = new TypedDFS(g,types);        
        return dfs.search(a, b);
    }

}
