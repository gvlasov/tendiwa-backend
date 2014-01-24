package tests.painting;

import org.tendiwa.core.EnhancedRectangle;
import org.tendiwa.core.GrowingRectangleSystem;
import org.tendiwa.core.RectangleSystem;
import org.tendiwa.drawing.TestCanvas;

public class TrailDrawTest {
public static void main(String[] args) {
	TestCanvas canvas = TestCanvas.builder().setScale(3).build();
	RectangleSystem rs = new GrowingRectangleSystem(0, new EnhancedRectangle(0, 0, 40, 50));
	canvas.draw(rs);
}
}
