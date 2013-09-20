package tendiwa.geometry;

import java.awt.*;

public class EnhancedPoint extends Point {
	private static final long serialVersionUID = -437683005315402667L;

	public EnhancedPoint(int x, int y) {
		super(x, y);
	}
	@Override
	public String toString() {
		return x + ":" + y;
	}

	/**
	 * Mutates this object changing its {@link Point#x} and {@link Point#y}.
	 * 
	 * @param direction
	 * @return
	 */
	public EnhancedPoint moveToSide(Direction direction) {
		int[] d = direction.side2d();
		x += d[0];
		y += d[1];
		return this;
	}

	public static EnhancedPoint fromStaticAndDynamic(int staticCoord, int dynamicCoord, Orientation orientation) {
		if (orientation.isVertical()) {
			return new EnhancedPoint(staticCoord, dynamicCoord);
		} else {
			return new EnhancedPoint(dynamicCoord, staticCoord);
		}
	}
	/**
	 * Returns a static coord if this point was a part of a line with given
	 * orientation.
	 * 
	 * @param orientation
	 * @return this.x if orientation is {@link Orientation#VERTICAL}, or this.y
	 *         if orientation is {@link Orientation#HORIZONTAL}
	 */
	public int getStaticCoord(Orientation orientation) {
		if (orientation.isVertical()) {
			return x;
		} else {
			return y;
		}
	}
	/**
	 * Returns a dynamic coord if this point was a part of a line with given
	 * orientation.
	 * 
	 * @param orientation
	 * @return this.x if orientation is {@link Orientation#HORIZONTAL}, or
	 *         this.y if orientation is {@link Orientation#VERTICAL}
	 */
	public int getDynamicCoord(Orientation orientation) {
		if (orientation.isHorizontal()) {
			return x;
		} else {
			return y;
		}
	}
}
