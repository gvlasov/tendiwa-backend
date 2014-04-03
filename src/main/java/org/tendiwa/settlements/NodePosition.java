package org.tendiwa.settlements;

import org.tendiwa.geometry.Point2D;

/**
 * [Kelly 4.3.3.3]
 * <p>
 *
 * @return
 */
public class NodePosition {
    /**
     * Position of a projection of point {@code point} to a line.
     * <p>
     * r = 0 means point = a, r = 1 means point = b.
     */
    public final double r;
    /**
     * Position of a point {@code point} relative to a perpendicular of ab.
     * <p>
     * s > 0 means point is to the right from a line, s < 0 meant point is to the left from a line.
     */
    public final double s;
    /**
     * Distance from {@code point} to a line.
     */
    public final double distance;

    /**
     * Computes distance from a point to a line.
     * <p>
     * Algorithm is described by O'Rourke at http://www.faqs.org/faqs/graphics/algorithms-faq/ in subject 1.02
     *
     * @param lineStart
     * @param lineEnd
     * @param point
     */
    public NodePosition(Point2D lineStart, Point2D lineEnd, Point2D point) {
        double l = lineStart.distanceTo(lineEnd);
        r = ((point.x - lineStart.x) * (lineEnd.x - lineStart.x)
                + (point.y - lineStart.y) * (lineEnd.y - lineStart.y))
                / (l * l);
        s = ((lineStart.y - point.y) * (lineEnd.x - lineStart.x)
                - (lineStart.x - point.x) * (lineEnd.y - lineStart.y))
                / (l * l);
        distance = Math.abs(s) * l;
    }
}
