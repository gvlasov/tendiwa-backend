package org.tendiwa.settlements;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Rectangle;

/**
 * A larger main rectangle with >= 0 other rectangles near it.
 * <p>
 * This class represents an approximation of a polygon with
 * axis-aligned rectangles.
 *
 * @see org.tendiwa.settlements.utils.RectangularBuildingLots For a way to create instances of this class.
 * @see org.tendiwa.settlements.EnclosedBlock For a polygon that is being approximated.
 */
public class RectangleWithNeighbors {
	public final Rectangle rectangle;
	public final ImmutableList<Rectangle> neighbors;

	public RectangleWithNeighbors(Rectangle rectangle, ImmutableList<Rectangle> neighbors) {
		this.rectangle = rectangle;
		this.neighbors = neighbors;
	}
}
