package org.tendiwa.demos.geometry.bisectors;

import org.tendiwa.geometry.Vector2D;
import org.tendiwa.geometry.Bisector;

final class VectorsAndBisector {
	final Vector2D cw;
	final Vector2D ccw;

	VectorsAndBisector(Vector2D cw, Vector2D ccw) {
		this.cw = cw;
		this.ccw = ccw;
	}

	Vector2D bisector() {
		return new Bisector(cw, ccw).asInbetweenVector();
	}
}
