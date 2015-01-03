package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.collections.DoublyLinkedNode;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;

import java.util.*;

final class ShrinkedFront {
	private final LinkedHashMap<Point2D, DoublyLinkedNode<Point2D>> pointsToNodes;

	ShrinkedFront(int numberOfPointsEstimation) {
		pointsToNodes = new LinkedHashMap<>(numberOfPointsEstimation);
	}

	void add(Point2D point1, Point2D point2) {
		DoublyLinkedNode<Point2D> node1 = obtainNode(point1);
		DoublyLinkedNode<Point2D> node2 = obtainNode(point2);
		node1.uniteWith(node2);
	}

	/**
	 * Returns the existing {@link org.tendiwa.collections.DoublyLinkedNode} for a {@link Point2D} if one exists, or
	 * creates a new one.
	 *
	 * @param point
	 * 	A point that is payload for a node.
	 * @return A node with {@code point} as payload.
	 */
	private DoublyLinkedNode<Point2D> obtainNode(Point2D point) {
		if (pointsToNodes.containsKey(point)) {
			return pointsToNodes.get(point);
		} else {
			DoublyLinkedNode<Point2D> newNode = new DoublyLinkedNode<>(point);
			pointsToNodes.put(point, newNode);
			return newNode;
		}
	}

	ImmutableSet<Polygon> constructPolygons() {
		ImmutableSet.Builder<Polygon> builder = ImmutableSet.builder();
		while (!pointsToNodes.isEmpty()) {
			DoublyLinkedNode<Point2D> node = pointsToNodes.values().stream().findAny().get();
			List<Point2D> points = new ArrayList<>();
			node.forEach(points::add);
			Polygon polygon = new Polygon(points);
			builder.add(polygon);
			polygon.forEach(pointsToNodes::remove);
		}
		return builder.build();
	}
}
