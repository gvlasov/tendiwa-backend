package tests.painting;

import java.awt.Rectangle;

import tendiwa.drawing.TestCanvas;
import tendiwa.geometry.GrowingRectangleSystem;
import tendiwa.geometry.RectangleSystem;

public class TrailDrawTest {
public static void main(String[] args) {
	TestCanvas canvas = TestCanvas.builder().setScale(3).build();
	RectangleSystem rs = new GrowingRectangleSystem(0, new Rectangle(40, 50));
	canvas.draw(rs);
}
}
