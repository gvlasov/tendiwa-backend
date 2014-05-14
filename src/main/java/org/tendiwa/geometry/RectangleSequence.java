package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;

import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Represents a most basic placeable collection of non-overlapping rectangles. Unlike {@link RectangleSystem},
 * RectangleSequence doesn't maintain neighborship and outerness of rectangles.
 * <p/>
 * This is one of the most basic conceptions of terrain generation used in this framework, so you better consult
 * tutorial to get started with it.
 */
public class RectangleSequence implements Iterable<Rectangle>, Placeable {
private static final AffineTransform TRANSFORM_CLOCKWISE = new AffineTransform(AffineTransform.getQuadrantRotateInstance(1, 0, 0));
private static final AffineTransform TRANSFORM_COUNTER_CLOCKWISE = new AffineTransform(AffineTransform.getQuadrantRotateInstance(3, 0, 0));
private static final AffineTransform TRANSFORM_HALF_CIRCLE = new AffineTransform(AffineTransform.getQuadrantRotateInstance(2, 0, 0));
/**
 * RectangleAreas that are parts of this RectangleSystem.
 */
protected LinkedHashSet<Rectangle> content;

/**
 * Creates an empty RectangleSequence.
 */
public RectangleSequence() {
	this.content = new LinkedHashSet<>();
}

@Override
public Rectangle place(RectangleSystemBuilder builder, int x, int y) {
	for (Rectangle r : content) {
		Rectangle actualRec = getActualRectangle(r, x, y);
		builder.placeRectangle(actualRec, DSL.atPoint(actualRec.getX(), actualRec.getY()));
	}
	Rectangle bounds = getBounds();
	return new Rectangle(x, y, bounds.getWidth(), bounds.getHeight());
}

@Override
public StepPlaceNextAt repeat(int count) {
	return new StepPlaceNextAt(count, this);
}

@Override
public void prebuild(RectangleSystemBuilder builder) {
}

@Override
public Placeable rotate(Rotation rotation) {
    // TODO: Remove dependency on AWT AffineTransform from this code.
	AffineTransform transform;
	switch (rotation) {
		case CLOCKWISE:
			transform = TRANSFORM_CLOCKWISE;
			break;
		case COUNTER_CLOCKWISE:
			transform = TRANSFORM_COUNTER_CLOCKWISE;
			break;
		case HALF_CIRCLE:
			transform = TRANSFORM_HALF_CIRCLE;
			break;
		default:
			throw new IllegalArgumentException();
	}
	RectangleSequence newRs = new RectangleSequence();
	for (Rectangle r : content) {
		newRs.addRectangle(new Rectangle(transform.createTransformedShape(r.toAwtRectangle()).getBounds()));
	}
	return newRs;
}

@Override
/**
 * Returns a set of all rectangles contained in this RectangleSystem.
 * @return An immutable set of all rectangles from this system.
 */
public Collection<Rectangle> getRectangles() {
	return ImmutableSet.copyOf(content);
}

@Override
public final Rectangle getBounds() {
	int minX = Integer.MAX_VALUE;
	int minY = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int maxY = Integer.MIN_VALUE;
	for (Rectangle r : content) {
		if (r.getX() < minX) {
			minX = r.getX();
		}
		if (r.getY() < minY) {
			minY = r.getY();
		}
		if (r.getX() + r.getWidth() - 1 > maxX) {
			maxX = r.getX() + r.getWidth() - 1;
		}
		if (r.getY() + r.getHeight() - 1 > maxY) {
			maxY = r.getY() + r.getHeight() - 1;
		}
	}
	return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
}

/**
 * Transforms a relative-coordinates rectangle to actual-coordinates rectangle.
 *
 * @param r
 * 	Rectangle from this template (a rectangle with relative coordinates).
 * @param x
 * 	X coordinate of north-west point of actual bounding rectangle.
 * @param y
 * 	Y coordinate of north-west point of actual bounding rectangle.
 * @return Actual coordinates rectangle.
 */
Rectangle getActualRectangle(Rectangle r, int x, int y) {
	assert content.contains(r);
	Rectangle boundingRec = getBounds();
	return new Rectangle(x + r.getX() - boundingRec.getX(), y + r.getY() - boundingRec.getY(), r.getWidth(), r.getHeight());
}

/**
 * Adds a new rectangle to this RectangleSequence. Doesn't check if the new rectangle overlaps any existing rectangles.
 *
 * @param r
 * 	New rectangle
 * @return Argument {@code r}
 */
public Rectangle addRectangle(Rectangle r) {
	content.add(r);
	return r;
}

@Override
public Iterator<Rectangle> iterator() {
	return content.iterator();
}

/**
 * Removes a rectangle.
 *
 * @param r
 * 	A rectangle to remove.
 * @throws IllegalArgumentException
 * 	If rectangle {@code r} is not present in this RectangleSequence.
 */
public void excludeRectangle(Rectangle r) {
	if (!content.contains(r)) {
		throw new IllegalArgumentException("No rectangle " + r + " present in system");
	}
	content.remove(r);
}

}
