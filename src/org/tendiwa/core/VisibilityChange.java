package org.tendiwa.core;

import com.google.common.collect.ImmutableList;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

class VisibilityChange implements Event {
private ImmutableList<RenderCell> seenCells;
private ImmutableList<Integer> unseenCells;
private ImmutableList<Item> seenItems;
private ImmutableList<RenderBorder> seenBorders;
private ImmutableList<RenderBorder> unseenBorders;

private final ImmutableList.Builder<RenderCell> seenBuilder = ImmutableList.builder();
private final ImmutableList.Builder<Integer> unseenBuilder = ImmutableList.builder();
private final ImmutableList.Builder<Item> seenItemsBuilder = ImmutableList.builder();
private final ImmutableList.Builder<RenderBorder> seenBordersBuilder = ImmutableList.builder();
private final ImmutableList.Builder<RenderBorder> unseenBordersBuilder = ImmutableList.builder();
private final HorizontalPlane plane;
private final int dx;
private final int dy;
private final Character player;
private final int xPrev;
private final int yPrev;
private final byte[][] visionPrevious;
private final byte[][] visionCurrent;
private boolean eventCreated = false;

/**
 * @param xPrev
 * 	X coordinate of {@link Character} on previous turn.
 * @param yPrev
 * 	Y coordinate of {@link Character} on previous turn.
 * @param visionPrevious
 * 	Vision cache of PlayerCharacter on previous turn. {@code visionCurrent[Character.VISION_RANGE][Character.VISION_RANGE]}
 * 	is the point Character was standing at on previous turn.
 * @param visionCurrent
 * 	Vision cache of PlayerCharacter on current turn. {@code visionCurrent[Character.VISION_RANGE][Character.VISION_RANGE]}
 * 	is the point Character is standing on current turn.
 */
public VisibilityChange(int xPrev, int yPrev, byte[][] visionPrevious, byte[][] visionCurrent) {
	this.xPrev = xPrev;
	this.yPrev = yPrev;
	this.visionPrevious = visionPrevious;
	this.visionCurrent = visionCurrent;
	plane = Tendiwa.getPlayerCharacter().getPlane();
	player = Tendiwa.getPlayerCharacter();
	dx = player.getX() - xPrev;
	dy = player.getY() - yPrev;
	compute();
}
private void compute() {
	int worldHeight = Tendiwa.getWorld().getHeight();
	// Loop over points in previous cache
	int startIndexX = Character.getStartIndexOfRelativeTable(xPrev, Character.VISION_RANGE);
	int startIndexY = Character.getStartIndexOfRelativeTable(yPrev, Character.VISION_RANGE);
	int endPrevX = Character.getEndIndexOfRelativeTableX(xPrev, Character.VISION_RANGE);
	int endPrevY = Character.getEndIndexOfRelativeTableY(yPrev, Character.VISION_RANGE);
	for (int i = startIndexX; i < endPrevX; i++) {
		for (int j = startIndexY; j < endPrevY; j++) {
			boolean pointIsInBothCaches = i - dx >= 0
				&& j - dy >= 0
				&& i - dx < Character.VISION_CACHE_WIDTH
				&& j - dy < Character.VISION_CACHE_WIDTH;
			if (pointIsInBothCaches) {
				if (visionPrevious[i][j] == Character.VISION_VISIBLE
					&& visionCurrent[i - dx][j - dy] == Character.VISION_INVISIBLE
					) {
					// If a point was known to be visible, and now it is invisible, then it is unseen
					int x = xPrev - Character.VISION_RANGE + i;
					int y = yPrev - Character.VISION_RANGE + j;
					if (plane.containsCell(x, y)) {
						unseenBuilder.add(x * worldHeight + y);
					}
				} else if (visionPrevious[i][j] == Character.VISION_INVISIBLE
					&& visionCurrent[i - dx][j - dy] == Character.VISION_VISIBLE
					) {
					// If a point was known to be invisible, and now it is visible, then it is seen
					int x = xPrev - Character.VISION_RANGE + i;
					int y = yPrev - Character.VISION_RANGE + j;
					addCellToSeen(x, y);
				}
			} else {
				// If point is only in the previous cache
				if (visionPrevious[i][j] == Character.VISION_VISIBLE) {
					// If a point was known to be visible, and now it is not known of its visibility,
					// therefore it is invisible and must be unseen
					int x = xPrev - Character.VISION_RANGE + i;
					int y = yPrev - Character.VISION_RANGE + j;
					if (plane.containsCell(x, y)) {
						unseenBuilder.add(x * worldHeight + y);
					}
				}
			}
		}
	}
	// Loop over points in the new cache that are _not_ in the previous cache
	int startPlayerX = Character.getStartIndexOfRelativeTable(player.getX(), Character.VISION_RANGE);
	int startPlayerY = Character.getStartIndexOfRelativeTable(player.getY(), Character.VISION_RANGE);
	int endPlayerX = Character.getEndIndexOfRelativeTableX(player.getX(), Character.VISION_RANGE);
	int endPlayerY = Character.getEndIndexOfRelativeTableY(player.getY(), Character.VISION_RANGE);
	for (int i = startPlayerX; i < endPlayerX; i++) {
		for (int j = startPlayerY; j < endPlayerY; j++) {
			// Condition from previous loop with reversed dx
			boolean pointIsInBothCaches = i + dx >= 0
				&& j + dy >= 0
				&& i + dx < Character.VISION_CACHE_WIDTH
				&& j + dy < Character.VISION_CACHE_WIDTH;
			if (pointIsInBothCaches) {
				// Points that are in both caches are already computed
				continue;
			}
			if (visionCurrent[i][j] == Character.VISION_VISIBLE) {
				// If it wasn't known of point's visibility, and now it is visible, therefore it was seen.
				int x = xPrev + dx - Character.VISION_RANGE + i;
				int y = yPrev + dy - Character.VISION_RANGE + j;
				addCellToSeen(x, y);
			}
		}
	}
	unseenCells = unseenBuilder.build();
	seenCells = seenBuilder.build();
	seenItems = seenItemsBuilder.build();
	int worldPrevVisionSquareStartX = xPrev - Character.VISION_RANGE;
	int worldPrevVisionSquareStartY = yPrev - Character.VISION_RANGE;
	SimpleGraph<EnhancedPoint, DefaultEdge> graphPrevious = getVisibleBordersGraph(
		worldPrevVisionSquareStartX,
		worldPrevVisionSquareStartY,
		visionPrevious
	);
	SimpleGraph<EnhancedPoint, DefaultEdge> graphCurrent = getVisibleBordersGraph(
		worldPrevVisionSquareStartX + dx,
		worldPrevVisionSquareStartY + dy,
		visionCurrent
	);
	for (RenderCell seenCell : seenCells) {
		EnhancedPoint vertex = new EnhancedPoint(seenCell.x, seenCell.y);
		assert graphCurrent.containsVertex(vertex) : "No vertex " + vertex + " in " + graphCurrent.vertexSet();
		for (DefaultEdge edge : graphCurrent.edgesOf(vertex)) {
			EnhancedPoint anotherPoint = graphCurrent.getEdgeSource(edge).equals(vertex) ? graphCurrent.getEdgeTarget(edge) : graphCurrent.getEdgeSource(edge);
			CardinalDirection side = (CardinalDirection) Directions.shiftToDirection(seenCell.x - anotherPoint.x, seenCell.y - anotherPoint.y);
			if (side.isHorizontal() && anotherPoint.x == worldPrevVisionSquareStartX + dx + Character.VISION_CACHE_WIDTH - 1) {
				continue;
			} else if (side.isVertical() && anotherPoint.y == worldPrevVisionSquareStartY + dy + Character.VISION_CACHE_WIDTH - 1) {
				continue;
			}
			if (visionCurrent[anotherPoint.x - (worldPrevVisionSquareStartX + dx)][anotherPoint.y - (worldPrevVisionSquareStartY + dy)] == Character.VISION_VISIBLE
				&& plane.hasBorderObject(anotherPoint.x, anotherPoint.y, side)
				) {
				seenBordersBuilder.add(new RenderBorder(
					anotherPoint.x,
					anotherPoint.y,
					side,
					plane.getBorderObject(anotherPoint.x, anotherPoint.y, side)
				));
			}
		}
	}
	for (int hash : unseenCells) {
		int[] coords = Chunk.cellHashToCoords(hash, worldHeight);
		EnhancedPoint vertex = new EnhancedPoint(coords[0], coords[1]);
		assert graphPrevious.containsVertex(vertex) : "No vertex " + vertex + " in " + graphPrevious.vertexSet();
		for (DefaultEdge edge : graphPrevious.edgesOf(vertex)) {
			EnhancedPoint anotherPoint = graphPrevious.getEdgeSource(edge).equals(vertex) ? graphPrevious.getEdgeTarget(edge) : graphPrevious.getEdgeSource(edge);
			CardinalDirection side = (CardinalDirection) Directions.shiftToDirection(coords[0] - anotherPoint.x, coords[1] - anotherPoint.y);
			if (side.isHorizontal() && anotherPoint.x == worldPrevVisionSquareStartX + Character.VISION_CACHE_WIDTH - 1) {
				continue;
			} else if (side.isVertical() && anotherPoint.y == worldPrevVisionSquareStartY + Character.VISION_CACHE_WIDTH - 1) {
				continue;
			}
			if (visionPrevious[anotherPoint.x - worldPrevVisionSquareStartX][anotherPoint.y - worldPrevVisionSquareStartY] == Character.VISION_VISIBLE
				&& plane.hasBorderObject(anotherPoint.x, anotherPoint.y, side)
				) {
				unseenBordersBuilder.add(new RenderBorder(
					anotherPoint.x,
					anotherPoint.y,
					side,
					plane.getBorderObject(anotherPoint.x, anotherPoint.y, side)
				));
			}
		}
	}
	seenBorders = seenBordersBuilder.build();
	unseenBorders = unseenBordersBuilder.build();
}
public EventFovChange createEvent() {
	if (eventCreated) {
		throw new RuntimeException("Event for this VisibilityChange has already been created");
	}

	eventCreated = true;
	return new EventFovChange(
		seenCells,
		unseenCells,
		seenItems,
		seenBorders,
		unseenBorders
	);
}

public SimpleGraph<EnhancedPoint, DefaultEdge> getVisibleBordersGraph(int startWorldX, int startWorldY, byte[][] relativeVisibility) {
	SimpleGraph<EnhancedPoint, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
	int l = relativeVisibility[0].length; // Loop over all rows and column except of last row and last column
	for (int i = 0; i < l; i++) {
		for (int j = 0; j < l; j++) {
			EnhancedPoint currentVertex = new EnhancedPoint(i + startWorldX, j + startWorldY);
			EnhancedPoint vertexFromEast = new EnhancedPoint(i + startWorldX + 1, j + startWorldY);
			EnhancedPoint vertexFromSouth = new EnhancedPoint(i + startWorldX, j + startWorldY + 1);
			if (j+1 < l && relativeVisibility[i][j] == Character.VISION_VISIBLE && relativeVisibility[i][j + 1] == Character.VISION_VISIBLE) {
				graph.addVertex(currentVertex);
				graph.addVertex(vertexFromSouth);
				// Border between two horizontal neighbors
				graph.addEdge(currentVertex, vertexFromSouth);
			}
			if (i+1 < l && relativeVisibility[i][j] == Character.VISION_VISIBLE && relativeVisibility[i + 1][j] == Character.VISION_VISIBLE) {
				graph.addVertex(currentVertex);
				graph.addVertex(vertexFromEast);
				// Border between two vertical neighbors
				graph.addEdge(currentVertex, vertexFromEast);
			}
		}
	}
	return graph;
}

private void addCellToSeen(int x, int y) {
	HorizontalPlane plane = Tendiwa.getPlayerCharacter().getPlane();
	seenBuilder.add(new RenderCell(
		x,
		y,
		plane.getFloor(x, y),
		plane.getGameObject(x, y)
	));
	if (plane.hasAnyItems(x, y)) {
		for (Item item : plane.getItems(x, y)) {
			seenItemsBuilder.add(item);
		}
	}
}

}
