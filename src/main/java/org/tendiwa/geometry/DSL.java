package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;
import org.tendiwa.core.OrdinalDirection;

public class DSL {
	public static final Rotation CLOCKWISE = Rotation.CLOCKWISE;
	public static final Rotation COUNTER_CLOCKWISE = Rotation.COUNTER_CLOCKWISE;
	public static final Rotation HALF_CIRCLE = Rotation.HALF_CIRCLE;
	public static final CardinalDirection E = Directions.E;
	public static final CardinalDirection N = Directions.N;
	public static final CardinalDirection W = Directions.W;
	public static final CardinalDirection S = Directions.S;
	public static final OrdinalDirection NE = Directions.NE;
	public static final OrdinalDirection NW = Directions.NW;
	public static final OrdinalDirection SE = Directions.SE;
	public static final OrdinalDirection SW = Directions.SW;

	private DSL() {
	}

	public static StepAwayFrom awayFrom(RectanglePointer pointer) {
		return new StepAwayFrom(pointer);
	}

	public static StepNear near(RectanglePointer pointer) {
		return new StepNear(pointer);
	}

	public static StepUnitedWith unitedWith(RectanglePointer pointer) {
		return new StepUnitedWith(pointer);
	}

	public static Placement somewhere() {
		return (placeable, builder) -> placeable;
	}

	public static Placement atPoint(final int x, final int y) {
		return (placeable, builder) -> {
			Rectangle bounds = placeable.bounds();
			return placeable.translate(x - bounds.x(), y - bounds.y());
		};
	}

	public static StepRectangle recursivelySplitRec(int width, int height) {
		return new StepRectangle(width, height);
	}
}
