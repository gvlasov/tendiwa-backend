package tendiwa.drawing;

import java.awt.Color;

import tendiwa.geometry.EnhancedPoint;
import tendiwa.geometry.RectangleSidePiece;

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
}
