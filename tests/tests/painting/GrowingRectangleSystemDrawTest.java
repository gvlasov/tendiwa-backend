package tests.painting;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.core.*;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.extensions.RecursivelySplitRectangleSystemFactory;

@RunWith(JukitoRunner.class)
public class GrowingRectangleSystemDrawTest {
@Inject
TestCanvas canvas;

@Test
void draw() {

	Rectangle er = new Rectangle(100, 100, 30, 30);
	GrowingRectangleSystem grs = new GrowingRectangleSystem(0, er);
	grs.grow(er, Directions.N, 100, 16, 0);
	grs.grow(er, Directions.E, 12, 16, 0);
	grs.grow(er, Directions.S, 12, 16, 0);
	grs.grow(er, Directions.W, 12, 16, 0);
	canvas.draw(grs);
	RectangleSystem rs = RecursivelySplitRectangleSystemFactory.create(0, 0, 100, 200, 3, 0);
	canvas.draw(rs);
}
}
