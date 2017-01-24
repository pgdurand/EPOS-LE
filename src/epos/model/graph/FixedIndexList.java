package epos.model.graph;

import java.util.Collection;
import java.util.Iterator;

/**
 * This is a dynamic but not shrinking list. Elements are vertices with a given index (or the index is automatically asigned).
 * this list extends itself, such that each element can be put into an array at its index position. Removing an
 * element does not resize the list, only the entry will be set to 0.
 *  
 * @author thasso
 *
 * @param <N>
 */
public class FixedIndexList<N extends IndexedElement> implements Iterable<N>{
	protected static final int DEFAULT_SIZE = 100;
	protected static final int DEFAULT_LOAD_FACTOR = 100;
	/**
	 * array that stores the vertices
	 */
	private N[] elements;
	/**
	 * next insert position
	 */
	private int position = 0;
	/**
	 * load factor
	 */
	private int loadFactor = DEFAULT_LOAD_FACTOR;
	/**
	 * number of vertices
	 */
	private int size = 0;
	
	private int initialSize = DEFAULT_SIZE;
	
	/**
	 * Constructs a new list with default size and load factor
	 *
	 */
	public FixedIndexList(){
		this(DEFAULT_SIZE);
	}
	/**
	 * Constructs a new list with default load factor and given size
	 * @param size
	 */
	public FixedIndexList(int size){		
		this(size, DEFAULT_LOAD_FACTOR);
	}
	
	/**
	 * Construcs a new list with given size and load factor
	 * 
	 * @param size
	 * @param loadFactor
	 */
	@SuppressWarnings("unchecked")
	public FixedIndexList(int size, int loadFactor){
		this.initialSize = size;
		this.loadFactor = loadFactor;
		this.elements = (N[]) new IndexedElement[initialSize];
		
	}
	/**
	 * Adds a vertex to the list. {@link Vertex.getIndex()} is used as the insertion index
	 * and the call is delegated to {@link #put(int, Vertex)}.
	 * 
	 * @param o the vertex
	 * @return true if vertex was successfully added
	 */
	public boolean put(N o) {
		return put(o.getIndex(), o);		
	}
	/**
	 * Adds the given vertex at the specified index. if index is less than 0, a new index is generated.
	 * Eventually this method resizes the storage array. also adding a vertex to an index that is blocked by another 
	 * vertex is not allowed and the insertin will fail.
	 * 
	 * @param index
	 * @param o
	 * @return
	 */
	protected boolean put(int index, N o) {
		/*
		 * create a new index
		 */
		if(index < 0){
			index = assignNextIndex();
		}
		/*
		 * extend the array, or check if the specified position
		 * is blocked by another vertex. if so, and the element is == the existing entry,
		 * return false, otherwise, the vertex is already inserted and we return true.
		 */
		if(index >= elements.length){
			extendArray(index-elements.length + loadFactor);			
		}else{
			// check for an existing object and eventually abort insertion
			if(elements[index] != null){
				if(elements[index] != o){
					return false;	
				}else{
					return true;
				}				
			}
		}
		/*
		 * set the vertex index and increase size.
		 */
		o.setIndex(index);
		size++;
		elements[index] = o;
		return true;
	}

	/**
	 * Compute the next insert position, this is the index of the vertex that has gets inserted and
	 * has no index.
	 * 
	 * @return
	 */
	protected int assignNextIndex() {
		/*
		 * if position is outside the array bound, its a valid insertionpoint (leads to array extension)
		 */
		if(position >= elements.length){
			return position;
		}
		/*
		 * if there is no element at the actual position, it is a valid insertion point
		 */
		if(elements[position] == null){
			position++;
			return position-1;
		}
		/*
		 * otherwise, there is a vertex at position, so we have to move forward until we
		 * find an empty spot in the array, or we reach the array bounds, which is also a valid position
		 */
		for (int i = position; i < elements.length; i++) {
			if(elements[i] == null){
				position = i + 1;
				return i; 
			}
		}
		/*
		 *if we reach this point, there was no free position available, set position to array length + 1
		 *to extend the array for the next inserted element 
		 */
		position = elements.length+1;
		return position-1;
	}
	
