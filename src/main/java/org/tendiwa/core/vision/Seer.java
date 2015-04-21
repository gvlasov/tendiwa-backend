package org.tendiwa.core.vision;

import org.tendiwa.core.*;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.core.meta.DoubleRange;
import org.tendiwa.core.meta.DoubleRangeCollection;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.*;

public class Seer {
	public static final int VISION_RANGE = 11;
	private final static double EPSILON = 0.01;
	private final static double visionSourceDiameter = 0.7;
	public final ModifiableCellVisionCache visionCache;
	final CellVisionCache visionPrevious;
	private final Cell character;
	private final SightPassabilityCriteria vision;
	/**
	 * Saves field of view on previous turn when it is needed to calculate differences between FOV on previous turn and
	 * current turn.
	 */

	private final ObstaclesCache obstaclesCache;
	private final ModifiableBorderVisionCache borderVision;
	private final BorderVisionCache borderVisionPrevious;
	private World world;

	public Seer(Cell character, SightPassabilityCriteria vision, ObstacleFindingStrategy strategy) {
		this.character = character;
		this.vision = vision;
		this.obstaclesCache = new ObstaclesCache(null, character, strategy);
		this.visionCache = new ModifiableCellVisionCache(character);
		this.visionPrevious = new CellVisionCache();
		this.borderVision = new ModifiableBorderVisionCache(character);
		this.borderVisionPrevious = new BorderVisionCache(character);
	}

	/**
	 * Returns the first index (either x or y) of a relative table (a FOV table, for example) which resides inside
	 * world
	 * rectangle {0, 0, world.width, world.height}.
	 * <p>
	 * There is only one method for the first index, but two methods for the last indices, because the least world
	 * coordinate is 0 on both x and y axes, but the greatest is different (world.width or world.height) for x and y
	 * axes.
	 *
	 * @param centerCoordinate
	 * 	Absolute coordinate (in world coordinates) of table's center by one of axes.
	 * @param tableRadius
	 * 	{@code (table_width-1)/2}
	 * @return First index in relative table's coordinates that resides inside world rectangle.
	 * @see org.tendiwa.pathfinding.dijkstra.PathTable For more information on relative tables.
	 * @see Seer#computeFullVisionCache()  For more information on relative tables.
	 */
	public static int getStartIndexOfRelativeTable(int centerCoordinate, int tableRadius) {
		return Math.max(0, -(centerCoordinate - tableRadius));
	}

	public static double getAngle(int fromX, int fromY, int toX, int toY) {
		return Math.atan2(toY - fromY, toX - fromX);
	}

	public void setWorld(World world) {
		this.world = world;
		obstaclesCache.setWorld(world);
	}

	/**
	 * Returns the last index on x axis of a relative table (a FOV table, for example) which resides inside world
	 * rectangle.
	 * <p>
	 * There is only one method for the first index, but two methods for the last indices, because the least world
	 * coordinate is 0 on both x and y axes, but the greatest is different (world.width or world.height) for x and y
	 * axes.
	 *
	 * @param centerCoordinate
	 * 	Absolute x coordinate of table's center in world coordinates.
	 * @param tableRadius
	 * 	{@code (table_width-1)/2}
	 * @return Last index in relative table's coordinates on x axis that resides inside world rectangle.
	 */
	public int getEndIndexOfRelativeTableX(int centerCoordinate, int tableRadius) {
		return Math.min(tableRadius * 2 + 1, world.getWidth() - centerCoordinate + tableRadius);
	}

	/**
	 * <p>Returns the last index on y axis of a relative table (a FOV table, for example) which resides inside world
	 * recangle.</p> <p/> <p>There is only one method for the first index, but two methods for the last indices,
	 * because
	 * the
	 * least world coordinate is 0 on both x and y axes, but the greatest is different (world.width or world.height)
	 * for
	 * x
	 * and y axes.</p>
	 *
	 * @param centerCoordinate
	 * 	Absolute y coordinate of table's center in world coordinates.
	 * @param tableRadius
	 * 	{@code (table_width-1)/2}
	 * @return Last index in relative table's coordinates on x axis that resides inside world rectangle.
	 */
	public int getEndIndexOfRelativeTableY(int centerCoordinate, int tableRadius) {
		return Math.min(tableRadius * 2 + 1, world.getHeight() - centerCoordinate + tableRadius);
	}

