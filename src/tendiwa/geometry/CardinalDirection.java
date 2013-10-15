package tendiwa.geometry;

import java.util.Arrays;
import java.util.List;

/**
 * CardinalDirection is not very different from OrdinalDirection, apart from
 * that CardinalDirection has {@link Orientation}.
 * 
 * @author suseika
 * 
 */
public enum CardinalDirection implements Direction {
	N, E, S, W;

	public static final List<CardinalDirection> ALL = Arrays.asList(N, E, S, W);

	/**
	 * <p>
	 * Returns an int corresponding to CardinalDirection.
	 * </p>
	 * <ul>
	 * <li>0 is {@link CardinalDirection#N}</li>
	 * <li>2 is {@link CardinalDirection#E}</li>
	 * <li>4 is {@link CardinalDirection#S}</li>
	 * <li>6 is {@link CardinalDirection#W}</li>
	 * </ul>
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

	public Orientation getOrientation() {
		switch (this) {
			case N:
			case S:
				return Orientation.VERTICAL;
			default:
				return Orientation.HORIZONTAL;
		}
	}
	public int[] side2d() {
		switch (this) {
			case N:
				return new int[] {
					0, -1
				};
			case E:
				return new int[] {
					1, 0
				};
			case S:
				return new int[] {
					0, 1
				};
			case W:
			default:
				return new int[] {
					-1, 0
				};
		}
	}
	public boolean isOpposite(Direction direction) {
		if (direction == null) {
			throw new NullPointerException();
		}
		switch (this) {
			case N:
				return direction == S ? true : false;
			case E:
				return direction == W ? true : false;
			case S:
				return direction == N ? true : false;
			case W:
			default:
				return direction == E ? true : false;
		}
	}

	public boolean isGrowing() {
		switch (this) {
			case E:
			case S:
				return true;
			default:
				return false;

		}
	}
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
	 * Returns 1 if going this direction increases coordinate, or -1 if going
	 * this direction decreases coordinate.
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
	 * <p>
	 * Returns the coordinate that lies closer in this direction.
	 * </p>
	 * 
	 * @param a
	 * @param b
	 * @see CardinalDirection#isGrowing()
	 * @see CardinalDirection#furthestCoordOf(int, int)
	 * @return If this direction is a growing one, returns the least of two
	 *         integers, otherwise returns the greatest.
	 */
	public int closestCoordOf(int a, int b) {
		if (isGrowing()) {
			return Math.min(a, b);
		} else {
			return Math.max(a, b);
		}
	}
	/**
	 * <p>
	 * Returns the coordinate that lies further in this direction. That is,
	 * </p>
	 * 
	 * @param a
	 * @param b
	 * @see CardinalDirection#isGrowing()
	 * @see CardinalDirection#closestCoordOf(int, int)
	 * @see 
	 * @return If this direction is a growing one, returns the greatest of two
	 *         integers, otherwise returns the least.
	 */
	public int furthestCoordOf(int a, int b) {
		if (isGrowing()) {
			return Math.max(a, b);
		} else {
			return Math.min(a, b);
		}
	}
}
