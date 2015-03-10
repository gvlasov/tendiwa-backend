package org.tendiwa.demos.geometry;

import org.apache.log4j.Logger;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Vector2D;

import java.awt.Color;
import java.util.stream.IntStream;

final class VectorBetweenVectorsDemo implements Runnable {

	int ARROWHEAD_LENGTH = 4;
	TestCanvas canvas = new TestCanvas(1, 200, 200);
	GifBuilder gifBuilder = new GifBuilder(canvas, 30, Logger.getRootLogger());

	Point2D center = new Point2D(100, 100);

	@Override
	public void run() {
		Vector2D cw = new Point2D(40, 10);
		Vector2D ccw = new Point2D(-13, 50);
		int samples = 60;
		IntStream.range(0, samples)
			.mapToDouble(i -> Math.PI * 2 / samples * i)
			.mapToObj(cw::rotate)
			.forEach(vector -> drawVectors(cw, ccw, vector));
		gifBuilder.saveAnimation(Utils.homeDirectory() + "/between.gif");
	}

	private void drawVectors(Vector2D cw, Vector2D ccw, Vector2D vector) {
		canvas.clear();
		canvas.draw(
			center.segmentTo(center.add(cw)),
			DrawingSegment2D.withColorDirected(Color.green, ARROWHEAD_LENGTH)
		);
		canvas.draw(
			center.segmentTo(center.add(ccw)),
			DrawingSegment2D.withColorDirected(Color.yellow, ARROWHEAD_LENGTH)
		);
		canvas.draw(
			center.segmentTo(center.add(vector)),
			DrawingSegment2D.withColorDirected(
				vector.isBetweenVectors(cw, ccw) ? Color.blue : Color.red,
				ARROWHEAD_LENGTH
			)
		);
		gifBuilder.saveFrame();
	}

	public static void main(String[] args) {
		Demos.run(VectorBetweenVectorsDemo.class);
	}
}
