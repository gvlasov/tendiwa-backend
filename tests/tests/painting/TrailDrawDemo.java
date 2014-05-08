package tests.painting;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.DrawingRectangleSystem;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.core.GrowingRectangleSystem;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.drawing.TestCanvas;

import java.awt.*;

import static java.awt.Color.*;

public class TrailDrawDemo implements Runnable {
    @Inject
    TestCanvas canvas;

    public static void main(String[] args) {
        Demos.run(TrailDrawDemo.class);
    }

    @Override
    public void run() {
        RectangleSystem rs = new GrowingRectangleSystem(0, new Rectangle(0, 0, 40, 50));
        canvas.draw(rs, DrawingRectangleSystem.withColors(BLACK, LIGHT_GRAY, GRAY, DARK_GRAY));
    }
}
