/*
 * Created on 13/06/2006
 */
package epos.algo.construction.nj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import epos.model.graph.DefaultEdgeFactory;
import epos.model.graph.DefaultVertex;
import epos.model.graph.Edge;
import epos.model.graph.EdgeType;
import epos.model.graph.SimpleGraph;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;


/**
 * <p>
 * Compute a Neighbour Joining Tree for a given distance matrix.
 * The implementation follows the implementation of QuickTree including the modifications
 * for the non negative edges, which can happen if the matrix is not even close to additivity.
 * </p>
 * <p>
 * Three possible strategies for rooting the final tree are supported:
 * <pre>
 * 1. arbitray: the root is placed between the last two unjoined nodes.
 * 2. outgrouping: the root is placed in the middle of the edge to the outgroupOTU
 * 3. mid-point: the longest branch is detected and the root is placed in the middle of this branch.
 *               the longest path here is the path from one node to another with the maximum sum of weights.
 * </pre>
 * </p> 
 * 
 * @author Thasso
 */
public class NJTree {
    
    /**
     * the root is placed between the last two unjoined nodes.
     */
    public static final int ROOT_ARBITRARY = 0;
    /**
     * the root is placed in the middle of the edge to the outgroupOTU
     */
    public static final int ROOT_OUTGROUP = 1;
    /**
     * the longest branch is detected and the root is placed in the middle of this branch.
     */
    public static final int ROOT_MIDPOINT = 2;
    
    /**
     * the rooting method, default is arbitrary
     */
    protected int rootingMethod = ROOT_ARBITRARY;
    
    /**
     * All clusters stored here
     */
    protected OTU[] otus;
    
    /**
     * the number of initial leaves of the tree -> the number of elements in the matrix
     */
    protected int n;
    /**
     * number of nodes in the tree, increased during iteration.
     */
    protected int k;
        
    /**
     * subtree length's for each node. Recomputed in each iteration step.
     */
    protected double[] r;
    
    /**
     * Name of an outgroup node
     */
    protected String outGroup;
    /**
     * The outgroup OTU
     */
    protected OTU outGroupOTU;
    
    /**
     * The undirected NJ Tree is stored here.
     */
    protected SimpleGraph g;

    /**
     * The distance matrix
     */
    protected double[][] distances;
    /**
     * node names
     */
    protected String[] names;
    
    /**
     * Create a new uninitialized NJTree.
     *
     */
    public NJTree(){
        super();
    }
    
    /**
     * Create a new NJTree and initialize data
     * 
     * @param distances
     * @param names
     */
    public NJTree(double[][] distances, String[] names){
        this();
        setData(distances, names);
    }
    
    /**
     * Set data, does not initialize the arrays !
     * 
     * @param distances
     * @param names
     */
    public void setData(double[][] distances, String[] names){
        this.distances = distances;
        this.names = names;
    }
    
    /**
     * Do the initialization
     *
     */
    protected boolean init(){
        if( names == null || distances == null)
            return false;
        
        g = new SimpleGraph();
        g.addEdgeFactory(new DefaultEdgeFactory<DefaultVertex>(EdgeType.UNDIRECTED));
        this.n = names.length;
        k = n;
        
        r = new double[2*n-1]; // number of nodes in a fully resolved rooted binary tree with n leaves
        
        otus = new OTU[2*n-1];
        for (int i = 0; i < n; i++) {
            otus[i] = new OTU();
            otus[i].name = names[i];
            otus[i].dmat = distances[i];
            g.addVertex(otus[i]);
        }
        
        
        /// detected outgroup OTU
        if(rootingMethod == ROOT_OUTGROUP && outGroup != null){
            for (int i = 0; i < otus.length; i++) {
                if(otus[i].name != null && otus[i].name.equals(outGroup)){
                    outGroupOTU = otus[i];
                    break;
                }
            }
        }

        return true;
    }
    
