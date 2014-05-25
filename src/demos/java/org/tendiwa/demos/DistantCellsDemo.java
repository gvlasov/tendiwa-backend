package org.tendiwa.demos;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.util.Modules;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.LargerScaleCanvasModule;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.DistantCellsFinder;
import org.tendiwa.geometry.Rectangle;

import java.awt.*;

import static com.google.inject.name.Names.named;
import static java.awt.Color.RED;
import static org.tendiwa.drawing.extensions.DrawingRectangle.withColor;

public class DistantCellsDemo implements Runnable {
    public static void main(String[] args) {
        Demos.run(
                DistantCellsDemo.class,
                Modules.override(new DistantCellsInBufferBorderModule()).with(
                        new AbstractModule() {
                            @Override
                            protected void configure() {
                                bind(Integer.class)
                                        .annotatedWith(named("minDistanceBetweenCells"))
                                        .toInstance(17);
                            }
                        }),
                new DrawingModule(),
                new LargerScaleCanvasModule()
        );
    }

    @Inject
    DistantCellsFinder cells;
    @Inject
    TestCanvas canvas;
    @Inject
    @Named("waterRectangle")
    Rectangle waterRec;

    @Override
    public void run() {
        canvas.draw(waterRec, withColor(Color.BLUE));
        for (Cell cell : cells) {
            System.out.println(cell);
            canvas.drawCell(cell, RED);
        }
    }
}