	/**
	 * Computes visibility as if Obstacle {@code excluded} was not present at all. It is needed to compute visibility
	 * of
	 * BorderObjects.
	 *
	 * @param x
	 * 	X coordinate of target cell in world coordinates
	 * @param y
	 * 	Y coordinate of target cell in world coordinates
	 * @param excluded
	 * 	The excluded obstacle. May be null, so no obstacle will be excluded.
	 * @return true if this Seer can see the cell x:y with the given obstacle excluded, false otherwise.
	 */
	private boolean isCellVisible(int x, int y, Border excluded) {
		if (!obstaclesCache.isBuilt()) {
			obstaclesCache.buildObstacles();
		}

		if (x == character.x() && y == character.y()) {
			if (excluded == null) {
				visionCache.cacheVision(x, y, Visibility.VISIBLE);
			}
			return true;
		}
		assert obstaclesCache.isBuilt();
		if (!cellIsInVisibilityRange(x, y)) {
			if (cellIsInVisibilityRectangle(x, y)) {
				// If coord is in a square of VISION_RANGE*VISION_RANGE,
				// but is in its corner so it's too distant to be seen.
				if (excluded == null) {
					visionCache.cacheVision(x, y, Visibility.INVISIBLE);
				}
			}
			return false;
		}
		DoubleRangeCollection ranges = new DoubleRangeCollection(new DoubleRange(-visionSourceDiameter / 2, visionSourceDiameter / 2));
		for (Border obstacle : obstaclesCache) {
			if (obstacle == excluded) {
				continue;
			}
			if (obstaclesCache.isTargetObjectObstacle(obstacle, x, y)) {
				continue;
			}
			Obstacle transformed = new Obstacle(
				obstacle,
				-x,
				-y,
				getAngle(character.x(), character.y(), x, y) - Math.PI / 2
			);
			if (transformedObstacleIsOnBothSidesFromXAxis(transformed)) {
				transformed = transformed.splitWithXAxis();
			}
			if (obstaclesCache.isObstacleInSeersCell(obstacle) && !isObstacleInTargetQuarter(obstacle, x, y)) {
				continue;
			}
			if (!isObstacleOnTheSameCellBlockingVision(obstacle, x, y) && !obstacleMayBlockVision(transformed, x, y)) {
				continue;
			}
			double transStartX = transformed.getX();
			double transEndX = transformed.getX() + transformed.getVector().get(0);
			if (Math.abs(transStartX - transEndX) < EPSILON) {
				// Cull those obstacles which are perpendicular to x axis (and thus not blocking vision)
				continue;
			}
			ranges.splitWith(Math.min(transStartX, transEndX), Math.max(transStartX, transEndX));
			if (ranges.size() == 0) {
				if (excluded == null) {
					visionCache.cacheVision(x, y, Visibility.INVISIBLE);
				}
				return false;
			}
		}
		if (excluded == null) {
			visionCache.cacheVision(x, y, Visibility.VISIBLE);
		}
		return true;
	}

	private boolean isObstacleOnTheSameCellBlockingVision(Border obstacle, int targetX, int targetY) {
		return obstaclesCache.isObstacleInSeersCell(obstacle) && isObstacleInTargetQuarter(obstacle, targetX, targetY);
	}

	private boolean isObstacleInTargetQuarter(Border obstacle, int targetX, int targetY) {
		assert obstaclesCache.isObstacleInSeersCell(obstacle);
		int dx = targetX - character.x();
		int dy = targetY - character.y();
		CardinalDirection side = obstaclesCache.getSideOfObstacleOnSeersCellBorder(obstacle);
		assert side != null;
		if (dx >= 0 && dy >= 0 && (side == Directions.S || side == Directions.E)) {
			// SE quarter
			return true;
		}
		if (dx >= 0 && dy <= 0 && (side == Directions.N || side == Directions.E)) {
			// NE quarter
			return true;
		}
		if (dx <= 0 && dy >= 0 && (side == Directions.S || side == Directions.W)) {
			// SW quarter
			return true;
		}
		if (dx <= 0 && dy <= 0 && (side == Directions.W || side == Directions.N)) {
			// NW quarter
			return true;
		}
		return false;
	}