	/**
	 * Extends the array by load factor
	 * @param extendLength
	 */
	@SuppressWarnings("unchecked")
	protected void extendArray(int extendLength) {
		N[] ne = (N[]) new IndexedElement[elements.length + extendLength];
		System.arraycopy(elements, 0, ne, 0, elements.length);
		elements = ne;
	}
	/**
	 * Clear the list. This also set the index of each vertex back to -1.
	 *
	 */
	@SuppressWarnings("unchecked")
	public void clear() {
		for (N e : elements) {
			e.setIndex(-1);
		}
		position = 0;
		size = 0;
		elements = (N[]) new IndexedElement[loadFactor];
	}
	/**
	 * Returns true if the list contains the given node
	 * @param v
	 * @return
	 */
	public boolean contains(N v) {
		return v.getIndex() < elements.length && v.getIndex() >= 0 && elements[v.getIndex()] == v; 
	}
	/**
	 * Returns true if the list contains all nodes in the Collection
	 * @param c
	 * @return
	 */
	public boolean containsAll(Collection<N> c) {
		for (N object : c) {
			if(!contains(object)) return false;			                 
		}
		return true;
	}
	/**
	 * Returns the vertex at given index. This returns null if the index is out of bounds of the underlying array,
	 * or if the element at the specified position is null.
	 * 
	 * @param index
	 * @return
	 */
	public N get(int index) {
		if(index >=0  && index < elements.length){
			return elements[index];
		}
		return null;
	}
	/**
	 * 
	 * @return true if list is empty
	 */
	public boolean isEmpty() {
		return size <=0 ;
	}
	/**
	 * 
	 * Removes vertex from the list. This checks if the list really contains the
	 * given {@link IndexedElement}. If you are sure that the element is contained in
	 * the list, directly call {@link #remove(int)} using teh index of the element and
	 * avoid the contains check.
	 * 
	 * @param v {@link IndexedElement} to remove
	 * @return true if removal was successful
	 */
	public boolean remove(N v) {
		if(!contains(v)) return false;
		return remove(v.getIndex()) != null;
	}
	
	/**
	 * Removes and returns the element at the given position. 
	 * @param index
	 * @return
	 */
	public N remove(int index) {		
		if(index >= 0 && index < elements.length){			
			N removed = elements[index];
			if(removed != null){				
				size--;
				removed.setIndex(-1);
			}
			elements[index] = null;	
			if(index < position){
				position = index;
			}
			return removed;
		}
		return null;
	}
	/**
	 * Returns the size of the list
	 * @return
	 */
	public int size() {
		return size;
	}
	
	class FixedIterator implements Iterable<N>, Iterator<N>{
		private int iteratorIndex = -1;

		public Iterator<N> iterator() {
			return this;
		}

		public boolean hasNext() {
			if(iteratorIndex < 0) {
				// find initial index
				iteratorIndex = 0;
				for (; iteratorIndex < elements.length; iteratorIndex++) {					
					if(elements[iteratorIndex] != null){
						break;
					}
				}				
			}
			return iteratorIndex < elements.length;
		}

		public N next() {			
			N ret = elements[iteratorIndex];
			int j = iteratorIndex+1;
			if(j < elements.length){
				boolean stop = false;
				for (; j < elements.length; j++) {
					iteratorIndex = j;
					if(elements[iteratorIndex] != null){
						stop= true;
						break;
					}
				}
				// check for a non break
				if(!stop){
					iteratorIndex = j;
				}
			}else{
				iteratorIndex = j;
			//	System.out.println("Stop ?");
			}						
			return ret == null && hasNext() ? next() : ret;
		}

		public void remove() {
			elements[iteratorIndex] = null;
		}	
	}

	public Iterator<N> iterator() {
		return new FixedIterator().iterator();
	}
	public int getMaximalIndex() {
		return elements.length;
	}	
}