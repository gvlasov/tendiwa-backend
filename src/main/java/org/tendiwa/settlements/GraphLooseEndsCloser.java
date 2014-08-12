package org.tendiwa.settlements;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Adds new edges and vertices to a planar graph so it doesn't have any vertices of degree 1 (loose ends).
 * <p>
 * This class is not intended to be used with planar graphs that have any vertices of degree 0.
 */
public class GraphLooseEndsCloser {
	private final Set<Point2D> used = new HashSet<>();
	private final UndirectedGraph<Point2D, Segment2D> sourceGraph;
	private double snapSize;
	private final Set<DirectionFromPoint> filamentEnds;

	/**
	 * @param sourceGraph
	 * 	A planar graph to mutate.
	 * @param snapSize
	 * 	How long an edge can be.
	 * @param filamentEnds
	 * 	Precomputed directions where a new edge from each point should go. If null,
	 * 	then it will be computed in this constructor.
	 */
	private GraphLooseEndsCloser(
		UndirectedGraph<Point2D, Segment2D> sourceGraph,
		double snapSize,
		Set<DirectionFromPoint> filamentEnds
	) {

		this.sourceGraph = sourceGraph;
		this.snapSize = snapSize;
		this.filamentEnds = filamentEnds == null ? createFilamentEnds(sourceGraph) : filamentEnds;
	}

	@SuppressWarnings("unused")
	public static StepWithFilamentEnds withSnapSize(double snapSize) {
		return new StepWithFilamentEnds(snapSize);
	}

	private static Set<DirectionFromPoint> createFilamentEnds(UndirectedGraph<Point2D, Segment2D> graph) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds new edges and vertices to {@link #sourceGraph}.
	 */
	void closeLooseEnds(
	) {
		for (DirectionFromPoint end : filamentEnds) {
			if (isUsed(end.node)) {
				continue;
			}
			assert sourceGraph.degreeOf(end.node) == 1;
			edgeToClosestSnap(end);
		}
	}

	/**
	 * Checks if the algorithm has already added an edge from a particular loose end.
	 *
	 * @param end
	 * 	A loose end.
	 * @return true if it did, false otherwise.
	 */
	private boolean isUsed(Point2D end) {
		return used.contains(end);
	}


	/**
	 * Creates an edge from a loose end to the most appropriate point in the graph.
	 *
	 * @param end
	 * 	A loose end.
	 */
	private void edgeToClosestSnap(DirectionFromPoint end) {
		SnapEvent test = new SnapTest(
			snapSize,
			end.node,
			end.node.moveBy(
				Math.cos(end.direction) * snapSize,
				Math.sin(end.direction) * snapSize
			),
			sourceGraph
		).snap();
		switch (test.eventType) {
			case NO_SNAP:
				assert false;
				break;
			case NODE_SNAP:
				sourceGraph.addEdge(end.node, test.targetNode);
				used.add(test.targetNode);
				break;
			case ROAD_SNAP:
				sourceGraph.removeEdge(test.road);
				sourceGraph.addVertex(test.targetNode);
				sourceGraph.addEdge(end.node, test.targetNode);
				sourceGraph.addEdge(test.road.start, test.targetNode);
				sourceGraph.addEdge(test.road.end, test.targetNode);
				break;
			case NO_NODE:
				assert false;
				break;
			default:
				assert false;
		}
	}


	public static class StepWithFilamentEnds {
		private final double snapSize;

		private StepWithFilamentEnds(double snapSize) {
			this.snapSize = snapSize;
		}

		/**
		 * Sets filament ends if they have already been computed so they the algorithm won't need to compute them
		 * again.
		 *
		 * @param filamentEnds
		 * 	A set if pairs "loose end and direction".
		 * @return The next step where you can select the graph to mutate.
		 */
		@SuppressWarnings("unused")
		public StepGraph withFilamentEnds(Set<DirectionFromPoint> filamentEnds) {
			Objects.requireNonNull(filamentEnds);
			return new StepGraph(snapSize, filamentEnds);
		}

		/**
		 * Selects a graph to mutate. Because {@link #withFilamentEnds(java.util.Set)} step is missed,
		 * filament ends will be computed separately.
		 *
		 * @param graph
		 * 	A graph to mutate.
		 */
		@SuppressWarnings("unused")
		public void mutateGraph(UndirectedGraph<Point2D, Segment2D> graph) {
			new GraphLooseEndsCloser(graph, snapSize, null).closeLooseEnds();
		}
	}

	public static class StepGraph {
		private final double snapSize;
		private final Set<DirectionFromPoint> filamentEnds;

		public StepGraph(double snapSize, Set<DirectionFromPoint> filamentEnds) {
			this.snapSize = snapSize;
			this.filamentEnds = filamentEnds;
		}

		/**
		 * Selects a graph to mutate. Because filament ends were set in the previous step
		 *
		 * @param graph
		 * 	A graph to mutate.
		 */
		@SuppressWarnings("unused")
		public void mutateGraph(UndirectedGraph<Point2D, Segment2D> graph) {
			new GraphLooseEndsCloser(graph, snapSize, filamentEnds).closeLooseEnds();
		}
	}
}
