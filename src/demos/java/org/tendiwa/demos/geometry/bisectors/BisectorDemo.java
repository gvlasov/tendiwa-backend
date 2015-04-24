package org.tendiwa.demos.geometry.bisectors;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.AnimationFrame;
import org.tendiwa.drawing.extensions.Gif;
import org.tendiwa.files.FileInHome;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Vector2D;

import java.util.stream.IntStream;

import static org.tendiwa.geometry.GeometryPrimitives.*;

final class BisectorDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(BisectorDemo.class);
	}


	@Override
	public void run() {
		int numberOfCircleDivisions = 20;
		Point2D center = point2D(100, 100);
		Vector2D cw = vector(40, 0);
		new Gif(
			new FileInHome("bisector.gif"),
			rectangle(200, 200),
			20,
			IntStream.range(0, numberOfCircleDivisions)
				.asDoubleStream()
				.map((i) -> ithFractionOfCircle(i, numberOfCircleDivisions))
				.mapToObj(angle -> cw.rotate(angle).multiply(1.3))
				.map(ccw -> new VectorsAndBisector(center, cw, ccw))
				.map(AnimationFrame::new)
		).save();
	}

	private double ithFractionOfCircle(double numerator, int denominator) {
		return Math.PI * 2 / denominator * numerator;
	}

}
