package org.tendiwa.demos;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.drawing.extensions.DrawingRectangleSystem;
import org.tendiwa.geometry.RectSet;
import org.tendiwa.geometry.StupidPriceduralRecs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.extensions.RecursivelySplitRectangleSystemFactory;

import java.awt.Color;
import java.util.Collection;

public class DrawingAlgorithmDemo implements Runnable {

	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(DrawingAlgorithmDemo.class, new DrawingModule());
	}

	@Override
	public void run() {

		Rectangle r = new Rectangle(5, 7, 8, 7);
		canvas.draw(r, DrawingRectangle.chequerwise(Color.ORANGE, Color.GREEN));

		Collection<Rectangle> recs = Lists.newArrayList(
			StupidPriceduralRecs.rectangleMovedFromOriginal(r, 5, 20),
			StupidPriceduralRecs.rectangleMovedFromOriginal(r, 5, 30),
			StupidPriceduralRecs.rectangleMovedFromOriginal(r, 5, 40),
			StupidPriceduralRecs.rectangleMovedFromOriginal(r, 5, 50));
		DrawingAlgorithm<RectSet> algorithm = DrawingRectangle.withColorLoop(
			Color.RED,
			Color.GREEN);
		for (Rectangle rec : recs) {
			canvas.draw(rec, algorithm);
		}
		RectangleSystem rs = RecursivelySplitRectangleSystemFactory.create(40, 5, 30, 50, 5, 2);
		canvas.draw(rs, DrawingRectangleSystem.withColors(Color.BLUE, Color.WHITE));
	}
}
