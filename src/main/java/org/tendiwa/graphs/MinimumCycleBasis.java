package org.tendiwa.graphs;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.graphs2d.Graph2D;

import java.util.*;

import static org.tendiwa.geometry.Vectors2D.*;

/**
 * Implementation of minimal cycle basis algorithm as described in the <a href="http://www.geometrictools
 * .com/Documentation/MinimalCycleBasis.pdf">[Eberly 2005]</a> paper.
 */
public class MinimumCycleBasis {
	private final ListenableUndirectedGraph<Point2D, Segment2D> graph;
	private final Comparator<Point2D> comparator = (v1, v2) -> {
		int v = (int) Math.signum(v1.x() - v2.x());
		if (v == 0) {
			v = (int) Math.signum(v1.y() - v2.y());
			if (v == 0) {
				throw new IllegalArgumentException(
					"Vertices have equal coordinates x:" + v1.x() + ";y:" + v2.y()
				);
			}
		}
		return v;
	};
	private final PrimitiveContainer primitives;
	private final Queue<Point2D> heap;
	private final Collection<Segment2D> cycleEdges = new HashSet<>();
	private final NeighborIndex<Point2D, Segment2D> neighborIndex;
	private Graph2D originalGraph;

	/**
	 * @param graph
	 * 	A graph from which to extract a minimum cycle basis.
	 * 	<p>
	 * 	A strategy of two methods describing how to get x and y coordinates from vertices.
	 */

