package org.tendiwa.geometry;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static junit.framework.Assert.assertEquals;
import static org.tendiwa.geometry.DSL.cell;

public class WaveTest {


    @Test
    public void testHandle() {
        Cell startCell = cell(5, 5);
        Rectangle rectangle = Recs.rectangleByCenterPoint(startCell, 7, 7);
        AtomicInteger i = new AtomicInteger(0);
        Consumer<Cell> handler = cell -> {
            System.out.println("hello");
            i.incrementAndGet();
        };
        new Wave(startCell, rectangle::contains).handle(handler);
        assertEquals(i.intValue(), 49);
    }
}
