package tendiwa.geometry;


/**
 * This class represents a junction between two rectangles. Is a rectangle that overlaps two rectangles with a single
 * {@link Segment} each. Junction consists of those two segments and space between rectangles. You can think of junction
 * as of a bridge with particular width between two rectangles.
 *
 * @author suseika
 */
public class RectanglesJunction {
final EnhancedRectangle r1;
final EnhancedRectangle r2;
private final Orientation orientation;
private final int coordinate;
private final int width;

public RectanglesJunction(Orientation orientation, int coordinate, int width, EnhancedRectangle r1, EnhancedRectangle r2) {
	this.orientation = orientation;
	this.coordinate = coordinate;
	this.width = width;
	this.r1 = r1;
	this.r2 = r2;
}

Segment getSegmentOnRectangle(EnhancedRectangle r) {
	throw new UnsupportedOperationException();
}

EnhancedRectangle getRectangleBetweenRectangles() {
	throw new UnsupportedOperationException();
}
}
