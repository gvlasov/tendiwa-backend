package org.tendiwa.geometry;

import org.tendiwa.core.Orientation;

final class CellsBetween {
	private final Rectangle a;
	private final Rectangle b;
	private final Orientation orientation;

	CellsBetween(
		Rectangle a,
		Rectangle b,
		Orientation orientation
	) {
		this.a = a;
		this.b = b;
		this.orientation = orientation;
	}

	int width() {
		int staticCoord1 = b.getMinStaticCoord(orientation);
		int staticCoord2 = a.getMinStaticCoord(orientation);
		int staticLength1 = b.getStaticLength(orientation);
		int staticLength2 = a.getStaticLength(orientation);
		assert staticCoord1 != staticCoord2 : "Rectangles can't have same static coord";
		assert !a.intersection(b).isPresent() : "Rectangles can't overlap";
		if (staticCoord1 > staticCoord2) {
			assert staticCoord1 - staticCoord2 - staticLength2 >= 0;
			return staticCoord1 - staticCoord2 - staticLength2;
		} else {
			assert staticCoord2 - staticCoord1 - staticLength1 >= 0;
			return staticCoord2 - staticCoord1 - staticLength1;
		}
	}
}
