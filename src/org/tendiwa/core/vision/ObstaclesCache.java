package org.tendiwa.core.vision;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.tendiwa.core.*;
import org.tendiwa.core.meta.CellPosition;

import java.util.*;

/**
 * Holds obstacles that block {@link Seer}'s vision. Seer's vision is blocked when there is a {@link BorderObject} on
 * {@link GameObject} on its path that blocks vision.
 * <p/>
 * Each obstacle is represented by an instance of {@link Border}.
 * <p/>
 * {@link BorderObject} produces one Border that blocks vision, and {@link GameObject} produces four of them (one for
 * each side of a cell GameObject resides in).
 */
class ObstaclesCache implements Iterable<Border> {
private final ObstacleFindingStrategy strategy;
private World world;
private Collection<Border> obstacles = new LinkedList<>();
private boolean built;
private Map<Border, CardinalDirection> obstaclesOnSeersCellBorder = new HashMap<>();
private Map<Border, Cell> obstacleToObjectPosition = new HashMap<>();
private Multimap<Cell, Border> objectPositionToObstacle = HashMultimap.create();
private CellPosition position;

ObstaclesCache(
	World world,
	CellPosition position,
	ObstacleFindingStrategy strategy
) {
	this.world = world;
	this.position = position;
	this.strategy = strategy;
}

/**
 * Fills ObstacleCache with obstacles by finding all of them in Seer's vision area and. Also calculates which obstacles
 * reside on the border of this Seer's cell (those need to be treated specially).
 */
void buildObstacles() {
	obstaclesOnSeersCellBorder.clear();
	obstacleToObjectPosition.clear();
	objectPositionToObstacle.clear();
	int endX = Math.min(world.getWidth() - 1, position.getX() + Seer.VISION_RANGE);
	int endY = Math.min(world.getHeight() - 1, position.getY() + Seer.VISION_RANGE);
	int startX = Math.max(0, position.getX() - Seer.VISION_RANGE);
	int startY = Math.max(0, position.getY() - Seer.VISION_RANGE);
	for (int x = startX; x <= endX; x++) {
		for (int y = startY; y <= endY; y++) {
			boolean[] sideOccupied = new boolean[]{false, false, false, false};
			Cell objectPosition = new Cell(x, y);
			if (strategy.isCellBlockingVision(x, y)) {
				sideOccupied[0] = true;
				sideOccupied[1] = true;
				sideOccupied[2] = true;
				sideOccupied[3] = true;
				addObjectObstacles(objectPosition);
			}
			for (CardinalDirection side : CardinalDirection.values()) {
				if (side.isGrowing()) {
					continue;
				}
				if (sideOccupied[side.getCardinalIndex()]) {
					continue;
				}
				Border border = new Border(x, y, side);
				if (strategy.isBorderBlockingVision(border)) {
					addSingleBorderObstacle(border);
				}
			}
		}
	}
	built = true;
}

private void addObjectObstacles(Cell objectPosition) {
	for (CardinalDirection side : CardinalDirection.values()) {
		Border border = new Border(objectPosition.getX(), objectPosition.getY(), side);
		addSingleBorderObstacle(border);
		obstacleToObjectPosition.put(border, objectPosition);
	}
}

/**
 * Created an obstacle on a side of a point.
 *
 * @param border
 */
private void addSingleBorderObstacle(Border border) {
	obstacles.add(border);
	objectPositionToObstacle.put(border.toPoint(), border);
	if (isOnBorderOfSeersCell(border)) {
		obstaclesOnSeersCellBorder.put(
			border,
			getSideOfObstacleRelativeToSeerPosition(border)
		);
	}
}

/**
 * Checks if a Border is a border of the cell where this Seer is. Those few (at most 4) Borders are computed beforehand
 * at {@link org.tendiwa.core.vision.ObstaclesCache#buildObstacles()}.
 *
 * @param obstacleBorder
 * 	Untransformed obstacleBorder.
 * @return true if it is in the same cell, false otherwise.
 */
boolean isObstacleInSeersCell(Border obstacleBorder) {
	return obstaclesOnSeersCellBorder.containsKey(obstacleBorder);
}

/**
 * Checks if
 *
 * @param border
 * @return
 */
private boolean isOnBorderOfSeersCell(Border border) {
	boolean sameX = border.x == position.getX();
	boolean sameY = border.y == position.getY();
	if (sameX && sameY) {
		return true;
	}
	if (sameY && border.x - position.getX() == 1 && border.side == Directions.W) {
		return true;
	}
	if (sameY && border.x - position.getX() == -1 && border.side == Directions.E) {
		return true;
	}
	if (sameX && border.y - position.getY() == 1 && border.side == Directions.N) {
		return true;
	}
	if (sameX && border.y - position.getY() == -1 && border.side == Directions.S) {
		return true;
	}
	return false;
}

/**
 * If Obstacle in in Seer's cell, returns {@code side}, otherwise returns {@code side.opposite()}.
 *
 * @param border
 * @return Side of obstacle relative to Seer's position.
 */
private CardinalDirection getSideOfObstacleRelativeToSeerPosition(Border border) {
	return position.getX() == border.x && position.getY() == border.y ? border.side : border.side.opposite();
}

boolean isBuilt() {
	return built;
}

@Override
public Iterator<Border> iterator() {
	return obstacles.iterator();
}

/**
 * Checks if this obstacleBorder is on a side of the object we're trying to see. In this case, this obstacleBorder
 * doesn't block vision, so we can see the object itself.
 *
 * @param obstacleBorder
 * 	Untransformed obstacleBorder.
 * @param targetX
 * 	X coordinate of a cell we're trying to see.
 * @param targetY
 * 	Y coordinate of a cell we're trying to see.
 * @return
 */
boolean isTargetObjectObstacle(Border obstacleBorder, int targetX, int targetY) {
	Cell objectPosition = obstacleToObjectPosition.get(obstacleBorder);
	return objectPosition != null && objectPosition.getX() == targetX && objectPosition.getY() == targetY;
}

CardinalDirection getSideOfObstacleOnSeersCellBorder(Border obstacle) {
	return obstaclesOnSeersCellBorder.get(obstacle);
}

/**
 * Checks if a Border is a border of the cell where this Seer is.
 *
 * @param obstacleBorder
 * 	Border where some obstacle resides.
 * @return true if obstacle is on the border of the same cell; false otherwise.
 */
private boolean isBorderNearSeer(Border obstacleBorder) {
	return obstaclesOnSeersCellBorder.containsKey(obstacleBorder);
}

void invalidate() {
	obstacles.clear();
	built = false;
}

Border findObstacleBorder(int x, int y, CardinalDirection side) {
	Border border = new Border(x, y, side);
	for (Border obstacle : objectPositionToObstacle.get(border.toPoint())) {
		if (obstacle.side == border.side) {
			return obstacle;
		}
	}
	return null;
}

public void setWorld(World world) {
	this.world = world;
}
}
