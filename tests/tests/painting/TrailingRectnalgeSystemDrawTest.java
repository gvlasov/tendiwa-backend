package tests.painting;

import java.awt.Point;

import painting.TestCanvas;
import tendiwa.core.meta.Range;
import tendiwa.geometry.TrailRectangleSystem;

public class TrailingRectnalgeSystemDrawTest extends TestCanvas {
public static void main(String[] args) {
	visualize();
}
@Override
public void paint() {
	setScale(2);
	TrailRectangleSystem trs = new TrailRectangleSystem(0, new Range(1, 10), new Point(12, 18));
	trs.buildToPoint(new Point(212, 18));
	draw(trs);
}
}
