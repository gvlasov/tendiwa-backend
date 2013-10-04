package tendiwa.geometry;

import java.awt.*;

/**
 * <p>EnhancedPoint introduces several useful methods over Point class, as well as introduces a new concept of
 * <b>dynamic coordinate</b> and <b>static coordinate</b>. They are the same as x-coordinate and y-coordinate. </p>
 * <p/> <p>X-coordinate is a horizontal dynamic coordinate, and it is a vertical static coordinate.</p> <p>On the
 * contrary, y-coordinate is a vertical dynamic coordinate and a horizontal static coordinate.</p> <p>Think of it the
 * following way: if you take a horizontal line consisting of points, each point will have the same y-coordinate (hence
 * y is horizontal static) and different x coordinate (so x is horizontal dynamic)</p>
 */
public class EnhancedPoint extends Point {
private static final long serialVersionUID = -437683005315402667L;

public EnhancedPoint(int x, int y) {
	super(x, y);
}

public static EnhancedPoint fromStaticAndDynamic(int staticCoord, int dynamicCoord, Orientation orientation) {
	if (orientation.isVertical()) {
		return new EnhancedPoint(staticCoord, dynamicCoord);
	} else {
		return new EnhancedPoint(dynamicCoord, staticCoord);
	}
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
