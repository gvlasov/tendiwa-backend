package org.tendiwa.geometry;

import org.jgrapht.graph.SimpleGraph;

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
		rectangleToLinkSource = builder.getByName(name).bounds();
		return withStep;
	}

	public WithStep link(int index) {
		rectangleToLinkSource = builder.getByIndex(index).bounds();
		return withStep;
	}

	public WithStep link(IntimateRectanglePointer pointer) {
		rectangleToLinkSource = pointer.find(builder).bounds();
		return withStep;
	}

	public SimpleGraph<Rectangle, RectanglesJunction> build() {
		return path;
	}

	public class WithStep {
		public WidthStep with(int index) {
			rectangleToLinkEnd = builder.getByIndex(index).bounds();
			return widthStep;
		}

		public WidthStep with(String name) {
			rectangleToLinkEnd = builder.getByName(name).bounds();
			return widthStep;
		}

		public WidthStep with(IntimateRectanglePointer pointer) {
			rectangleToLinkEnd = pointer.find(builder).bounds();
			return widthStep;
		}
	}

	public class WidthStep {
		/**
		 * A step of PathBuilder.link().with().width().shift() sequence. Specifies width of the junction that is being
		 * built.
		 *
		 * @param width
		 * 	Width of a new junction in cells.
		 * @return Next step of method chain — shift()
		 */
		public ShiftStep width(int width) {
			junctionWidth = width;
			return shiftStep;
		}
	}

	public class ShiftStep {
		public PathBuilder shift(int shift) {
			OrthoCellSegment segment = rectangleToLinkSource.getProjectionSegment(rectangleToLinkEnd);
			path.addVertex(rectangleToLinkSource);
			path.addVertex(rectangleToLinkEnd);
			path.addEdge(
				rectangleToLinkSource,
				rectangleToLinkEnd,
				new RectanglesJunction(
					segment.orientation(),
					segment.min() + shift,
					junctionWidth,
					rectangleToLinkSource,
					rectangleToLinkEnd
				)
			);
			return PathBuilder.this;
		}
	}
}
