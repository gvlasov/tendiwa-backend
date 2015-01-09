package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Recs2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.Point2DRowComparator;

import java.awt.Color;
import java.util.*;

/**
 * After the innards of an enclosing cycle were flood-filled, there may be left some secondary road network blocks
 * that are not properly divided into more blocks because the shape of the original network graph prevents flood
 * filling from accessing some of graph's parts. This class finds points from which you need to start new flood
 * fillings to properly divide the leftover blocks.
 */
public class UnfilledBlocksDetector {
	private final Map<Segment2D, List<Point2D>> pointsOnPolygonBorder;
	private final HolderOfSplitCycleEdges holderOfSplitCycleEdges;
	private final UndirectedGraph<Point2D, Segment2D> relevantNetwork;
	private final Set<Point2D> cycleNodes;
	private final Point2D startingPoint;
	private int numberOfChains;


	public UnfilledBlocksDetector(
		Map<Segment2D, List<Point2D>> pointsOnPolygonBorder,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges,
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		Set<Point2D> cycleNodes
	) {
		this.pointsOnPolygonBorder = pointsOnPolygonBorder;
		this.holderOfSplitCycleEdges = holderOfSplitCycleEdges;
		this.relevantNetwork = relevantNetwork;
		this.cycleNodes = cycleNodes;

		numberOfChains = countSecondaryRoadNetworkExits();
		Map<Segment2D, Collection<Point2D>> actualSegmentsToStartingPoints = findActualSegmentsForPoints();
		Collection<List<Segment2D>> chainsOf2DegreeVertices = findChainsOf2DegreeVertices();
		Collection<List<Point2D>> pointsOn2DegreeChains = groupPointsBy2DegreeChains(
			actualSegmentsToStartingPoints,
			chainsOf2DegreeVertices
		);
		startingPoint = findStartingPoint(pointsOn2DegreeChains);
	}

	private Point2D findStartingPoint(Collection<List<Point2D>> pointsOn2DegreeChains) {
		for (List<Point2D> points : pointsOn2DegreeChains) {
			if (points.size() > 3) {
				// Which point should we pick is arguable, but I can't figure out a better way to pick one.
				return points.get(points.size()/2);
			}
		}
		return null;
	}

	/**
	 * @return A point from which next flood fill by {@link org.tendiwa.settlements.networks.SecondaryRoadNetwork}
	 * should be started, or null if there is no such point (all blocks are small enough).
	 */
	Point2D getStartingPoint() {
		return startingPoint;
	}

	private Collection<List<Segment2D>> findChainsOf2DegreeVertices() {
		List<List<Segment2D>> answer = new ArrayList<>(numberOfChains);
		List<Segment2D> currentChain = createNewChain(numberOfChains);
		answer.add(currentChain);

		Point2D startVertex, currentVertex, nextVertex;
		startVertex = currentVertex = findVertexOfDegreeGt2();
		nextVertex = getInitialDirectionVertex(currentVertex);

		while (nextVertex != startVertex) {
			TestCanvas.canvas.draw(nextVertex, DrawingPoint2D.withColorAndSize(Color.yellow, 6));
			currentChain.add(relevantNetwork.getEdge(currentVertex, nextVertex));
			if (relevantNetwork.degreeOf(currentVertex) > 2) {
				currentChain = createNewChain(numberOfChains);
				answer.add(currentChain);
			}
			Point2D previousVertex = currentVertex;
			currentVertex = nextVertex;
			nextVertex = getNextVertex(previousVertex, currentVertex);
			assert currentVertex != nextVertex;
		}
		currentChain.add(relevantNetwork.getEdge(currentVertex, nextVertex));

		return answer;
	}

	private Point2D getNextVertex(Point2D previousVertex, Point2D currentVertex) {
		for (Segment2D edge : relevantNetwork.edgesOf(currentVertex)) {
			if (edge.start != previousVertex && cycleNodes.contains(edge.start)) {
				return edge.start;
			}
			if (edge.end != previousVertex && cycleNodes.contains(edge.end)) {
				return edge.end;
			}
		}
		throw new RuntimeException("Could not find next vertex");
	}

	private ArrayList<Segment2D> createNewChain(int numberOfChains) {
		return new ArrayList<>(cycleNodes.size() / numberOfChains);
	}

