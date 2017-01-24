package epos.model.tree;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/*
 * Created on May 8, 2003
 */
 
/**
 * The partition of a Node. The Partition is a set of Leaves. All Leaves in the Set lie under
 * a unique Node in a Tree, but the Partition has no own Node because it is possible 
 * that there are several Nodes in different Trees, and they all have the same Partition.
 * 
 * There is a HashMap in which all leaves of the Partition are contained. The internal
 * Nodes are not saved in there, only the leaves containd in the Partition. 
 *
 * @author Thasso
 * @see ptree.tree.TreeNode
 */

public class Partition implements Comparable{
	//stupid svn 

	/**
	 * The HashMap that contains the Elements of the Partition.
	 */
	protected HashMap leavesMap = new HashMap();
	/**
	 * This TreeSet is used to get a Sorted Set of the Partition.
	 */
	//protected TreeSet leavesSet = null;
	
	
	
	/**
	 * The Minimal Key in The Partition.
	 */
	protected Comparable minKey = null;
	/**
	 * The Maximal Key in The Partition.
	 */
	protected Comparable maxKey = null;
	
	
	/**
	 * This Constructor creates a new Partition Object.
	 *
	 */
	public Partition() {
	}

		
	/**
	 * Adds a TreeNode to the Partiton.
	 * 
	 * @param node
	 */
	public void add(TreeNode node){
	    if(!node.isLeaf())
	        return; // TODO: Throw some exception here
	    
		String key = node.getLabel();//  getUserObject()).getID();
		if(key == null){
			//throw new NullPointerException();
			key="";
		}
		
		this.leavesMap.put(key, node);
		if (minKey == null) this.minKey = ((Comparable)key);
		else if(minKey.compareTo(key)> 0){
			minKey = (Comparable) key;
		}
		if (maxKey == null) this.maxKey = ((Comparable)key);
		else if(maxKey.compareTo(key) < 0){
			maxKey = (Comparable) key;
		}
	}
	
	/**
	 * Add a partition onto this one.
	 * 
	 * @param p Partition
	 */
	public void add(Partition p){
		Iterator i = p.getLeavesIterator();
		while(i.hasNext()){
		    TreeNode n = (TreeNode)i.next();
		    //System.out.println("Try to add node from partition " + n);
			this.add(p.getNode(n));
			//System.out.println(" done");
		}
	}
	/**
	 *  Same as getNode
	 * @param key The Key Object
	 * @return TreeNode
	 */
	public TreeNode get(Object key){	     
		return (TreeNode) this.leavesMap.get(key);
	}
	
	/**
	 * Returns true if this Partition contains the Key.
	 * 
	 * @param key The Key
	 * @return true if the Key is in the Partition
	 */
	public boolean contains(Object key){
		return this.leavesMap.containsKey(key);
	}

	/**
	 * Returns true if this Partition contains the TreeNode.
	 * 
	 * @param key The Key
	 * @return true if the Key is in the Partition
	 */
	public boolean contains(TreeNode node){
		return this.leavesMap.containsKey(node.getLabel());
	}
	
	/**
	 * Return True if the partition is Empty.
	 * 
	 * @return true if the Partition is Empty
	 */
	public boolean isEmpty(){
		return this.leavesMap.isEmpty();
	}
	
	/*
	 * Wenn noetig hier noch ne remove(TreeNode) ne remove(TreeNode[])
	 * remove(key) vieleicht auch remove(Partition)
	 */
	 
	/*ueberprueft, ob eine Partition Teilmenge einer anderen ist
	 *gibt 0 zur�ck, wenn die Partition keine Teilmenge ist, ansonsten 1. 
	 */
	/**
	 * Checks if this partition is subset of another partition.
	 * if the other partition contains all leaves of this parititon
	 * 1 will be returned otherwise 0.
	 * 
	 */
	public int isSubset(Partition otherPartition) {
		if (otherPartition == null)
			return 0;
		
		Iterator myLeavesIterator = getLeavesIterator();
		
		while (myLeavesIterator.hasNext()) {
			Object key = myLeavesIterator.next();
			Object value = otherPartition.getNode(key);

			if (value == null) {//alle Nodes der Partition muessen in der anderen vorkommen
				return 0;
			}
		}
		return 1;
	}
	
	/**
	 * This Method intersects this Partition with another
	 * Partition and returns the intersected Partition.
	 * Returns null if the intersected Partition is Empty.
	 * 
	 * I improved the intersection a little bit. First there is a check
	 * about the max ad min Key in the Partition. If and only if
	 * one Partition, or parts of it, could be in the other Partition,
	 * we do the intersection. 
	 * Before we starte we choose the smaller PArtition to be our comparator.
	 * 
	 * @param otherPartition Partition to intersect with 
	 * @return Partition Intersected Partition
	*/
	public Partition intersect(Partition otherPartition) {

		if (otherPartition == null)
			return null;
		
		//???
		try{
		if (this.minKey.compareTo(otherPartition.getMaxKey()) > 0)
			return null;
		if (otherPartition.getMinKey().compareTo(this.getMaxKey()) > 0)
			return null;
		}catch (ClassCastException e){
			System.out.println("Class Cast Exception" + e.getMessage() + " " + e);
		}
		Partition schnitt = new Partition();
		Iterator myLeavesIterator = null;
		
		//Choose smaller partition
		//if(this.getSize() <= otherPartition.getSize()){		
			myLeavesIterator = getLeavesIterator();
		//}else{
		//	myLeavesIterator = otherPartition.getLeavesIterator();
		//}
		while (myLeavesIterator.hasNext()) {
			Object key = myLeavesIterator.next(); // ist Mein Key
			Object value = otherPartition.get(key);
			// in der otherPartition ?
			if (value != null) {
				// mein Node Object in die r?ckgabepartition
				// sonst kommen immer die gleichen Partitionen zurueck
			    TreeNode node = this.get(key);
				schnitt.add(node);
			}
		}				
	
		if (schnitt.isEmpty()) {
			return null;
		} else {
			return schnitt;
		}
	}

