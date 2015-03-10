package org.tendiwa.geometry;

/**
 * An immutable point with double coordinates.
 * <p>
 * Implements {@link Vector2D} to operate on Point2D as on a 2-dimensional vector.
 */
public class Point2D implements Vector2D {
	public final double x;
	public final double y;

	public Point2D(double x, double y) {
		assert !Double.isNaN(x);
		assert !Double.isNaN(y);
		this.x = x;
		this.y = y;
	}

	public static Point2D of(Vector2D vector) {
		return new Point2D(vector.getX(), vector.getY());
	}


	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Point2D point2D = (Point2D) o;

		if (Double.compare(point2D.x, x) != 0) return false;
		if (Double.compare(point2D.y, y) != 0) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Finds Chebyshev distance between this cell and another cell.
	 * <p>
	 * Finding Chebyshev distance is much cheaper than finding Euclidean distance with {@link #distanceTo(Point2D)}.
	 *
	 * @param point
	 * 	Another point.
	 * @return Chebyshev distance between two cells.
	 */
	public double chebyshovDistanceTo(Point2D point) {
		return Math.max(Math.abs(point.x - x), Math.abs(point.y - y));
	}

	public double angleTo(Point2D end) {
		double angle = Math.atan2(end.y - y, end.x - x);
		if (angle < 0) {
			angle = Math.PI * 2 + angle;
		}
		return angle;
	}

	@Override
	public String toString() {
		return "{" +
			x +
			":" + y +
			'}';
	}

	public double distanceTo(Point2D end) {
		return Math.sqrt((end.x - this.x) * (end.x - this.x) + (end.y - this.y) * (end.y - this.y));
	}

	public double squaredDistanceTo(Point2D end) {
		return (end.x - this.x) * (end.x - this.x) + (end.y - this.y) * (end.y - this.y);
	}

	public Cell toCell() {
		return new Cell((int) Math.round(x), (int) Math.round(y));
	}

	/**
	 * Creates a new point moved from this one by {dx:dy}.
	 *
	 * @param dx
	 * 	Change in x coordinate.
	 * @param dy
	 * 	Change in y coordinate.
	 * @return A new Point2D.
	 */
	public Point2D moveBy(double dx, double dy) {
		return new Point2D(x + dx, y + dy);
	}


	public double distanceToLine(Segment2D line) {
		double dx = line.end.x - line.start.x;
		double dy = line.end.y - line.start.y;
		// TODO: Replace with line.length()
		double normalLength = Math.sqrt(dx * dx + dy * dy);
		return Math.abs(
			(x - line.start.x) * dy
				- (y - line.start.y) * dx
		) / normalLength;
	}

	@Override
	public Point2D subtract(Vector2D point) {
		return new Point2D(x - point.getX(), y - point.getY());
	}

	/**
	 * Sums two vectors.
	 *
	 * @param vector
	 * 	An
	 * @return
	 */
	@Override
	public Point2D add(Vector2D vector) {
		return new Point2D(x + vector.getX(), y + vector.getY());
	}

	/**
	 * Distance from {0:0} to {@link #x}:{@link #y}.
	 *
	 * @return Distance from the beginning of coordinate space to this point.
	 */
	@Override
	public double magnitude() {
		return Math.sqrt(x * x + y * y);
	}

	@Override
	public Point2D divide(double scalar) {
		return new Point2D(x / scalar, y / scalar);
	}

	@Override
	public Point2D normalize() {
		return divide(magnitude());
	}

	@Override
	public Point2D multiply(double magnitude) {
		return new Point2D(x * magnitude, y * magnitude);
	}

	public static int compareCoordinatesLinewise(Point2D a, Point2D b) {
		int signum = (int) Math.signum(a.x - b.x);
		if (signum == 0) {
			return (int) Math.signum(a.y - b.y);
		}
		return signum;
	}

	public Segment2D segmentTo(Point2D anotherEnd) {
		return new Segment2D(this, anotherEnd);
	}
}
