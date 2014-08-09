package org.tendiwa.settlements;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.*;

public final class StreetsDetector {
	private final UndirectedGraph<Point2D, Segment2D> cityGraph;
	private final Map<Point2D, Deque<Point2D>> ends = new HashMap<>();
	private final Multimap<Segment2D, List<Point2D>> deferredEnds = HashMultimap.create();
	private final Collection<Point2D> usedVertices;
	private final HashSet<Segment2D> usedEdges = new HashSet<>();

	public static Set<List<Point2D>> detectStreets(UndirectedGraph<Point2D, Segment2D> cityGraph) {
		return new StreetsDetector(cityGraph).compute();
	}

	private StreetsDetector(UndirectedGraph<Point2D, Segment2D> cityGraph) {
		this.cityGraph = cityGraph;
		this.usedVertices = new HashSet<>(cityGraph.vertexSet().size());
	}

	private Set<List<Point2D>> compute() {
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
					rememberChainFromIntersection((List<Point2D>) chain);
				}
			}
		}
		assembleDeferredChains();

		Set<List<Point2D>> answer = new LinkedHashSet<>();
		for (Deque<Point2D> chain : ends.values()) {
			// LinkedList implements both Deque and List interfaces
			answer.add((List<Point2D>) chain);
		}
		return answer;
	}

	private void assembleDeferredChains() {
	}

	private void rememberChainFromIntersection(List<Point2D> chain) {
		int lastButOne = chain.size() - 1;
		for (int i = 0; i < lastButOne; i++) {
			deferredEnds.put(cityGraph.getEdge(chain.get(i), chain.get(i + 1)), chain);
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
			if (bestPair.angle < Math.PI / 2) {
//				If angle is to extreme to be considered a continuous chain
//				continue;
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
		if (ends.containsKey(oneEnd)) {
			chain = joinChains(chain, oneEnd);
		}
		if (ends.containsKey(anotherEnd)) {
			chain = joinChains(chain, anotherEnd);
		}
		ends.put(chain.getFirst(), chain);
		ends.put(chain.getLast(), chain);
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

	private Deque<Point2D> joinChains(Deque<Point2D> chain, Point2D end) {
		Deque<Point2D> anotherChain = ends.get(end);
		if (anotherChain != chain) {
			// If chain doesn't loop (end and start are not the same vertex)
			ends.remove(anotherChain.getFirst());
			ends.remove(anotherChain.getLast());
			if (anotherChain.getFirst().equals(end)) {
				if (chain.getFirst().equals(end)) {
					while (!chain.isEmpty()) {
						anotherChain.addFirst(chain.pollFirst());
					}
				} else {
					assert chain.getLast().equals(end);
					while (!chain.isEmpty()) {
						anotherChain.addFirst(chain.pollLast());
					}
				}
			} else {
				assert anotherChain.getLast().equals(end);
				if (chain.getFirst().equals(end)) {
					while (!chain.isEmpty()) {
						anotherChain.addLast(chain.pollFirst());
					}
				} else {
					assert chain.getLast().equals(end);
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
		 * it doesn't matter for {@link #compareTo(org.tendiwa.settlements.StreetsDetector.EdgePair)}.
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
