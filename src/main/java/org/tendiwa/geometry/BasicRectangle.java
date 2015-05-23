package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

public final class BasicRectangle implements Rectangle {
	private final int x;
	private final int y;
	private final int width;
	private final int height;

	public BasicRectangle(int x, int y, int width, int height) {
		if (width == 0 || height == 0) {
			throw new IllegalArgumentException(
				"Width or height can't be 0. Trying to create rectangle " + width + "×" + height
			);
		}
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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
		return this;
	}

	@Override
	public ImmutableCollection<NamedRecTree> parts() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RecTree part(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RecTree nestedPart(String name) {
		throw new UnsupportedOperationException();
	}
}
