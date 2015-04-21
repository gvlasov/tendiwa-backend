package org.tendiwa.drawing.extensions;

import java.awt.Color;
import java.util.Iterator;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.RectangleSidePiece;

import com.google.common.collect.Iterables;

@SuppressWarnings("unused")
public class DrawingRectangleSidePiece {
	public static DrawingAlgorithm<RectangleSidePiece> withColors(final Color color1, final Color color2) {
		return (piece, canvas) -> {
			for (BasicCell point : piece.getSegment()) {
				point.moveToSide(piece.getDirection());
				if ((point.x() + point.y()) % 2 == 0) {
					canvas.drawCell(point.x(), point.y(), color1);
				} else {
					canvas.drawCell(point.x(), point.y(), color2);
				}
			}
		};
	}

	public static DrawingAlgorithm<RectangleSidePiece> withColor(final Color color) {
		return (piece, canvas) -> {
			for (BasicCell point : piece.getSegment()) {
				point.moveToSide(piece.getDirection());
				canvas.drawCell(point.x(), point.y(), color);
			}

		};
	}

	public static DrawingAlgorithm<RectangleSidePiece> withColorLoop(final Color... colors) {
		final Iterator<Color> iter = Iterables.cycle(colors).iterator();
		return (piece, canvas) -> {
			for (BasicCell point : piece.getSegment()) {
				point.moveToSide(piece.getDirection());
				canvas.drawCell(point.x(), point.y(), iter.next());
			}
		};
	}
}
