package epos.model.graph;

import java.util.Iterator;

public class EmptyIterable<T> implements Iterable<T>, Iterator<T> {

	public Iterator<T> iterator() {
		return this;
	}

	public boolean hasNext() {
		return false;
	}

	public T next() {
		return null;
	}

	public void remove() {
	}
}
