/*
 * Created on Jun 27, 2006
 */
package epos.model.graph;




public class DefaultEdge<T extends Vertex> implements Edge<T> {

    protected EdgeType type;
    protected T source;
    protected T target;
    private double weight = 1.0;

    public DefaultEdge(EdgeType type, T source, T target){
        super();
        this.type = type;
        this.source = source;
        this.target = target;
    }
    
    public EdgeType getType() {
        return type;
    }

    public boolean isDirected() {
        return getType().isDirected();
    }

    public T getSource() {
        return source;
    }

    public T getTarget() {
        return target;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public T getOpposit(T node) {
        if(node.equals(source))
            return target;
        return source;       
    }
    
    
    public boolean equals(Object o){
        if(o instanceof Edge){
        	//if both edges are of type 'undirected', it doesn't matter what is source and what target
        	if(this.getType().equals(EdgeType.UNDIRECTED) && ((Edge)o).getType().equals(EdgeType.UNDIRECTED)){
        		return ((Edge)o).getSource().equals(source) && ((Edge)o).getTarget().equals(target) || ((Edge)o).getSource().equals(target) && ((Edge)o).getTarget().equals(source);
        	}else{
        		return ((Edge)o).getSource().equals(source) && ((Edge)o).getTarget().equals(target);
        	}
        }
        return false;
    }

}
