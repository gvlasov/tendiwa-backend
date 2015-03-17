package org.tendiwa.settlements.utils.streetsDetector;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import javax.annotation.Nonnull;

/**
 * Two segments ("bones") sharing an endpoint. Joints are sortable by the angle between the bones.
 */
final class Joint implements Comparable<Joint> {
	public static final boolean ANY_BOOLEAN_VALUE = true;
	/**
	 * Angle in radians between segments coming from the same Point2D.
	 * Value of this field may be {@code > Math.PI} angle or the corresponding {@code < Math.PI} angle (depending
	 * on whether you go clockwise or counterclockwise to measure angle),
	 * it doesn't matter for {@link #compareTo(Joint)}.
	 */
	private final double angle;
	private final Point2D start;
	private final Point2D end;
	private final Point2D middle;
	final Segment2D bone1;
	final Segment2D bone2;

	Joint(Segment2D bone1, Segment2D bone2) {
		if (bone2.oneOfEndsIs(bone1.start)) {
			this.middle = bone1.start;
		} else {
			assert bone2.oneOfEndsIs(bone1.end);
			this.middle = bone1.end;
		}
		this.start = bone1.anotherEnd(middle);
		this.end = bone2.anotherEnd(middle);
		this.bone1 = bone1;
		this.bone2 = bone2;
		this.angle = angle();
	}

	private double angle() {
		double angle = Vectors2D.angleBetweenVectors(
			new double[]{start.x - middle.x, start.y - middle.y},
			new double[]{end.x - middle.x, end.y - middle.y},
			ANY_BOOLEAN_VALUE
		);
		if (angle > Math.PI) {
			angle = Math.PI * 2 - angle;
		}
		assert angle > 0 && angle < Math.PI + Vectors2D.EPSILON;
		return angle;
	}

	boolean isAngleTooExtreme() {
		return Math.abs(angle - Math.PI) > Math.PI / 2;
	}

	@Override
	public int compareTo(@Nonnull Joint o) {
		double diff = Math.abs(angle - Math.PI) - Math.abs(o.angle - Math.PI);
		if (diff > 0) {
			return 1;
		}
		if (diff < 0) {
			return -1;
		}
		double slope = start.angleTo(end);
		double anotherSlope = o.start.angleTo(o.end);
		if (slope > anotherSlope) {
			return 1;
		}
		if (slope < anotherSlope) {
			return -1;
		}
		return 0;
	}
}
