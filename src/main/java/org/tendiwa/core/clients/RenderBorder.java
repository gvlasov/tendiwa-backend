package org.tendiwa.core.clients;

import org.tendiwa.core.Border;
import org.tendiwa.core.BorderObject;
import org.tendiwa.core.CardinalDirection;

/**
 * Same as {@link RenderCell}, but for borders between cells.
 */
public class RenderBorder extends Border {
	private final BorderObject object;
	private boolean isVisible;

	public RenderBorder(int x, int y, CardinalDirection side, BorderObject object) {
		super(x, y, side);
		this.object = object;
		this.isVisible = true;
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

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
}
