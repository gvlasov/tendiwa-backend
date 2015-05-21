package org.tendiwa.graphs.graphs2d;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Collection;
import java.util.Set;

public interface MutableGraph2D extends SplittableGraph2D {
	default void addSegmentAsEdge(Segment2D segment) {
		boolean added = addEdge(segment.start(), segment.end(), segment);
		if (!added) {
			throw new IllegalArgumentException("Segment " + segment + " is already contained in this graph");
		}
	}

	default MutableGraph2D without(UndirectedGraph<Point2D, Segment2D> graph) {
		MutableGraph2D answer = new BasicMutableGraph2D();
		vertexSet().forEach(answer::addVertex);
		edgeSet().forEach(answer::addSegmentAsEdge);
		graph.edgeSet().forEach(answer::removeEdge);
		vertexSet().stream()
			.filter(v -> degreeOf(v) != 0)
			.filter(v -> answer.degreeOf(v) == 0)
			.forEach(answer::removeVertex);
		return answer;
	}

	@Override
	default void integrateCutSegment(CutSegment2D cutSegment) {
		Segment2D originalSegment = cutSegment.originalSegment();
		boolean removed = removeEdge(
			cutSegment.originalSegment()
		);
		if (!removed) {
			throw new IllegalArgumentException(
				"Can't integrate shredded segment: there wasn't any original segment " +
					originalSegment + " in the graph"
			);
		}
		cutSegment.segmentStream()
			.map(Segment2D::end)
			.forEach(this::addVertex);
		cutSegment.forEach(this::addSegmentAsEdge);
		assert !cutSegment.hasBeenCut() || !containsEdge(originalSegment);
	}

	default void removeEdgeAndOrphanedVertices(Segment2D edge) {
		assert containsEdge(edge);
		removeEdge(edge);
		if (degreeOf(edge.start()) == 0) {
			removeVertex(edge.start());
		}
		if (degreeOf(edge.end()) == 0) {
			removeVertex(edge.end());
		}
	}

	@Override
	int degreeOf(Point2D vertex);

	@Override
	Set<Segment2D> getAllEdges(Point2D sourceVertex, Point2D targetVertex);

	@Override
	Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex);

	@Override
	EdgeFactory<Point2D, Segment2D> getEdgeFactory();

	@Override
	@Deprecated
	Segment2D addEdge(Point2D sourceVertex, Point2D targetVertex);

	@Override
	@Deprecated
	boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Segment2D segment2D);

	@Override
	boolean addVertex(Point2D point2D);

	@Override
	boolean containsEdge(Point2D sourceVertex, Point2D targetVertex);

	@Override
	boolean containsEdge(Segment2D segment2D);

	@Override
	boolean containsVertex(Point2D point2D);

	@Override
	Set<Segment2D> edgeSet();

	@Override
	Set<Segment2D> edgesOf(Point2D vertex);

	@Override
	boolean removeAllEdges(Collection<? extends Segment2D> edges);

	@Override
	Set<Segment2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex);

	@Override
	boolean removeAllVertices(Collection<? extends Point2D> vertices);

	@Override
	Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex);

	@Override
	boolean removeEdge(Segment2D segment2D);

	@Override
	boolean removeVertex(Point2D point2D);

	@Override
	Set<Point2D> vertexSet();

	@Override
	Point2D getEdgeSource(Segment2D segment2D);

	@Override
	Point2D getEdgeTarget(Segment2D segment2D);

	@Override
	double getEdgeWeight(Segment2D segment2D);
}
