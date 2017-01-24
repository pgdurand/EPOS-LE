/*
 * Created on 22.11.2004
 */
package epos.algo.construction.aglomerative;

import java.util.HashMap;

import org.jdesktop.application.AbstractBean;

import epos.model.graph.Edge;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;


/**
 * @author Thasso
 */
public class AglomerativeClustering extends AbstractBean {
	
	public static final int METHOD_UPGMA = 0;
	public static final int METHOD_WPGMA = 1;
	public static final int METHOD_SINGLE_LINKAGE = 2;
	public static final int METHOD_COMPLETE_LINKAGE = 3;
	
	public static final String[] METHOD_NAMES = new String[]{
	        "UPGMA", "WPGMA", "Single Linkage", "Complete Linkage"};
	
	private double[][] distances;
	protected Tree tree;
	
	protected HashMap<Cluster, TreeNode> cluster2node = new HashMap();
	
	private String clusterMethod = "UPGMA";
	
	protected int method = METHOD_UPGMA;
	
	public Cluster[] cluster;
	public String[] names;
	private Tree resultTree;
	
	public AglomerativeClustering(){
		tree = new Tree();
	}

	public AglomerativeClustering(double[][] distances, String[] names){
		this();
		this.distances = distances;
        this.names = names;
	}
	public AglomerativeClustering(double[][] distances,String[] names, int method){
		this(distances, names);
		if(method >= 0 && method <= 3)
			this.method = method;
		else 
			throw new IllegalArgumentException("Method not supported!");
	}		

	protected void init(){
			cluster = new Cluster[names.length];            
			for (int i = 0; i < cluster.length; i++) {               
				cluster[i] = new Cluster(1, names[i]);
				TreeNode n = new TreeNode(cluster[i].toString());//tree.newNode(cluster[i]);
				tree.addVertex(n);
				cluster2node.put(cluster[i], n);
			}	        
	}

	protected void computeTree(){
		int end = distances.length;
		int step = 1;
		
		while (distances.length >= 2){
			int[] min = findMinimum(distances);
			double d = distances[min[0]][min[1]];			
			distances = recomputeMatrix(distances,min[0],min[1], cluster);
			Cluster[] nc = new Cluster[cluster.length - 1];			
			int off = 0;
			for (int i = 0; i < cluster.length; i++) {
				if(i != min[0] && i != min[1])
					nc[i - off] = cluster[i];					
				else
					off++;
			}
			nc[nc.length - 1] = joinCluster(cluster[min[0]], cluster[min[1]],d, tree);//new Cluster(cluster[min[0]].getSize() + cluster[min[1]].getSize());
			cluster = nc;
			step++;
		}
	}
	
	protected Cluster joinCluster(Cluster n1, Cluster n2, double distance, Tree g){
		double d = distance/2d;
		
		Cluster cluster = new Cluster(n1.getSize() + n2.getSize());
		cluster.setDistanceToLeaves(d);
		TreeNode cnode = new TreeNode(cluster.toString());
		g.addVertex(cnode);
		cluster2node.put(cluster, cnode);
            Edge e1 = g.addEdge(cnode, cluster2node.get(n1));
            e1.setWeight(d - n1.getDistanceToLeaves());
            //g.addEdge(e1);            
            Edge e2 = g.addEdge(cnode, cluster2node.get(n2));
            e2.setWeight(d - n2.getDistanceToLeaves());
            //g.addEdge(e2);            
		return cluster;
	}
	
	protected double[][] recomputeMatrix(double[][] matrix, int i, int j, Cluster[] cluster){
		double[][] newMatrix = new double[matrix.length - 1][ matrix.length - 1];

		int off1 = 0;
		for (int k = 0; k < matrix.length; k++) {
			int off2 = 0;
			if(k != i && k != j)			
				for (int l = 0; l < matrix.length; l++) {
					if(l != i && l != j)
						newMatrix[k - off1][l - off2] = matrix[k][l];
					else
						off2++;
				}
			else
				off1++;
		}
		int off = 0;
		for (int k = 0; k < newMatrix.length - 1; k++) {
			if(( k == i || k == j ) && off < 2)
				off++;
			if( (k + off == i || k + off == j) && off < 2)
				off++;
			newMatrix[k][newMatrix.length - 1] = distance(i,j,k + off, matrix, cluster[i].getSize(), cluster[j].getSize());
		}
		off = 0;
		for (int k = 0; k < newMatrix.length - 1; k++) {
			if(( k == i || k == j ) && off < 2)
				off++;
			if( (k + off == i || k + off == j) && off < 2)
				off++;

			newMatrix[newMatrix.length - 1][k] = distance(i,j,k + off, matrix, cluster[i].getSize(), cluster[j].getSize());
		}
		return newMatrix;
	}
	
