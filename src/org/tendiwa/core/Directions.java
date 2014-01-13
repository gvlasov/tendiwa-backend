package org.tendiwa.core;

import org.tendiwa.core.meta.Chance;

public class Directions {
	public static final CardinalDirection N = CardinalDirection.N;
	public static final OrdinalDirection NE = OrdinalDirection.NE;
	public static final CardinalDirection E = CardinalDirection.E;
	public static final OrdinalDirection SE = OrdinalDirection.SE;
	public static final CardinalDirection S = CardinalDirection.S;
	public static final OrdinalDirection SW = OrdinalDirection.SW;
	public static final CardinalDirection W = CardinalDirection.W;
	public static final OrdinalDirection NW = OrdinalDirection.NW;
	public static final CardinalDirection[] CARDINAL_DIRECTIONS = {
		CardinalDirection.N,
		CardinalDirection.E,
		CardinalDirection.S,
		CardinalDirection.W
	};
	public static final Direction[] ALL_DIRECTIONS = {
		CardinalDirection.N,
		OrdinalDirection.NE,
		CardinalDirection.E,
		OrdinalDirection.SE,
		CardinalDirection.S,
		OrdinalDirection.SW,
		CardinalDirection.W,
		OrdinalDirection.NW
	};
	static double TAN1;
	static double TAN2;

	static {
		TAN1 = Math.tan(Math.PI / 8);
		TAN2 = Math.tan(Math.PI / 8 * 3);
	}

	/**
	 * Returns Direction corresponding to number direction. Integer direction
	 * must be from 1 to 8, 1 is for SideTest.N, each next is for the next
	 * direction clockwise.
	 */
	public static Direction intToDirection(int integer) {
		switch (integer) {
			case 0:
				return N;
			case 1:
				return NE;
			case 2:
				return E;
			case 3:
				return SE;
			case 4:
				return S;
			case 5:
				return SW;
			case 6:
				return W;
			case 7:
				return NW;
			default:
				throw new Error("Not appropriate direction int!");
		}
	}
	public static OrdinalDirection getDirectionBetween(CardinalDirection dir1, CardinalDirection dir2) {
		if (dir1 == N) {
			if (dir2 == E) {
				return NE;
			} else if (dir2 == W) {
				return NW;
			}
		} else if (dir1 == E) {
			if (dir2 == N) {
				return NE;
			} else if (dir2 == S) {
				return SE;
			}
		} else if (dir1 == S) {
			if (dir2 == E) {
				return SE;
			} else if (dir2 == W) {
				return SW;
			}
		} else if (dir1 == W) {
			if (dir2 == N) {
				return NW;
			} else if (dir2 == S) {
				return SW;
			}
		}
		throw new IllegalArgumentException(
			"Sides " + dir1 + " and " + dir2 + " are not close as cardinal directions");
	}
	/**
	 * Converts shift in cells to a cardinal direction by finding out in what
	 * 1/8 of full circle lies the shifted point.
	 * 
	 * @param dx
	 *            Shift in cells by x-axis, any integer.
	 * @param dy
	 *            Shift in cells by y-axis, any integer.
	 * @return Direction that corresponds to the given shift.
	 */
	public static Direction shiftToDirection(int dx, int dy) {
		assert !(dx == 0 && dy == 0);
		// I quarter of the cartesian coordinate system where y-axis points
		// down.
		if (dx >= 0 && dy > 0) {
			if (dx / dy > TAN2) {
				return E;
			}
			if (dx / dy < TAN1) {
				return S;
			}
			return SE;
		}
		// II quarter
		if (dx <= 0 && dy > 0) {
			if (dx / dy < -TAN2) {
				return W;
			}
			if (dx / dy > -TAN1) {
				return S;
			}
			return SW;
		}
		// III quarter
		if (dx <= 0 && dy < 0) {
			if (dx / dy > TAN2) {
				return W;
			}
			if (dx / dy < TAN1) {
				return N;
			}
			return NW;
		}
		if (dy == 0) {
			if (dx > 0) {
				return E;
			}
			return W;
		}
		// IV quarter
		if (dx / dy < -TAN2) {
			return E;
		}
		if (dx / dy > -TAN1) {
			return N;
		}
		return NE;
	}
	/**
	 * Randomly returns one of {@link CardinalDirection}s.
	 */
	public static CardinalDirection getRandomCardinal() {
		switch (Chance.rand(1, 4)) {
			case 1:
				return N;
			case 2:
				return E;
			case 3:
				return S;
			case 4:
			default:
				return W;
		}
	}
}
