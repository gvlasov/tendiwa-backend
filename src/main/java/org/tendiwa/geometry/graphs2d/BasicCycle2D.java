package org.tendiwa.geometry.graphs2d;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.Graph2D_Wr;

import java.util.Iterator;
import java.util.List;

public final class BasicCycle2D extends Graph2D_Wr implements Cycle2D {
	private final Graph2D graph;

	public BasicCycle2D(Graph2D graph) {
		super(graph);
		makeSureItIsCycle(graph);
		this.graph = graph;
	}

	private void makeSureItIsCycle(UndirectedGraph<Point2D, Segment2D> graph) {
		// TODO: Implement checking for cycle
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isClockwise() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Segment2D> toSegments() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return edgeSet().size();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return vertexSet().contains(o);
	}

	@Override
	public Iterator<Point2D> iterator() {
		return vertexSet().iterator();
	}


	@Override
	public Point2D get(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException();
	}
}
