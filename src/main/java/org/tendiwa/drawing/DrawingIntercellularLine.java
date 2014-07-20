package org.tendiwa.drawing;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.IntercellularLine;

import java.awt.Color;

public class DrawingIntercellularLine {
	public static DrawingAlgorithm<IntercellularLine> withColor(Color color) {
		return (shape, canvas) -> {
			if (shape.getOrientation() == Orientation.HORIZONTAL) {
				int nCoord = shape.getStaticCoordFromSide(CardinalDirection.N);
				canvas.drawLine(
					0,
					nCoord,
					canvas.getWidth(),
					nCoord,
					color
				);
				int sCoord = shape.getStaticCoordFromSide(CardinalDirection.S);
				canvas.drawLine(
					0,
					sCoord,
					canvas.getWidth(),
					sCoord,
					color
				);
			} else {
				assert shape.getOrientation() == Orientation.VERTICAL;
				int wCoord = shape.getStaticCoordFromSide(CardinalDirection.W);
				canvas.drawLine(
					wCoord,
					0,
					wCoord,
					canvas.getHeight(),
					color
				);
				int eCoord = shape.getStaticCoordFromSide(CardinalDirection.E);
				canvas.drawLine(
					eCoord,
					0,
					eCoord,
					canvas.getHeight(),
					color
				);
			}
		};
	}
}