    /**
     * Start the computation and returns the resulting tree.
     * 
     * @return
     */
    public Tree getTree() {
        if(!init())
            return null;
        /// cluster OTU's
        // this is the main loop
        while (k < 2 * n - 2){
            computeR();
            int[] nn = getNextNeighbours();
            join(nn[0], nn[1]);
        }
        
        int l0 = 0, l1 = 1;
        
        // two unjoined OTU's left now. (l0 and l1)
        // detect them, (one is k-1) 
        // annd append en edge between them, weighted with d(l0, l1)
        if (k> 2) {
            int k2= k-2;
            while (otus[k2].joined)
                --k2;
            l0 = k2;
            l1 = k-1;
            double dij = d(l0, l1);
            dij = dij< 0 ? 0 : dij;
            Edge e = g.addEdge(otus[l0], otus[l1]);
            e.setWeight(dij);                        
        }

        /*
         * Now the rooting of the final tree.
         * Three possible strategies.
         * 
         * 1. arbitray: the root is placed between the last two unjoined nodes.
         * 2. outgrouping: the root is placed in the middle of the edge to the outgroupOTU
         * 3. mid-point: the longest branch is detected and the root is placed in the middle of this branch. 
         */
        OTU ol0 = otus[l0];
        OTU ol1 = otus[l1];
        OTU root = null;
        double dl0 = -1;
        double dl1 = -1;
        
        /*
         * do the root detection, this modifys ol0 and ol1 if a root is detected  
         */
        if(rootingMethod == ROOT_OUTGROUP && outGroupOTU != null){
            //List edges = outGroupOTU.edges();//g.edgesOf(outGroupOTU);
            //if(edges.size() > 1)
            if(outGroupOTU.degree() > 1)
                throw new RuntimeException("The outgroup has more than one edge, something went seriously wrong!");
            else{
                ol0 = outGroupOTU;
                ol1 = (OTU) ((Edge<OTU>)outGroupOTU.edges().iterator().next()).getOpposit(outGroupOTU);//  ((Edge)edges.get(0)).oppositeVertex(ol0);
            }
        }else if(rootingMethod == ROOT_MIDPOINT){            
            Counter c = getLongestBranch(ol0, ol1);
            List edges = c.edgeList;
            double maxLength = c.max;
            double sum = 0;
            for (Iterator it = edges.iterator(); it.hasNext();) {
                Edge e = (Edge) it.next();
                sum += e.getWeight();
                if(sum >= (maxLength/2)){
                    ol0 = (OTU) e.getSource();
                    ol1 = (OTU) e.getTarget();
                    dl0 = (maxLength/2) - (sum - e.getWeight()); 
                    dl1 = sum - (maxLength/2);
                    break;
                }
            }
        }
        
        /// set root node
        otus[k] = new OTU();            
        root = otus[k];
        /// split edge between l0 and l1
        
        Edge re = g.getEdge(ol0, ol1);
        double d = re.getWeight();
        g.removeEdge(re);
        g.addVertex(otus[k]);
        Edge e1 = g.addEdge(ol0, otus[k]);
        e1.setWeight(dl0 < 0 ? d/2: dl0);
        Edge e2 = g.addEdge(ol1, otus[k]);
        e2.setWeight(dl1 < 0 ? d/2: dl1);
        
        /// now build the resulting tree;        
        Tree t = new Tree();
        TreeNode r = new TreeNode();
        t.addVertex(r);
        hangInG(t, r, root, null);
        return t;
            
    }
    
        
    /**
     * Recomputes the r list.
     * <pre>
     * r[i] = sum / (N-2)
     * 
     * where N is the number of leaves in the current matrix and sum is :
     * 
     * sum := SUM(d(i,j)) for all i != j and i and j are not joined already
     * </pre> 
     */
    public void computeR(){         
        int L = 2 * n - k;          // the number of leaves in the current matrix
                                    // not the number of initial taxas ???        
        for (int i = 0; i < k; i++) {
            if(otus[i].joined) continue; /// if node is allready joined, continue with next            
            double sum = 0;           
            for (int j = 0; j < k; j++) {
                if(i == j) continue; // just for completenes, d(i,i) = 0 ! or at least should be !
                if(otus[j].joined) continue; // ignore already joined nodes
                sum += d(i, j);
            }
            r[i] = sum / (L-2);          
        }
    }
    
