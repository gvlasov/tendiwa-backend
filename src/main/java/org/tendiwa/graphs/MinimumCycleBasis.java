package org.tendiwa.graphs;

import com.google.common.collect.Iterators;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.*;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.geometry.extensions.PolygonSegments;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;

import java.awt.Color;
import java.util.*;

import static org.tendiwa.geometry.Vectors2D.*;

/**
 * Implementation of minimal cycle basis algorithm as described in the <a href="http://www.geometrictools
 * .com/Documentation/MinimalCycleBasis.pdf">[Eberly 2005]</a> paper.
 *
 * @param <V>
 * 	Type of vertices.
 * @param <E>
 * 	Type of edges.
 */
public class MinimumCycleBasis<V, E> {
	private final ListenableUndirectedGraph<V, E> graph;
	private final Comparator<V> comparator = new Comparator<V>() {
		@Override
		public int compare(V v1, V v2) {
			int v = (int) Math.signum(positionAdapter.getX(v1) - positionAdapter.getX(v2));
			if (v == 0) {
				v = (int) Math.signum(positionAdapter.getY(v1) - positionAdapter.getY(v2));
				if (v == 0) {
					throw new IllegalArgumentException("Vertices have equal coordinates x:" + positionAdapter.getX(v1) + ";y:" + positionAdapter.getY(v2));
				}
			}
			return v;
		}
	};
	private final PrimitiveContainer<V> primitives;
	private final Queue<V> heap;
	private final Collection<E> cycleEdges = new HashSet<>();
	private final NeighborIndex<V, E> neighborIndex;
	private UndirectedGraph<V, E> originalGraph;
	private VertexPositionAdapter<V> positionAdapter;
	private static final Iterator<Color> colors = Iterators.cycle(Color.red, Color.blue, Color.yellow, Color.green, Color.cyan);

	/**
	 * @param graph
	 * 	A graph from which to extract a minimum cycle basis.
	 * @param positionAdapter
	 * 	[Eberly 2005, formula on p. 25]
	 * 	<p>
	 * 	A strategy of two methods describing how to get x and y coordinates from vertices.
	 */

	public MinimumCycleBasis(UndirectedGraph<V, E> graph, VertexPositionAdapter<V> positionAdapter) {
		originalGraph = graph;
		this.positionAdapter = positionAdapter;
		this.primitives = new PrimitiveContainer<>();
		// Listenable graph is used here because we need to determine neighbor vertices,
		// and it is better done with a listenable graph.
		this.graph = new ListenableUndirectedGraph<V, E>(new SimpleGraph<>(graph.getEdgeFactory()));
		// TODO: Should we add vertices and edges explicitly here?
		for (V v : graph.vertexSet()) {
			this.graph.addVertex(v);
		}
		for (E e : graph.edgeSet()) {
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
			V v0 = heap.peek();// OR .remove()?
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
			assert Boolean.TRUE;
		}
	}


