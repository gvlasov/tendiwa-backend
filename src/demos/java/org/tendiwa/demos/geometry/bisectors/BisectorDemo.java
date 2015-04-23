package org.tendiwa.demos.geometry.bisectors;

import org.apache.log4j.Logger;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.files.FileInHome;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Vector2D;

import java.awt.Color;
import java.io.File;
import java.util.stream.IntStream;

import static org.tendiwa.geometry.GeometryPrimitives.*;

final class BisectorDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(BisectorDemo.class);
	}

	final TestCanvas canvas = new TestCanvas(1, 200, 200);
	final GifBuilder gif = new GifBuilder(canvas, 10, Logger.getRootLogger());

	final File animationFile = new FileInHome("bisectors.gif");

	final Point2D center = point2D(100, 100);
	final int iterations = 20;

	@Override
	public void run() {
		Vector2D cw = vector(40, 0);
		IntStream.range(0, iterations)
			.asDoubleStream()
			.map(i -> Math.PI * 2 / iterations * i)
			.mapToObj(angle -> new VectorsAndBisector(cw, cw.rotate(angle).multiply(1.3)))
			.forEach(vab -> {
				canvas.fillBackground(Color.white);
				canvas.draw(vab, DrawingVectorsAndBisector.around(center));
				gif.saveFrame();
			});
		gif.saveAnimation(animationPath);

		canvas.fillBackground(Color.white);
		canvas.draw(
			new VectorsAndBisector(
				vector(20, 0),
				vector(-20, 0)
			),
			DrawingVectorsAndBisector.around(center)
		);
	}

}
