package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.Point2D;

import java.util.Comparator;

/**
 * Compares {@link Point2D} in the same order as we read: top-left are less, bottom-right are bigger.
 */
public class Point2DRowComparator implements Comparator<Point2D> {
    private static final Point2DRowComparator instance = new Point2DRowComparator();

    @Override
    public int compare(Point2D o1, Point2D o2) {
        if (o1.y != o2.y) {
            return (int) (o1.y - o2.y);
        } else {
            return (int) (o1.x - o2.x);
        }
    }

    public static Point2DRowComparator getInstance() {
        return instance;
    }
}
