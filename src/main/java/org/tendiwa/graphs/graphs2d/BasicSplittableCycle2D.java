package org.tendiwa.graphs.graphs2d;

import org.jgrapht.EdgeFactory;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.graphs2d.Cycle2D;

import java.util.*;

public class BasicSplittableCycle2D extends ArrayList<Point2D> implements Cycle2D, SplittableGraph2D {
	private final MutableGraph2D graph;
	private final boolean isCycleClockwise;
	private final Set<Segment2D> reverseEdges = new HashSet<>();

	public BasicSplittableCycle2D(Polygon polygon) {
		this.graph = new BasicMutableGraph2D();
		this.isCycleClockwise = JTSUtils.isYDownCCW(polygon);
		addVerticesAndEdges(polygon);
	}

	@Override
	public boolean isClockwise() {
		return isCycleClockwise;
	}

	public boolean isClockwise(Segment2D edge) {
		return isCycleClockwise ^ isAgainstCycleDirection(edge);
	}

	private boolean isAgainstCycleDirection(Segment2D edge) {
		return reverseEdges.contains(edge);
	}

	private void addVerticesAndEdges(Polygon polygon) {
		List<Segment2D> segments = polygon.toSegments();
		addVertex(polygon.get(0));
		for (int i = 0; i < polygon.size(); i++) {
			Point2D point = polygon.get(i);
			Segment2D segment = segments.get(i);
			if (point == segment.start()) {
				addVertex(segment.end());
			} else {
				assert point == segment.end();
				setReverse(segment);
				addVertex(segment.start());
			}
			addEdge(segment.start(), segment.end(), segment);
		}
	}

	private void setReverse(Segment2D edge) {
		assert !reverseEdges.contains(edge);
		reverseEdges.add(edge);
	}

	@Override
	public void integrateCutSegment(CutSegment2D cutSegment) {
		graph.integrateCutSegment(cutSegment);
		Segment2D originalSegment = cutSegment.originalSegment();
		Vector2D originalVector = originalSegment.asVector();
		boolean isSplitEdgeAgainst = isAgainstCycleDirection(originalSegment);
		cutSegment.segmentStream()
			.filter(segment -> isSplitEdgeAgainst ^ originalVector.dotProduct(segment.asVector()) < 0)
			.forEach(this::setReverse);
		reverseEdges.remove(originalSegment);
	}


	@Override
	public Set<Segment2D> getAllEdges(
		Point2D sourceVertex, Point2D targetVertex
	) {
		return graph.getAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return graph.getEdgeFactory();
	}

	@Override
	public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Segment2D segment2D) {
		return graph.containsEdge(segment2D);
	}

	@Override
	public boolean containsVertex(Point2D point2D) {
		return graph.containsVertex(point2D);
	}

	@Override
	public Set<Segment2D> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public Set<Segment2D> edgesOf(Point2D vertex) {
		return graph.edgesOf(vertex);
	}

	@Override
	public Set<Point2D> vertexSet() {
		return graph.vertexSet();
	}

	@Override
	public Point2D getEdgeSource(Segment2D segment2D) {
		return graph.getEdgeSource(segment2D);
	}

	@Override
	public Point2D getEdgeTarget(Segment2D segment2D) {
		return graph.getEdgeTarget(segment2D);
	}

	@Override
	public double getEdgeWeight(Segment2D segment2D) {
		return graph.getEdgeWeight(segment2D);
	}

	@Override
	public boolean contains(Object o) {
		return graph.vertexSet().contains(o);
	}
}
