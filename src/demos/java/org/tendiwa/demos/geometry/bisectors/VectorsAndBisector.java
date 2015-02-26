package org.tendiwa.demos.geometry.bisectors;

import org.tendiwa.geometry.Vector2D;
import org.tendiwa.geometry.extensions.straightSkeleton.Bisector;

final class VectorsAndBisector {
	final Vector2D cw;
	final Vector2D ccw;
	final Vector2D bisector;

	VectorsAndBisector(Vector2D cw, Vector2D ccw) {
		this.cw = cw;
		this.ccw = ccw;
		this.bisector = new Bisector(cw, ccw).asVector();
	}
}
