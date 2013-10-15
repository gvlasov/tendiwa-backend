package tests.painting;

import com.google.common.collect.Lists;
import tendiwa.drawing.DrawingAlgorithm;
import tendiwa.drawing.DrawingRectangle;
import tendiwa.drawing.DrawingRectangleSystem;
import tendiwa.drawing.TestCanvas;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.Placeable;
import tendiwa.geometry.RectangleSystem;
import tendiwa.geometry.RecursivelySplitRectangleSystemFactory;

import java.awt.*;
import java.util.Collection;

public class DrawingAlgorithmTest {
	public static void main(String[] args) {
		TestCanvas canvas = TestCanvas.builder().setScale(3).build();
		
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
