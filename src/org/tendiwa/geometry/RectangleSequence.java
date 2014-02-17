package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.core.Rotation;

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
public class RectangleSequence implements Iterable<EnhancedRectangle>, Placeable {
private static final AffineTransform TRANSFORM_CLOCKWISE = new AffineTransform(AffineTransform.getQuadrantRotateInstance(1, 0, 0));
private static final AffineTransform TRANSFORM_COUNTER_CLOCKWISE = new AffineTransform(AffineTransform.getQuadrantRotateInstance(3, 0, 0));
private static final AffineTransform TRANSFORM_HALF_CIRCLE = new AffineTransform(AffineTransform.getQuadrantRotateInstance(2, 0, 0));
/**
 * RectangleAreas that are parts of this RectangleSystem.
 */
protected LinkedHashSet<EnhancedRectangle> content;

/**
 * Creates an empty RectangleSequence.
 */
public RectangleSequence() {
	this.content = new LinkedHashSet<>();
}

@Override
public EnhancedRectangle place(RectangleSystemBuilder builder, int x, int y) {
	for (EnhancedRectangle r : content) {
		EnhancedRectangle actualRec = getActualRectangle(r, x, y);
		builder.placeRectangle(actualRec, DSL.atPoint(actualRec.getX(), actualRec.getY()));
	}
	EnhancedRectangle bounds = getBounds();
	return new EnhancedRectangle(x, y, bounds.getWidth(), bounds.getHeight());
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
	for (EnhancedRectangle r : content) {
		newRs.addRectangle(new EnhancedRectangle(transform.createTransformedShape(r.toAwtRectangle()).getBounds()));
	}
	return newRs;
}

@Override
/**
 * Returns a set of all rectangles contained in this RectangleSystem.
 * @return An immutable set of all rectangles from this system.
 */
public Collection<EnhancedRectangle> getRectangles() {
	return ImmutableSet.copyOf(content);
}

@Override
public final EnhancedRectangle getBounds() {
	int minX = Integer.MAX_VALUE;
	int minY = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int maxY = Integer.MIN_VALUE;
	for (EnhancedRectangle r : content) {
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
	return new EnhancedRectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
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
EnhancedRectangle getActualRectangle(EnhancedRectangle r, int x, int y) {
	assert content.contains(r);
	EnhancedRectangle boundingRec = getBounds();
	return new EnhancedRectangle(x + r.getX() - boundingRec.getX(), y + r.getY() - boundingRec.getY(), r.getWidth(), r.getHeight());
}

/**
 * Adds a new rectangle to this RectangleSequence. Doesn't check if the new rectangle overlaps any existing rectangles.
 *
 * @param r
 * 	New rectangle
 * @return Argument {@code r}
 */
public EnhancedRectangle addRectangle(EnhancedRectangle r) {
	content.add(r);
	return r;
}

@Override
public Iterator<EnhancedRectangle> iterator() {
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
public void excludeRectangle(EnhancedRectangle r) {
	if (!content.contains(r)) {
		throw new IllegalArgumentException("No rectangle " + r + " present in system");
	}
	content.remove(r);
}

}
