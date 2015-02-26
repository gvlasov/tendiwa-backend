package org.tendiwa.demos.geometry.bisectors;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;

import java.awt.Color;

import static org.tendiwa.drawing.extensions.DrawingSegment2D.withColorDirected;

final class DrawingVectorsAndBisector {

	public static final int ARROWHEAD_LENGTH = 4;

	static DrawingAlgorithm<VectorsAndBisector> around(Vector2D center) {
		return new DrawingAlgorithm<VectorsAndBisector>() {

			@Override
			public void draw(VectorsAndBisector what, DrawableInto canvas) {
				drawVectorWithColor(what.cw, Color.red, canvas);
				drawVectorWithColor(what.ccw, Color.blue, canvas);
				drawVectorWithColor(what.bisector, Color.green, canvas);
			}

			private void drawVectorWithColor(Vector2D vector, Color color, DrawableInto canvas) {
				canvas.draw(
					new Segment2D(
						Point2D.of(center),
						Point2D.of(center.add(vector))
					),
					withColorDirected(color, ARROWHEAD_LENGTH)
				);
			}
		};
	}
}
