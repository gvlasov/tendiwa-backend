package org.tendiwa.geometry;

public interface Point2D extends Vector2D {

	public default double angleTo(Vector2D end) {
		double angle = Math.atan2(end.y() - y(), end.x() - x());
		if (angle < 0) {
			angle = Math.PI * 2 + angle;
		}
		return angle;
	}


	public default double distanceTo(Point2D end) {
		return Math.sqrt((end.x() - this.x()) * (end.x() - this.x()) + (end.y() - this.y()) * (end.y() - this.y()));
	}

	public default double squaredDistanceTo(Point2D end) {
		return (end.x() - this.x()) * (end.x() - this.x()) + (end.y() - this.y()) * (end.y() - this.y());
	}
	@Override
	default Point2D add(Vector2D vector) {
		return new BasicPoint2D(x()+vector.x(), y()+vector.y());
	}

	public default BasicCell toCell() {
		return new BasicCell((int) Math.round(x()), (int) Math.round(y()));
	}


	public default double distanceToLine(Segment2D line) {
		double dx = line.end().x() - line.start().x();
		double dy = line.end().y() - line.start().y();
		// TODO: Replace with line.length()
		double normalLength = Math.sqrt(dx * dx + dy * dy);
		return Math.abs(
			(x() - line.start().x()) * dy
				- (y() - line.start().y()) * dx
		) / normalLength;
	}


	public static int compareCoordinatesLinewise(Point2D a, Point2D b) {
		int signum = (int) Math.signum(a.x() - b.x());
		if (signum == 0) {
			return (int) Math.signum(a.y() - b.y());
		}
		return signum;
	}

	public default Segment2D segmentTo(Point2D anotherEnd) {
		return new BasicSegment2D(this, anotherEnd);
	}
}
