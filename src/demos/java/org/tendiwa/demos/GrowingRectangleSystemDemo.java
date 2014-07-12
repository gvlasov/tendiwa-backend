package org.tendiwa.demos;

import com.google.inject.Inject;
import org.tendiwa.core.*;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.extensions.DrawingRectangleSystem;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.extensions.RecursivelySplitRectangleSystemFactory;

import java.awt.*;

public class GrowingRectangleSystemDemo implements Runnable {
    @Inject
    TestCanvas canvas;

    public static void main(String[] args) {
        Demos.run(GrowingRectangleSystemDemo.class);
    }

    @Override
    public void run() {

        Rectangle er = new Rectangle(100, 100, 30, 30);
        GrowingRectangleSystem grs = new GrowingRectangleSystem(0, er);
        grs.grow(er, Directions.N, 100, 16, 0);
        grs.grow(er, Directions.E, 12, 16, 0);
        grs.grow(er, Directions.S, 12, 16, 0);
        grs.grow(er, Directions.W, 12, 16, 0);
        DrawingAlgorithm<RectangleSystem> grayscale = DrawingRectangleSystem.withColors(Color.BLACK, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY);
        canvas.draw(grs, grayscale);
        RectangleSystem rs = RecursivelySplitRectangleSystemFactory.create(0, 0, 100, 200, 3, 0);
        canvas.draw(rs, grayscale);
    }
}
