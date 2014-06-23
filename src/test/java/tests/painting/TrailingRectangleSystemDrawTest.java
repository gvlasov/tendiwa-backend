package tests.painting;

import com.google.inject.Inject;
import org.tendiwa.core.FuckingTrailRectangleSystem;
import org.tendiwa.core.meta.Range;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.extensions.DrawingRectangleSystem;
import org.tendiwa.drawing.TestCanvas;

import java.awt.*;

public class TrailingRectangleSystemDrawTest implements Runnable {
    @Inject
    TestCanvas canvas;

    public static void main(String[] args) {
        Demos.run(TrailingRectangleSystemDrawTest.class);
    }

    public void run() {
        int numberOfTests = 1000;
        for (int i = 0; i < numberOfTests; i++) {
            FuckingTrailRectangleSystem trs = new FuckingTrailRectangleSystem(
                    10,
                    new Range(1, 15),
                    new Point(12, 18)).buildToPoint(new Point(212, 32));
            if (FuckingTrailRectangleSystem.STOP) {
                canvas.draw(trs, DrawingRectangleSystem.withColors(Color.BLACK, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY));
                break;
            }
        }
    }
}