	private boolean obstacleMayBlockVision(Obstacle transformed, int toX, int toY) {
		if (transformed.getY() > 0) {
			// If the obstacle is behind target cell
			return false;
		}
		if (transformed.getY() < -Cells.distanceDouble(toX, toY, character.x(), character.y())) {
			// If the obstacle is behind Seer
			return false;
		}
		return true;
	}

	private boolean cellIsInVisibilityRectangle(int x, int y) {
		// TODO: Cache this rectangle
		return StupidPriceduralRecs.rectangleByCenterPoint(
			new BasicCell(character.x(), character.y()),
			ModifiableCellVisionCache.VISION_CACHE_WIDTH, ModifiableCellVisionCache.VISION_CACHE_WIDTH
		).contains(x, y);
	}

	/**
	 * Checks if a cell is close enough to this Seer to be seen.
	 *
	 * @param x
	 * 	X coordinate of a cell to be seen, in world coordinates
	 * @param y
	 * 	Y coordinate of a cell to be seen, in world coordinates
	 * @return true if cell is close enough to be seen; false if it is too far away to be seen.
	 */
	private boolean cellIsInVisibilityRange(int x, int y) {
		return Math.floor(new BasicCell(character.x(), character.y()).distanceInt(x, y)) <= Seer.VISION_RANGE;
	}

	/**
	 * Checks if ends of an obstacle line from different sides of x-axis, i.e. their y-coordinates have opposite signs.
	 *
	 * @param transformed
	 * 	coordinate system.
	 * @return true if obstacle resides from both sides of x-axis; false otherwise.
	 */
	private boolean transformedObstacleIsOnBothSidesFromXAxis(Obstacle transformed) {
		double transStartY = transformed.getY();
		double transEndY = transformed.getY() + transformed.getVector().get(1);
		return transStartY > 0 && transEndY < 0 || transStartY < 0 && transEndY > 0;
	}

	public void invalidateVisionCache() {
		visionCache.invalidate();
		borderVision.invalidate();
		obstaclesCache.invalidate();
	}