	// sorts the Iterator
//	private TreeSet getSortedSet() {
//		if (leavesSet == null) {
//			Iterator i = leavesMap.keySet().iterator();
//			leavesSet = new TreeSet();
//			while (i.hasNext()) {
//				leavesSet.add(i.next());
//			}
//		}
//		return this.leavesSet;
//	}

	/**
	 * Returns the size of the Partition.
	 * 
	 * @return int Size of the Partition
	 */
	public int getSize() {
		return this.leavesMap.size();
	}

	/**
	 * Returns the TreeNode of the Key if it is in this Partition.
	 * If not it returns null.
	 * 
	 * @param key The Key of the TreeNode
	 * @return The TreeNode
	 */
	public TreeNode getNode(Object key) {
		return (TreeNode) leavesMap.get(((TreeNode)key).getLabel());
	}
	
	/**
	 * Returns a sorted Iterator over all Leaves in this Partition.
	 * This Iterator contains the UserObject ID`s that represent the
	 * Leaves, NOT the TreeNode Objects themselves. To get the TreeNode
	 * you have to use getNode(key) with a Key from the Iterator.
	 * 
	 * @return Iterator The Iterator over all Leaves in the Partition
	 * @see ptree.tree.Partition#getNode()
	 */
	public Iterator getLeavesIterator() {
		//return this.getSortedSet().iterator();
		return this.leavesMap.keySet().iterator();
	}

	/**
	 * Returns a String representation of the Partition.
	 * Better use the toString Method.
	 * 
	 * @return String The String representation.
	 * @deprecated Use the standart toString() Method!
	 * 
	 */
	
	public String toHashString(){
		Iterator i = this.getLeavesIterator();
		Vector leaves = new Vector();
		int n = 0;
		while (i.hasNext()) {
			int j = 0;
			Object next = i.next();
			while (j < leaves.size()){
				
				if(leaves.elementAt(j).toString().compareTo(next.toString()) > 0){		
			     break;
				}
				j++;
			}
			leaves.insertElementAt(next,j);
			
		}
		String orderedHash = new String();
		for(int j = 0; j < leaves.size();j++){
			orderedHash = orderedHash + leaves.elementAt(j).toString() + ",";			
		}
				
		orderedHash = orderedHash.substring(0,orderedHash.length()-1);
				
		return orderedHash;
	}
	
	
	/** 
	 * Returns the String representation of the Partition.
	 * 
	 * @see java.lang.Object#toString()
	 */
	
	public String toString() {
		Iterator i = this.getLeavesIterator();
		String s = "[";
		while (i.hasNext()) {
			s = s + (i.next() + ",");
		}
		s = s.substring(0, s.length() - 1) + "]";
		return s;
	}
	
	/**
	 * Returns the Maximal Key Value in the Partition.
	 * 
	 * @return Integer Maximal Key
	 */
	public Comparable getMaxKey() {
		return maxKey;
	}

	/**
	 * Returns the Minimal Key Value in the Partition
	 *
	 * @return Integer Minimal Key
	 */
	public Comparable getMinKey() {
		return minKey;
	}
	
	/**
	 * A Partition is equal to an Object, if the Object
	 * is a Partition and contains the same Elements.	 
	 * 
	 * @param Object The Object 
	 * @return boolean Returns true if both Objects are equal.
	 *
	 */
	public boolean equals(Object o){
		try {
			Partition p = (Partition) o;
			//return this.getLeavesSet().equals(p.getLeavesSet());	
			return leavesMap.keySet().equals(p.leavesMap.keySet());
		} catch (Exception e) {
			return false;
		}	
			
	}

	/**
	 * Compares this Partition to another Partition.
	 * 
	 * Returns 0 if both are equal, this means they have the same size and
	 * contain the same Elements. A negative Value is returned if the size of
	 * this Partition is smaller than the size of the other Partition. A positive
	 * Value is returned if the size of this Patition is larger than the size of the
	 * other one. If both Partitons have the same size but do not contain the same Elements,
	 * an Exception is thrown.
	 * 
	 * @param otherPartition
	 * @return int 
	 */
	public int compareTo(Partition otherPartition) throws Exception{
		if (this.getSize() < otherPartition.getSize())
			return -1;
		if(this.getSize() > otherPartition.getSize())
			return 1;
		if (this.equals(otherPartition))
			return 0;
		throw new Exception();
	}

//	/**
//	 * @return
//	 */
//	public TreeSet getLeavesSet() {
//		return this.getSortedSet();
//	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {		
		try {
			return compareTo((Partition) o);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return 0;
	}
	
	public int hashCode(){		
//		if(leavesSet == null)
//			getSortedSet();
//		return leavesSet.hashCode();
		return leavesMap.keySet().hashCode();

	}
	
	/**
	 * Returns an Array of all Leaves in this Partition
	 * @return Array of the Leaves in this Partition
	 */
	public TreeNode[] getLeavesArray(){
		TreeNode[] ar = new TreeNode[leavesMap.size()];
		Iterator e = leavesMap.values().iterator();
		int index = 0;
		while (e.hasNext()) {
			TreeNode element = (TreeNode) e.next();
			ar[index++] = element;
		}
		return ar;
	}
}
