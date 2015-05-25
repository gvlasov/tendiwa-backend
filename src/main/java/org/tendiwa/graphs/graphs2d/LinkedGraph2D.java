package org.tendiwa.graphs.graphs2d;

import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.collections.DoublyLinkedNode;
import org.tendiwa.collections.SuccessiveTuples;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Segment2D;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.tendiwa.collections.Collectors.toImmutableList;

/**
 * This class is used for implementing {@link Polygon}s that need O(1) edge splitting time.
 */
final class LinkedGraph2D extends SimpleGraph<DoublyLinkedNode<Point2D>, Segment2D> {
	private final Map<Point2D, DoublyLinkedNode<Point2D>> pointToNode;

	public LinkedGraph2D(Polygon polygon) {
		super((a, b) -> polygon.edge(a.getPayload(), b.getPayload()));
		// *2 because we expect some sides to be split
		this.pointToNode = new LinkedHashMap<>(polygon.size() * 2);
		polygon.forEach(this::addVertexAndNode);
		polygon.toSegments().forEach(
			segment -> addEdge(
				getNode(segment.start()),
				getNode(segment.end())
			)
		);
		SuccessiveTuples.forEachLooped(
			polygon,
			this::connectNodesOfPoints
		);
	}

	private void connectNodesOfPoints(Point2D a, Point2D b) {
		DoublyLinkedNode<Point2D> nodeA = getNode(a);
		DoublyLinkedNode<Point2D> nodeB = getNode(b);
		nodeA.connectWithNext(nodeB);
		nodeB.connectWithPrevious(nodeA);
	}

	private DoublyLinkedNode<Point2D> getNode(Point2D point) {
		assert pointToNode.containsKey(point);
		return pointToNode.get(point);
	}

	private void addVertexAndNode(Point2D point) {
		DoublyLinkedNode<Point2D> node = new DoublyLinkedNode<>(point);
		pointToNode.put(point, node);
		addVertex(node);
	}

	void splitEdge(CutSegment2D cutSegment) {
		new CutInsertion(cutSegment).insert();
	}

	Segment2D getEdgeBetweenPoints(Point2D sourceVertex, Point2D targetVertex) {
		return getEdge(
			getNode(sourceVertex),
			getNode(targetVertex)
		);
	}

	boolean containsEdgeBetweenPoints(Point2D sourceVertex, Point2D targetVertex) {
		return containsEdge(
			getNode(sourceVertex),
			getNode(targetVertex)
		);
	}

	boolean containsVertexAtPoint(Point2D point) {
		return containsVertex(getNode(point));
	}

	Set<Segment2D> edgesOfPoint(Point2D vertex) {
		return edgesOf(getNode(vertex));
	}

	Set<Point2D> pointSet() {
		return pointToNode.keySet();
	}

	private class CutInsertion {

		private final DoublyLinkedNode<Point2D> start;
		private final DoublyLinkedNode<Point2D> end;
		private final CutSegment2D cutSegment;

		CutInsertion(CutSegment2D cutSegment) {
			this.cutSegment = cutSegment;
			this.start = getNode(cutSegment.originalSegment().start());
			this.end = getNode(cutSegment.originalSegment().end());
		}

		private void insert() {
			cutSegment.pointStream().forEach(LinkedGraph2D.this::addVertexAndNode);
			List<Segment2D> consecutiveSegments = cutSegment.segmentStream().collect(toImmutableList());
//			unlinkNodes();
			connectUnlinkedAndNewNodes(consecutiveSegments);
		}

		private void connectUnlinkedAndNewNodes(
			List<Segment2D> consecutiveSegments
		) {
			DoublyLinkedNode<Point2D> current = start;
			int segmentIndex = 0;
			do {
				DoublyLinkedNode<Point2D> next = getNode(
					consecutiveSegments.get(segmentIndex).anotherEnd(current.getPayload())
				);
				current.connectWithNext(next);
				next.connectWithPrevious(current);
				addEdge(current, next);
				current = next;
			} while (current != end);
		}

		// TODO: Maybe this can be a method of DoublyLinkedNode?
		private void unlinkNodes() {
			if (start.getNext() == end) {
				assert end.getPrevious() == start;
				start.connectWithNext(null);
				end.connectWithPrevious(null);
			} else {
				assert start.getPrevious() == end && end.getNext() == start;
				start.connectWithPrevious(null);
				end.connectWithPrevious(null);
			}
		}
	}

}
