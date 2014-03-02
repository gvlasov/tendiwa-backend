package org.tendiwa.geometry;

/**
 * An immutable point with double coordinates.
 */
public class Point2D implements Position2D {
public final double x;
public final double y;

public Point2D(double x, double y) {
	this.x = x;
	this.y = y;
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

public double angleTo(Point2D end) {
	double angle = Math.atan2(end.y - y, end.x - x);
	if (angle < 0) {
		angle = Math.PI * 2 + angle;
	}
	return angle;
}

@Override
public String toString() {
	return "Point2D{" +
		"x=" + x +
		", y=" + y +
		'}';
}

public double distanceTo(Point2D end) {
	return Math.sqrt(Math.pow(end.x - this.x, 2) + Math.pow(end.y - this.y, 2));
}
}
