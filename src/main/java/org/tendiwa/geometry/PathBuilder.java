package org.tendiwa.geometry;

import org.jgrapht.graph.SimpleGraph;

/**
 * Builds new {@link InterrectangularPath}s using rectangles in a {@link org.tendiwa.geometry.RectangleSystemBuilder}.
 */
public class PathBuilder {

private final SimpleGraph<Rectangle, RectanglesJunction> path;
private final WidthStep widthStep = new WidthStep();
private final ShiftStep shiftStep = new ShiftStep();
private final WithStep withStep = new WithStep();
private final RectangleSystemBuilder builder;
private Rectangle rectangleToLinkSource;
private Rectangle rectangleToLinkEnd;
private int junctionWidth;

PathBuilder(RectangleSystemBuilder builder) {
	this.builder = builder;
	path = new SimpleGraph<>(RectanglesJunction.class);
}

public WithStep link(String name) {
	rectangleToLinkSource = builder.getByName(name).getBounds();
	return withStep;
}

public WithStep link(int index) {
	rectangleToLinkSource = builder.getByIndex(index).getBounds();
	return withStep;
}

public WithStep link(RectanglePointer pointer) {
	rectangleToLinkSource = builder.getRectangleByPointer(pointer).getBounds();
	return withStep;
}

public SimpleGraph<Rectangle, RectanglesJunction> build() {
	return path;
}

public class WithStep {
	public WidthStep with(int index) {
		rectangleToLinkEnd = builder.getByIndex(index).getBounds();
		return widthStep;
	}

	public WidthStep with(String name) {
		rectangleToLinkEnd = builder.getByName(name).getBounds();
		return widthStep;
	}

	public WidthStep with(RectanglePointer pointer) {
		rectangleToLinkEnd = builder.getRectangleByPointer(pointer).getBounds();
		return widthStep;
	}
}

public class WidthStep {
	/**
	 * A step of PathBuilder.link().with().width().shift() sequence. Specifies width of the junction that is being built.
	 *
	 * @param width Width of a new junction in cells.
	 * @return Next step of method chain — shift()
	 */
	public ShiftStep width(int width) {
		junctionWidth = width;
		return shiftStep;
	}
}

public class ShiftStep {
	public PathBuilder shift(int shift) {
		Segment segment = rectangleToLinkSource.getIntersectionSegment(rectangleToLinkEnd);
		path.addVertex(rectangleToLinkSource);
		path.addVertex(rectangleToLinkEnd);
		path.addEdge(
			rectangleToLinkSource,
			rectangleToLinkEnd,
			new RectanglesJunction(
				segment.getOrientation(),
				segment.getStartCoord() + shift,
				junctionWidth,
				rectangleToLinkSource,
				rectangleToLinkEnd
			)
		);
		return PathBuilder.this;
	}
}
}
