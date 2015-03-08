package org.tendiwa.demos.geometry.bisectors;

import org.apache.log4j.Logger;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Vector2D;

import java.awt.Color;
import java.util.stream.IntStream;

final class BisectorDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(BisectorDemo.class);
	}

	final TestCanvas canvas = new TestCanvas(1, 200, 200);
	final GifBuilder gif = new GifBuilder(canvas, 10, Logger.getRootLogger());

	final String animationPath = "/home/suseika/bisectors.gif";

	final Point2D center = new Point2D(100, 100);
	final int iterations = 20;

	@Override
	public void run() {
		Vector2D cw = Vector2D.vector(40, 0);
		IntStream.range(0, iterations)
			.asDoubleStream()
			.map(a -> Math.PI * 2 / iterations * a)
			.mapToObj(a -> new VectorsAndBisector(cw, cw.rotate(a).multiply(1.3)))
			.forEach(vab -> {
				canvas.fillBackground(Color.white);
				canvas.draw(vab, DrawingVectorsAndBisector.around(center));
				gif.saveFrame();
			});
		gif.saveAnimation(animationPath);

		canvas.fillBackground(Color.white);
		canvas.draw(
			new VectorsAndBisector(
				new Point2D(20, 0),
				new Point2D(-20, 0)
			),
			DrawingVectorsAndBisector.around(center)
		);
	}

}