	public BasicCell getRayEnd(int endX, int endY) {
		BasicCell characterCoord = new BasicCell(character.x(), character.y());
		if (characterCoord.isNear(endX, endY) || character.x() == endX && character.y() == endY) {
			return new BasicCell(endX, endY);
		}
		if (endX == character.x() || endY == character.y()) {
			if (endX == character.x()) {
				int dy = Math.abs(endY - character.y()) / (endY - character.y());
				for (int i = character.y() + dy; i != endY + dy; i += dy) {
					if (!vision.canSee(endX, i)) {
						return new BasicCell(endX, i - dy);
					}
				}
			} else {
				int dx = Math.abs(endX - character.x()) / (endX - character.x());
				for (int i = character.x() + dx; i != endX + dx; i += dx) {
					if (!vision.canSee(i, endY)) {
						return new BasicCell(i - dx, endY);
					}
				}
			}
			return new BasicCell(endX, endY);
		} else if (Math.abs(endX - character.x()) == 1) {
			int dy = Math.abs(endY - character.y()) / (endY - character.y());
			int y1 = endY, y2 = endY;
			for (int i = character.y() + dy; i != endY + dy; i += dy) {
				if (!vision.canSee(endX, i)) {
					y1 = i - dy;
					break;
				}
				if (i == endY) {
					return new BasicCell(endX, endY);
				}
			}
			for (int i = character.y() + dy; i != endY + dy; i += dy) {
				if (!vision.canSee(character.x(), i)) {
					y2 = i - dy;
					break;
				}
			}
			BasicCell answer;
			if (characterCoord.distanceDouble(endX, y1) > characterCoord.distanceDouble(character.x(), y2)) {
				answer = new BasicCell(endX, y1);
			} else {
				answer = new BasicCell(character.x(), y2);
			}
			if (answer.x() == character.x()
				&& answer.y() == y2
				&& vision.canSee(endX, endY)) {
				// If answer is the furthest cell on the same line, but
				// {endX:endY} is free
				answer = new BasicCell(endX, endY);
			} else if (answer.x() == character.x()
				&& answer.y() == y2
				&& !vision.canSee(endX, endY)) {
				// If answer is the furthest cell on the same line, and
				// {endX:endY} has no passage
				answer = new BasicCell(answer.x(), answer.y() - dy);
			}
			return answer;
		} else if (Math.abs(endY - character.y()) == 1) {
			int dx = Math.abs(endX - character.x()) / (endX - character.x());
			int x1 = endX, x2 = endX;
			for (int i = character.x() + dx; i != endX + dx; i += dx) {
				if (!vision.canSee(i, endY)) {
					x1 = i - dx;
					break;
				}
				if (i == endX) {
					return new BasicCell(endX, endY);
				}
			}
			for (int i = character.x() + dx; i != endX + dx; i += dx) {
				if (!vision.canSee(i, character.y())) {
					x2 = i - dx;
					break;
				}
			}
			BasicCell answer;
			if (characterCoord.distanceDouble(x1, endY) > characterCoord.distanceDouble(x2, character.y())) {
				answer = new BasicCell(x1, endY);
			} else {
				answer = new BasicCell(x2, character.y());
			}
			if (answer.x() == x2
				&& answer.y() == character.y()
				&& vision.canSee(endX, endY)) {
				// If answer is the furthest cell on the same line, but
				// {endX:endY} is free
				answer = new BasicCell(endX, endY);
			} else if (answer.x() == x2
				&& answer.y() == character.y()
				&& !vision.canSee(endX, endY)) {
				// If answer is the furthest cell on the same line, and
				// {endX:endY} has no passage
				answer = new BasicCell(answer.x() - dx, answer.y());
			}

			return answer;
		} else if (Math.abs(endX - character.x()) == Math.abs(endY - character.y())) {
			int dMax = Math.abs(endX - character.x());
			int dx = endX > character.x() ? 1 : -1;
			int dy = endY > character.y() ? 1 : -1;
			int cx = character.x();
			int cy = character.y();
			for (int i = 1; i <= dMax; i++) {
				cx += dx;
				cy += dy;
				if (!vision.canSee(cx, cy)) {
					return new BasicCell(cx - dx, cy - dy);
				}

			}
			return new BasicCell(endX, endY);
		} else {
			double[][] start = new double[2][2];
			double[] end = new double[4];
			end[0] = (endX > character.x()) ? endX - 0.5 : endX + 0.5;
			end[1] = (endY > character.y()) ? endY - 0.5 : endY + 0.5;
			end[2] = endX;
			end[3] = endY;
			start[0][0] = (endX > character.x()) ? character.x() + 0.5 : character.x() - 0.5;
			start[0][1] = (endY > character.y()) ? character.y() + 0.5 : character.y() - 0.5;
			start[1][0] = (endX > character.x()) ? character.x() + 0.5 : character.x() - 0.5;
			// start[0][1]=this.y;
			// start[1][0]=this.x;
			start[1][1] = (endY > character.y()) ? character.y() + 0.5 : character.y() - 0.5;
			BasicCell[] rays = rays(character.x(), character.y(), endX, endY);
			int breakX = character.x(), breakY = character.y();
			jump:
			for (int k = 0; k < 3; k++) {
				int endNumX = (k == 0 || k == 1) ? 0 : 2;
				int endNumY = (k == 0 || k == 2) ? 1 : 3;
				for (int j = 0; j < 1; j++) {
					if (start[j][0] == character.x() && start[j][1] == character.y()) {
						continue;
					}
					double xEnd = end[endNumX];
					double yEnd = end[endNumY];
					double xStart = start[j][0];
					double yStart = start[j][1];
					for (BasicCell c : rays) {
						try {
							if (!vision.canSee(c.x(), c.y())) {
								if (Math.abs(((yStart - yEnd) * c.x()
									+ (xEnd - xStart) * c.y() + (xStart
									* yEnd - yStart * xEnd))
									/ Math.sqrt(Math.abs((xEnd - xStart)
									* (xEnd - xStart)
									+ (yEnd - yStart)
									* (yEnd - yStart)))) <= 0.5) {
									continue jump;
								}

							} else {
								breakX = c.x();
								breakY = c.y();
							}
						} catch (Exception e) {
							throw new Error();
						}
					}
					return new BasicCell(endX, endY);
				}
			}
			return new BasicCell(breakX, breakY);
		}
	}

