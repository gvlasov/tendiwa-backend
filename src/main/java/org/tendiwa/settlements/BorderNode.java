package org.tendiwa.settlements;

import org.tendiwa.geometry.Point2D;

class BorderNode extends Point2D {
	private final boolean isOnRegionBorder;

	BorderNode(double x, double y, boolean isOnRegionBorder) {
		super(x, y);
		this.isOnRegionBorder = isOnRegionBorder;
	}
}
