package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.BasicRectangle2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle2D;
import org.tendiwa.geometry.Segment2D;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;
import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

final class Marker implements Rectangle2D {
	private final BasicRectangle2D box;
	private final Segment2D post;
	private final Point2D stringStart;

	public Marker(Point2D point, String text, DrawableInto canvas) {

		double tailHeight = 10. / canvas.getScale();
		double textWidth = ((double) canvas.textWidth(text)) / canvas.getScale();
		double lineHeight = ((double) canvas.textLineHeight()) / canvas.getScale();
		double padding = 2. / canvas.getScale();

		post = segment2D(
			point.x(),
			point.y(),
			point.x(),
			point.y() - tailHeight
		);
		double boxHeight = lineHeight + padding * 2;
		double boxWidth = textWidth + padding * 2;
		box = new BasicRectangle2D(
			post.end().x() - boxWidth / 2,
			post.end().y() - boxHeight,
			boxWidth,
			boxHeight
		);
		stringStart = point2D(
			box.x + padding,
			box.y + boxHeight - padding
		);
	}

	Point2D textStart() {
		return stringStart;
	}

	@Override
	public double x() {
		return box.x();
	}

	@Override
	public double y() {
		return box.y();
	}

	@Override
	public double width() {
		return box.width();
	}

	@Override
	public double height() {
		return box.height();
	}

	Segment2D post() {
		return post;
	}
}
