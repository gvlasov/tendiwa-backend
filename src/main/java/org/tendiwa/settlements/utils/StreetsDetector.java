package org.tendiwa.settlements.utils;

import com.google.common.collect.*;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.Subgraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.*;

/**
 * Finds chains of {@link org.tendiwa.geometry.Segment2D} that are to form {@link org.tendiwa.settlements.streets.Street}s
 * among edges of a planar graph.
 */
public final class StreetsDetector {
	private static final Object JOINING_PROHIBITED = Boolean.TRUE;
	private final UndirectedGraph<Point2D, Segment2D> cityGraph;
	private final Map<Point2D, Deque<Point2D>> ends = new HashMap<>();
	private final Multimap<Segment2D, List<Point2D>> deferredChains = HashMultimap.create();
	private final Collection<Point2D> usedVertices;
	private final HashSet<Segment2D> usedEdges = new HashSet<>();
	private final Table<Segment2D, Point2D, Object> joiningProhibited = HashBasedTable.create();

	/**
	 * Finds streets made of edges of a planar graph.
	 *
	 * @param cityGraph
	 * 	A planar graph.
	 * @return Streets found in a road graph.
	 */
	public static Set<ImmutableList<Point2D>> detectStreets(UndirectedGraph<Point2D, Segment2D> cityGraph) {
		List<Set<Point2D>> connectivityComponents = new ConnectivityInspector<>(cityGraph).connectedSets();
		ImmutableSet.Builder<ImmutableList<Point2D>> builder = ImmutableSet.builder();
		for (Set<Point2D> component : connectivityComponents) {
			UndirectedSubgraph<Point2D, Segment2D> componentSubgraph = new UndirectedSubgraph<>(
				cityGraph,
				component,
				findEdgesOfComponent(cityGraph, component)
			);
			Set<ImmutableList<Point2D>> streets = new StreetsDetector(componentSubgraph).compute();
			builder.addAll(streets);
		}
		return builder.build();
	}

	private static Set<Segment2D> findEdgesOfComponent(UndirectedGraph<Point2D, Segment2D> cityGraph, Set<Point2D> component) {
		ImmutableSet.Builder<Segment2D> builder = ImmutableSet.builder();
		for (Point2D vertex : component) {
			for (Segment2D edge : cityGraph.edgesOf(vertex)) {
				if (component.contains(edge.start) && component.contains(edge.end)) {
					builder.add(edge);
				}
			}
		}
		return builder.build();
	}

	private StreetsDetector(UndirectedGraph<Point2D, Segment2D> cityGraph) {
		this.cityGraph = cityGraph;
		this.usedVertices = new HashSet<>(cityGraph.vertexSet().size());
	}

	private Set<ImmutableList<Point2D>> compute() {
		for (Point2D vertex : cityGraph.vertexSet()) {
			if (cityGraph.degreeOf(vertex) <= 2) {
				if (usedVertices.contains(vertex)) {
					continue;
				}
				Deque<Point2D> chain = find2DegreeChain(vertex);
				if (chain.size() >= 2) {
					addChain(chain);
				}
			} else if (cityGraph.degreeOf(vertex) >= 3) {
				for (Deque<Point2D> chain : splitIntersectionIntoChains(vertex)) {
					// Unlike 2-degree vertices case, here we don't add produced chains right away, because otherwise
					// the chains would interfere with each other. Instead, we defer assembling chains on
					// intersections (i.e., on vertices with degree >= 3) until all 2-vertex chains on intersections
					// are found.
					List<Point2D> asList = (List<Point2D>) chain;
					rememberChainFromIntersection(asList);
				}
			}
		}
		assembleDeferredChains();

		Set<ImmutableList<Point2D>> answer = new LinkedHashSet<>();
		for (Deque<Point2D> chain : ends.values()) {
			// LinkedList implements both Deque and List interfaces
			answer.add(ImmutableList.copyOf((List<Point2D>) chain));
		}
		// TODO: Detect chains that end inside themselves and break them into several chains
		return answer;
	}

