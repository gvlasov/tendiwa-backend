package tests.painting;

import java.awt.Rectangle;

import painting.TestCanvas;
import tendiwa.geometry.GrowingRectangleSystem;
import tendiwa.geometry.RectangleSystem;

public class TrailDrawTest extends TestCanvas {
public static void main(String[] args) {
	visualize();
}
public void paint() {
	RectangleSystem rs = new GrowingRectangleSystem(0, new Rectangle(40, 50));
}
}
