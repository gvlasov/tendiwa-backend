package tests.painting;

import java.awt.Rectangle;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.core.GrowingRectangleSystem;
import org.tendiwa.core.RectangleSystem;

public class TrailDrawTest {
public static void main(String[] args) {
	TestCanvas canvas = TestCanvas.builder().setScale(3).build();
	RectangleSystem rs = new GrowingRectangleSystem(0, new Rectangle(40, 50));
	canvas.draw(rs);
}
}