	public BasicCell[] rays(int startX, int startY, int endX, int endY) {
		return Utils.concatAll(
			CellSegment.cells(startX, startY, endX, endY),
			CellSegment.cells(startX, startY + (endY > startY ? 1 : -1), endX + (endX > startX ? -1 : 1), endY),
			CellSegment.cells(startX + (endX > startX ? 1 : -1), startY, endX, endY + (endY > startY ? -1 : 1))
		);
	}

	public void storeVisionCacheToPreviousVisionCache() {
		visionCache.storeTo(visionPrevious);
		borderVision.storeTo(borderVisionPrevious);
	}

	public boolean canSee(int x, int y) {
		if (Math.abs(x - character.x()) > VISION_RANGE) {
			return false;
		}
		if (Math.abs(y - character.y()) > VISION_RANGE) {
			return false;
		}
		Visibility visionFromCache = visionCache.getVisionFromCache(x, y);
		if (visionFromCache == Visibility.NOT_COMPUTED) {
			return isCellVisible(x, y, null);
		} else {
			return visionFromCache == Visibility.VISIBLE;
		}
	}

	public Visibility canSeeBorder(Border border) {
		if (Math.abs(border.x - character.x()) > VISION_RANGE) {
			return Visibility.INVISIBLE;
		}
		if (Math.abs(border.y - character.y()) > VISION_RANGE) {
			return Visibility.INVISIBLE;
		}
		return borderVision.get(border);
	}

	/**
	 * Computes vision to all cells around this Seer. This is usually needed only for player character to render his
	 * field
	 * of view in client — non player characters compute visibility only to particular cells as needed.
	 */
	public void computeFullVisionCache() {
		int startX = getStartIndexOfRelativeTable(character.x(), VISION_RANGE);
		int startY = getStartIndexOfRelativeTable(character.y(), VISION_RANGE);
		int endX = getEndIndexOfRelativeTableX(character.x(), VISION_RANGE);
		int endY = getEndIndexOfRelativeTableY(character.y(), VISION_RANGE);
		borderVision.saveCurrentCenterCoordinates(character);
		computeCellVision(startX, startY, endX, endY);
		computeAllBordersVisibility(startX, startY, endX, endY);
		obstaclesCache.invalidate();
	}

