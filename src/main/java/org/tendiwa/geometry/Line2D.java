package org.tendiwa.geometry;

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

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
