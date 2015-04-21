package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;

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
}
