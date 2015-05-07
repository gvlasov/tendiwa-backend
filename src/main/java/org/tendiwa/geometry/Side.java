package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;

public interface Side extends OrthoCellSegment {
	CardinalDirection face();

	default int distanceTo(Side side) {
		if (!face().isOpposite(side.face())) {
			throw new IllegalArgumentException("You can only compute distance between opposite sides");
		}
		return Math.abs(
			getStaticCoord() - side.getStaticCoord()
		);
	}

	default Rectangle crust(int depth) {
		if (face() == Directions.N) {
			return new BasicRectangle(
				getX(),
				getY(),
				length(),
				depth
			);
		} else if (face() == Directions.E) {
			return new BasicRectangle(
				getX() - length() + 1,
				getY(),
				depth,
				length()
			);
		} else if (face() == Directions.S) {
			return new BasicRectangle(
				getX(),
				getY() - length() + 1,
				length(),
				depth
			);
		} else {
			assert face() == Directions.W;
			return new BasicRectangle(
				getX(),
				getY(),
				depth,
				length()
			);
		}
	}
}
