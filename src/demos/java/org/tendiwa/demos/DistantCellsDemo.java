package org.tendiwa.demos;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableCell;
import org.tendiwa.geometry.BasicBoundedCells;
import org.tendiwa.geometry.DistantCellsFinder;
import org.tendiwa.geometry.Rectangle;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class DistantCellsDemo implements Runnable {

	public static void main(String[] args) {
		Demos.run(DistantCellsDemo.class);
	}

	@Override
	public void run() {
		Rectangle waterRec = rectangle(600, 500);
		int distanceBetweenCells = 80;
		DistantCellsFinder distantCells = new DistantCellsFinder(
			new BasicBoundedCells(
				(x, y) -> (x + y) % 77 == 0,
				waterRec
			),
			distanceBetweenCells
		);
		TestCanvas canvas = new TestCanvas(1, waterRec);
		canvas.draw(
			new DrawableRectangle(
				waterRec,
				Color.blue
			)
		);
		canvas.drawAll(
			distantCells,
			cell -> new DrawableCell(cell, Color.red)
		);
	}
}
