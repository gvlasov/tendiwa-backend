package org.tendiwa.core;

public interface Direction {
public int toInt();

/**
 * Returns a direction that is clockwise from this direction, e.g for N returns NE.
 *
 * @return CardinalDirection or OrdinalDirection enums instance.
 */
public Direction clockwise();

/**
 * Returns a direction that is counterclockwise from this direction, e.g for N returns NE.
 *
 * @return CardinalDirection or OrdinalDirection enums instance.
 */
public Direction counterClockwise();

/**
 * Same as calling direction.clockwise().clockwise(), where the resulting direction will be one quarter of full circle
 * clockwise from initial direction.
 */
public Direction clockwiseQuarter();

/**
 * Same as calling direction.counterClockwise().counterClockwise(), where the resulting direction will be one quarter
 * of full circle counterclockwise from initial direction.
 */
public Direction counterClockwiseQuarter();

public Direction opposite();

/**
 * Converts direction to shift in cells by x-axis and y-axis.
 *
 * @return An array of 2 ints where [0] is shift by x-axis, and 1 is shift by y-axis.
 */
public int[] side2d();

public boolean isOpposite(Direction direction);

public boolean isPerpendicular(Direction direction);

boolean isCardinal();
}
