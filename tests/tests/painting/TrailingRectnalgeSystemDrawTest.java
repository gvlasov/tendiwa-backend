package tests.painting;

import java.awt.Point;

import org.tendiwa.core.meta.Range;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.TestCanvasBuilder;
import org.tendiwa.core.FuckingTrailRectangleSystem;

public class TrailingRectnalgeSystemDrawTest {
	public static void main(String[] args) {
		TestCanvas canvas = new TestCanvasBuilder().setScale(2).build();
		int numberOfTests = 1000;
		for (int i = 0; i < numberOfTests; i++) {
			FuckingTrailRectangleSystem trs = new FuckingTrailRectangleSystem(
				10,
				new Range(1, 15),
				new Point(12, 18)).buildToPoint(new Point(212, 32));
			if (FuckingTrailRectangleSystem.STOP) {
				canvas.draw(trs);
				break;
			}
		}
	}
}