	private void assembleDeferredChains() {
		Set<Segment2D> commonEdges = ImmutableSet.copyOf(deferredChains.keySet());
		for (Segment2D edge : commonEdges) {
			Collection<List<Point2D>> chainsSharingEdge = deferredChains.get(edge);
			assert chainsSharingEdge.size() <= 2;
			if (chainsSharingEdge.size() == 2) {
				Iterator<List<Point2D>> iterator = chainsSharingEdge.iterator();
				// Take both chains
				List<Point2D> one = iterator.next();
				List<Point2D> another = iterator.next();
				deferredChains.removeAll(edge);
				assert !deferredChains.containsKey(edge);
				Collection<List<Point2D>> values = deferredChains.values();
				values.remove(one);
				values.remove(another);
				List<Point2D> newChain = uniteChains(one, another);
				// At this moment, one or another may be mutated
				assert !newChain.isEmpty();
				assert !one.isEmpty();
				assert !another.isEmpty();
				deferredChains.put(getEdgeFromBeginning(newChain), newChain);
				deferredChains.put(getEdgeFromEnd(newChain), newChain);
			} else {
				assert chainsSharingEdge.size() < 2;
			}
		}
		assert deferredChains.values().stream().allMatch(a -> a.size() > 0);
		assert deferredChains.values().stream().allMatch(a -> !a.isEmpty());
		for (List<Point2D> point2Ds : deferredChains.values()) {
			assert !point2Ds.isEmpty();
		}

		for (Segment2D edge : deferredChains.keySet()) {
			for (List<Point2D> chain : deferredChains.get(edge)) {
				if (chain.isEmpty()) {
					continue;
				}
				addChain((Deque<Point2D>) chain);
			}
		}
	}

	private Segment2D getEdgeFromEnd(List<Point2D> chain) {
		return cityGraph.getEdge(chain.get(chain.size() - 1), chain.get(chain.size() - 2));
	}

	private Segment2D getEdgeFromBeginning(List<Point2D> chain) {
		return cityGraph.getEdge(chain.get(0), chain.get(1));
	}

	/**
	 * Unites chains {@code one} and {@code another} by adding vertices of {@code another} to one preserving the
	 * relative order.
	 *
	 * @param one
	 * 	A chain. May be mutated, don't reuse.
	 * @param another
	 * 	Another chain. May be mutated, don't reuse.
	 * @return A chain constructed by uniting {@code one} and {@code another} into a single chain.
	 */
	private List<Point2D> uniteChains(List<Point2D> one, List<Point2D> another) {
		assert allEdgesOfChainAreInGraph(one);
		assert allEdgesOfChainAreInGraph(another);
		Deque<Point2D> mutated = (Deque<Point2D>) one;
//		TestCanvas.canvas.draw(one, DrawingChain.withColor(Color.red));
//		TestCanvas.canvas.draw(another, DrawingChain.withColor(Color.blue));
//		TestCanvas.canvas.draw(mutated.getFirst(), DrawingPoint2D.withColorAndSize(Color.red, 6));
//		TestCanvas.canvas.draw(mutated.getLast(), DrawingPoint2D.withColorAndSize(Color.red, 6));
//		TestCanvas.canvas.draw(another.get(0), DrawingPoint2D.withColorAndSize(Color.blue, 4));
//		TestCanvas.canvas.draw(another.get(another.size() - 1), DrawingPoint2D.withColorAndSize(Color.blue, 4));
		assert mutated.size() >= 2;
		assert another.size() >= 2;
		int startIndex;
		int direction;
		int endIndex;
		boolean addToEnd;
		if (mutated.size() == 2) {
			// If one chain contains just 2 point (that is, one edge), then simply return another chain
			// (this is not merely an optimization; not doing so results in errors since with 2 vertices we can't
			// distinguish some of the cases in 'if-else's below).
			return another;
		}
		if (one.get(0).equals(another.get(1)) && one.get(1).equals(another.get(0))) {
			// First edge of one equals first edge of another
			startIndex = 2;
			direction = 1;
			endIndex = another.size();
			addToEnd = false;
		} else if (one.get(0).equals(another.get(another.size() - 2)) && one.get(1).equals(another.get(another.size() - 1))) {
			// First edge of one equals last edge of another
			startIndex = another.size() - 3;
			direction = -1;
			endIndex = -1;
			addToEnd = false;
		} else if (one.get(one.size() - 1).equals(another.get(1)) && one.get(one.size() - 2).equals(another.get(0))) {
			// Last edge of one equals first edge of another
			startIndex = 2;
			direction = 1;
			endIndex = another.size();
			addToEnd = true;
		} else {
			assert one.get(one.size() - 1).equals(another.get(another.size() - 2)) && one.get(one.size() - 2).equals
				(another.get(another.size() - 1));
			// Last edge of one equals last edge of another
			startIndex = another.size() - 3;
			direction = -1;
			endIndex = -1;
			addToEnd = true;
		}
		LinkedList<Point2D> buf = new LinkedList<>(one);
		for (int i = startIndex; i != endIndex; i += direction) {
			if (addToEnd) {
				mutated.addLast(another.get(i));
			} else {
				mutated.addFirst(another.get(i));
			}
		}
//		TestCanvas.canvas.draw((List<Point2D>) mutated, DrawingChain.withColor(Color.green));
		assert allEdgesOfChainAreInGraph((List<Point2D>) mutated);
		return (List<Point2D>) mutated;

	}

