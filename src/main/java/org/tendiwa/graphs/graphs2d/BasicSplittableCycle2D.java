package org.tendiwa.graphs.graphs2d;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.graphs2d.Cycle2D;

import java.util.LinkedList;
import java.util.Set;

public class BasicSplittableCycle2D extends LinkedList<Point2D> implements Cycle2D, SplittableGraph2D {
	private final LinkedGraph2D linkedGraph;
	private final boolean isCycleClockwise;
	private final ReverseEdges reverseEdges;

	public BasicSplittableCycle2D(Polygon polygon) {
		this.isCycleClockwise = JTSUtils.isYDownCCW(polygon);
		this.linkedGraph = new LinkedGraph2D(polygon);
		this.reverseEdges = new ReverseEdges(polygon);
	}

	@Override
	public void integrateCutSegment(CutSegment2D cutSegment) {
		linkedGraph.splitEdge(cutSegment);
		reverseEdges.replaceReverseEdge(cutSegment);
	}


	@Override
	public boolean isClockwise() {
		return isCycleClockwise;
	}

	@Override
	public ImmutableList<Point2D> toImmutableList() {
		return ImmutableList.copyOf(this);
	}

	public boolean isClockwise(Segment2D edge) {
		return isCycleClockwise ^ reverseEdges.isAgainstCycleDirection(edge);
	}

	@Override
	public Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return linkedGraph.getEdgeBetweenPoints(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return linkedGraph.containsEdgeBetweenPoints(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Segment2D segment2D) {
		return linkedGraph.containsEdge(segment2D);
	}

	@Override
	public boolean containsVertex(Point2D point2D) {
		return linkedGraph.containsVertexAtPoint(point2D);
	}

	@Override
	public Set<Segment2D> edgeSet() {
		return linkedGraph.edgeSet();
	}

	@Override
	public Set<Segment2D> edgesOf(Point2D vertex) {
		return linkedGraph.edgesOfPoint(vertex);
	}

	@Override
	public Set<Point2D> vertexSet() {
		return linkedGraph.pointSet();
	}

	@Override
	public Point2D getEdgeSource(Segment2D segment) {
		return linkedGraph.getEdgeSource(segment).getPayload();
	}

	@Override
	public Point2D getEdgeTarget(Segment2D segment) {
		return linkedGraph.getEdgeTarget(segment).getPayload();
	}

	@Override
	public boolean contains(Object o) {
		return vertexSet().contains(o);
	}
}
