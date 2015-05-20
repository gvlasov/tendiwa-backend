package org.tendiwa.geometry.smartMesh;

import lombok.Lazy;
import org.jgrapht.EdgeFactory;
import org.tendiwa.collections.SuccessiveTuples;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.graphs2d.Cycle2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.GraphChainTraversal;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.util.*;

/**
 * Holds a graph of a cycle within which {@link FloodPart} is constructed,
 * and for each edge remembers whether that edge goes clockwise or counter-clockwise. That effectively means that
 * OrientedCycle can tell if its innards are to the right or to the left from its certain edge.
 */
final class OrientedCycle implements MutableGraph2D, Cycle2D {
	private final MinimalCycle<Point2D, Segment2D> originalMinimalCycle;
	private final Graph2D splitOriginalGraph;
	private final Set<Segment2D> reverseEdges = new HashSet<>();

	OrientedCycle(
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		Graph2D splitOriginalGraph
	) {
		this.originalMinimalCycle = originalMinimalCycle;
		this.splitOriginalGraph = splitOriginalGraph;
	}

	// TODO: primitive support for @Lazy annotation
	@Lazy
	private Boolean isCycleClockwise() {
		return JTSUtils.isYDownCCW(originalMinimalCycle.vertexList());
	}

	@Lazy
	private MutableGraph2D cycleGraph() {
		MutableGraph2D cycleGraph = new BasicMutableGraph2D();
		SuccessiveTuples.forEachLooped(
			originalMinimalCycle.asVertices(),
			(previous, current, next) -> {
				if (splitOriginalGraph.containsEdge(current, next)) {
					addAutoDirectedEdge(cycleGraph, current, next);
				} else {
					GraphChainTraversal
						.traverse(splitOriginalGraph)
						.startingWith(current)
						.awayFrom(splitOriginalGraph.findNeighborOnSegment(current, current.segmentTo(previous)))
						.past(splitOriginalGraph.findNeighborOnSegment(current, current.segmentTo(next)))
						.until(triplet -> triplet.next() == next)
						.stream()
						.filter(triplet -> triplet.next() != null)
						.forEach(
							triplet -> addAutoDirectedEdge(
								cycleGraph,
								triplet.current(),
								triplet.next()
							)
						);
				}
			}
		);
		return cycleGraph;
	}

	private void addAutoDirectedEdge(
		MutableGraph2D cycleGraph,
		Point2D current,
		Point2D next
	) {
		assert splitOriginalGraph.containsEdge(current, next);
		Segment2D edge = splitOriginalGraph.getEdge(current, next);
		cycleGraph.addVertex(current);
		cycleGraph.addVertex(next);
		cycleGraph.addSegmentAsEdge(edge);
		if (isEdgeReverse(edge)) {
			setReverse(edge);
		}
	}

	private boolean isEdgeReverse(Segment2D edge) {
		if (splitOriginalGraph.getEdgeSource(edge) != edge.start()) {
			assert splitOriginalGraph.getEdgeSource(edge) == edge.end()
				&& splitOriginalGraph.getEdgeTarget(edge) == edge.start();
			return true;
		} else {
			assert splitOriginalGraph.getEdgeSource(edge) == edge.start()
				&& splitOriginalGraph.getEdgeTarget(edge) == edge.end();
			return false;
		}

	}

	boolean isAgainstCycleDirection(Segment2D edge) {
		return reverseEdges.contains(edge);
	}

	@Override
	public void integrateCutSegment(CutSegment2D cutSegment) {
		MutableGraph2D.super.integrateCutSegment(cutSegment);
		Segment2D originalSegment = cutSegment.originalSegment();
		Vector2D originalVector = originalSegment.asVector();
		boolean isSplitEdgeAgainst = isAgainstCycleDirection(originalSegment);
		cutSegment.segmentStream()
			.filter(segment -> isSplitEdgeAgainst ^ originalVector.dotProduct(segment.asVector()) < 0)
			.forEach(this::setReverse);
		reverseEdges.remove(originalSegment);
	}

	/**
	 * For each of two new parts of a spilt edge, calculates if that part goes clockwise or counter-clockwise and
	 * remembers that information.
	 * <p>
	 * This method should be called each time an edge of this OrientedCycle is split with
	 */
	private void setReverse(Segment2D edge) {
		assert !reverseEdges.contains(edge);
		reverseEdges.add(edge);
	}

