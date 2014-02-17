package tests.painting;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.geometry.EnhancedRectangle;
import org.tendiwa.geometry.Placeable;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.RecursivelySplitRectangleSystemFactory;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingRectangle;
import org.tendiwa.drawing.DrawingRectangleSystem;
import org.tendiwa.drawing.TestCanvas;

import java.awt.*;
import java.util.Collection;

@RunWith(JukitoRunner.class)
public class DrawingAlgorithmTest {
@Inject
TestCanvas canvas;

@Test
void draw() {

	EnhancedRectangle r = new EnhancedRectangle(5, 7, 8, 7);
	canvas.draw(r, DrawingRectangle.chequerwise(Color.ORANGE, Color.GREEN));

	Collection<EnhancedRectangle> recs = Lists.newArrayList(
		EnhancedRectangle.rectangleMovedFromOriginal(r, 5, 20),
		EnhancedRectangle.rectangleMovedFromOriginal(r, 5, 30),
		EnhancedRectangle.rectangleMovedFromOriginal(r, 5, 40),
		EnhancedRectangle.rectangleMovedFromOriginal(r, 5, 50));
	DrawingAlgorithm<Placeable> algorithm = DrawingRectangle.withColorLoop(
		Color.RED,
		Color.GREEN);
	for (EnhancedRectangle rec : recs) {
		canvas.draw(rec, algorithm);
	}
	RectangleSystem rs = RecursivelySplitRectangleSystemFactory.create(40, 5, 30, 50, 5, 2);
	canvas.draw(rs, DrawingRectangleSystem.withColors(Color.BLUE, Color.WHITE));
}
}
