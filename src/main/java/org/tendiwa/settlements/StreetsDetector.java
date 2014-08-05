package org.tendiwa.settlements;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.*;

public final class StreetsDetector {
	private final UndirectedGraph<Point2D, Segment2D> cityGraph;
	private final Map<Point2D, Deque<Point2D>> ends;
	private final Collection<Point2D> inChains;

	public static Set<List<Point2D>> detectStreets(UndirectedGraph<Point2D, Segment2D> cityGraph) {
		return new StreetsDetector(cityGraph).compute();
	}

	private StreetsDetector(UndirectedGraph<Point2D, Segment2D> cityGraph) {
		this.cityGraph = cityGraph;
		ends = new HashMap<>();

		inChains = new HashSet<>(cityGraph.vertexSet().size());
	}

	private Set<List<Point2D>> compute() {

		for (Point2D vertex : cityGraph.vertexSet()) {
			if (inChains.contains(vertex)) {
				continue;
			}
			if (cityGraph.degreeOf(vertex) <= 2) {
				Deque<Point2D> chain = find2DegreeChain(vertex);
				if (chain.size() >= 2) {
					addChain(chain);
				}
			} else if (cityGraph.degreeOf(vertex) >= 3) {
				for (Deque<Point2D> chain : splitIntersectionIntoChains(vertex)) {
					addChain(chain);
				}
			}
		}
		Set<List<Point2D>> answer = new LinkedHashSet<>();
		for (Deque<Point2D> chain : ends.values()) {
			// LinkedList implements both Deque and List interfaces
			answer.add((List<Point2D>) chain);
		}
		return answer;
	}

	private Collection<Deque<Point2D>> splitIntersectionIntoChains(Point2D vertex) {
		assert cityGraph.degreeOf(vertex) >= 3;
		SortedSet<EdgePair> sorted = new TreeSet<>();
		List<Segment2D> edges = new ArrayList<>(cityGraph.edgesOf(vertex));
		int size = edges.size();
		//
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				EdgePair pair = new EdgePair(edges.get(i), edges.get(j));
				boolean added = sorted.add(pair);
			}
		}
		Collection<Segment2D> usedEdges = new HashSet<>();
		Collection<Deque<Point2D>> answer = new HashSet<>();
		// Add pairs of edges angle between which is closest to Math.PI radians.
		while (!sorted.isEmpty()) {
			EdgePair bestPair = sorted.last();
			sorted.remove(bestPair);
			if (usedEdges.contains(bestPair.one) || usedEdges.contains(bestPair.two)) {
				continue;
			}
			answer.add(new LinkedList<Point2D>() {
				{
					add(bestPair.start);
					add(bestPair.middle);
					add(bestPair.end);
				}
			});
			usedEdges.add(bestPair.one);
			usedEdges.add(bestPair.two);
		}
		// There may be edges left without pair. Add all of them as 2-vertex chains.
		for (Segment2D edge : edges) {
			if (!usedEdges.contains(edge)) {
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
			inChains.add(point);
		}
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
		/**
		 * This field is necessary to be used in the {@link #compareTo(org.tendiwa.settlements.StreetsDetector.EdgePair)}
		 * method to differentiate two distinct pairs with the same angle.
		 */
		private final double slope;

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
			this.angle = Vectors2D.angleBetweenVectors(
				new double[]{middle.x - start.x, middle.y - start.y},
				new double[]{end.x - middle.x, end.y - middle.y},
				ANY_BOOLEAN_VALUE
			);
			this.slope = start.angleTo(end);
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
			if (slope > o.slope) {
				return 1;
			}
			if (slope < o.slope) {
				return -1;
			}
//			throw new RuntimeException();
			return 0;
		}
	}
}