    /**
     * Computes the next neighbours, the next two nodes that will be joined.
     * The criterion is d(i, j) - (r[i] + r[j]), which is minimized.
     * 
     * @return int[] containing  i and j
     */
    public int[] getNextNeighbours(){
        int mini = 0, minj = 0;
        double mind = Double.POSITIVE_INFINITY;
        double mij = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < k; i++) {
            if(otus[i].joined) continue; // continue if i is already joined
            for (int j = 0; j < i; j++) {
                if(otus[j].joined) continue; // continue if j is already joined
                mij = d(i, j) - (r[i] + r[j]); 
                if(mij < mind){
                    mind = mij;
                    minj = j;
                    mini = i;
                }                    
            }
        }
        return new int[]{mini, minj};
    }
    
    /**
     * Joins nodes i and j to a new OTU nad removes them from the list of 
     * joinable nodes. 
     * 
     * @param i node
     * @param j node
     */
    protected void join(int i, int j){
        double dij = d(i,j);
        
        double dik = (dij + r[i] - r[j]) * 0.5;
        double djk = dij - dik;
        
        /// distance to other nodes
        double[] dmat = new double[k];
        for (int k = 0; k < dmat.length; k++) {
            if (!otus[k].joined && k != i && k != j)
                dmat[k] = (d(i, k) + d(j, k) - dij) * 0.5;
        }
        
        /* Adjustment remove negative branch lengths */
        if (dik < 0.0) {
          dik = 0.0;
          djk = dij;
          if (djk < 0.0)
            djk = 0.0;
        }
        else if (djk < 0.0) {
          djk = 0.0;
          dik = dij;
          if (dik < 0.0)
            dik = 0.0;
        }
                
        /// new joined OTU
        otus[k] = new OTU();
        otus[k].dmat = dmat;
                        
        /// set i and j joined
        otus[i].joined = true;
        otus[j].joined = true;
        
        g.addVertex(otus[k]);
        Edge e1 = g.addEdge(otus[k], otus[i]);
        e1.setWeight(dik);
        Edge e2 = g.addEdge(otus[k], otus[j]);
        e2.setWeight(djk);
        
        
        /// next k
        k++;                              
    }
    
    /**
     * Returns the distance from node i to node j.
     * 
     * @param i node
     * @param j node
     * @return distance
     */
    protected double d(int i, int j){        
        /// use max(i,j) to find the last added OTU. This ensures 
        /// that the dmat of the otu is correct because after joining nodes
        /// the dmat's of already existing nodes are NOT updated but the 
        /// newly created node knows the correct and updated distance
        return otus[Math.max(i,j)].dmat[Math.min(i,j)];
    }

    /**
     * returns a Counter that contains a list of edges representing the longest path in
     * the tree from some vertex to another.
     * As this method works on the unrooted tree, it takes two nodes and creates a dummy root
     * between them. This directs the graph and enables a faster computation of the longest path. 
     * 
     * @param node1 split point  
     * @param node2 split point 
     * @return Counter with the edgelist
     */
    protected Counter getLongestBranch(OTU node1, OTU node2) {        
        //// place a dummy root somewhere / optimal would be a balancing root                       
        OTU dummyRoot = new OTU();
        /// split edge between l0 and l1
        Edge re = g.getEdge(node1, node2);
        double d = re.getWeight();
        g.removeEdge(re);
        g.addVertex(dummyRoot);
        Edge e1 = g.addEdge(node1, dummyRoot);
        e1.setWeight(d/2);
        Edge e2 = g.addEdge(node2, dummyRoot);
        e2.setWeight(d/2);
        Counter c = new Counter();
        countPath(dummyRoot, null, c);
        
        ArrayList edgeList = new ArrayList();                       
        grepEdgeList(c.maxNode, c.maxNodeParent, edgeList);
        int i1 = edgeList.indexOf(e1);
        int i2 = edgeList.indexOf(e1);
        if(i1 > 0 && i2 > 0){
            edgeList.remove(e1);
            edgeList.remove(e2);
            edgeList.add(Math.min(i1, i2), re);
        }
        /// remove dummy root
        g.removeVertex(dummyRoot);
        g.addEdge(re);
        c.edgeList = edgeList;
        return c;
    }
    
    /**
     * @return Returns the outGroup.
     */
    public String getOutGroup() {
        return outGroup;
    }

    /**
     * Set an outgroup. Rooting method is set to outgroup.
     * 
     * @param outGroup The outGroup to set.
     */
    public void setOutGroup(String outGroup) {
        this.outGroup = outGroup;
        if(outGroup == null){
            outGroupOTU = null;
        }                    
        rootingMethod = ROOT_OUTGROUP;
    }

    /**
     * @return Returns the rootingMethod.
     */
    public int getRootingMethod() {
        return rootingMethod;
    }

    /**
     * @param rootingMethod The rootingMethod to set.
     */
    public void setRootingMethod(int rootingMethod) {
        this.rootingMethod = rootingMethod;
    }


    ////////////// helpers
    //
    //
    private void countPath(OTU node, OTU parent, Counter counter) {
//        List edges = node.edges();//g.edgesOf(node);
//        if(edges.size() == 1){
    	if(node.degree()==1){
            node.maxEdge = ((Edge)node.edges().iterator().next()).getWeight();//node.edge.getWeight();
            node.maxSubtreePath = node.maxEdge;            
        }else{
            double maxME = 0;
            double sub = 0;
            Edge maxEdge = null;
            double parentWeight = 0;
            for (Iterator it = node.edges().iterator(); it.hasNext();) {
                Edge<OTU> e = (Edge<OTU>) it.next();
                OTU c = (OTU) e.getOpposit(node);
                if(parent != null && parent == c){
                    parentWeight = e.getWeight();
                    continue;
                }
                countPath(c, node, counter);
                
                sub += c.maxEdge;
                if(maxME < c.maxEdge){
                    maxME =  c.maxEdge;
                    maxEdge = e;
                }
            }
            node.maxSubtreePath = sub;
            node.maxEdge = maxME + parentWeight;
            node.edge = maxEdge;
            
            if(counter.max < sub){
                counter.max = sub;
                counter.maxNode = node;
                counter.maxNodeParent = parent;
            }
        }
    }    
    private void grepEdgeList(OTU node, OTU parent, List edgeList) {
//        List edges = g.edgesOf(node);        
//        if(edges.size() == 1){
    	if(node.degree() == 1){
            throw new RuntimeException("Cant find longest path starting at a leave!");
        }else{
            boolean first = true;
            for (Iterator it = node.edges().iterator(); it.hasNext();) {
                Edge e = (Edge) it.next();
                OTU c = (OTU) e.getOpposit(node);
                if(parent != null && parent == c){
                    continue;
                }                
                if(!first)
                    edgeList.add(e);
                grepE(c, edgeList, first);
                if(first)
                    edgeList.add(e);

                first = !first;
            }
        }
    }
    private void grepE(OTU node, List edgeList, boolean depthFirst) {
        if(depthFirst){
            if(node.edge != null){
                grepE((OTU) node.edge.getOpposit(node), edgeList, depthFirst);
                edgeList.add(node.edge);
            }
        }else{
            if(node.edge != null){
                edgeList.add(node.edge);
                grepE((OTU) node.edge.getOpposit(node), edgeList, depthFirst);
            }
        }
    }
    private void hangInG(Tree t, TreeNode parent, OTU node, OTU parentNode) {
        //List edges = g.edgesOf(node);
        for (Iterator it = node.edges().iterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            OTU c = (OTU) e.getOpposit(node);
            if(parentNode != null && parentNode == c) continue;
            TreeNode n = new TreeNode();
            t.addVertex(n);
            if(c.name != null) n.setLabel(c.name);
            t.addEdge(parent, n).setWeight(e.getWeight());
            //parent.addChild(n, e.getWeight());
            hangInG(t, n, c, node);
        }
    }
    //
    //
    //////// end helpers

    class OTU extends DefaultVertex{
        public Edge edge;
        String name = null;
        boolean joined = false;
        double[] dmat;
        
        double maxSubtreePath;
        double maxEdge;
        
        
        public OTU(){super();}
        public OTU(String name){this();this.name = name;}
        public String toString(){
            if(name != null)
                return name;
            return "";
        }
       
    }
    
    class Counter{
        public ArrayList edgeList;
        public OTU maxNodeParent;
        OTU maxNode = null;
        double max = 0;
    }
    
}
