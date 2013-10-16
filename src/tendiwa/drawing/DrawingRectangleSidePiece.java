package tendiwa.drawing;

import java.awt.Color;
import java.util.Iterator;

import tendiwa.core.EnhancedPoint;
import tendiwa.core.RectangleSidePiece;

import com.google.common.collect.Iterables;

public class DrawingRectangleSidePiece {
	public static DrawingAlgorithm<RectangleSidePiece> withColors(final Color color1, final Color color2) {
		return new DrawingAlgorithm<RectangleSidePiece>() {

			@Override
			public void draw(RectangleSidePiece piece) {
				for (EnhancedPoint point : piece.getSegment()) {
					point.moveToSide(piece.getDirection());
					if ((point.x + point.y) % 2 == 0) {
						drawPoint(point.x, point.y, color1);
					} else {
						drawPoint(point.x, point.y, color2);
					}
				}
			}
		};
	}
	public static DrawingAlgorithm<RectangleSidePiece> withColor(final Color color) {
		return new DrawingAlgorithm<RectangleSidePiece>() {
			@Override
			public void draw(RectangleSidePiece piece) {
				for (EnhancedPoint point : piece.getSegment()) {
					point.moveToSide(piece.getDirection());
					drawPoint(point.x, point.y, color);
				}
				
			}
		};
	}
	public static DrawingAlgorithm<RectangleSidePiece> withColorLoop(final Color... colors) {
		return new DrawingAlgorithm<RectangleSidePiece>() {
			final Iterator<Color> iter = Iterables.cycle(colors).iterator();

			@Override
			public void draw(RectangleSidePiece piece) {
				for (EnhancedPoint point : piece.getSegment()) {
					point.moveToSide(piece.getDirection());
					drawPoint(point.x, point.y, iter.next());
				}
			}
		};
	}
}
