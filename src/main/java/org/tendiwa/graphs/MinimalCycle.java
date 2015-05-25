package org.tendiwa.graphs;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.BasicPolygon;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;

import java.util.ArrayList;
import java.util.List;

/**
 * A cycle of vertices, ordered clockwise.
 */
public final class MinimalCycle extends BasicPolygon {
	private final List<Point2D> cycle = new ArrayList<>();
	private Graph2D graph;

	@Override
	public Segment2D edge(Point2D a, Point2D b) {
		return graph.getEdge(a, b);
	}

	/**
	 * @param graph
	 * 	A larger graph in which this minimal cycle exists.
	 * @param cycle
	 * 	A list of cells of this minimal cycle.
	 */
	MinimalCycle(Graph2D graph, List<Point2D> cycle) {
		super(cycle);
		assert cycle.size() > 2;
		this.graph = graph;
		this.cycle.addAll(cycle);
	}

	@Override
	public ImmutableList<Point2D> toImmutableList() {
		return ImmutableList.copyOf(cycle);
	}
}