	/**
	 * Counts how many {@link #cycleNodes} have degree > 2, that is, how many of them are vertices of the secondary
	 * road network.
	 *
	 * @return
	 */
	private int countSecondaryRoadNetworkExits() {
		return (int) cycleNodes.stream()
			.filter(vertex -> relevantNetwork.degreeOf(vertex) > 2)
			.count();
	}

	/**
	 * Searches amount {@link #cycleNodes} for any vertex with degree > 2.
	 *
	 * @return A vertex of network's cycle.
	 */
	private Point2D findVertexOfDegreeGt2() {
		for (Point2D vertex : cycleNodes) {
			if (relevantNetwork.degreeOf(vertex) > 2) {
				return vertex;
			}
		}
		throw new RuntimeException("Could not find a vertex of degree > 2");
	}

	/**
	 * Deterministically finds a neighbor of {@code startVertex} in {@link #relevantNetwork} that is a vertex of
	 * network's cycle.
	 *
	 * @param startVertex
	 * 	A vertex on network's cycle.
	 * @return A neighbor vertex of {@code startVertex} that is on network's cycle too.
	 */
	private Point2D getInitialDirectionVertex(Point2D startVertex) {
		Set<Segment2D> neighborEdges = relevantNetwork.edgesOf(startVertex);
		List<Point2D> neighborVertices = new ArrayList<>(neighborEdges.size() + 1);
		neighborVertices.add(startVertex);
		neighborEdges.forEach(edge -> {
			neighborVertices.add(edge.start);
			neighborVertices.add(edge.end);
		});
		neighborVertices.sort(Point2DRowComparator.getInstance());
		for (Point2D vertex : neighborVertices) {
			if (cycleNodes.contains(vertex)) {
				return vertex;
			}
		}
		throw new RuntimeException("Could not find next vertex");
	}

	private Collection<List<Point2D>> groupPointsBy2DegreeChains(
		Map<Segment2D, Collection<Point2D>> actualSegmentsToStartingPoints,
		Collection<List<Segment2D>> chainsOf2DegreeVertices
	) {
		Collection<List<Point2D>> answer = new ArrayList<>(numberOfChains);
		for (List<Segment2D> chain : chainsOf2DegreeVertices) {
			List<Point2D> pointsOfChain = new ArrayList<>();
			answer.add(pointsOfChain);
			for (Segment2D segment : chain) {
				actualSegmentsToStartingPoints.get(segment).forEach(pointsOfChain::add);
			}
		}
		return answer;
	}

	/**
	 * Finds actual segments on which starting points reside.
	 *
	 * @return A map for starting points to actual segments of a network's cycle.
	 */
	private Map<Segment2D, Collection<Point2D>> findActualSegmentsForPoints() {
		int numberOfCycleNodes = cycleNodes.size();
		Map<Segment2D, Collection<Point2D>> answer = new HashMap<>(numberOfCycleNodes);
		for (Segment2D originalEdge : pointsOnPolygonBorder.keySet()) {
			for (Point2D startingPoint : pointsOnPolygonBorder.get(originalEdge)) {
				Segment2D actualSegment;
				if (holderOfSplitCycleEdges.isEdgeSplit(originalEdge)) {
					actualSegment = findActualSplitSegment(startingPoint, originalEdge);
				} else {
					actualSegment = originalEdge;
				}
				answer.computeIfAbsent(
					actualSegment,
					edge -> new ArrayList<>(numberOfCycleNodes / numberOfChains)
				).add(startingPoint);
			}
		}
		return answer;
	}

	/**
	 * Finds total amount of elements in all lists.
	 *
	 * @param lists
	 * 	All lists.
	 * @return Total amount of elements in all lists.
	 */
	private int countPoints(Collection<List<Point2D>> lists) {
		int numberOfPoints = 0;
		for (List<Point2D> list : lists) {
			numberOfPoints += list.size();
		}
		return numberOfPoints;
	}

	/**
	 * Finds the actual split segment on which {@code startingPoint} resides.
	 *
	 * @param startingPoint
	 * 	A point on a network cycle's edge.
	 * @param originalEdge
	 * 	An original edge of network's cycle.
	 * @return A split segment which is a part of {@code originalEdge}.
	 */
	private Segment2D findActualSplitSegment(Point2D startingPoint, Segment2D originalEdge) {
		for (Segment2D splitSegment : holderOfSplitCycleEdges.getGraph(originalEdge).edgeSet()) {
			if (Recs2D.boundingBox(splitSegment).contains(startingPoint)) {
				return splitSegment;
			}
		}
		throw new RuntimeException("Could not find an actual segment");
	}
}
