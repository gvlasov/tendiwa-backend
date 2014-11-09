package org.tendiwa.geometry.extensions.polygonRasterization;

final class ResultData {
	final int minX;
	final int minY;
	final boolean[][] bitmap;

	public ResultData(int minX, int minY, boolean[][] bitmap) {
		this.minX = minX;
		this.minY = minY;
		this.bitmap = bitmap;
	}
}
