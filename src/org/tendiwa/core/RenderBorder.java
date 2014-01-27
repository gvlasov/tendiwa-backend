package org.tendiwa.core;

/**
 * Same as {@link RenderCell}, but for borders between cells.
 */
public class RenderBorder extends  Border {
private final BorderObject object;

public RenderBorder(int x, int y, CardinalDirection side, BorderObject object) {
	super(x, y, side);
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