	private boolean allEdgesOfChainAreInGraph(List<Point2D> chain) {
		int lastButOne = chain.size() - 1;
		for (int i = 0; i < lastButOne; i++) {
			if (!cityGraph.containsEdge(chain.get(i), chain.get(i + 1))) {
				return false;
			}
		}
		return true;
	}

	private void rememberChainFromIntersection(List<Point2D> chain) {
		assert !chain.isEmpty();
		int lastButOne = chain.size() - 1;
		for (int i = 0; i < lastButOne; i++) {
			Segment2D edge = cityGraph.getEdge(chain.get(i), chain.get(i + 1));
			deferredChains.put(edge, chain);
			assert deferredChains.get(edge).size() <= 2;
		}
	}

	private Collection<Deque<Point2D>> splitIntersectionIntoChains(Point2D vertex) {
		assert cityGraph.degreeOf(vertex) >= 3;
		SortedSet<EdgePair> sorted = new TreeSet<>();
		List<Segment2D> edges = new ArrayList<>(cityGraph.edgesOf(vertex));
		int size = edges.size();
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				EdgePair pair = new EdgePair(edges.get(i), edges.get(j));
				sorted.add(pair);
			}
		}
		// Here are stored edges coming from this vertex that already have the best pair.
		Collection<Segment2D> chainedEdges = new HashSet<>();
		Collection<Deque<Point2D>> answer = new HashSet<>();
		// Add pairs of edges angle between which is closest to Math.PI radians.
		while (!sorted.isEmpty()) {
			EdgePair bestPair = sorted.first();
			sorted.remove(bestPair);
			if (chainedEdges.contains(bestPair.one) || chainedEdges.contains(bestPair.two)) {
				continue;
			}
			if (Math.abs(bestPair.angle - Math.PI) > Math.PI / 2) {
//				If angle is to extreme to be considered a continuous chain
				prohibitJoining(bestPair.one, vertex);
				prohibitJoining(bestPair.two, vertex);
				continue;
			}
			answer.add(new LinkedList<Point2D>() {
				{
					add(bestPair.start);
					add(bestPair.middle);
					add(bestPair.end);
				}
			});
			chainedEdges.add(bestPair.one);
			chainedEdges.add(bestPair.two);
		}
		// There may be edges left without pair. Add all of them as 2-vertex chains.
		for (Segment2D edge : edges) {
			if (!chainedEdges.contains(edge)) {
				answer.add(new LinkedList<Point2D>() {
					{
						add(edge.start);
						add(edge.end);
					}
				});
			}
		}
		return answer;

	}

	/**
	 * Remember that a particular end of a chain should never be joined with another chain (that doesn't prohibit
	 * joining a chain from the other end of the chain).
	 *
	 * @param edge
	 * 	An edge of a chain.
	 * @param vertex
	 * 	A vertex of a chain that has edge {@code edge} coming from it.
	 */
	private void prohibitJoining(Segment2D edge, Point2D vertex) {
		assert edge.end.equals(vertex) || edge.start.equals(vertex);
		joiningProhibited.put(edge, vertex, JOINING_PROHIBITED);
	}

	private boolean isJoiningProhibited(Segment2D edge, Point2D vertex) {
		return joiningProhibited.contains(edge, vertex);
	}

	private void addChain(Deque<Point2D> chain) {
		int size = chain.size();
		assert size >= 2 : size;
		for (Point2D point : chain) {
			usedVertices.add(point);
		}
		markUsedEdges(chain);
		// Combine existing chains on both ends of the new one with the new one
		Point2D oneEnd = chain.getFirst();
		Point2D anotherEnd = chain.getLast();
		if (isEndOfAnyChain(oneEnd) && !isEndOfChainProhibited(chain, oneEnd)) {
			chain = joinChains(chain, oneEnd);
		}
		if (isEndOfAnyChain(anotherEnd) && !isEndOfChainProhibited(chain, anotherEnd)) {
			chain = joinChains(chain, anotherEnd);
		}
		ends.put(chain.getFirst(), chain);
		ends.put(chain.getLast(), chain);
	}

	private boolean isEndOfChainProhibited(Deque<Point2D> chain, Point2D end) {
		List<Point2D> asList = (List<Point2D>) chain;
		Segment2D edge = cityGraph.getEdge(asList.get(0), asList.get(1));
		if (asList.get(0).equals(end) && isJoiningProhibited(edge, end)) {
			return true;
		}
		int lastIndex = asList.size() - 1;
		edge = cityGraph.getEdge(asList.get(lastIndex), asList.get(lastIndex - 1));
		if (asList.get(lastIndex).equals(end) && isJoiningProhibited(edge, end)) {
			return true;
		}
		return false;
	}

	private boolean isEndOfAnyChain(Point2D oneEnd) {
		return ends.containsKey(oneEnd);
	}

	private void markUsedEdges(Deque<Point2D> chain) {
		Point2D previous = null;
		for (Point2D current : chain) {
			if (previous == null) {
				previous = current;
				continue;
			}
			Segment2D edge = cityGraph.getEdge(previous, current);
			assert edge != null;
			usedEdges.add(edge);
			previous = current;
		}
	}


	/**
	 * Joins 2 chains that have vertex {@code end} in common. For each chain, end may be either its first or last
	 * vertex.
	 *
	 * @param chain
	 * 	A chain that starts or ends with {@code end}. This collection may be mutated inside this method.
	 * @param end
	 * 	A point where both chaits start or end.
	 * @return
	 */
	private Deque<Point2D> joinChains(Deque<Point2D> chain, Point2D end) {
		Deque<Point2D> anotherChain = ends.get(end);
		if (anotherChain != chain) {
			// If chain doesn't loop (end and start are not the same vertex)
			ends.remove(anotherChain.getFirst());
			ends.remove(anotherChain.getLast());
			if (anotherChain.getFirst().equals(end)) {
				if (chain.getFirst().equals(end)) {
					chain.removeFirst();
					while (!chain.isEmpty()) {
						anotherChain.addFirst(chain.pollFirst());
					}
				} else {
					assert chain.getLast().equals(end);
					chain.removeLast();
					while (!chain.isEmpty()) {
						anotherChain.addFirst(chain.pollLast());
					}
				}
			} else {
				assert anotherChain.getLast().equals(end);
				if (chain.getFirst().equals(end)) {
					chain.removeFirst();
					while (!chain.isEmpty()) {
						anotherChain.addLast(chain.pollFirst());
					}
				} else {
					assert chain.getLast().equals(end);
					chain.removeLast();
					while (!chain.isEmpty()) {
						anotherChain.addLast(chain.pollLast());
					}
				}
			}
			return anotherChain;
		}
		return chain;
	}

	private Deque<Point2D> find2DegreeChain(Point2D vertex) {
		assert cityGraph.degreeOf(vertex) <= 2;
		Deque<Point2D> answer = new LinkedList<>();
		answer.addFirst(vertex);
		boolean addFirst = true;
		for (Segment2D edge : cityGraph.edgesOf(vertex)) {
			Point2D nextNeighbor = edge.start.equals(vertex) ? edge.end : edge.start;
			Point2D currentVertex = edge.start.equals(vertex) ? edge.start : edge.end;
			while (cityGraph.degreeOf(nextNeighbor) == 2) {
				if (addFirst) {
					answer.addFirst(nextNeighbor);
				} else {
					answer.addLast(nextNeighbor);
				}
				for (Segment2D nextEdge : cityGraph.edgesOf(nextNeighbor)) {
					if (!nextEdge.start.equals(nextNeighbor) && !nextEdge.start.equals(currentVertex)) {
						currentVertex = nextNeighbor;
						nextNeighbor = nextEdge.start;
						break;
					}
					if (!nextEdge.end.equals(nextNeighbor) && !nextEdge.end.equals(currentVertex)) {
						currentVertex = nextNeighbor;
						nextNeighbor = nextEdge.end;
						break;
					}
				}
				if (nextNeighbor == vertex) {
					// If we did a full circle to the vertex we started with,
					// then break the loop
					break;
				}
			}
			if (cityGraph.degreeOf(nextNeighbor) == 1) {
				if (addFirst) {
					answer.addFirst(nextNeighbor);
				} else {
					answer.addLast(nextNeighbor);
				}
			}
			addFirst = !addFirst;
		}
		return answer;
	}

	private static class EdgePair implements Comparable<EdgePair> {
		public static final boolean ANY_BOOLEAN_VALUE = true;
		/**
		 * Angle in radians between segments coming from the same Point2D.
		 * Value of this field may be {@code > Math.PI} angle or the corresponding {@code < Math.PI} angle (depending
		 * on whether you go clockwise or counterclockwise to measure angle),
		 * it doesn't matter for {@link #compareTo(StreetsDetector.EdgePair)}.
		 */
		private final double angle;
		private final Point2D start;
		private final Point2D end;
		private final Point2D middle;
		private final Segment2D one;
		private final Segment2D two;

		private EdgePair(Segment2D one, Segment2D two) {
			if (one.start.equals(two.start)) {
				this.start = one.end;
				this.middle = one.start;
				this.end = two.end;
			} else if (one.start.equals(two.end)) {
				this.start = one.end;
				this.middle = one.start;
				this.end = two.start;
			} else if (one.end.equals(two.start)) {
				this.start = one.start;
				this.middle = one.end;
				this.end = two.end;
			} else {
				assert one.end.equals(two.end);
				this.start = one.start;
				this.middle = one.end;
				this.end = two.start;
			}
			this.one = one;
			this.two = two;
			double angle = Vectors2D.angleBetweenVectors(
				new double[]{start.x - middle.x, start.y - middle.y},
				new double[]{end.x - middle.x, end.y - middle.y},
				ANY_BOOLEAN_VALUE
			);
			if (angle > Math.PI) {
				angle = Math.PI * 2 - angle;
			}
			assert angle > 0 && angle < Math.PI + Vectors2D.EPSILON;
			this.angle = angle;
		}

		@Override
		public int compareTo(EdgePair o) {
			double diff = Math.abs(angle - Math.PI) - Math.abs(o.angle - Math.PI);
			if (diff > 0) {
				return 1;
			}
			if (diff < 0) {
				return -1;
			}
			double slope = start.angleTo(end);
			double anotherSlope = o.start.angleTo(o.end);
			if (slope > anotherSlope) {
				return 1;
			}
			if (slope < anotherSlope) {
				return -1;
			}
			return 0;
		}
	}
}
