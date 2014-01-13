package org.tendiwa.core;

/**
 * Same as {@link RenderCell}, but for borders between cells.
 */
public class RenderBorder {
private final int x;
private final int y;
private final CardinalDirection side;
private final BorderObject object;

public RenderBorder(int x, int y, CardinalDirection side, BorderObject object) {
	assert side != null;
	if (side != Directions.N && side != Directions.W) {
		if (side == Directions.E) {
			side = Directions.W;
			x += 1;
		} else {
			assert side == Directions.S;
			side = Directions.N;
			y += 1;
		}
	}
	this.x = x;
	this.y = y;
	this.side = side;
	this.object = object;
}

public int getX() {
	return x;
}

public int getY() {
	return y;
}

public CardinalDirection getSide() {
	return side;
}

public BorderObject getObject() {
	return object;
}
}