	private void extractIsolatedVertex(V v0) {
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
	private void extractFilament(V v0, V v1) {
		if (isCycleEdge(v0, v1)) {
			if (graph.degreeOf(v0) >= 3) {
				boolean edgeRemoved = graph.removeEdge(graph.getEdge(v0, v1));
				assert edgeRemoved;
				v0 = v1;
				if (graph.degreeOf(v0) == 1) {
					v1 = neighborIndex.neighborListOf(v0).get(0);
				}
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
			Filament<V, E> filament = new Filament<>(originalGraph);
			if (graph.degreeOf(v0) >= 3) {
				filament.insert(v0);
				graph.removeEdge(v0, v1);
				v0 = v1;
				if (graph.degreeOf(v0) == 1) {
					v1 = neighborIndex.neighborListOf(v0).get(0);
				}
			}
			while (graph.degreeOf(v0) == 1) {
				filament.insert(v0);
				v1 = neighborIndex.neighborListOf(v0).get(0);
				heap.remove(v0);
				graph.removeEdge(v0, v1);
				graph.removeVertex(v0);
				v0 = v1;
			}
			filament.insert(v0);
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
	private void extractPrimitive(V v0) {
		Set<V> visited = new HashSet<>();
		List<V> sequence = new ArrayList<>();
		sequence.add(v0);
		V v1 = getMost(null, v0, true);
		V vprev = v0;
		V vcurr = v1;
		while (vcurr != null && !vcurr.equals(v0) && !visited.contains(vcurr)) {
			sequence.add(vcurr);
			visited.add(vcurr);
			V vnext = getMost(vprev, vcurr, false);
			vprev = vcurr;
			vcurr = vnext;
		}
		if (vcurr == null) {
			// Filament found, not necessarily rooted at v0
			extractFilament(vprev, neighborIndex.neighborsOf(vprev).iterator().next());
		} else if (vcurr.equals(v0)) {
			// Minimal cycle found
			MinimalCycle<V, E> cycle = new MinimalCycle<>(originalGraph, sequence);
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
		} else {
			// A cycle has been found, but it is not guaranteed to be a minimal
			// cycle. This implies v0 is part of a filament. Locate the
			// starting point for the filament by traversing from v0 away
			// from the initial v1.
			// TODO: ^^ this comment is taken from Eberly's paper, but it is not true. Finding a cycle here does not
			// imply v0 is part of a filament. A counter-example is a cycle within cycle connected with a filament
			// whose start is a vertex other than v0.
			while (graph.degreeOf(v0) == 2) {
				if (!neighborIndex.neighborListOf(v0).get(0).equals(v1)) {
					v1 = v0;
					v0 = neighborIndex.neighborListOf(v0).get(0);
				} else {
					v1 = v0;
					v0 = neighborIndex.neighborListOf(v0).get(1);
				}
				TestCanvas.canvas.draw(new Segment2D((Point2D)v1, (Point2D)v0), DrawingSegment2D.withColorDirected
					(Color.magenta, 5));
			}
			extractFilament(v0, v1);
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
	private V getMost(V vprev, V vcurr, boolean clockwise) {
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
				positionAdapter.getX(vcurr) - positionAdapter.getX(vprev),
				positionAdapter.getY(vcurr) - positionAdapter.getY(vprev)
			};
		}

		V vnext = null;
		for (V vertex : neighborIndex.neighborsOf(vcurr)) {
			if (!vertex.equals(vprev)) {
				vnext = vertex;
				break;
			}
		}
		// vnext should be an adjacent vertex of vcurr not equal to vprev
		assert vnext != null;

		double[] dnext = new double[]{
			positionAdapter.getX(vnext) - positionAdapter.getX(vcurr),
			positionAdapter.getY(vnext) - positionAdapter.getY(vcurr)
		};
		boolean currWasReversed = dotProduct(dcurr, dnext) < 1;

		double vcurrIsConvex = perpDotProduct(dnext, dcurr);
		// There's no notion of almost parallel vectors in the original algorithm description,
		// however having parallel consecutive segments introduces a new kind of error where an angle is
		// slightly > PI instead of being slightly < PI, or vice versa. That occurs due to floating point rounding
		// errors.
		boolean isNextConsideredParallel = areParallel(dcurr, dnext);

		for (V vadj : neighborIndex.neighborsOf(vcurr)) {
			if (vadj.equals(vprev) || vadj.equals(vnext)) {
				continue;
			}
			double[] dadj = new double[]{
				positionAdapter.getX(vadj) - positionAdapter.getX(vcurr),
				positionAdapter.getY(vadj) - positionAdapter.getY(vcurr)
			};
			if (isNextConsideredParallel) {
				// When vectors dcurr and dnext are almost parallel, we need a distinct way of finding the next
				// (counter-)clockwise vertex.
				double angle = angleBetweenVectors(dcurr, dadj, clockwise);
				if (
//					clockwise && positionToCurr < 0 && positionToPrev < 0
//						|| !clockwise && positionToCurr > 0 && positionToPrev > 0
					currWasReversed ? angle > Math.PI : angle < Math.PI
					) {
//					assert Math.abs(angle - Math.PI) > Vectors2D.EPSILON;
//					assert !areParallel(dcurr, dadj);
					vnext = vadj;
					dnext = dadj;
					vcurrIsConvex = perpDotProduct(dnext, dcurr);
					isNextConsideredParallel = areParallel(dcurr, dnext);
					currWasReversed = false;
//					assert !isNextConsideredParallel; // Probably...
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
	 * @param pointX
	 * @param pointY
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @return
	 * @see <a href="http://stackoverflow.com/questions/1560492/how-to-tell-whether-a-point-is-to-the-right-or-left-side-of-a-line">
	 * Stackoverflow</a>
	 */
	public static double pointPositionRelativeToLine(
		double pointX, double pointY,
		double startX, double startY,
		double endX, double endY
	) {
		return ((endX - startX) * (pointY - startY) - (endY - startY) * (pointX - startX));
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
	private void markCycleEdge(V v0, V v1) {
		boolean add = cycleEdges.add(graph.getEdge(v0, v1));
//        assert add;
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
	private boolean isCycleEdge(V v0, V v1) {
		return cycleEdges.contains(graph.getEdge(v0, v1));
	}

	private Queue<V> sortVertices(UndirectedGraph<V, E> graph) {
		PriorityQueue<V> vs = new PriorityQueue<>(graph.vertexSet().size(), comparator);
		vs.addAll(graph.vertexSet());
		return vs;
	}

	public Set<V> isolatedVertexSet() {
		return primitives.isolatedVertices;
	}

	public Set<Filament<V, E>> filamentsSet() {
		return primitives.filaments;
	}

	public Set<MinimalCycle<V, E>> minimalCyclesSet() {
		return primitives.minimalCycles;
	}

	private class PrimitiveContainer<V> {
		private final Set<V> isolatedVertices = new LinkedHashSet<>();
		private final Set<Filament<V, E>> filaments = new LinkedHashSet<>();
		private final Set<MinimalCycle<V, E>> minimalCycles = new LinkedHashSet<>();

		private void add(V isolatedVertex) {
			isolatedVertices.add(isolatedVertex);
			TestCanvas.canvas.draw((Point2D) isolatedVertex, DrawingPoint2D.withColorAndSize(Color.blue, 3));
			assert Boolean.TRUE;
		}

		private void add(Filament<V, E> filament) {
			filaments.add(filament);
			TestCanvas.canvas.draw((List<Point2D>) filament.vertexList(), DrawingPolyline.withColor(Color.green));
			assert Boolean.TRUE;
		}

		private void add(MinimalCycle<V, E> cycle) {
			minimalCycles.add(cycle);
			TestCanvas.canvas.draw((List<Point2D>) cycle.vertexList(), DrawingPolygon.withColor(Color.red));
			assert Boolean.TRUE;
		}
	}
}
