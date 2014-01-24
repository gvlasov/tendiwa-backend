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
private Collection<Border> obstacles = new LinkedList<>();
private boolean built;
private Map<Border, CardinalDirection> obstaclesOnSeersCellBorder = new HashMap<>();
private Map<Border, EnhancedPoint> obstacleToObjectPosition = new HashMap<>();
private Multimap<EnhancedPoint, Border> objectPositionToObstacle = HashMultimap.create();
private CellPosition character;

ObstaclesCache(CellPosition character) {
	this.character = character;
}

/**
 * Fills ObstacleCache with obstacles by finding all of them in Seer's vision area and. Also calculates which obstacles
 * reside on the border of this Seer's cell (those need to be treated specially).
 */
void buildObstacles() {
	obstaclesOnSeersCellBorder.clear();
	obstacleToObjectPosition.clear();
	objectPositionToObstacle.clear();
	HorizontalPlane plane = Tendiwa.getPlayerCharacter().getPlane();
	int endX = Math.min(Tendiwa.getWorldWidth() - 1, character.getX() + Seer.VISION_RANGE);
	int endY = Math.min(Tendiwa.getWorldHeight() - 1, character.getY() + Seer.VISION_RANGE);
	int startX = Math.max(0, character.getX() - Seer.VISION_RANGE);
	int startY = Math.max(0, character.getY() - Seer.VISION_RANGE);
	for (int x = startX; x <= endX; x++) {
		for (int y = startY; y <= endY; y++) {
			boolean[] sideOccupied = new boolean[]{false, false, false, false};
			if (plane.getPassability(x, y) == Passability.NO) {
				EnhancedPoint objectPosition = new EnhancedPoint(x, y);
				sideOccupied[0] = true;
				sideOccupied[1] = true;
				sideOccupied[2] = true;
				sideOccupied[3] = true;
				addObstacle(objectPosition, Directions.N);
				addObstacle(objectPosition, Directions.E);
				addObstacle(objectPosition, Directions.S);
				addObstacle(objectPosition, Directions.W);
			}
			for (CardinalDirection side : CardinalDirection.values()) {
				if (side.isGrowing()) {
					continue;
				}
				if (sideOccupied[side.getCardinalIndex()]) {
					continue;
				}
				if (plane.hasBorderObject(x, y, side)) {
					Border obstacle = new Border(x, y, side);
					obstacles.add(obstacle);
					if (isOnBorderOfSeersCell(x, y, side)) {
						obstaclesOnSeersCellBorder.put(
							obstacle,
							getSideOfObstacleRelativeToSeerPosition(x, y, side)
						);
					}
				}
			}
		}
	}
	built = true;
}

/**
 * Created an obstacle on a side of a point.
 *
 * @param objectPosition
 * 	Point where the obstacle resides.
 * @param side
 * 	Side from which obstacle resides.
 */
private void addObstacle(EnhancedPoint objectPosition, CardinalDirection side) {
	Border obstacle = new Border(objectPosition.x, objectPosition.y, side);
	obstacles.add(obstacle);
	obstacleToObjectPosition.put(obstacle, objectPosition);
	objectPositionToObstacle.put(objectPosition, obstacle);
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
 * @param x
 * @param y
 * @param side
 * @return
 */
private boolean isOnBorderOfSeersCell(int x, int y, CardinalDirection side) {
	boolean sameX = x == character.getX();
	boolean sameY = y == character.getY();
	if (sameX && sameY) {
		return true;
	}
	if (sameY && x - character.getX() == 1 && side == Directions.W) {
		return true;
	}
	if (sameY && x - character.getX() == -1 && side == Directions.E) {
		return true;
	}
	if (sameX && y - character.getY() == 1 && side == Directions.N) {
		return true;
	}
	if (sameX && y - character.getY() == -1 && side == Directions.S) {
		return true;
	}
	return false;
}

/**
 * If Obstacle in in Seer's cell, returns {@code side}, otherwise returns {@code side.opposite()}.
 *
 * @param x
 * 	X coordinate of an Obstacle's {@link BorderObject} in world coordinates.
 * @param y
 * 	Y coordinate of an Obstacle's {@link BorderObject} in world coordinates.
 * @param side
 * 	Side of BorderObject as it is stored in a {@link HorizontalPlane}.
 * @return Side of obstacle relative to Seer's position.
 */
private CardinalDirection getSideOfObstacleRelativeToSeerPosition(int x, int y, CardinalDirection side) {
	return character.getX() == x && character.getY() == y ? side : side.opposite();
}

boolean isBuilt() {
	return built;
}

@Override
public Iterator<Border> iterator() {
	return obstacles.iterator();
}

/**
 * Checks if this obstacleBorder is a side to the object we're trying to see. In this case, this obstacleBorder doesn't
 * block vision, so we can see the object itself.
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
	EnhancedPoint objectPosition = obstacleToObjectPosition.get(obstacleBorder);
	return objectPosition != null && objectPosition.x == targetX && objectPosition.y == targetY;
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
}
