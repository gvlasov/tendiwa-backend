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
import org.tendiwa.settlements.LineIntersection;

import java.awt.*;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class PointsTest {
    @Inject
    TestCanvas canvas;

    @Test
    public void draw() {
        Point2D start = new Point2D(136.30, 205.59);
        Point2D start1 = new Point2D(131.17, 214.18);
        Point2D end = new Point2D(146.68, 209.13);
        Point2D end1 = new Point2D(138.43, 202.02);
        canvas.draw(new Line2D(start, end));
        canvas.draw(new Line2D(start1, end1));
        canvas.draw(start, DrawingPoint.withColorAndSize(Color.BLACK, 6));
        canvas.draw(start1, DrawingPoint.withColorAndSize(Color.BLUE, 6));
        System.out.println(
                new LineIntersection(
                        start,
                        end,
                        new Line2D(start1, end1)
                ).getIntersectionPoint(start, end)
        );

        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
