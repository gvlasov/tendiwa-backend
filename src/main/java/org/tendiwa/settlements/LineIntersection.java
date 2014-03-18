package org.tendiwa.settlements;

import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

public class LineIntersection {
    final boolean intersects;
    final double r;
    final double s;

    LineIntersection(Point2D sourceNode, Point2D targetPoint, Line2D segment) {
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
        System.out.println("r line intersection " + r);
        s = ((ca.y * ab.x) - (ca.x * ab.y)) / denom;
        intersects = !(r == 0 && s == 0);
    }

    Point2D getIntersectionPoint(Point2D sourceNode, Point2D targetPoint) {
        return new Point2D(
                sourceNode.x + (targetPoint.x - sourceNode.x) * r,
                sourceNode.y + (targetPoint.y - sourceNode.y) * r
        );
    }
}
