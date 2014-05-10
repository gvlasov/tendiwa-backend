package org.tendiwa.geometry;

import org.tendiwa.settlements.LineIntersection;

/**
 * An immutable line
 */
public class Line2D {
    public final Point2D start;
    public final Point2D end;


    public Line2D(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "Line2D{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    public double length() {
        return start.distanceTo(end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line2D line2D = (Line2D) o;

        if (end != null ? !end.equals(line2D.end) : line2D.end != null) return false;
        if (start != null ? !start.equals(line2D.start) : line2D.start != null) return false;

        return true;
    }

    /**
     * Finds a point of intersection between this line and another line.
     *
     * @param line
     *         Another line.
     * @return A Point2D where these two lines intersect, or null if lines don't intersect.
     * @see #intersects(Line2D)
     */
    public Point2D intersection(Line2D line) {
        LineIntersection lineIntersection = new LineIntersection(start, end, line);
        if (!lineIntersection.intersects) {
            return null;
        }
        return lineIntersection.getIntersectionPoint(start, end);
    }

    /**
     * Checks if this line intersects another line. This is less expensive than finding the intersection point with
     * {@link #intersection(Line2D)}.
     *
     * @param line
     *         Another line.
     * @return true if lines intersect, false otherwise.
     * @see #intersection(Line2D)
     */
    public boolean intersects(Line2D line) {
        return new LineIntersection(start, end, line).intersects;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
