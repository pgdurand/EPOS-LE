package epos.model.graph.methods;

import epos.model.graph.Vertex;

public interface ConnectivityInspector {
    public void delete(Vertex n1, Vertex n2);
    public void insert(Vertex n1, Vertex n2);
    public boolean isConnected(Vertex a, Vertex b);    

}
