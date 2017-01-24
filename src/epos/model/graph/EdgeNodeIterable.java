package epos.model.graph;

import java.util.Iterator;
import java.util.List;

public class EdgeNodeIterable<N extends Vertex, E extends Edge<N>> implements Iterable<N>, Iterator<N> {

    Iterator<E> e;

    N n;

    public EdgeNodeIterable(List<E> edges, N node) {
        e = edges.iterator();
        n = node;
    }

    public Iterator<N> iterator() {
        return this;
    }

    public boolean hasNext() {
        return e.hasNext();
    }

    public N next() {
        return e.next().getOpposit(n);
    }

    public void remove() {
    }
}
