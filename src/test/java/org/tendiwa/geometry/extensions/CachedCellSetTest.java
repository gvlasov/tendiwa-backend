package org.tendiwa.geometry.extensions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.junit.Assert;
import org.junit.Test;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Rectangle;

import static org.tendiwa.geometry.DSL.rectangle;

public class CachedCellSetTest {
    @Test
    public void iterations() {
        Rectangle shape = new Rectangle(5, 5, 5, 5);
        CachedCellSet cells = new CachedCellSet(
                new ChebyshevDistanceBuffer(
                        3,
                        shape::contains
                ),
                rectangle(15, 15)
        );
        Assert.assertEquals(96, cells.toList().size());
    }

    @Test
    public void iterations2() {
        Rectangle shape = new Rectangle(5, 5, 5, 5);
        CachedCellSet cells = new CachedCellSet(
                new ChebyshevDistanceBuffer(
                        5,
                        shape::contains
                ),
                rectangle(15, 15)
        );
        Assert.assertEquals(200, cells.toList().size());
    }
}
