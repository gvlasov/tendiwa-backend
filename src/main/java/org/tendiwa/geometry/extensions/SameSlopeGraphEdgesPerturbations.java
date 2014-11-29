package org.tendiwa.geometry.extensions;

import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.map.hash.TDoubleObjectHashMap;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.*;
import java.util.stream.Collectors;

public final class SameSlopeGraphEdgesPerturbations {

	public static final int MAX_ITERATIONS = 10;

	private SameSlopeGraphEdgesPerturbations() {
		throw new UnsupportedOperationException();
	}

	public static void perturb(UndirectedGraph<Point2D, Segment2D> graphToMutate, double magnitude) {
		int verticesNumber = graphToMutate.vertexSet().size();
		TDoubleObjectMap<Collection<Segment2D>> slopeToEdges = computeSlopes(graphToMutate.edgeSet(), verticesNumber);
		Perturbation perturbation = new Perturbation(magnitude);
		for (int i = 0; i < MAX_ITERATIONS; i++) {
			for (double slope : slopeToEdges.keys()) {
				Collection<Segment2D> sameSlopeEdges = slopeToEdges.get(slope);
				if (sameSlopeEdges.size() > 1) {
					sameSlopeEdges.forEach(perturbation::addEdge);
					perturbation.apply(graphToMutate);
				}
			}
			if (!perturbation.hasBeenApplied()) {
				return;
			}
			slopeToEdges = computeSlopes(graphToMutate.edgeSet(), verticesNumber);
			perturbation.clear();
		}
		throw new GeometryException("Failed to perturb a graph after " + MAX_ITERATIONS + " attempts");
	}


	/**
	 * Compute a map from angle coefficients to collections of edges having those angle coefficients.
	 *
	 * @param edges
	 * 	All edges.
	 * @param verticesNumber
	 * 	How many vertices are there.
	 * @return A map from slopes to all edges having that slope.
	 */
	private static TDoubleObjectMap<Collection<Segment2D>> computeSlopes(
		Collection<Segment2D> edges,
		int verticesNumber
	) {
		TDoubleObjectMap<Collection<Segment2D>> slopeToEdge = new TDoubleObjectHashMap<>(verticesNumber);
		for (Segment2D edge : edges) {
			double slope = computeSlope(edge);
			if (!slopeToEdge.containsKey(slope)) {
				slopeToEdge.put(slope, new LinkedHashSet<>());
			}
			slopeToEdge.get(slope).add(edge);
		}
		return slopeToEdge;
	}

	/**
	 * Computes angular coefficient of a line on which {@code segment} lies.
	 *
	 * @param segment
	 * 	A segment to compute a slope of.
	 * @return Angular coefficient of a segment's line.
	 */
	private static double computeSlope(Segment2D segment) {
		double slope;
		if (segment.dx() == 0) {
			slope = Double.POSITIVE_INFINITY;
		} else {
			slope = segment.dy() / segment.dx();
		}
		return slope;
	}

	private static final class Perturbation {

		private final Random random;
		private UndirectedGraph<Point2D, Object> graph = createGraph();

		private SimpleGraph<Point2D, Object> createGraph() {
			return new SimpleGraph<>((a, b) -> new Object());
		}

		private final Map<Point2D, Point2D> originalToPerturbed = new HashMap<>();
		private final double magnitude;
		private boolean applied = false;

		Perturbation(double magnitude) {
			this.magnitude = magnitude;
			this.random = new Random(0);
		}

		/**
		 * Resets state of this object to be used in the next iteration.
		 */
		void clear() {
			graph = createGraph();
			originalToPerturbed.clear();
			applied = false;
		}

		private Point2D getPerturbed(Point2D original) {
			Point2D perturbed;
			if (!originalToPerturbed.containsKey(original)) {
				perturbed = perturb(original);
				graph.addVertex(perturbed);
				originalToPerturbed.put(original, perturbed);
			} else {
				perturbed = originalToPerturbed.get(original);
			}
			return perturbed;
		}

		void addEdge(Segment2D edge) {
			if (shitpoint.distanceTo(edge.start) < 1.5 || shitpoint.distanceTo(edge.end) < 1.5) {
				System.out.println(edge + " " + computeSlope(edge));
			}
			graph.addEdge(
				getPerturbed(edge.start),
				getPerturbed(edge.end)
			);
		}

		private static Point2D shitpoint = new Point2D(1441, 344);

		boolean hasBeenApplied() {
			return applied;
		}

		/**
		 * Perturbs vertices and edges of {@code graph}.
		 *
		 * @param outputGraph
		 * 	A graph whose vertices are to be moved from original positions to perturbed poisitions.
		 */
		void apply(UndirectedGraph<Point2D, Segment2D> outputGraph) {
			for (Point2D originalVertex : originalToPerturbed.keySet()) {
				if (!outputGraph.containsVertex(originalVertex)) {
					continue;
				}
				Point2D perturbed = originalToPerturbed.get(originalVertex);
				moveVertex(outputGraph, originalVertex, perturbed);
			}
			applied = true;
		}

		/**
		 * Creates a new point, slightly moved from {@code originalVertex}.
		 *
		 * @param originalVertex
		 * 	The original point.
		 * @return A point slightly moved from {@code originalVertex}.
		 */
		private Point2D perturb(Point2D originalVertex) {
			return new Point2D(
				originalVertex.x + (random.nextDouble() - 0.5) * magnitude,
				originalVertex.y + (random.nextDouble() - 0.5) * magnitude
			);
		}

		/**
		 * Moves a vertex of a graph from one position to another, rebuilding edges coming to and from that vertex.
		 *
		 * @param graph
		 * 	A graph to mutate.
		 * @param originalVertex
		 * 	Original vertex.
		 * @param newPosition
		 * 	Changed vertex.
		 */
		static void moveVertex(
			UndirectedGraph<Point2D, Segment2D> graph,
			Point2D originalVertex,
			Point2D newPosition
		) {
			Set<Segment2D> edges = graph.edgesOf(originalVertex);
			Set<Point2D> otherEnds = edges.stream()
				.map(e -> e.start == originalVertex ? e.end : e.start)
				.collect(Collectors.toSet());
			graph.removeVertex(originalVertex);
			graph.addVertex(newPosition);
			otherEnds.forEach(end -> graph.addEdge(newPosition, end));
		}
	}
}
