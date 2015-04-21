package org.tendiwa.geometry.graphs2d;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

public final class BasicCycle2D implements Cycle2D {
	private final UndirectedGraph<Point2D, Segment2D> graph;

	public BasicCycle2D(UndirectedGraph<Point2D, Segment2D> graph) {
		makeSureItIsCycle(graph);
		this.graph = graph;
	}

	private void makeSureItIsCycle(UndirectedGraph<Point2D, Segment2D> graph) {
		// TODO: Implement checking for cycle
		assert Boolean.TRUE;
	}

	@Override
	public UndirectedGraph<Point2D, Segment2D> graph() {
		return graph;
	}
}
