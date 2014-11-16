package org.tendiwa.drawing;

public final class MagnifierCanvas extends BaseTestCanvas {
	public MagnifierCanvas(int scale, int centerX, int centerY, int pixelWidth, int pixelHeight) {
		super(
			scale,
			centerX - pixelWidth / scale / 2,
			centerY - pixelHeight / scale / 2,
			pixelWidth / scale,
			pixelHeight / scale
		);
	}
}
