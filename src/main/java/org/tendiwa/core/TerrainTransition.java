package org.tendiwa.core;

import org.tendiwa.core.meta.Chance;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.*;

import java.util.HashSet;

/**
 * This class creates smooth transitions from one ammunitionType of terrain (or other {@link PlaceableInCell} entities)
 * to another.
 * <p/>
 * It takes a rectangle and marks several of its {@link Direction}s as a "from" directions, and from those edges of the
 * rectangle will gradually be placed tiles that contain certain placeable entities. They will head to the middle of the
 * rectangle and after several cells will be replaced by {@code to} entities.
 */
public class TerrainTransition {
private static final TerrainDiffusionStopCondition DEFAULT_STOP_CONDITION = new TerrainDiffusionStopCondition() {

	@Override
	public boolean check(int depth, int iterationNumber) {
		int diffusionStartDepth = 8;
		if (depth - iterationNumber < diffusionStartDepth) {
			return Chance.roll(30);
		}
		return false;
	}
};

/**
 * @param from
 * 	Entities to come from border.
 */
public TerrainTransition(Location location, Rectangle rectangle, TypePlaceableInCell from, int depth, HashSet<Direction> fromDirections, TerrainDiffusionStopCondition stopCondition) {
	for (Direction dir : fromDirections) {
		if (!dir.isCardinal()) {
			continue;
		}
		CardinalDirection cardinalDirection = (CardinalDirection) dir;
		int dimensionBySide = rectangle.getDimensionBySide(cardinalDirection);
		Segment segmentWithoutCorners = rectangle.getSegmentInsideFromSide(
			cardinalDirection,
			0,
			dimensionBySide
		);

		// Place cells
		int segmentEndCoord = segmentWithoutCorners.getEndCoord();
		for (
			int segmentDynamicCoord = segmentWithoutCorners.getStartCoord();
			segmentDynamicCoord <= segmentEndCoord;
			segmentDynamicCoord++
			) {
			// This loop places `from` elements.
			int columnDynamicCoord = segmentWithoutCorners.getStaticCoord();
			int growing = -cardinalDirection.getGrowing();
			int i = 0;
			for (;
			     columnDynamicCoord != segmentWithoutCorners.getStaticCoord() + depth * growing;
			     columnDynamicCoord += growing
				) {
				if (stopCondition.check(depth, i)) {
					break;
				}
				int x, y;
				if (segmentWithoutCorners.getOrientation().isHorizontal()) {
					x = segmentDynamicCoord;
					y = columnDynamicCoord;
				} else {
					x = columnDynamicCoord;
					y = segmentDynamicCoord;
				}
				location.getActivePlane().place(from, location.x + x, location.y + y);
				i++;
			}
		}
		// Draw corners
		OrdinalDirection corner = cardinalDirection.clockwise();
		CardinalDirection perpendicularAfterCorner = corner.clockwise();
		boolean hasCorner = fromDirections.contains(corner);
		boolean hasPerpendicularSide = fromDirections.contains(corner.clockwise());
		if (hasPerpendicularSide) {
			// Do nothing
			drawTwoSideCorner(location, from, rectangle, depth, corner);
		} else if (!hasPerpendicularSide && !hasCorner) {
			drawSingleSideCorner(location, from, rectangle, depth, corner, cardinalDirection);
		} else if (hasCorner && !hasPerpendicularSide) {
			drawNoSideCorner(location, from, rectangle, depth, corner);
		}

		corner = cardinalDirection.counterClockwise();
		perpendicularAfterCorner = corner.counterClockwise();
		hasCorner = fromDirections.contains(corner);
		hasPerpendicularSide = fromDirections.contains(corner.counterClockwise());
		if (hasPerpendicularSide) {
			// Do nothing
			drawTwoSideCorner(location, from, rectangle, depth, corner);
		} else if (!hasPerpendicularSide && !hasCorner) {
			drawSingleSideCorner(location, from, rectangle, depth, corner, cardinalDirection);
		} else if (hasCorner && !hasPerpendicularSide) {
			drawNoSideCorner(location, from, rectangle, depth, corner);
		}
	}
	// Most handling of ordinal directions was done in the previous loop on cardinal directions.
	// Here we only need to handle the cases when there are no cardinal directions near an ordinal.
	corners:
	for (Direction dir : fromDirections) {
		if (dir.isCardinal()) {
			continue;
		}
		OrdinalDirection corner = (OrdinalDirection) dir;
		CardinalDirection[] components = corner.getComponents();
		for (CardinalDirection component : components) {
			if (fromDirections.contains(component)) {
				break corners;
			}
		}
		drawNoSideCorner(location, from, rectangle, depth, corner);
	}
}

private void drawNoSideCorner(Location location, TypePlaceableInCell from, Rectangle rectangle, int depth, OrdinalDirection corner) {
	Cell diagonalLineStartPoint = rectangle.getCorner(corner);
	CardinalDirection[] components = corner.opposite().getComponents();
	for (int i = 0; i < depth; i++) {
		Cell diagonalLineNextPoint = new Cell(diagonalLineStartPoint);
		for (
			int j = 0; j <= i; j++, diagonalLineNextPoint = diagonalLineNextPoint.moveToSide(Directions.getDirectionBetween(components[0].opposite(), components[1]))) {
			if (i == depth - 1 && Chance.roll(20)) {
				continue;
			}
			EntityPlacer.place(location.getActivePlane(), from, location.x + diagonalLineNextPoint.getX(), location.y + diagonalLineNextPoint.getY());

		}
		diagonalLineStartPoint = diagonalLineStartPoint.newRelativePoint(components[0]);
	}
}

private void drawTwoSideCorner(Location location, TypePlaceableInCell from, Rectangle rectangle, int depth, OrdinalDirection corner) {
	Cell cornerPoint = rectangle.getCorner(corner);
	Rectangle cornerRec = Recs.growFromCell(cornerPoint.getX(), cornerPoint.getY(), corner.opposite(), depth, depth);
	location.square(cornerRec, from, true);
}

/**
 * @param rectangle
 * @param depth
 * @param corner
 * 	Corner of {@code side}
 * @param side
 */
private void drawSingleSideCorner(Location location, TypePlaceableInCell from, Rectangle rectangle, int depth, OrdinalDirection corner, CardinalDirection side) {
	Cell cornerPoint = rectangle.getCorner(corner);
	CardinalDirection anotherComponent = corner.anotherComponent(side);
	Orientation rectangleSideOrientation = anotherComponent.getOrientation();
	int sideGrowing = -side.getGrowing();
	int segmentGrowing = -anotherComponent.getGrowing();
	for (int i = 0; i < depth; i++) {
		for (int j = 0; j <= i; j++) {
			if (j == i && Chance.roll(10)) {
				continue;
			}
			Cell cornerFormingCell = Cells.fromStaticAndDynamic(
				cornerPoint.getStaticCoord(rectangleSideOrientation) + j * sideGrowing,
				cornerPoint.getDynamicCoord(rectangleSideOrientation) + i * segmentGrowing,
				rectangleSideOrientation
			);
			EntityPlacer.place(location.getActivePlane(), from, location.x + cornerFormingCell.getX(), location.y + cornerFormingCell.getY());
		}
	}
	Rectangle cornerRectangle = Recs.growFromCell(cornerPoint.getX(), cornerPoint.getY(), corner.opposite(), depth, depth);
	cornerRectangle.getSideAsSegment(side);
}

private interface TerrainDiffusionStopCondition {
	boolean check(int depth, int iterationNumber);
}

public static class TerrainTransitionBuilder {
	private TypePlaceableInCell from;
	private HashSet<Direction> directions = new HashSet<>();
	private int depth = -1;
	private Rectangle rectangle;
	private TerrainDiffusionStopCondition condition = DEFAULT_STOP_CONDITION;
	private Location location;

