package org.tendiwa.geometry;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Line2DTest {
    @Test
    public void intersection() {
        Line2D line1 = new Line2D(
                new Point2D(0, 0),
                new Point2D(4, 4)
        );
        Line2D line2 = new Line2D(
                new Point2D(0, 4),
                new Point2D(4, 0)
        );
        assertEquals(line1.intersection(line2), new Point2D(2, 2));
    }

    @Test
    public void noIntersection() {

        Line2D line1 = new Line2D(
                new Point2D(0, 0),
                new Point2D(4, 0)
        );
        Line2D line2 = new Line2D(
                new Point2D(0, 1),
                new Point2D(4, 1)
        );
        assertTrue(!line1.intersects(line2));
        assertNull(line1.intersection(line2));
    }
}
