package tests.painting;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.geometry.EnhancedRectangle;
import org.tendiwa.core.GrowingRectangleSystem;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.drawing.TestCanvas;

@RunWith(JukitoRunner.class)
public class TrailDrawTest {
@Inject
TestCanvas canvas;

@Test
void draw() {

	RectangleSystem rs = new GrowingRectangleSystem(0, new EnhancedRectangle(0, 0, 40, 50));
	canvas.draw(rs);
}
}
