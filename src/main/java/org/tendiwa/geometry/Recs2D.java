package org.tendiwa.geometry;

public final class Recs2D {
	/**
	 * @param rectangle
	 * @param amount
	 * @return
	 * @see org.tendiwa.geometry.Rectangle#stretch(int)
	 */
	public static Rectangle2D stretch(Rectangle rectangle, double amount) {
		return new Rectangle2D(
			rectangle.x - amount,
			rectangle.y - amount,
			rectangle.width + amount * 2,
			rectangle.height + amount * 2
		);
	}
}
