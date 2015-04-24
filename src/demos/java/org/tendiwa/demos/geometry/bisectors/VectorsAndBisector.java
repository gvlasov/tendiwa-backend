package org.tendiwa.demos.geometry.bisectors;

import org.tendiwa.drawing.AwtCanvas;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.extensions.DrawableSegment2D;
import org.tendiwa.geometry.*;

import java.awt.Color;

final class VectorsAndBisector extends Bisector_Wr implements Drawable<Canvas> {
	private final Point2D start;
	final Vector2D cw;
	final Vector2D ccw;

	VectorsAndBisector(Point2D start, Vector2D cw, Vector2D ccw) {
		super(new BasicBisector(cw, ccw));
		this.start = start;
		this.cw = cw;
		this.ccw = ccw;
	}

	@Override
	public void drawIn(Canvas canvas) {
		Segment2D bisectorSegment = start.segmentTo(start.add(asInbetweenVector()));
		Segment2D cwSegment = start.segmentTo(start.add(cw));
		Segment2D ccwSegment = start.segmentTo(start.add(ccw));
		canvas.draw(
			new DrawableSegment2D.Thin(
				bisectorSegment,
				Color.black
			)
		);
		canvas.draw(
			new DrawableSegment2D.Thin(
				cwSegment,
				Color.red
			)
		);
		canvas.draw(
			new DrawableSegment2D.Thin(
				ccwSegment,
				Color.blue
			)
		);
	}
}
