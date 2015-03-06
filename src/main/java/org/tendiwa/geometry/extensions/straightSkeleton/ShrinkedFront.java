package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import org.tendiwa.collections.DoublyLinkedNode;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.RayIntersection;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.*;

final class ShrinkedFront implements Penetrable {

	private final double depth;
	public List<Segment2D> edges;
	private final LinkedHashMap<Point2D, DoublyLinkedNode<Point2D>> pointsToNodes;
	private final BiMap<Point2D, Segment2D> intersectionsOnSegments;

	/**
	 * @param faces
	 * 	Clockwise polygons partitioning a compound polygon. For each partitioning polygon, its last edge is the only
	 * 	edge touching the perimeter of the compound polygon.
	 * @param depth
	 * 	How much to intrude the polygon.
	 */
	ShrinkedFront(Set<Polygon> faces, double depth) {
		this.depth = depth;
		// Minimum possible number of points on a front is faces.size(), so we pick a value twice as big. That should
		// be enough for most cases and not too much.
		this.pointsToNodes = new LinkedHashMap<>(faces.size() * 2);
		this.intersectionsOnSegments = HashBiMap.create();
		faces.stream()
			.map(face -> new FacePenetration(face, this))
			.forEach(this::integrate);
	}

	void integrate(Iterator<Point2D> penetration) {
		while (penetration.hasNext()) {
			add(
				// Get two consecutive intersection points
				penetration.next(),
				penetration.next()
			);
		}
	}

	@Override
	public void add(Point2D point1, Point2D point2) {
		TestCanvas.canvas.draw(new Segment2D(point1, point2), DrawingSegment2D.withColorThin(Color.orange));
		DoublyLinkedNode<Point2D> node1 = obtainNode(point1);
		DoublyLinkedNode<Point2D> node2 = obtainNode(point2);
		node1.uniteWith(node2);
	}

	@Override
	public Optional<Point2D> obtainItersectionPoint(Segment2D inner, Segment2D intruded) {
		Segment2D reverse = inner.reverse();
		if (intersectionsOnSegments.containsValue(reverse)) {
			return Optional.of(getExistingIntersectionPoint(reverse));
		} else {
			if (intersectionsOnSegments.containsValue(inner)) {
				TestCanvas.canvas.draw(inner, DrawingSegment2D.withColorDirected(Color.white, 1));
				assert false;
			}
			RayIntersection intersection = new RayIntersection(inner, intruded);
			if (intersection.r > 0 && intersection.r < 1) {
				Point2D intersectionPoint = new RayIntersection(intruded, inner).commonPoint();
				intersectionsOnSegments.put(intersectionPoint, inner);
				return Optional.of(intersectionPoint);
			}
		}
		return Optional.empty();
	}

	@Override
	public double depth() {
		return depth;
	}

	private Point2D getExistingIntersectionPoint(Segment2D reverse) {
		return intersectionsOnSegments
			.inverse()
			.get(reverse);
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

	public ImmutableSet<Polygon> polygons() {
		ImmutableSet.Builder<Polygon> builder = ImmutableSet.builder();
		while (!pointsToNodes.isEmpty()) {
			DoublyLinkedNode<Point2D> node = pointsToNodes.values().stream()
				.findFirst()
				.get();
			List<Point2D> points = new ArrayList<>();
			node.forEach(points::add);
			Polygon polygon = new Polygon(points);
			builder.add(polygon);
			polygon.forEach(pointsToNodes::remove);
		}
		return builder.build();
	}
}