	public TerrainTransitionBuilder() {

	}

	public TerrainTransitionBuilder setLocation(Location location) {
		this.location = location;
		return this;
	}

	public TerrainTransitionBuilder setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
		return this;
	}

	public TerrainTransitionBuilder setFrom(TypePlaceableInCell from) {
		this.from = from;
		return this;
	}

	public TerrainTransitionBuilder setDepth(int depth) {
		if (depth < 0) {
			throw new IllegalArgumentException("depth can't be < 0");
		}
		this.depth = depth;
		return this;
	}

	public TerrainTransitionBuilder addFromDirection(Direction direction) {
		if (directions.contains(direction)) {
			throw new IllegalArgumentException("Direction " + direction + " is already present in builder");
		}
		directions.add(direction);
		return this;
	}

	/**
	 * <p>Sets condition on which a column of cells (from border to center) stops growing and placing algorithm continues
	 * on placing next column.</p> <p>If you don't use this method on builder, the default condition will be used</p>
	 *
	 * @param condition
	 * 	A functor with condition.
	 * @return The same TerrainTransition to call next methods in a chain.
	 * @see TerrainTransition#DEFAULT_STOP_CONDITION
	 */
	public TerrainTransitionBuilder setStopCondition(TerrainDiffusionStopCondition condition) {
		this.condition = condition;
		return this;
	}

	public TerrainTransition build() {
		if (location == null) {
			throw new IllegalStateException("Parameter `location` is not set");
		}
		if (rectangle == null) {
			throw new IllegalStateException("Parameter `rectangle` is not set");
		}
		if (depth == -1) {
			throw new IllegalStateException("Parameter `depth` is not set");
		}
		if (directions.isEmpty()) {
			throw new IllegalStateException("No directions specified");
		}
		return new TerrainTransition(location, rectangle, from, depth, directions, condition);
	}
}
}
