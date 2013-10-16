package tendiwa.core;

import java.awt.*;

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
	public Point getCornerPointOfQuarter(OrdinalDirection side) {
		if (side == null) {
			throw new NullPointerException();
		}
		switch (side) {
			case NE:
				return new Point(x,y);
			case SE:
				return new Point(x, y+1);
			case SW:
				return new Point(x-1, y+1);
			case NW:
			default:
				return new Point(x-1, y);
		}
	}

}
