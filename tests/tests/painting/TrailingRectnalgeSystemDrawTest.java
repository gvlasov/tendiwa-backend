package tests.painting;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.core.FuckingTrailRectangleSystem;
import org.tendiwa.core.meta.Range;
import org.tendiwa.drawing.TestCanvas;

import java.awt.*;

@RunWith(JukitoRunner.class)
public class TrailingRectnalgeSystemDrawTest {
@Inject
TestCanvas canvas;

@Test
void draw() {

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