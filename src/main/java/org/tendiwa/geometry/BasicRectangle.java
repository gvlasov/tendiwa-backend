package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;
import org.tendiwa.core.Orientation;

public final class BasicRectangle implements Rectangle {
	private final int x;
	private final int y;
	private final int width;
	private final int height;

	public BasicRectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if (width == 0 || height == 0) {
			throw new IllegalArgumentException("Width or height can't be 0");
		}
	}

	public BasicRectangle(Rectangle r) {
		this(r.x(), r.y(), r.width(), r.height());
	}

	@Override
	public String toString() {
		return "{" + x + "," + y + "," + width + "," + height + "}";
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}

	@Override
	public ImmutableCollection<NamedRectSet> parts() {
		return null;
	}

	@Override
	public RectSet part(String name) {
		return null;
	}

	@Override
	public RectSet nestedPart(String name) {
		return null;
	}
}