	public MinimumCycleBasis(Graph2D graph) {
		originalGraph = graph;
		this.primitives = new PrimitiveContainer();
		// Listenable graph is used here because we need to determine neighbor vertices,
		// and it is better done with a listenable graph.
		this.graph = new ListenableUndirectedGraph<Point2D, Segment2D>(new SimpleGraph<>(BasicSegment2D::new));
		// TODO: Should we add vertices and edges explicitly here?
		for (Point2D v : graph.vertexSet()) {
			this.graph.addVertex(v);
		}
		for (Segment2D e : graph.edgeSet()) {
			this.graph.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e), e);
		}
		if (graph.vertexSet().size() > 0) {
			neighborIndex = new NeighborIndex<>(this.graph);
			this.graph.addGraphListener(neighborIndex);

			heap = sortVertices(graph);
			extractPrimitives();
		} else {
			// For an empty graph
			heap = null;
			neighborIndex = null;
		}
	}

	/**
	 * [Eberly 2005, p 25, function Graph::ExtractPrimitives]
	 * <p>
	 * Finds and saves all primitives: isolated vertices, filaments and minimum cycles.
	 */
	private void extractPrimitives() {
		while (!heap.isEmpty()) {
			Point2D v0 = heap.peek();// OR .remove()?
			int i = graph.degreeOf(v0);
			switch (i) {
				case 0:
					extractIsolatedVertex(v0);
					break;
				case 1:
					extractFilament(v0, getMost(null, v0, true));
					break;
				default:
					extractPrimitive(v0);
			}
		}
	}

	private void extractIsolatedVertex(Point2D v0) {
		heap.remove(v0);
		graph.removeVertex(v0);
		primitives.add(v0);
	}

	/**
	 * [Eberly 2005, function Graph::ExtractFilament on p.30]
	 * <p>
	 * Tries to find a filament, or to remove edges left after finding a cycle.
	 *
	 * @param v0
	 * 	Source edge, least in the lexicographical order.
	 * @param v1
	 * 	Target edge.
	 */
	private void extractFilament(Point2D v0, Point2D v1) {
		if (isCycleEdge(v0, v1)) {
			if (graph.degreeOf(v0) >= 3) {
				boolean edgeRemoved = graph.removeEdge(graph.getEdge(v0, v1));
				assert edgeRemoved;
				v0 = v1;
			}
			while (graph.degreeOf(v0) == 1) {
				v1 = neighborIndex.neighborListOf(v0).get(0);
				if (isCycleEdge(v0, v1)) {
					heap.remove(v0);
					graph.removeEdge(v0, v1);
					graph.removeVertex(v0);
					v0 = v1;
				} else {
					break;
				}
			}
			if (graph.degreeOf(v0) == 0) {
				heap.remove(v0);
				graph.removeVertex(v0);
			}
		} else {
			Filament filament = new Filament(originalGraph);
			if (graph.degreeOf(v0) >= 3) {
				filament.addInFront(v0);
				graph.removeEdge(v0, v1);
				v0 = v1;
				if (graph.degreeOf(v0) == 1) {
					v1 = neighborIndex.neighborListOf(v0).get(0);
				}
			}
			while (graph.degreeOf(v0) == 1) {
				filament.addInFront(v0);
				v1 = neighborIndex.neighborListOf(v0).get(0);
				heap.remove(v0);
				graph.removeEdge(v0, v1);
				graph.removeVertex(v0);
				v0 = v1;
			}
			filament.addInFront(v0);
			if (graph.degreeOf(v0) == 0) {
				// If end of a filament is not a branch point
				heap.remove(v0);
				graph.removeEdge(v0, v1);
				graph.removeVertex(v0);
			}
			primitives.add(filament);
		}

	}

	/**
	 * [Eberly 2005, function Graph::ExtractPrimitive of p. 32]
	 * <p>
	 * Tries to extract a minimal cycle, or leads to extraction of a filament.
	 *
	 * @param v0
	 * 	Source edge, least in the lexicographical order.
	 */
	private void extractPrimitive(Point2D v0) {
		Set<Point2D> visited = new HashSet<>();
		List<Point2D> sequence = new ArrayList<>();
		sequence.add(v0);
		Point2D v1 = getMost(null, v0, true);
		Point2D vprev = v0;
		Point2D vcurr = v1;
		while (vcurr != null && !vcurr.equals(v0) && !visited.contains(vcurr)) {
			sequence.add(vcurr);
			visited.add(vcurr);
			Point2D vnext = getMost(vprev, vcurr, false);
			vprev = vcurr;
			vcurr = vnext;
		}
		if (vcurr == null) {
			// Filament found, not necessarily rooted at v0
			extractFilament(vprev, neighborIndex.neighborsOf(vprev).iterator().next());
		} else if (vcurr.equals(v0)) {
			extractMinimalCycle(v0, v1, sequence);
		} else {
			// A cycle has been found, but it is not guaranteed to be a minimal
			// cycle. This implies vcurr (as it occured twice in sequence) is part of a filament.
			// Next point in the filament is one step back in sequence.
			extractFilament(vcurr, sequence.get(sequence.indexOf(vcurr) - 1));
		}
	}

	private void extractMinimalCycle(Point2D v0, Point2D v1, List<Point2D> sequence) {
		Polygon cycle = new MinimalCycle(originalGraph, sequence);
		primitives.add(cycle);

		for (int i = 0, l = sequence.size() - 1; i < l; i++) {
			markCycleEdge(sequence.get(i), sequence.get(i + 1));
		}
		markCycleEdge(sequence.get(0), sequence.get(sequence.size() - 1));
		graph.removeEdge(v0, v1);
		if (graph.degreeOf(v0) == 1) {
			// Remove the filament rooted at v0
			extractFilament(v0, neighborIndex.neighborListOf(v0).get(0));
		}
		if (graph.containsVertex(v1) && graph.degreeOf(v1) == 1) {
			// Remove the filament rooted at v1
			extractFilament(v1, neighborIndex.neighborListOf(v1).get(0));
		}
	}

	/**
	 * [Eberly 2005, function Graph::GetClockwiseMost from p. 22, function Graph::GetCounterClockwiseMost form p. 23]
	 * <p>
	 * Finds a clockwise-most or a counterclockwise-most vertex relative to a given edge.
	 *
	 * @param vprev
	 * 	Start of a given edge.
	 * @param vcurr
	 * 	End of a given edge. The order of vprev and vcurr does matter.
	 * @param clockwise
	 * 	true if searching clockwise, false if searching counter-clockwise.
	 * @return A vertex that is clockwise- or counterclockwise-most relative to a vector {@code vcurr-vprev}.
	 */
	private Point2D getMost(Point2D vprev, Point2D vcurr, boolean clockwise) {
		if (neighborIndex.neighborsOf(vcurr).size() == 1 && neighborIndex.neighborsOf(vcurr).iterator().next()
			.equals(vprev)) {
			return null;
		}
		double[] dcurr;
		boolean supportingLineUsed = vprev == null;
		if (supportingLineUsed) {
			dcurr = new double[]{0, -1};
		} else {
			dcurr = new double[]{
				vcurr.x() - vprev.x(),
				vcurr.y() - vprev.y()
			};
		}

		Point2D vnext = null;
		for (Point2D vertex : neighborIndex.neighborsOf(vcurr)) {
			if (!vertex.equals(vprev)) {
				vnext = vertex;
				break;
			}
		}
		// vnext should be an adjacent vertex of vcurr not equal to vprev
		assert vnext != null;

		double[] dnext = new double[]{
			vnext.x() - vcurr.x(),
			vnext.y() - vcurr.y()
		};
		boolean currWasReversed = dotProduct(dcurr, dnext) < 1;

		double vcurrIsConvex = perpDotProduct(dnext, dcurr);
		// There's no notion of almost parallel vectors in the original algorithm description,
		// however having parallel consecutive segments introduces a new kind of error where an angle is
		// slightly > PI instead of being slightly < PI, or vice versa. That occurs due to floating point rounding
		// errors.
		boolean isNextConsideredParallel = areParallel(dcurr, dnext);

		for (Point2D vadj : neighborIndex.neighborsOf(vcurr)) {
			if (vadj.equals(vprev) || vadj.equals(vnext)) {
				continue;
			}
			double[] dadj = new double[]{
				vadj.x() - vcurr.x(),
				vadj.y() - vcurr.y()
			};
			if (isNextConsideredParallel) {
				// When vectors dcurr and dnext are almost parallel, we need a distinct way of finding the next
				// (counter-)clockwise vertex.
				double angle = angleBetweenVectors(dcurr, dadj, clockwise);
				if (currWasReversed ? angle > Math.PI : angle < Math.PI) {
					vnext = vadj;
					dnext = dadj;
					vcurrIsConvex = perpDotProduct(dnext, dcurr);
					isNextConsideredParallel = areParallel(dcurr, dnext);
				}
			} else if (vcurrIsConvex < 0) {
				boolean equation = clockwise ?
					perpDotProduct(dcurr, dadj) < 0 || perpDotProduct(dnext, dadj) < 0
					: perpDotProduct(dcurr, dadj) > 0 && perpDotProduct(dnext, dadj) > 0;
				if (equation) {
					vnext = vadj;
					dnext = dadj;
					vcurrIsConvex = perpDotProduct(dnext, dcurr);
					isNextConsideredParallel = areParallel(dcurr, dnext);
				}
			} else {
				boolean equation = clockwise ?
					perpDotProduct(dcurr, dadj) < 0 && perpDotProduct(dnext, dadj) < 0
					: perpDotProduct(dcurr, dadj) > 0 || perpDotProduct(dnext, dadj) > 0;
				if (equation) {
					vnext = vadj;
					dnext = dadj;
					vcurrIsConvex = perpDotProduct(dnext, dcurr);
					isNextConsideredParallel = areParallel(dcurr, dnext);
				}
			}
			currWasReversed = false;
		}
		return vnext;
	}

	/**
	 * [Eberly 2005, function Graph::ExtractPrimitive on p. 33, line e.isCycle = true]
	 * <p>
	 * Remembers that an edge is a part of a minimal cycle.
	 *
	 * @param v0
	 * 	Edge start.
	 * @param v1
	 * 	Edge end. Order doesn't matter since the graph is undirected.
	 */
	private void markCycleEdge(Point2D v0, Point2D v1) {
		cycleEdges.add(graph.getEdge(v0, v1));
	}

	/**
	 * Checks if an edge was determined to be a part of a minimal cycle.
	 *
	 * @param v0
	 * 	Edge start.
	 * @param v1
	 * 	Edge end. Order doesn't matter since the graph is undirected.
	 * @return true if it was, false otherwise.
	 */
	private boolean isCycleEdge(Point2D v0, Point2D v1) {
		return cycleEdges.contains(graph.getEdge(v0, v1));
	}

	private Queue<Point2D> sortVertices(Graph2D graph) {
		PriorityQueue<Point2D> vs = new PriorityQueue<>(graph.vertexSet().size(), comparator);
		vs.addAll(graph.vertexSet());
		return vs;
	}

	public Set<Point2D> isolatedVertexSet() {
		return primitives.isolatedVertices;
	}

	public Set<Polyline> filamentsSet() {
		return primitives.filaments;
	}

	public Set<Polygon> minimalCyclesSet() {
		return primitives.minimalCycles;
	}

	private class PrimitiveContainer {
		private final Set<Point2D> isolatedVertices = new LinkedHashSet<>();
		private final Set<Polyline> filaments = new LinkedHashSet<>();
		private final Set<Polygon> minimalCycles = new LinkedHashSet<>();

		private void add(Point2D isolatedVertex) {
			isolatedVertices.add(isolatedVertex);
		}

		private void add(Polyline filament) {
			filaments.add(filament);
		}

		private void add(Polygon cycle) {
			minimalCycles.add(cycle);
		}
	}
}
