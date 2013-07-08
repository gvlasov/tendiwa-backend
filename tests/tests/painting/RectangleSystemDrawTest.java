package tests.painting;

import painting.TestCanvas;
import tendiwa.geometry.RandomRectangleSystem;
import tendiwa.geometry.RectangleSystem;

public class RectangleSystemDrawTest extends TestCanvas {
public static void main(String[] args) {
	visualize();
}
public void paint() {
	RectangleSystem rs = new RandomRectangleSystem(0, 0, 1280, 1024, 3, 0);
	draw(rs);
}
}
