package org.tendiwa.core;

import java.util.Arrays;
import java.util.List;

/**
 * CardinalDirection is not very different from OrdinalDirection, apart from that CardinalDirection has {@link
 * Orientation}.
 *
 * @author suseika
 */
public enum CardinalDirection implements Direction {
	N, E, S, W;
public static final int[] dx = new int[]{0, 1, 0, -1};
public static final int[] dy = new int[]{-1, 0, 1, 0};
public static final List<CardinalDirection> ALL = Arrays.asList(N, E, S, W);

/**
 * Returns a direction corresponding to a number. Clockwise: 0 is N, 1 is E, 2 is S, 3 is W.
 *
 * @param index
 * 	A number in [0..3] range.
 * @return A direction corresponding to that number.
 * @throws IllegalArgumentException
 * 	If {@code index} is not in range [0..3].
 */
public static CardinalDirection sideFromCardinalIndex(int index) {
	if (index == 0) {
		return N;
	}
	if (index == 1) {
		return E;
	}
	if (index == 2) {
		return S;
	}
	if (index == 3) {
		return W;
	}
	throw new IllegalArgumentException("Only indices 0 to 3 inclusive are allowed (you provided index " + index + ")");
}

/**
 * Returns an int corresponding to CardinalDirection.
 * <p/>
 * {@link CardinalDirection#N} is 0
 * <p/>
 * {@link CardinalDirection#E} is 2
 * <p/>
 * {@link CardinalDirection#S} is 4
 * <p/>
 * {@link CardinalDirection#W} is 6
 *
 * @see {@link OrdinalDirection#toInt()}
 */
@Override
public int toInt() {
	switch (this) {
		case N:
			return 0;
		case E:
			return 2;
		case S:
			return 4;
		case W:
		default:
			return 6;
	}
}

@Override
public OrdinalDirection clockwise() {
	switch (this) {
		case N:
			return OrdinalDirection.NE;
		case E:
			return OrdinalDirection.SE;
		case S:
			return OrdinalDirection.SW;
		case W:
		default:
			return OrdinalDirection.NW;
	}
}

@Override
public OrdinalDirection counterClockwise() {
	switch (this) {
		case N:
			return OrdinalDirection.NW;
		case E:
			return OrdinalDirection.NE;
		case S:
			return OrdinalDirection.SE;
		case W:
		default:
			return OrdinalDirection.SW;
	}
}

@Override
public CardinalDirection clockwiseQuarter() {
	switch (this) {
		case N:
			return E;
		case E:
			return S;
		case S:
			return W;
		case W:
		default:
			return N;
	}
}

@Override
public CardinalDirection counterClockwiseQuarter() {
	switch (this) {
		case N:
			return W;
		case E:
			return N;
		case S:
			return E;
		case W:
		default:
			return S;
	}
}

@Override
public CardinalDirection opposite() {
	switch (this) {
		case N:
			return S;
		case E:
			return W;
		case S:
			return N;
		case W:
		default:
			return E;
	}
}

@Override
public String toString() {
	switch (this) {
		case N:
			return "N";
		case E:
			return "E";
		case S:
			return "S";
		case W:
		default:
			return "W";
	}
}

/**
 * Returns orientation of a CardinalDirection.
 *
 * @return {@link Orientation#VERTICAL} if this is {@link Directions#N} or {@link Directions#S}, or {@link
 *         Orientation#HORIZONTAL} if this is {@link Directions#W} or {@link Directions#E}.
 */
public Orientation getOrientation() {
	switch (this) {
		case N:
		case S:
			return Orientation.VERTICAL;
		default:
			return Orientation.HORIZONTAL;
	}
}

@Override
public int[] side2d() {
	switch (this) {
		case N:
			return new int[]{
				0, -1
			};
		case E:
			return new int[]{
				1, 0
			};
		case S:
			return new int[]{
				0, 1
			};
		default:
			assert this == W;
			return new int[]{
				-1, 0
			};
	}
}

@Override
public boolean isOpposite(Direction direction) {
	if (direction == null) {
		throw new NullPointerException();
	}
	switch (this) {
		case N:
			return direction == S;
		case E:
			return direction == W;
		case S:
			return direction == N;
		default:
			assert this == W;
			return direction == E;
	}
}

/**
 * Returns true if going this direction increases value of x or y coordinate, or false if it decreases coordinate
 * <p/>
 * (Going some CardinalDirection can't leave coordinate the same or increase one while decreasing another).
 *
 * @return true if this direction is {@link Directions#E} or {@link Directions#S}, false otherwise.
 */
public boolean isGrowing() {
	switch (this) {
		case E:
		case S:
			return true;
		default:
			return false;

	}
}

/**
 * Returns true if an arrow pointing in this direction would be vertical.
 *
 * @return true if this direction is {@link Directions#N} or {@link Directions#S}, false otherwise.
 */
public boolean isVertical() {
	switch (this) {
		case N:
		case S:
			return true;
		default:
			return false;

	}
}

@Override
public boolean isPerpendicular(Direction direction) {
	if (this == N || this == S) {
		if (direction == E || direction == W) {
			return true;
		}
		return false;
	} else {
		assert this == E || this == W;
		if (direction == N || direction == S) {
			return true;
		}
		return false;
	}
}

@Override
public boolean isCardinal() {
	return true;
}

/**
 * Returns 1 if going this direction increases coordinate, or -1 if going this direction decreases coordinate.
 *
 * @return
 * @see CardinalDirection#isGrowing()
 * @see Direction#side2d()
 */
public int getGrowing() {
	switch (this) {
		case N:
			return -1;
		case E:
			return 1;
		case S:
			return 1;
		case W:
			return -1;
		default:
			throw new Error();
	}
}

/**
 * <p> Returns the coordinate that lies closer in this direction. </p>
 *
 * @param a
 * @param b
 * @return If this direction is a growing one, returns the least of two integers, otherwise returns the greatest.
 * @see CardinalDirection#isGrowing()
 * @see CardinalDirection#furthestCoordOf(int, int)
 */
public int closestCoordOf(int a, int b) {
	if (isGrowing()) {
		return Math.min(a, b);
	} else {
		return Math.max(a, b);
	}
}

/**
 * Returns the coordinate that lies further in this direction.
 *
 * @param a
 * 	One value.
 * @param b
 * 	Another value (order doesn't matter).
 * @return If this direction is a growing one ({@link org.tendiwa.core.CardinalDirection#isGrowing()}), returns the
 *         greatest of two integers, otherwise returns the least.
 * @see CardinalDirection#isGrowing()
 * @see CardinalDirection#closestCoordOf(int, int)
 */
public int furthestCoordOf(int a, int b) {
	if (isGrowing()) {
		return Math.max(a, b);
	} else {
		return Math.min(a, b);
	}
}

/**
 * Returns 0 for {@link Directions#N}, 1 for {@link Directions#E}, 2 for {@link Directions#S} and 3 for {@link
 * Directions#W}.
 * <p/>
 * This method is convenient when you need to store values corresponding to CardinalSides in an array.
 *
 * @return 0 for N, 1 for E, 2 for S and 3 for W.
 */
public int getCardinalIndex() {
	if (this == N) {
		return 0;
	}
	if (this == E) {
		return 1;
	}
	if (this == S) {
		return 2;
	}
	assert this == W;
	return 3;
}

/**
 * Checks whether this direction is a horizontal one.
 *
 * @return Returns true if this is {@link CardinalDirection#E} or {@link CardinalDirection#W}, otherwise returns false.
 */
public boolean isHorizontal() {
	return this == E || this == W;
}

}