	private void computeCellVision(int startX, int startY, int endX, int endY) {
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				isCellVisible(character.x() - VISION_RANGE + i, character.y() - VISION_RANGE + j, null);
			}
		}
	}

	/**
	 * Computes visibility of all borders. Each {@link Directions#N} and {@link Directions#W} border in each cell is
	 * tried.
	 *
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	private void computeAllBordersVisibility(int startX, int startY, int endX, int endY) {
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				int actualWorldX = character.x() - VISION_RANGE + i;
				int actualWorldY = character.y() - VISION_RANGE + j;
				computeBorderVisibility(actualWorldX, actualWorldY, Directions.W);
				computeBorderVisibility(actualWorldX, actualWorldY, Directions.N);
			}
		}

	}

	private boolean computeBorderVisibility(int x, int y, CardinalDirection side) {
		assert !side.isGrowing();
		int xOriginal = x;
		int yOriginal = y;
		Visibility visionCloserToSeer = visionCache.getVisionFromCache(x, y);
		int[] dCoords = side.side2d();
		int xNeighbor = x + dCoords[0];
		int yNeighbor = y + dCoords[1];
		// x and y are inside vision rectangle, but xNeighbor and yNeighbor may be outside.
		if (!getVisionRectangle().contains(xNeighbor, yNeighbor)) {
			borderVision.cacheBorderVision(x, y, side, Visibility.INVISIBLE);
			return false;
		}
		Visibility visionFurtherToSeer = visionCache.getVisionFromCache(xNeighbor, yNeighbor);
		if (visionCloserToSeer == Visibility.INVISIBLE && visionFurtherToSeer == Visibility.VISIBLE) {
			int ibuf = x;
			x = xNeighbor;
			xNeighbor = ibuf;

			ibuf = y;
			y = yNeighbor;
			yNeighbor = ibuf;

			Visibility buf = visionCloserToSeer;
			visionCloserToSeer = visionFurtherToSeer;
			visionFurtherToSeer = buf;
		}
		if (visionCloserToSeer == Visibility.VISIBLE && visionFurtherToSeer == Visibility.VISIBLE) {
			borderVision.cacheBorderVision(x, y, side, Visibility.VISIBLE);
			return true;
		} else if (visionCloserToSeer == Visibility.INVISIBLE) {
			assert visionFurtherToSeer == Visibility.INVISIBLE;
			borderVision.cacheBorderVision(x, y, side, Visibility.INVISIBLE);
			return false;
		} else {
			assert visionCloserToSeer == Visibility.VISIBLE && visionFurtherToSeer != Visibility.VISIBLE : visionCloserToSeer + " " + visionFurtherToSeer;
			Border obstacle = obstaclesCache.findObstacleBorder(xOriginal, yOriginal, side);
			boolean canSee = isCellVisible(xNeighbor, yNeighbor, obstacle);
			borderVision.cacheBorderVision(xOriginal, yOriginal, side, canSee ? Visibility.VISIBLE : Visibility.INVISIBLE);
			return canSee;
		}
	}

	public ModifiableCellVisionCache getVisionCache() {
		return visionCache;
	}

	public CellVisionCache getPreviousVisionCache() {
		return visionPrevious;
	}

	public ModifiableBorderVisionCache getBorderVisionCache() {
		return borderVision;
	}

	public BorderVisionCache getPreviousBorderVisionCache() {
		return borderVisionPrevious;
	}

	/**
	 * Returns the minimum rectangle where this Seer's vision range is contained.
	 *
	 * @return Minimum rectangle where this Seer's vision range is contained.
	 */
	public Rectangle getVisionRectangle() {
		BasicCell startPoint = getActualVisionRecStartPoint();
		int actualWorldEndX = Math.min(
			world.getWidth() - 1,
			character.x() - Seer.VISION_RANGE + ModifiableCellVisionCache.VISION_CACHE_WIDTH
		);
		int actualWorldEndY = Math.min(
			world.getHeight() - 1,
			character.y() - Seer.VISION_RANGE + ModifiableCellVisionCache.VISION_CACHE_WIDTH
		);
		return new Rectangle(
			startPoint.x(),
			startPoint.y(),
			actualWorldEndX - startPoint.x(),
			actualWorldEndY - startPoint.y()
		);
	}

	/**
	 * Returns north-western point of a rectangle that contains the vision range of this Seer.
	 *
	 * @return North-western point of a rectangle that contains the vision range of this Seer.
	 */
	public BasicCell getActualVisionRecStartPoint() {
		return new BasicCell(
			Math.max(0, character.x() - Seer.VISION_RANGE),
			Math.max(0, character.y() - Seer.VISION_RANGE)
		);
	}

	/**
	 * Returns north-western point of a rectangle that contains the vision range of this Seer.
	 *
	 * @return North-western point of a rectangle that contains the vision range of this Seer.
	 */
	public BasicCell getTheoreticalVisionRecStartPoint() {
		return new BasicCell(
			character.x() - Seer.VISION_RANGE,
			character.y() - Seer.VISION_RANGE
		);
	}
}