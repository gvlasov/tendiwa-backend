package org.tendiwa.graphs;

public class IsolatedVertex<V> implements Primitive<V> {
	private V vertex;

	public IsolatedVertex(V vertex) {
		super();

		this.vertex = vertex;
	}

	@Override
	public void insert(V vertex) {
		throw new UnsupportedOperationException();
	}
}
