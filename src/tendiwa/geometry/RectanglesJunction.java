package tendiwa.geometry;

/**
 * This class represents a junction between two rectangles that are neighbors within a {@link RectangleSystem}
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
}
