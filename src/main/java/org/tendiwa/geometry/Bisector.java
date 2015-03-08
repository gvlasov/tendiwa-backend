package org.tendiwa.geometry;

public final class Bisector {
	private final Vector2D vector;
	private final boolean isReflex;

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
		this.isReflex = ccw.makesReflexAngle(cw);
		this.vector = bisector;
	}

	private double averageMagnitude(Vector2D cw, Vector2D ccw) {
		return (cw.magnitude() / 2 + ccw.magnitude() / 2);
	}

	/**
	 * @return A vector of magnitude between |cw| and |ccw|.
	 * @see Bisector#Bisector(org.tendiwa.geometry.Vector2D,
	 * org.tendiwa.geometry.Vector2D)
	 */
	public Vector2D asSumVector() {
		return vector;
	}

	public Vector2D asInbetweenVector() {
		return isReflex ? vector.reverse() : vector;
	}
}
