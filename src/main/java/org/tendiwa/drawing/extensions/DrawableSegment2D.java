package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Segment2D_Wr;
import org.tendiwa.geometry.Vector2D;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public final class DrawableSegment2D extends Segment2D_Wr implements Drawable {
	private final Color color;

	public DrawableSegment2D(
		Segment2D segment,
		Color color
	) {
		super(segment);
		this.color = color;
	}

	@Override
	public void drawIn(Canvas canvas) {
		canvas.drawRasterLine(this, color);
	}

	public final static class Thin extends Segment2D_Wr implements Drawable {

		private final Color color;

		public Thin(
			Segment2D segment,
			Color color
		) {
			super(segment);
			this.color = color;
		}

		@Override
		public void drawIn(Canvas canvas) {
			canvas.drawSegment2D(this, color);
		}
	}

	public static final class Arrow extends Segment2D_Wr implements Drawable {

		private final Color color;
		private final double arrowheadLength;

		public Arrow(Segment2D segment, Color color, double arrowheadLength) {
			super(segment);
			this.color = color;
			this.arrowheadLength = arrowheadLength;
		}

		@Override
		public void drawIn(Canvas canvas) {
			Segment2D leftHalfarrow = segment2D(end(), endOfLeftHalfarrow());
			Segment2D rightHalfarrow = segment2D(end(), endOfRightHalfarrow());
			canvas.draw(new DrawableSegment2D.Thin(this, color));
			canvas.draw(new DrawableSegment2D.Thin(leftHalfarrow, color));
			canvas.draw(new DrawableSegment2D.Thin(rightHalfarrow, color));
		}

		private Point2D endOfRightHalfarrow() {
			Vector2D vector = asVector();
			return end()
				.add(vector.multiply(-arrowheadLength / vector.magnitude()))
				.add(vector.rotateQuarterClockwise().multiply(-arrowheadLength / vector.magnitude()));
		}

		private Point2D endOfLeftHalfarrow() {
			Vector2D vector = asVector();
			return end()
				.add(vector.multiply(-arrowheadLength / vector.magnitude()))
				.add(vector.rotateQuarterClockwise().multiply(arrowheadLength / vector.magnitude()));
		}
	}
}
