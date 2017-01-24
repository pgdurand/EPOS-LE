/*
 * Created on Jun 26, 2006
 */
package epos.model.graph.methods;

import epos.model.graph.EdgeType;
import epos.model.graph.Vertex;



public interface TypedConnectivityInspector {

    public void delete(Vertex n1, Vertex n2, EdgeType type);
    public void insert(Vertex n1, Vertex n2, EdgeType type);
    public boolean isConnected(Vertex a, Vertex b, EdgeType[] types);    
}