	/**
	 * Recompute distance from new cluster (i,j) to the node k,
	 * ni,nj are the number of nodes in cluster i and j.
	 * 
	 * @param i first part of the new cluster
	 * @param j second part of the new cluster
	 * @param k another cluster k != i && k != j
	 * @param matrix the old matrix where i and j were not combined
	 * @param ni the size of the cluster i
	 * @param nj the size of the cluster ij
	 * @return distance from new cluster (i,j) to cluster k
	 */
	private double distance(int i, int j, int k, double[][] matrix, int ni, int nj) {
		double d = 0;
		switch (method) {
			case METHOD_UPGMA :
				d = ((double)ni * matrix[i][k] + (double)nj * matrix[j][k]) / (double)(ni + nj);				
				break;
			case METHOD_WPGMA :
				d = (matrix[i][k] + matrix[j][k]) / 2d;				
				break;
			case METHOD_SINGLE_LINKAGE :
				d = Math.min(matrix[i][k],matrix[j][k]);				
				break;
			case METHOD_COMPLETE_LINKAGE :
				d = Math.max(matrix[i][k],matrix[j][k]);				
				break;
			default :
				d = ((double)ni * matrix[i][k] + (double)nj * matrix[j][k]) / (double)(ni + nj);
				break;
		}
		return d;
	}
	
	public Tree getTree(){	    
		init();
		computeTree();
		return tree;
	}
	
	// dummy matrix printing for small values
	private void printMatrix(double[][] matrix, String[] names){
		StringBuffer s = new StringBuffer();
		int length = matrix.length;
		s.append("\t");
		for (int i = 0; i < names.length; i++) {
			s.append("\t<"+names[i]+">");
		}
		s.append("\n");
		for (int i = 0; i < matrix.length; i++) {
			s.append("\t<"+names[i]+">");
			for (int j = 0; j < matrix.length; j++) {
				s.append("\t" + matrix[i][j]);
			}
			s.append("\n");
		}
		System.out.println(s.toString());
	}
	
	/**
	 * Find the minimum value in a double[][] n x n symetric matrix 
	 * Returns int[] of the coordinates of the minimum value.
	 * (excludes 0 as minimum value)
	 * 
	 * @param m
	 * @return
	 */
	protected int[] findMinimum(double[][] m) {
		int[] min = new int[]{0,0};
		double minimum = Double.MAX_VALUE;
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				if(i != j && m[i][j] < minimum ){
					minimum = m[i][j];
					min[0] = i;
					min[1] = j;
				}
			}
		}
		return min;
	}

    /**
     * @return
     */
    public String getMethodName() {
        return METHOD_NAMES[method];
    }

    
	public void run() throws Exception {
		resultTree = getTree();
	}

	

	public String getClusterMethod() {
		return clusterMethod;
	}

	public void setClusterMethod(String clusterMethod) {
		String old = this.clusterMethod;
		this.clusterMethod = clusterMethod;
		firePropertyChange("clusterMethod", old, this.clusterMethod);
		//"UPGMA", "WPGMA", "Single Linkage", "Complete Linkage"
		if(clusterMethod.equals("UPGMA")){
			method = METHOD_UPGMA;
		}else if(clusterMethod.equals("WPGMA")){
			method = METHOD_WPGMA;
		}else if(clusterMethod.equals("Single Linkage")){
			method = METHOD_SINGLE_LINKAGE;
		}else if(clusterMethod.equals("Complete Linkage")){
			method = METHOD_COMPLETE_LINKAGE;
		}
	}


}

class Cluster {
	int size = 1;
	double distanceToLeaves = 0;
	Object userObject = null;
	
	public Cluster(){	
		super();
		size = 1;
	}
	
	public Cluster(int size){
	    this();
		this.size = size;
	}
	
	public Cluster(int size, Object userObject){
	    this();
		this.size = size;
		this.userObject = userObject;
	}
	
	public int getSize(){
		return size;
	}
	public void setSize(int size){
		this.size = size;
	}
	public double getDistanceToLeaves() {
		return distanceToLeaves;
	}
	public void setDistanceToLeaves(double distanceToLeaves) {
		this.distanceToLeaves = distanceToLeaves;
	}
	public String toString(){
	    if(userObject != null)
	        return userObject.toString();
	    else
	        return "";//Integer.toString(getSize());
	}
    public Object getUserObject() {
        return userObject;
    }
    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }
}
