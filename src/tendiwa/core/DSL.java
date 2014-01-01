package tendiwa.core;

import tendiwa.drawing.TestCanvas;

public class DSL {
public static final RectanglePointer LAST_RECTANGLE = RectanglePointer.LAST_RECTANGLE;
public static final RectanglePointer FIRST_RECTANGLE = RectanglePointer.FIRST_RECTANGLE;
public static final RectanglePointer REMEMBERED_RECTANGLE = RectanglePointer.REMEMBERED_RECTANGLE;
public static final RectanglePointer LAST_BOUNDING_REC = RectanglePointer.LAST_BOUNDING_REC;
public static final RectanglePointer REMEMBERED_BOUNDING_REC = RectanglePointer.REMEMBERED_BOUNDING_REC;
public static final RectanglePointer FOUND_RECTANGLES = RectanglePointer.FOUND_RECTANGLES;
public static final Rotation CLOCKWISE = Rotation.CLOCKWISE;
public static final Rotation COUNTER_CLOCKWISE = Rotation.COUNTER_CLOCKWISE;
public static final Rotation HALF_CIRCLE = Rotation.HALF_CIRCLE;
public static final CardinalDirection E = Directions.E;
public static final CardinalDirection N = Directions.N;
public static final CardinalDirection W = Directions.W;
public static final CardinalDirection S = Directions.S;
public static final OrdinalDirection NE = Directions.NE;
public static final OrdinalDirection NW = Directions.NW;
public static final OrdinalDirection SE = Directions.SE;
public static final OrdinalDirection SW = Directions.SW;

private DSL() {
}

public static EnhancedRectangle rectangle(int width, int height) {
	return new EnhancedRectangle(0, 0, width, height);
}

public static RectangleSystemBuilder builder(int borderWidth) {
	return new RectangleSystemBuilder(borderWidth);
}

public static StepAwayFrom awayFrom(RectanglePointer pointer) {
	return new StepAwayFrom(pointer);
}

public static StepNear near(RectanglePointer pointer) {
	return new StepNear(pointer);
}

public static StepUnitedWith unitedWith(RectanglePointer pointer) {
	return new StepUnitedWith(pointer);
}

public static Placement somewhere() {
	return new Placement() {
		@Override
		public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
			return placeable.place(builder, 0, 0);
		}
	};
}

public static Placement atPoint(final int x, final int y) {
	return new Placement() {
		@Override
		public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
			return placeable.place(builder, x, y);
		}
	};
}

public static TestCanvas canvas() {
	return TestCanvas.builder().build();
}

public static TestCanvas canvas(int scale) {
	return TestCanvas.builder().setScale(scale).build();
}

public static PathBuilder path(RectangleSystemBuilder builder) {
	return new PathBuilder(builder);
}

public static WorldRectangleBuilder worldBuilder() {
	return new WorldRectangleBuilder();
}

public static StepRectangle recursivelySplitRec(int width, int height) {
	return new StepRectangle(width, height);
}
}
