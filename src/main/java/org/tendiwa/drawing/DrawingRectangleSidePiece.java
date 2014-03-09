package org.tendiwa.drawing;

import java.awt.Color;
import java.util.Iterator;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.RectangleSidePiece;

import com.google.common.collect.Iterables;

public class DrawingRectangleSidePiece {
	public static DrawingAlgorithm<RectangleSidePiece> withColors(final Color color1, final Color color2) {
		return new DrawingAlgorithm<RectangleSidePiece>() {

			@Override
			public void draw(RectangleSidePiece piece) {
				for (Cell point : piece.getSegment()) {
					point.moveToSide(piece.getDirection());
					if ((point.getX() + point.getY()) % 2 == 0) {
						drawPoint(point.getX(), point.getY(), color1);
					} else {
						drawPoint(point.getX(), point.getY(), color2);
					}
				}
			}
		};
	}
	public static DrawingAlgorithm<RectangleSidePiece> withColor(final Color color) {
		return new DrawingAlgorithm<RectangleSidePiece>() {
			@Override
			public void draw(RectangleSidePiece piece) {
				for (Cell point : piece.getSegment()) {
					point.moveToSide(piece.getDirection());
					drawPoint(point.getX(), point.getY(), color);
				}
				
			}
		};
	}
	public static DrawingAlgorithm<RectangleSidePiece> withColorLoop(final Color... colors) {
		return new DrawingAlgorithm<RectangleSidePiece>() {
			final Iterator<Color> iter = Iterables.cycle(colors).iterator();

			@Override
			public void draw(RectangleSidePiece piece) {
				for (Cell point : piece.getSegment()) {
					point.moveToSide(piece.getDirection());
					drawPoint(point.getX(), point.getY(), iter.next());
				}
			}
		};
	}
}
