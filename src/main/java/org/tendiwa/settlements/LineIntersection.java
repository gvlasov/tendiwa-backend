package org.tendiwa.settlements;

import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

public class LineIntersection {
    public final boolean intersects;
    /**
     * Relative distance from {@code sourceNode} to {@code targetPoint}, 0.0 means intersection is at {@code
     * sourceNode}, 1.0 means it's at target point. Note that {@code r} is computed even if lines are parallel,
     * in which case {@code r == Infinity}.
     */
    public final double r;
    final double s;

    public LineIntersection(Point2D sourceNode, Point2D targetPoint, Line2D segment) {
        Point2D ab = new Point2D(
                targetPoint.x - sourceNode.x,
                targetPoint.y - sourceNode.y
        );
        Point2D cd = new Point2D(
                segment.end.x - segment.start.x,
                segment.end.y - segment.start.y
        );
        double denom = (ab.x * cd.y) - (ab.y * cd.x);
        Point2D ca = new Point2D(
                sourceNode.x - segment.start.x,
                sourceNode.y - segment.start.y
        );
        r = ((ca.y * cd.x) - (ca.x * cd.y)) / denom;
        s = ((ca.y * ab.x) - (ca.x * ab.y)) / denom;
        intersects = (denom != 0) && !(r == 0 && s == 0);
    }

    public Point2D getIntersectionPoint(Point2D sourceNode, Point2D targetPoint) {
        return new Point2D(
                sourceNode.x + (targetPoint.x - sourceNode.x) * r,
                sourceNode.y + (targetPoint.y - sourceNode.y) * r
        );
    }
}
