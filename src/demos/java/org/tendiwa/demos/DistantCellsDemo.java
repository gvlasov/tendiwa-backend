package org.tendiwa.demos;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.util.Modules;
import org.tendiwa.data.DistantCellsInBufferBorderModule;
import org.tendiwa.drawing.LargerScaleCanvasModule;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.DistantCellsFinder;
import org.tendiwa.geometry.Rectangle;

import java.awt.Color;

import static com.google.inject.name.Names.named;
import static java.awt.Color.*;
import static org.tendiwa.drawing.extensions.DrawingRectangle.withColor;

public class DistantCellsDemo implements Runnable {
	@Inject
	DistantCellsFinder cells;
	@Inject
	TestCanvas canvas;
	@Inject
	@Named("waterRectangle")
	Rectangle waterRec;

	public static void main(String[] args) {
		Demos.run(
			DistantCellsDemo.class,
			Modules.override(new DistantCellsInBufferBorderModule()).with(
				new AbstractModule() {
					@Override
					protected void configure() {
						bind(Integer.class)
							.annotatedWith(named("minDistanceBetweenCells"))
							.toInstance(17);
					}
				}),
			new DrawingModule(),
			new LargerScaleCanvasModule()
		);
	}

	@Override
	public void run() {
		canvas.draw(waterRec, withColor(Color.BLUE));
		for (BasicCell cell : cells) {
			System.out.println(cell);
			canvas.drawCell(cell, RED);
		}
	}
}
