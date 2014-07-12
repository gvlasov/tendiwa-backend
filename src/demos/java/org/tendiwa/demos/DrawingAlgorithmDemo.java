package org.tendiwa.demos;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.drawing.extensions.DrawingRectangleSystem;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.extensions.RecursivelySplitRectangleSystemFactory;

import java.awt.*;
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
