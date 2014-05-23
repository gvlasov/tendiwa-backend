package org.tendiwa.geometry.extensions;

import org.junit.Test;
import org.tendiwa.geometry.Point2D;

import static org.junit.Assert.assertTrue;

public class Point2DRowComparatorTest {
    @Test
    public void testCompare() throws Exception {
        assertTrue(
                new Point2DRowComparator()
                        .compare(new Point2D(1, 1), new Point2D(1, 2)) < 0
        );
        assertTrue(
                new Point2DRowComparator()
                        .compare(new Point2D(1, 100.5), new Point2D(1, 100.5)) == 0
        );
        assertTrue(
                new Point2DRowComparator()
                        .compare(new Point2D(5, 8), new Point2D(8, 5)) > 0
        );
    }
}
