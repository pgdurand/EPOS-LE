/*
 * Created on Jun 27, 2006
 */
package epos.model.graph;

/**
 * Edge type can be used to create different edges. Default types are directed or undirected edge.
 * 
 * @author Thasso
 */
public class EdgeType{
    
    public static final  EdgeType DIRECTED = new EdgeType("Directed", true);
    public static final  EdgeType UNDIRECTED = new EdgeType("Undirected", false);
    
    
    protected boolean directed = false;
    protected String name = null;
    
    /**
     * The new Edge type should get a unique name and can be directed/undirected
     * 
     * @param name the name
     * @param directed 
     */
    public EdgeType(String name, boolean directed){
        this.name = name;
        this.directed = directed;
    }

    /**
     * Returns true if this edgetype is directed
     * 
     * @return the directed
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * Returns the name of the edge type
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }    
    
    public String toString(){
        return name;
    }
    
    public boolean equals(Object o){
        if(o instanceof EdgeType){
            return ((EdgeType)o).name.equals(name);
        }
        return false;
    }
    
    public int hashCode(){
        return name.hashCode();
    }
}
