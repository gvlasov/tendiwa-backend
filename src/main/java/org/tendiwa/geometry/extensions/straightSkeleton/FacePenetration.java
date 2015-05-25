package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawablePoint2D;
import org.tendiwa.drawing.extensions.DrawablePolygon;
import org.tendiwa.drawing.extensions.DrawableSegment2D;
import org.tendiwa.geometry.ParallelSegment;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.StraightSkeletonFace;

import java.awt.Color;
import java.util.Iterator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

final class FacePenetration implements Iterator<Point2D> {

	private final Queue<Point2D> queue;

	public FacePenetration(StraightSkeletonFace face, Penetrable front) {
//		TestCanvas.canvas.draw(faceFront(face), DrawingSegment2D.withColorDirected(Color.cyan, 1));
		queue = new PriorityQueue<>(Point2D::compareCoordinatesLinewise);
		Segment2D intruded = intrudeFaceFront(face, front.depth());
		face.toSegments().stream()
			.map(segment -> front.obtainIntersectionPoint(segment, intruded))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.forEach(queue::add);

		boolean ok = queue.size() % 2 == 0;
		if (!ok) {
			new TestCanvas(4, rectangle(100, 100))
				.draw(
					new DrawablePolygon.Thin(face, Color.red)
					.andThen(new DrawablePoint2D.Circle(queue.peek(),Color.blue, 1))
					.andThen(new DrawableSegment2D.Thin(intruded, Color.green))
				);
			assert Boolean.TRUE;
		}
		assert ok : queue.size();
	}

	private Segment2D intrudeFaceFront(StraightSkeletonFace face, double depth) {
//		TestCanvas.canvas.draw(new Segment2D(face.get(0), face.get(face.size() - 1)), DrawingSegment2D.withColorThin(Color
//			.magenta));
		return new ParallelSegment(face.front(), depth, true);
	}

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public Point2D next() {
		return queue.poll();
	}
}