	// TODO: bisector is not deviated
	public Ray deviatedAngleBisector(Point2D bisectorStart, boolean inward) {
		Set<Segment2D> adjacentEdges = cycleGraph().edgesOf(bisectorStart);
		assert adjacentEdges.size() == 2;
		Iterator<Segment2D> iterator = adjacentEdges.iterator();

		Segment2D previous = iterator.next();
		if (!isClockwise(previous)) {
			previous = previous.reverse();
		}
		Segment2D next = iterator.next();
		if (!isClockwise(next)) {
			next = next.reverse();
		}

		if (next.end().equals(previous.start())) {
			Segment2D buf = previous;
			previous = next;
			next = buf;
		}
		Segment2D bisectorSegment =
			new BasicSegment2D(
				bisectorStart,
				bisectorStart.add(
					new BasicBisector(
						next.asVector(),
						previous.asVector().reverse()
					).asInbetweenVector()
						.multiply(inward ? 1 : -1)
				)
			);
		return new Ray(
			bisectorStart,
			bisectorSegment.start().angleTo(bisectorSegment.end())
		);
	}

	public boolean isClockwise(Segment2D edge) {
		return isCycleClockwise() ^ isAgainstCycleDirection(edge);
	}

	Ray normal(SplitSegment2D segmentWithPoint, boolean inward) {
		Point2D cwPoint, ccwPoint;
		if (isClockwise(segmentWithPoint.originalSegment()) ^ inward) {
			cwPoint = segmentWithPoint.originalStart();
			ccwPoint = segmentWithPoint.originalEnd();
		} else {
			cwPoint = segmentWithPoint.originalEnd();
			ccwPoint = segmentWithPoint.originalStart();
		}
		Point2D rayStart = segmentWithPoint.middlePoint();
		Point2D pointOnRay = rayStart.add(
			new BasicBisector(
				cwPoint.subtract(rayStart),
				ccwPoint.subtract(rayStart)
			).asInbetweenVector()
		);
		return new Ray(
			rayStart,
			rayStart.angleTo(pointOnRay)
		);
	}

	@Override
	public Set<Segment2D> getAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return cycleGraph().getAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return cycleGraph().getEdge(sourceVertex, targetVertex);
	}

	@Override
	public EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return cycleGraph().getEdgeFactory();
	}

	@Deprecated
	@Override
	public Segment2D addEdge(Point2D sourceVertex, Point2D targetVertex) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Segment2D segment2D) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean addVertex(Point2D point2D) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return cycleGraph().containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Segment2D segment2D) {
		return cycleGraph().containsEdge(segment2D);
	}

	@Override
	public boolean containsVertex(Point2D point2D) {
		return cycleGraph().containsVertex(point2D);
	}

	@Override
	public Set<Segment2D> edgeSet() {
		return cycleGraph().edgeSet();
	}

	@Override
	public Set<Segment2D> edgesOf(Point2D vertex) {
		return cycleGraph().edgesOf(vertex);
	}

	@Deprecated
	@Override
	public boolean removeAllEdges(Collection<? extends Segment2D> edges) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Set<Segment2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean removeAllVertices(Collection<? extends Point2D> vertices) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean removeEdge(Segment2D segment2D) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean removeVertex(Point2D point2D) {
		throw new UnsupportedOperationException();
	}


	@Override
	public Set<Point2D> vertexSet() {
		return cycleGraph().vertexSet();
	}

	@Override
	public Point2D getEdgeSource(Segment2D segment2D) {
		return cycleGraph().getEdgeSource(segment2D);
	}

	@Override
	public Point2D getEdgeTarget(Segment2D segment2D) {
		return cycleGraph().getEdgeTarget(segment2D);
	}

	@Override
	public double getEdgeWeight(Segment2D segment2D) {
		return cycleGraph().getEdgeWeight(segment2D);
	}

	@Override
	public boolean isClockwise() {
		return isCycleClockwise();
	}

	@Override
	public int size() {
		return cycleGraph().vertexSet().size();
	}

	@Override
	public boolean contains(Object o) {
		return cycleGraph().vertexSet().contains(o);
	}

	@Override
	public Iterator<Point2D> iterator() {
		throw new UnsupportedOperationException();
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

	@Override
	public int degreeOf(Point2D vertex) {
		return cycleGraph().degreeOf(vertex);
	}
}