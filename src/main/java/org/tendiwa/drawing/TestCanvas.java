package org.tendiwa.drawing;

import org.tendiwa.geometry.Dimension;

public final class TestCanvas extends BaseAwtCanvas {
	public TestCanvas(int scale, Dimension size) {
		super(scale, 0, 0, size.width(), size.height());
	}
}
