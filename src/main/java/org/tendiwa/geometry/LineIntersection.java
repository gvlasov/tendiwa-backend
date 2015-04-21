package org.tendiwa.geometry;

import org.tendiwa.core.Intersection;
import org.tendiwa.core.OrdinalDirection;

public class LineIntersection extends Intersection {
	int x;
	int y;

	LineIntersection(IntercellularLine line1, IntercellularLine line2) {
		if (!line1.isPerpendicular(line2)) {
			throw new IllegalArgumentException("Lines must be perpendicular");
		}
		IntercellularLine horizontal = line1.orientation.isHorizontal() ? line1 : line2;
		IntercellularLine vertical = line1.orientation.isVertical() ? line1 : line2;
		x = vertical.constantCoord;
		y = horizontal.constantCoord;
	}

	@Override
	public BasicCell getCornerPointOfQuarter(OrdinalDirection side) {
		if (side == null) {
			throw new NullPointerException();
		}
		switch (side) {
			case NE:
				return new BasicCell(x, y);
			case SE:
				return new BasicCell(x, y + 1);
			case SW:
				return new BasicCell(x - 1, y + 1);
			case NW:
			default:
				return new BasicCell(x - 1, y);
		}
	}

}
