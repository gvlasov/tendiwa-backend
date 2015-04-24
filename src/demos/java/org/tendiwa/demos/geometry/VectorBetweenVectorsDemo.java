package org.tendiwa.demos.geometry;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.AnimationFrame;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.extensions.DrawableSegment2D;
import org.tendiwa.drawing.extensions.Gif;
import org.tendiwa.files.FileInHome;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Vector2D;

import java.awt.Color;
import java.util.stream.IntStream;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

final class VectorBetweenVectorsDemo implements Runnable {

	int ARROWHEAD_LENGTH = 4;
	Point2D center = point2D(100, 100);

	@Override
	public void run() {
		Vector2D cw = point2D(40, 10);
		Vector2D ccw = point2D(-13, 50);
		int samples = 60;
		new Gif(
			new FileInHome("between.gif"),
			rectangle(200, 200),
			30,
			IntStream.range(0, samples)
				.mapToDouble(i -> Math.PI * 2 / samples * i)
				.mapToObj(cw::rotate)
				.map(vector -> new DrawableThreeVectors(cw, ccw, vector))
				.map(AnimationFrame::new)
		).save();
	}

	private final class DrawableThreeVectors implements Drawable {

		private final Vector2D cw;
		private final Vector2D ccw;
		private final Vector2D vector;

		public DrawableThreeVectors(
			Vector2D cw,
			Vector2D ccw,
			Vector2D vector
		) {
			this.cw = cw;
			this.ccw = ccw;
			this.vector = vector;
		}

		@Override
		public void drawIn(Canvas canvas) {
			canvas.draw(
				new DrawableSegment2D.Arrow(
					center.segmentTo(center.add(cw)),
					Color.green,
					ARROWHEAD_LENGTH
				)
			);
			canvas.draw(
				new DrawableSegment2D.Arrow(
					center.segmentTo(center.add(ccw)),
					Color.yellow,
					ARROWHEAD_LENGTH
				)
			);
			canvas.draw(
				new DrawableSegment2D.Arrow(
					center.segmentTo(center.add(vector)),
					vector.isBetweenVectors(cw, ccw) ? Color.blue : Color.red,
					ARROWHEAD_LENGTH
				)
			);
		}
	}

	public static void main(String[] args) {
		Demos.run(VectorBetweenVectorsDemo.class);
	}
}
