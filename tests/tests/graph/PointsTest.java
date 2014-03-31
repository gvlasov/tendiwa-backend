package tests.graph;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.DrawingPoint;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

import java.awt.*;

/**
 * Created by suseika on 3/30/14.
 */
@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class PointsTest {
    @Inject
    TestCanvas canvas;

    @Test
    public void draw() {
        Point2D start = new Point2D(194.89, 142.28);
        Point2D start1 = new Point2D(204.5, 139.5);
        canvas.draw(new Line2D(
                start,
                new Point2D(188.27, 135.59)
        ));
        canvas.draw(new Line2D(
                start1,
                new Point2D(184.80, 145.21)
        ));
        canvas.draw(start, DrawingPoint.withColorAndSize(Color.BLACK, 6));
        canvas.draw(start1, DrawingPoint.withColorAndSize(Color.BLUE, 6));

        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
