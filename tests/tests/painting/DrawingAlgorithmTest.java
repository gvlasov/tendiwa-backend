package tests.painting;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingRectangle;
import org.tendiwa.drawing.DrawingRectangleSystem;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.extensions.RecursivelySplitRectangleSystemFactory;

import java.awt.*;
import java.util.Collection;

@RunWith(JukitoRunner.class)
public class DrawingAlgorithmTest {
@Inject
TestCanvas canvas;

@Test
void draw() {

	Rectangle r = new Rectangle(5, 7, 8, 7);
	canvas.draw(r, DrawingRectangle.chequerwise(Color.ORANGE, Color.GREEN));

	Collection<Rectangle> recs = Lists.newArrayList(
		Recs.rectangleMovedFromOriginal(r, 5, 20),
		Recs.rectangleMovedFromOriginal(r, 5, 30),
		Recs.rectangleMovedFromOriginal(r, 5, 40),
		Recs.rectangleMovedFromOriginal(r, 5, 50));
	DrawingAlgorithm<Placeable> algorithm = DrawingRectangle.withColorLoop(
		Color.RED,
		Color.GREEN);
	for (Rectangle rec : recs) {
		canvas.draw(rec, algorithm);
	}
	RectangleSystem rs = RecursivelySplitRectangleSystemFactory.create(40, 5, 30, 50, 5, 2);
	canvas.draw(rs, DrawingRectangleSystem.withColors(Color.BLUE, Color.WHITE));
}
}
