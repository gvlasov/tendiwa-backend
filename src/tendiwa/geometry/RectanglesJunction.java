package tendiwa.geometry;

import java.awt.Rectangle;

import tendiwa.core.meta.Direction;

/**
 * This class represents a junction between two rectangles that are neighbors
 * within a {@link RectangleSystem}
 * @author suseika
 *
 */
public class RectanglesJunction {
	private final Direction direction;
	private final int coordinate;
	private final int width;
	final RectangleArea r1;
	final RectangleArea r2;
	public RectanglesJunction(Direction direction, int coordinate, int width, RectangleArea r1, RectangleArea r2) {
		this.direction = direction;
		this.coordinate = coordinate;
		this.width = width;
		this.r1 = r1;
		this.r2 = r2;
	}
}
