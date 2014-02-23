package org.tendiwa.core;

import com.google.common.collect.ImmutableList;
import org.tendiwa.core.events.EventFovChange;
import org.tendiwa.core.vision.*;

class VisibilityChange {
private final BorderVisionCache borderVisionCurrent;
private final BorderVisionCache borderVisionPrevious;
private final ImmutableList.Builder<RenderCell> seenBuilder = ImmutableList.builder();
private final ImmutableList.Builder<Integer> unseenBuilder = ImmutableList.builder();
private final ImmutableList.Builder<Item> seenItemsBuilder = ImmutableList.builder();
private final ImmutableList.Builder<RenderBorder> seenBordersBuilder = ImmutableList.builder();
private final ImmutableList.Builder<Border> unseenBordersBuilder = ImmutableList.builder();
private final HorizontalPlane plane;
private final int dx;
private final int dy;
private final Character player;
private final World world;
private final int xPrev;
private final int yPrev;
private final Seer seer;
private final CellVisionCache visionPrevious;
private final CellVisionCache visionCurrent;
private ImmutableList<RenderCell> seenCells;
private ImmutableList<Integer> unseenCells;
private ImmutableList<Item> seenItems;
private ImmutableList<RenderBorder> seenBorders;
private ImmutableList<Border> unseenBorders;
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
public VisibilityChange(World world, Character character, int xPrev, int yPrev, Seer seer, CellVisionCache visionPrevious, CellVisionCache visionCurrent, BorderVisionCache borderPrevious, BorderVisionCache borderCurrent) {
	this.world = world;
	this.xPrev = xPrev;
	this.yPrev = yPrev;
	this.seer = seer;
	this.visionPrevious = visionPrevious;
	this.visionCurrent = visionCurrent;
	this.borderVisionCurrent = borderCurrent;
	this.borderVisionPrevious = borderPrevious;
	plane = character.getPlane();
	player = character;
	dx = player.getX() - xPrev;
	dy = player.getY() - yPrev;
	compute();
}

private void compute() {
	int worldHeight = world.getHeight();
	// Loop over points in previous cache
	int startIndexX = Seer.getStartIndexOfRelativeTable(xPrev, Seer.VISION_RANGE);
	int startIndexY = Seer.getStartIndexOfRelativeTable(yPrev, Seer.VISION_RANGE);
	int endPrevX = seer.getEndIndexOfRelativeTableX(xPrev, Seer.VISION_RANGE);
	int endPrevY = seer.getEndIndexOfRelativeTableY(yPrev, Seer.VISION_RANGE);
	Visibility[][] visionPreviousContent = visionPrevious.getContent();
	Visibility[][] visionCurrentContent = visionCurrent.getContent();
	for (int i = startIndexX; i < endPrevX; i++) {
		for (int j = startIndexY; j < endPrevY; j++) {
			boolean pointIsInBothCaches = i - dx >= 0
				&& j - dy >= 0
				&& i - dx < ModifiableCellVisionCache.VISION_CACHE_WIDTH
				&& j - dy < ModifiableCellVisionCache.VISION_CACHE_WIDTH;
			if (pointIsInBothCaches) {
				if (visionPreviousContent[i][j] == Visibility.VISIBLE
					&& visionCurrentContent[i - dx][j - dy] == Visibility.INVISIBLE
					) {
					// If a point was known to be visible, and now it is invisible, then it is unseen
					int x = xPrev - Seer.VISION_RANGE + i;
					int y = yPrev - Seer.VISION_RANGE + j;
					if (plane.containsCell(x, y)) {
						unseenBuilder.add(x * worldHeight + y);
					}
				} else if (visionPreviousContent[i][j] == Visibility.INVISIBLE
					&& visionCurrentContent[i - dx][j - dy] == Visibility.VISIBLE
					) {
					// If a point was known to be invisible, and now it is visible, then it is seen
					int x = xPrev - Seer.VISION_RANGE + i;
					int y = yPrev - Seer.VISION_RANGE + j;
					addCellToSeen(x, y);
				}
			} else {
				// If point is only in the previous cache
				if (visionPreviousContent[i][j] == Visibility.VISIBLE) {
					// If a point was known to be visible, and now it is not known of its visibility,
					// therefore it is invisible and must be unseen
					int x = xPrev - Seer.VISION_RANGE + i;
					int y = yPrev - Seer.VISION_RANGE + j;
					if (plane.containsCell(x, y)) {
						unseenBuilder.add(x * worldHeight + y);
					}
				}
			}
		}
	}
	// Loop over points in the new cache that are _not_ in the previous cache
	int startPlayerX = Seer.getStartIndexOfRelativeTable(player.getX(), Seer.VISION_RANGE);
	int startPlayerY = Seer.getStartIndexOfRelativeTable(player.getY(), Seer.VISION_RANGE);
	int endPlayerX = seer.getEndIndexOfRelativeTableX(player.getX(), Seer.VISION_RANGE);
	int endPlayerY = seer.getEndIndexOfRelativeTableY(player.getY(), Seer.VISION_RANGE);
	for (int i = startPlayerX; i < endPlayerX; i++) {
		for (int j = startPlayerY; j < endPlayerY; j++) {
			// Condition from previous loop with reversed dx
			boolean pointIsInBothCaches = i + dx >= 0
				&& j + dy >= 0
				&& i + dx < ModifiableCellVisionCache.VISION_CACHE_WIDTH
				&& j + dy < ModifiableCellVisionCache.VISION_CACHE_WIDTH;
			if (pointIsInBothCaches) {
				// Points that are in both caches are already computed
				continue;
			}
			if (visionCurrentContent[i][j] == Visibility.VISIBLE) {
				// If it wasn't known of point's visibility, and now it is visible, therefore it was seen.
				int x = xPrev + dx - Seer.VISION_RANGE + i;
				int y = yPrev + dy - Seer.VISION_RANGE + j;
				addCellToSeen(x, y);
			}
		}
	}
	unseenCells = unseenBuilder.build();
	seenCells = seenBuilder.build();
	seenItems = seenItemsBuilder.build();
//	int worldPrevVisionSquareStartX = xPrev - Seer.VISION_RANGE;
//	int worldPrevVisionSquareStartY = yPrev - Seer.VISION_RANGE;
//	SimpleGraph<Cell, DefaultEdge> graphPrevious = getVisibleBordersGraph(
//		worldPrevVisionSquareStartX,
//		worldPrevVisionSquareStartY,
//		visionPreviousContent
//	);
//	SimpleGraph<Cell, DefaultEdge> graphCurrent = getVisibleBordersGraph(
//		worldPrevVisionSquareStartX + dx,
//		worldPrevVisionSquareStartY + dy,
//		visionCurrentContent
//	);
//	for (RenderCell seenCell : seenCells) {
//		Cell vertex = new Cell(seenCell.x, seenCell.y);
//		assert graphCurrent.containsVertex(vertex) : "No seen vertex " + vertex + " in " + graphCurrent.vertexSet();
//		for (DefaultEdge edge : graphCurrent.edgesOf(vertex)) {
//			Cell anotherPoint = graphCurrent.getEdgeSource(edge).equals(vertex) ? graphCurrent.getEdgeTarget(edge) : graphCurrent.getEdgeSource(edge);
//			CardinalDirection side = (CardinalDirection) Directions.shiftToDirection(seenCell.x - anotherPoint.x, seenCell.y - anotherPoint.y);
//			if (side.isHorizontal() && anotherPoint.x == worldPrevVisionSquareStartX + dx + ModifiableCellVisionCache.VISION_CACHE_WIDTH - 1) {
//				continue;
//			} else if (side.isVertical() && anotherPoint.y == worldPrevVisionSquareStartY + dy + ModifiableCellVisionCache.VISION_CACHE_WIDTH - 1) {
//				continue;
//			}
//			if (visionCurrentContent[anotherPoint.x - (worldPrevVisionSquareStartX + dx)][anotherPoint.y - (worldPrevVisionSquareStartY + dy)] == Visibility.VISIBLE
//				&& plane.hasBorderObject(anotherPoint.x, anotherPoint.y, side)
//				) {
//				seenBordersBuilder.add(new RenderBorder(
//					anotherPoint.x,
//					anotherPoint.y,
//					side,
//					plane.getBorderObject(anotherPoint.x, anotherPoint.y, side)
//				));
//			}
//		}
//	}
//	for (int hash : unseenCells) {
//		int[] coords = Chunk.cellHashToCoords(hash, worldHeight);
//		Cell vertex = new Cell(coords[0], coords[1]);
//		assert graphPrevious.containsVertex(vertex) : "No unseen vertex " + vertex + " in " + graphPrevious.vertexSet();
//		for (DefaultEdge edge : graphPrevious.edgesOf(vertex)) {
//			Cell anotherPoint = graphPrevious.getEdgeSource(edge).equals(vertex) ? graphPrevious.getEdgeTarget(edge) : graphPrevious.getEdgeSource(edge);
//			CardinalDirection side = (CardinalDirection) Directions.shiftToDirection(coords[0] - anotherPoint.x, coords[1] - anotherPoint.y);
//			if (side.isHorizontal() && anotherPoint.x == worldPrevVisionSquareStartX + ModifiableCellVisionCache.VISION_CACHE_WIDTH - 1) {
//				continue;
//			} else if (side.isVertical() && anotherPoint.y == worldPrevVisionSquareStartY + ModifiableCellVisionCache.VISION_CACHE_WIDTH - 1) {
//				continue;
//			}
//			if (visionPreviousContent[anotherPoint.x - worldPrevVisionSquareStartX][anotherPoint.y - worldPrevVisionSquareStartY] == Visibility.VISIBLE
//				&& plane.hasBorderObject(anotherPoint.x, anotherPoint.y, side)
//				) {
//				unseenBordersBuilder.add(new RenderBorder(
//					anotherPoint.x,
//					anotherPoint.y,
//					side,
//					plane.getBorderObject(anotherPoint.x, anotherPoint.y, side)
//				));
//			}
//		}
//	}
	for (BorderVisibility border : borderVisionCurrent) {
		if (border.visibility == Visibility.VISIBLE && !borderVisionPrevious.isVisible(border)) {
			seenBordersBuilder.add(new RenderBorder(
				border.x,
				border.y,
				border.side,
				plane.getBorderObject(border)
			));
		}
	}
	for (BorderVisibility border : borderVisionPrevious) {
		if (border.visibility == Visibility.VISIBLE && !borderVisionCurrent.isVisible(border)) {
			unseenBordersBuilder.add(new Border(border.x, border.y, border.side));
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

private void addCellToSeen(int x, int y) {
	seenBuilder.add(new RenderCell(
		world,
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
