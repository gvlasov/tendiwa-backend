package org.tendiwa.geometry.graphs2d;

import org.tendiwa.geometry.BasicPolygon;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphChainTraversal;
import org.tendiwa.graphs.graphs2d.Graph2D_Wr;

import java.util.Iterator;
import java.util.List;

import static org.tendiwa.collections.Collectors.toImmutableList;

public class BasicCycle2D extends Graph2D_Wr implements Cycle2D {

	private final Polygon polygon;

	public BasicCycle2D(Graph2D graph) {
		super(graph);
		if (!isCycle(graph)) {
			throw new IllegalArgumentException(
				"Graph is not a cycle because it has vertices with degree != 2"
			);
		}
		this.polygon = createPolygon(graph);
	}

	private Polygon createPolygon(Graph2D graph) {
		return new BasicPolygon(
			GraphChainTraversal.traverse(graph)
				.startingWith(
					graph.vertexSet().stream().findFirst().get()
				)
				.stream()
				.map(GraphChainTraversal.NeighborsTriplet::current)
				.collect(toImmutableList())
		);
	}

	private boolean isCycle(Graph2D graph) {
		return graph.vertexSet()
			.stream()
			.allMatch(v -> graph.degreeOf(v) == 2);
	}

	@Override
	public boolean isClockwise() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Segment2D> toSegments() {
		return polygon.toSegments();
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
		return polygon.contains(o);
	}

	@Override
	public Iterator<Point2D> iterator() {
		return polygon.iterator();
	}


	@Override
	public Point2D get(int index) {
		return polygon.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return polygon.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return polygon.lastIndexOf(o);
	}
}
