package org.tendiwa.geometry.smartMesh;

import org.jgrapht.Graph;
import org.tendiwa.collections.SuccessiveTuples;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Bisector;
import org.tendiwa.graphs.GraphChainTraversal;
import org.tendiwa.graphs.GraphChainTraversal.NeighborsTriplet;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Holds a graph of a cycle within which {@link FloodPart} is constructed,
 * and for each edge remembers whether that edge goes clockwise or counter-clockwise. That effectively means that
 * OrientedCycle can tell if its innards are to the right or to the left from its certain edge.
 */
final class OrientedCycle implements NetworkPart {
	private final boolean isCycleClockwise;
	private final MutableGraph2D splitOriginalGraph;
	private final MutableGraph2D cycleGraph;
	private final Set<Segment2D> reverseEdges = new HashSet<>();

	OrientedCycle(
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		MutableGraph2D splitOriginalGraph
	) {
		this.splitOriginalGraph = splitOriginalGraph;
		this.cycleGraph = createCycleGraph(originalMinimalCycle);
		this.isCycleClockwise = JTSUtils.isYDownCCW(originalMinimalCycle.vertexList());
	}

	@Override
	public MutableGraph2D graph() {
		return cycleGraph;
	}

	private MutableGraph2D createCycleGraph(
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle
	) {
		MutableGraph2D cycleGraph = new MutableGraph2D();
		SuccessiveTuples.forEachLooped(
			originalMinimalCycle.asVertices(),
			(previous, current, next) -> {
				if (splitOriginalGraph.containsEdge(current, next)) {
					addAutoDirectedEdge(cycleGraph, current, next);
				} else {
					GraphChainTraversal
						.traverse(splitOriginalGraph)
						.startingWith(current)
						.awayFrom(splitOriginalGraph.findNeighborOnSegment(current, new Segment2D(current, previous)))
						.past(splitOriginalGraph.findNeighborOnSegment(current, new Segment2D(current, next)))
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

	private void addAutoDirectedEdge(Graph<Point2D, Segment2D> cycleGraph, Point2D current, Point2D next) {
		assert splitOriginalGraph.containsEdge(current, next);
		cycleGraph.addVertex(current);
		cycleGraph.addVertex(next);
		Segment2D edge = splitOriginalGraph.getEdge(current, next);
		if (splitOriginalGraph.getEdgeSource(edge) != current) {
			assert splitOriginalGraph.getEdgeSource(edge) == next
				&& splitOriginalGraph.getEdgeTarget(edge) == current;
			reverseEdges.add(edge);
		} else {
			assert splitOriginalGraph.getEdgeSource(edge) == current
				&& splitOriginalGraph.getEdgeTarget(edge) == next;
		}
		cycleGraph.addEdge(current, next, edge);
	}

	boolean isAgainstCycleDirection(Segment2D edge) {
		return reverseEdges.contains(edge);
	}

	/**
	 * For each of two new parts of a spilt edge, calculates if that part goes clockwise or counter-clockwise and
	 * remembers that information.
	 * <p>
	 * This method should be called each time an edge of this OrientedCycle is split with
	 */
	@Override
	public void integrateSplitEdge(CutSegment2D cutSegment) {
		Segment2D originalSegment = cutSegment.originalSegment();
		Vector2D originalVector = originalSegment.asVector();
		boolean isSplitEdgeAgainst = isAgainstCycleDirection(originalSegment);
		cutSegment.segmentStream()
			.filter(segment -> isSplitEdgeAgainst ^ originalVector.dotProduct(segment.asVector()) < 0)
			.forEach(this::setReverse);
		reverseEdges.remove(originalSegment);
		NetworkPart.super.integrateSplitEdge(cutSegment);
	}

	private void setReverse(Segment2D edge) {
		assert !reverseEdges.contains(edge);
		reverseEdges.add(edge);
	}

	// TODO: bisector is not deviated
	public Ray deviatedAngleBisector(Point2D bisectorStart, boolean inward) {
		Set<Segment2D> adjacentEdges = cycleGraph.edgesOf(bisectorStart);
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

		if (next.end.equals(previous.start)) {
			Segment2D buf = previous;
			previous = next;
			next = buf;
		}
		Segment2D bisectorSegment =
			new Segment2D(
				bisectorStart,
				bisectorStart.add(
					new Bisector(
						next.asVector(),
						previous.asVector().reverse()
					).asInbetweenVector()
						.multiply(inward ? 1 : -1)
				)
			);
		return new Ray(
			bisectorStart,
			bisectorSegment.start.angleTo(bisectorSegment.end)
		);
	}

	boolean isClockwise(Segment2D edge) {
		return isCycleClockwise ^ isAgainstCycleDirection(edge);
	}

	List<Point2D> vertexList() {
		return GraphChainTraversal
			.traverse(graph())
			.startingWith(graph().vertexSet().stream().findFirst().get())
			.stream()
			.map(NeighborsTriplet::current)
			.collect(Collectors.toList());
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
			new Bisector(
				cwPoint.subtract(rayStart),
				ccwPoint.subtract(rayStart)
			).asInbetweenVector()
		);
		return new Ray(
			rayStart,
			rayStart.angleTo(pointOnRay)
		);
	}
}