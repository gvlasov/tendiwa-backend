package tendiwa.core;

/**
 * Same as {@link RenderCell}, but for borders between cells.
 */
public class RenderBorder {
private final int x;
private final int y;
private final CardinalDirection side;

public RenderBorder(int x, int y, CardinalDirection side) {
	assert side == Directions.N || side == Directions.W;
	this.x = x;
	this.y = y;
	this.side = side;
}
}
