package org.tendiwa.settlements;

import org.junit.Test;
import org.tendiwa.geometry.Segment2D;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LineIntersectionTest {
    @Test
    public void segmentsDontIntersectWithEndPoints() {
        Segment2D a = Segment2D.create(0, 0, 4, 0);
        Segment2D b = Segment2D.create(4, 0, 8, 0);
        assertFalse(new LineIntersection(a, b).segmentsIntersect());
    }

    @Test
    public void segmentsDontIntersect() {
        Segment2D a = Segment2D.create(0, 0, 4, 0);
        Segment2D b = Segment2D.create(2, -4, 2, -2);
        assertFalse(new LineIntersection(a, b).segmentsIntersect());
    }

    @Test
    public void segmentsIntersect() {
        Segment2D a = Segment2D.create(0, 0, 4, 0);
        Segment2D b = Segment2D.create(2, -2, 2, 2);
        assertTrue(new LineIntersection(a, b).segmentsIntersect());
    }
}
