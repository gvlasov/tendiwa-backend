package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPolygon;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.Iterator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

final class FacePenetration implements Iterator<Point2D> {

	private final Queue<Point2D> queue;

	public FacePenetration(Polygon face, Penetrable front) {
		TestCanvas.canvas.draw(faceFront(face), DrawingSegment2D.withColorDirected(Color.cyan, 1));
		queue = new PriorityQueue<>(Point2D::compareCoordinatesLinewise);
		Segment2D intruded = intrudeFaceFront(face, front.depth());
		face.toSegments().stream()
			.map(segment -> front.obtainItersectionPoint(segment, intruded))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.forEach(queue::add);

		assert queue.size() % 2 == 0 : queue.size();
	}

	private Segment2D intrudeFaceFront(Polygon face, double depth) {
//		TestCanvas.canvas.draw(new Segment2D(face.get(0), face.get(face.size() - 1)), DrawingSegment2D.withColorThin(Color
//			.magenta));
		return faceFront(face).createParallelSegment(depth, true);
	}

	public Segment2D faceFront(Polygon face) {
		return new Segment2D(
			face.get(0),
			face.get(face.size() - 1)
		);
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
