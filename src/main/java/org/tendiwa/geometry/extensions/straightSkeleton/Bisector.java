package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Vector2D;
import org.tendiwa.geometry.Vectors2D;

public final class Bisector {
	private final Vector2D vector;

	public Bisector(Vector2D cw, Vector2D ccw) {
		if (cw.isZero()) {
			throw new IllegalArgumentException("Trying to compute bisector when one of the vectors is 0");
		}
		if (ccw.isZero()) {
			throw new IllegalArgumentException("Trying to compute bisector when one of the vectors is 0");
		}
		Vector2D bisectorDirection = cw.normalize().add(ccw.normalize());
		if (bisectorDirection.isZero()) {
			bisectorDirection = ccw.rotateQuarterClockwise();
		}
		Vector2D bisector = bisectorDirection.normalize().multiply(averageMagnitude(cw, ccw));
		if (Vectors2D.perpDotProduct(cw, ccw) > 0) {
			bisector = bisector.reverse();
		}
		this.vector = bisector;
	}

	private double averageMagnitude(Vector2D cw, Vector2D ccw) {
		return (cw.magnitude() / 2 + ccw.magnitude() / 2);
	}

	public Vector2D asVector() {
		return vector;
	}
}
