package tendiwa.geometry;

/**
 * Placeable is a shape that consists of rectangles. Only Placeable objects can be placed into a {@link
 * RectangleSystemBuilder}.
 */
public interface Placeable {
/**
 * Returns a minimum rectangle that contains all rectangles in this template.
 *
 * @return Minimum rectangle that contains all rectangles in this template.
 */
EnhancedRectangle getBounds();

/**
 * Adds rectangles of this shape to RectangleSystemBuilder.
 *
 * @param builder
 * 	Builder to place Placeable's rectangles.
 * @param x
 * 	X-coordinate of the whole shape to be placed at.
 * @param y
 * 	Y-coordinate of the whole shape to ba placed at.
 * @return Bounding rectangle of the whole shape placed at [x:y]
 */
EnhancedRectangle place(RectangleSystemBuilder builder, int x, int y);

/**
 * Starts a chain of methods that create a new Placeable by repeating this one.
 *
 * @param count
 * 	How many times to repeat this placeable.
 * @return Next step of chain
 */
StepPlaceNextAt repeat(int count);

/**
 * Here can go any computations that must be performed before placing this Placeable into a {@link
 * RectangleSystemBuilder}. Usually this method is empty.
 *
 * @param builder
 * 	A RectangleSystemBuilder this Placeable goes to.
 */
void prebuild(RectangleSystemBuilder builder);

/**
 * Creates a new Placeable by rotating rectangles of this one.
 *
 * @param rotation
 * 	How to rotate.
 * @return A new Placeable.
 */
Placeable rotate(Rotation rotation);
}
