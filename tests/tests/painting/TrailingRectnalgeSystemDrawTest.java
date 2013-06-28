package tests.painting;

import java.awt.Point;

import painting.TestCanvas;
import tendiwa.core.meta.Range;
import tendiwa.geometry.FuckingTrailRectangleSystem;

public class TrailingRectnalgeSystemDrawTest extends TestCanvas {
public static void main(String[] args) {
	visualize();
}
//@Override
//public void paint() {
//	setScale(2);
//	FuckingTrailRectangleSystem trs = new FuckingTrailRectangleSystem(10, new Range(1, 15), new Point(12, 18));
//	trs.buildToPoint(new Point(212, 32));
//	draw(trs);
//}

@Override
public void paint() {
	setScale(2);
	int numberOfTests = 1000;
	for (int i=0; i<numberOfTests; i++) {
		FuckingTrailRectangleSystem trs = new FuckingTrailRectangleSystem(10, new Range(1,15), new Point(12, 18))
			.buildToPoint(new Point(212, 32));
		if (FuckingTrailRectangleSystem.STOP) {
			draw(trs);
			break;
		}
	}
}
}
