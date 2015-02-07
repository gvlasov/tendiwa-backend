package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.Point2DRowComparator;
import org.tendiwa.graphs.CycleTraverser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * After the innards of an enclosing cycle were flood-filled, there may be left some secondary road network blocks
 * that are not properly divided into more blocks because the shape of the original network graph prevents flood
 * filling from accessing some of graph's parts. This class finds points from which you need to start new flood
 * fillings to properly divide the leftover blocks.
 */
public class UnfilledBlocksDetector {
	private final Set<Point2D> startingPoints;
	private final UndirectedGraph<Point2D, Segment2D> relevantNetwork;
	private final Set<Point2D> cycleNodes;
	private Reconstruction reconstruction;


	public UnfilledBlocksDetector(
		Set<Point2D> startingPoints,
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		Set<Point2D> cycleNodes
	) {
		this.startingPoints = startingPoints;
		this.relevantNetwork = relevantNetwork;
		this.cycleNodes = cycleNodes;
		this.reconstruction = new Reconstruction();
	}

	/**
	 * Recomputes the starting point for {@link UnfilledBlocksDetector#getStartingPoint()}.
	 */
	private final class Reconstruction {
		private final int numberOfChains;
		private List<Segment2D> currentChain;
		private final Point2D startingPoint;
		private final List<List<Segment2D>> answer;

		Reconstruction() {
			numberOfChains = countSecondaryRoadNetworkExits();
			answer = new ArrayList<>(numberOfChains);
			Collection<List<Segment2D>> chainsOf2DegreeVertices = findChainsOf2DegreeVertices();
			Collection<List<Point2D>> pointsOn2DegreeChains = groupPointsBy2DegreeChains(
				chainsOf2DegreeVertices
			);
			startingPoint = findStartingPoint(pointsOn2DegreeChains);
		}

		private Collection<List<Segment2D>> findChainsOf2DegreeVertices() {
			currentChain = createNewChain(numberOfChains);
			answer.add(currentChain);

			CycleTraverser.forGraph(relevantNetwork)
				.sortNeighborsWith(Point2DRowComparator.getInstance())
				.withCycleVertices(cycleNodes)
				.forEach(this::growChain);

			return answer;
		}

		private void growChain(Point2D currentVertex, Point2D nextVertex) {
//			TestCanvas.canvas.draw(nextVertex, DrawingPoint2D.withColorAndSize(Color.yellow, 6));
			currentChain.add(relevantNetwork.getEdge(currentVertex, nextVertex));
			if (relevantNetwork.degreeOf(nextVertex) > 2) {
				currentChain = createNewChain(numberOfChains);
				answer.add(currentChain);
			}
		}

		private Collection<List<Point2D>> groupPointsBy2DegreeChains(
			Collection<List<Segment2D>> chainsOf2DegreeVertices
		) {
			Collection<List<Point2D>> answer = new ArrayList<>(numberOfChains);
			for (List<Segment2D> chain : chainsOf2DegreeVertices) {
				List<Point2D> pointsOfChain = new ArrayList<>();
				answer.add(pointsOfChain);
				for (Segment2D segment : chain) {
					if (startingPoints.contains(segment.start)) {
						pointsOfChain.add(segment.start);
					}
					if (startingPoints.contains(segment.end)) {
						pointsOfChain.add(segment.start);
					}
				}
			}
			return answer;
		}

		private Point2D findStartingPoint(Collection<List<Point2D>> pointsOn2DegreeChains) {
			for (List<Point2D> points : pointsOn2DegreeChains) {
				if (points.size() > 3) {
					// Which point should we pick is arguable, but I can't figure out a better way to pick one.
					return points.get(points.size() / 2);
				}
			}
			return null;
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
	}

	public void update() {
		Point2D previousStartingPoint = reconstruction.startingPoint;
		this.reconstruction = new Reconstruction();
		assert reconstruction.startingPoint != previousStartingPoint;
	}


	boolean canOfferStartingPoint() {
		return reconstruction.startingPoint != null;
	}


	/**
	 * @return A point from which next flood fill by {@link org.tendiwa.settlements.networks.SecondaryRoadNetwork}
	 * should be started, or null if there is no such point (all blocks are small enough).
	 */
	Point2D getStartingPoint() {
		return reconstruction.startingPoint;
	}
}
