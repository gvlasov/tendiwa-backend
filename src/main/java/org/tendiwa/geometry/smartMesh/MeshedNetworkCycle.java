package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.*;
import org.tendiwa.geometry.graphs2d.Cycle2D;
import org.tendiwa.graphs.graphs2d.SplittableGraph2D;

import java.util.Iterator;
import java.util.Set;

interface MeshedNetworkCycle extends Cycle2D, SplittableGraph2D, OrientedPolygon {
	// TODO: bisector is not deviated
	default Ray deviatedAngleBisector(Point2D bisectorStart, boolean inward) {
		Set<Segment2D> adjacentEdges = edgesOf(bisectorStart);
		assert adjacentEdges.size() == 2;
		Iterator<Segment2D> iterator = adjacentEdges.iterator();

		Segment2D previous = iterator.next();
		if (!isClockwise(previous)) {
			previous = previous.reverse();
		}
		Segment2D next = iterator.next();
		if (!isClockwise(next)) {
			next = next.reverse();
		}

		if (next.end().equals(previous.start())) {
			Segment2D buf = previous;
			previous = next;
			next = buf;
		}
		Segment2D bisectorSegment =
			new BasicSegment2D(
				bisectorStart,
				bisectorStart.add(
					new BasicBisector(
						next.asVector(),
						previous.asVector().reverse()
					).asInbetweenVector()
						.multiply(inward ? 1 : -1)
				)
			);
		return new Ray(
			bisectorStart,
			bisectorSegment.start().angleTo(bisectorSegment.end())
		);
	}

	default Ray normal(SplitSegment2D segmentWithPoint, boolean inward) {
		Ray ray = segmentWithPoint.leftNormal();
		return isClockwise(segmentWithPoint.originalSegment()) ^ inward ?
			ray :
			ray.inverse();
	}
}