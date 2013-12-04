package tendiwa.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.tendiwa.events.*;
import tendiwa.core.meta.Coordinate;
import tendiwa.core.meta.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Character implements PlaceableInCell, PathWalker, GsonForStaticDataSerializable {
public static final long serialVersionUID = 1832389411;
public final static int FRACTION_NEUTRAL = -1, FRACTION_PLAYER = 1,
	FRACTION_AGRESSIVE = 0;
public static final int VISION_RANGE = 11;
/**
 * Value for {@link Character#visionCache} meaning that vision of particular cell is not computed for current
 * character's position yet.
 */
public static final byte VISION_NOT_COMPUTED = 0;
/**
 * Value for {@link Character#visionCache} meaning that a particular cell is visible from this Character's current
 */
public static final byte VISION_VISIBLE = 1;
/**
 * Value for {@link Character#visionCache} meaning that a particular cell is invisible from this Character's current
 */
public static final byte VISION_INVISIBLE = 2;
public static final byte VISION_CACHE_WIDTH = (byte) (VISION_RANGE * 2 + 1);
public final int id = new UniqueObject().id;
public final ItemCollection inventory = new ItemCollection();
public final Equipment equipment = new Equipment(2, ApparelSlot.values());
protected final CharacterType characterType;
protected final String name;
protected final HashMap<Integer, Character.Effect> effects = new HashMap<>();
private final Object renderLockObject = Tendiwa.getServer();
/**
 * <p>Here is cached whether this Character sees a cell relative to his current position or not.</p> <p>{@code
 * visionCache[VISION_CACHE_WIDTH][VISION_CACHE_WIDTH]} is Character's current cell.</p>
 */
public byte[][] visionCache = new byte[VISION_CACHE_WIDTH][VISION_CACHE_WIDTH];
protected Body body;
protected int actionPoints;
protected int ep;
protected int energy;
protected int maxEp;
protected int fraction;
protected HorizontalPlane plane;
protected Chunk chunk;
protected int x;
protected int y;
protected ArrayList<Integer> spells = new ArrayList<>();
protected boolean isAlive;
protected CharacterState state = CharacterState.DEFAULT;
protected TimeStream timeStream;
private boolean isVisionCacheEmpty = true;
/**
 * Saves field of view on previous turn when it is needed to calculate diffirences between FOV on previous turn and
 * current turn.
 */
private byte[][] visionPrevious = new byte[VISION_CACHE_WIDTH][VISION_CACHE_WIDTH];

public Character(HorizontalPlane plane, CharacterType characterType, int x, int y, String name) {
	// Common character creation: with all attributes, in location.
	super();
	this.name = name;
	this.plane = plane;
	this.chunk = plane.getChunkWithCell(x, y);
	fraction = 0;
	isAlive = true;
	this.characterType = characterType;
	this.x = x;
	this.y = y;
}

/**
 * <p>Returns the first index (either x or y) of a relative table (a FOV table, for example) which resides inside world
 * rectangle {0, 0, world.width, world.height}.</p> <p>There is only one method for the first index, but two methods for
 * the last indices, because the least world coordinate is 0 on both x and y axes, but the greatest is different
 * (world.width or world.height) for x and y axes.</p>
 *
 * @param centerCoordinate
 * 	Absolute coordinate (in world coordinates) of table's center by one of axes.
 * @param tableRadius
 * 	{@code (table_width-1)/2}
 * @return First index in relative table's coordinates that resides inside world rectangle.
 * @see PathTable For more information on relative tables.
 * @see tendiwa.core.Character#computeFullVisionCache()  For more information on relative tables.
 */
public static int getStartIndexOfRelativeTable(int centerCoordinate, int tableRadius) {
	return Math.max(0, -(centerCoordinate - tableRadius));
}

/**
 * <p>Returns the last index on x axis of a relative table (a FOV table, for example) which resides inside world
 * recangle.</p> <p/> <p>There is only one method for the first index, but two methods for the last indices, because the
 * least world coordinate is 0 on both x and y axes, but the greatest is different (world.width or world.height) for x
 * and y axes.</p>
 *
 * @param centerCoordinate
 * 	Absolute x coordinate of table's center in world coordinates.
 * @param tableRadius
 * 	{@code (table_width-1)/2}
 * @return Last index in relative table's coordinates on x axis that resides inside world rectangle.
 */
public static int getEndIndexOfRelativeTableX(int centerCoordinate, int tableRadius) {
	return Math.min(tableRadius * 2 + 1, Tendiwa.getWorldWidth() - centerCoordinate + tableRadius);
}

/**
 * <p>Returns the last index on y axis of a relative table (a FOV table, for example) which resides inside world
 * recangle.</p> <p/> <p>There is only one method for the first index, but two methods for the last indices, because the
 * least world coordinate is 0 on both x and y axes, but the greatest is different (world.width or world.height) for x
 * and y axes.</p>
 *
 * @param centerCoordinate
 * 	Absolute y coordinate of table's center in world coordinates.
 * @param tableRadius
 * 	{@code (table_width-1)/2}
 * @return Last index in relative table's coordinates on x axis that resides inside world rectangle.
 */
public static int getEndIndexOfRelativeTableY(int centerCoordinate, int tableRadius) {
	return Math.min(tableRadius * 2 + 1, Tendiwa.getWorldHeight() - centerCoordinate + tableRadius);
}

/* Actions */
protected void attack(Character aim) {
	aim.getDamage(7, DamageType.PLAIN);
	moveTime(500);
	throw new UnsupportedOperationException();
}

protected void shootMissile(int toX, int toY, ItemPile missile) {
	loseItem(missile);
	Coordinate end = getRayEnd(toX, toY);
	plane.addItem(missile, end.x, end.y);
//	Cell aimCell = plane.getCell(toX, toY);
//	if (aimCell.character() != null) {
//		aimCell.character().getDamage(10, DamageType.PLAIN);
//	}
//	throw new UnsupportedOperationException();
}

protected void shootMissile(int toX, int toY, UniqueItem item) {
	loseItem(item);
	Coordinate end = getRayEnd(toX, toY);
	plane.addItem(item, end.x, end.y);
	Character character = plane.getCharacter(end.x, end.y);
	if (character != null) {
		character.getDamage(10, DamageType.PLAIN);
	}
	throw new UnsupportedOperationException();
}

protected void castSpell(int spellId, int x, int y) {
	moveTime(500);
	// TODO Implement spellcasting
	throw new UnsupportedOperationException();
}

public void learnSpell(int spellId) {
	spells.add(spellId);
}

protected void die() {
	isAlive = false;
	timeStream.claimCharacterDisappearance(this);
	plane.getChunkWithCell(x, y).removeCharacter(this);
	throw new UnsupportedOperationException();
}

public void putOn(UniqueItem item) {
	synchronized (renderLockObject) {
		inventory.removeUnique(item);
		equipment.putOn(item);
		if (isPlayer()) {
			Tendiwa.getClientEventManager().event(new EventPutOn(this, item));
		}
	}
	moveTime(500);
}

public boolean isPlayer() {
	return this == Tendiwa.getPlayer();
}

public void wield(Item item) {
	synchronized (renderLockObject) {
		if (item.getType().isStackable()) {
			ItemPile pile = new ItemPile(item.getType(), 1);
			inventory.removePile(pile);
			equipment.wield(pile);
		} else {
			inventory.removeItem(item);
			equipment.wield(item);
		}
		Tendiwa.getClientEventManager().event(new EventWield(this, item));
	}
}

public void cease(Item item) {
	synchronized (renderLockObject) {
		inventory.add(item);
		equipment.cease(item);
		Tendiwa.getClientEventManager().event(new EventUnwield(this, item));
	}
}

public void takeOff(UniqueItem item) {
	synchronized (renderLockObject) {
		inventory.add(item);
		equipment.takeOff(item);
		Tendiwa.getClientEventManager().event(new EventTakeOff(this, item));
	}
	moveTime(500);
}

/**
 * Pick up an item lying on the same cell where the character stands.
 */
public void pickUp(ItemPile pile) {
	Tendiwa.getClientEventManager().event(new EventItemDisappear(x, y, pile));
	plane.removeItem(pile, x, y);
	Tendiwa.getClientEventManager().event(new EventGetItem(pile));
	getItem(pile);
	moveTime(500);
	throw new UnsupportedOperationException();
}

/**
 * Pick up an item lying on the same cell where the character stands.
 */
public void pickUp(UniqueItem item) {
	getItem(item);
	plane.removeItem(item, x, y);
	moveTime(500);
	throw new UnsupportedOperationException();
}

public void drop(Item item) {
	loseItem(item);
	Chunk chunk = plane.getChunkWithCell(x, y);
	chunk.addItem(item, x - chunk.getX(), y - chunk.getY());
	Tendiwa.getClientEventManager().event(new EventItemAppear(item));
	moveTime(500);
}

protected void takeFromContainer(ItemPile pile, Container container) {
	getItem(pile);
	container.removePile(pile);
	moveTime(500);
	throw new UnsupportedOperationException();
}

protected void takeFromContainer(UniqueItem item, Container container) {
	getItem(item);
	container.removeUnique(item);
	moveTime(500);
	throw new UnsupportedOperationException();
}

protected void putToContainer(ItemPile pile, Container container) {
	loseItem(pile);
	container.add(pile);
	moveTime(500);
	throw new UnsupportedOperationException();
}

protected void putToContainer(UniqueItem item, Container container) {
	loseItem(item);
	container.add(item);
	moveTime(500);
	throw new UnsupportedOperationException();
}

protected void idle() {
	moveTime(500);
}

protected void step(int x, int y) {
	move(x, y);
	if (state == CharacterState.RUNNING) {
		changeEnergy(-30);
		moveTime(200);
	} else {
		moveTime(500);
	}

}

protected void makeSound(SoundType type) {
	timeStream.makeSound(x, y, type);
}

	/* Special actions */

protected void enterState(CharacterState state) {
	this.state = state;
	throw new UnsupportedOperationException();
}

/**
 * Pushes another character so he moves to another cell
 *
 * @param character
 * 	A Character being pushed.
 * @param side
 * 	SideTest to push relative to the character being pushed.
 */
protected void push(Character character, Direction side) {
	int[] d = side.side2d();
	int nx = character.x + d[0];
	int ny = character.y + d[1];
	if (plane.getPassability(nx, ny) == Chunk.Passability.FREE) {
		int bufX = character.x;
		int bufY = character.y;
		character.move(nx, ny);
		if (!new Coordinate(x, y).isNear(nx, ny)) {
			move(bufX, bufY);
		}
	}
	moveTime(500);
}

protected void changePlaces(Character character) {
	int prevX = x;
	int prevY = y;
	move(character.x, character.y);
	character.move(prevX, prevY);
	changeEnergy(-30);
	// This event is needed for client to correctly
	// handle characters' new positions in Terrain.cells
	moveTime(500);
	throw new UnsupportedOperationException();
}

protected void jump(int x, int y) {
	move(x, y);
	changeEnergy(-40);
	moveTime(500);
	throw new UnsupportedOperationException();
}

private void cacheVision(int x, int y, byte visible) {
	visionCache[(byte) (x - this.x + VISION_RANGE)][(byte) (y - this.y + VISION_RANGE)] = visible;
	isVisionCacheEmpty = false;
}

private byte getVisionFromCache(int x, int y) {
	return visionCache[(byte) (x - this.x + VISION_RANGE)][(byte) (y - this.y + VISION_RANGE)];
}

void invalidateVisionCache() {
	for (byte i = 0; i < VISION_CACHE_WIDTH; i++) {
		for (byte j = 0; j < VISION_CACHE_WIDTH; j++) {
			visionCache[i][j] = VISION_NOT_COMPUTED;
		}
	}
	isVisionCacheEmpty = true;
}

/* Vision */
public boolean initialCanSee(int x, int y) {
	Coordinate characterCoord = new Coordinate(this.x, this.y);
	if (characterCoord.isNear(x, y) || this.x == x && this.y == y) {
		cacheVision(x, y, VISION_VISIBLE);
		return true;
	}
	if (Math.floor(characterCoord.distance(x, y)) > Character.VISION_RANGE) {
		cacheVision(x, y, VISION_INVISIBLE);
		return false;
	}
	if (x == this.x || y == this.y) {
		if (x == this.x) {
			int dy = Math.abs(y - this.y) / (y - this.y);
			for (int i = this.y + dy; i != y; i += dy) {
				if (plane.getPassability(x, i) == Chunk.Passability.NO) {
					cacheVision(x, y, VISION_INVISIBLE);
					return false;
				}
			}
		} else {
			int dx = Math.abs(x - this.x) / (x - this.x);
			for (int i = this.x + dx; i != x; i += dx) {
				if (plane.getPassability(i, y) == Chunk.Passability.NO) {
					cacheVision(x, y, VISION_INVISIBLE);
					return false;
				}
			}
		}
		cacheVision(x, y, VISION_VISIBLE);
		return true;
	} else if (Math.abs(x - this.x) == 1) {
		int yMin = Math.min(y, this.y);
		int yMax = Math.max(y, this.y);
		for (int i = yMin + 1; i < yMax; i++) {
			if (plane.getPassability(x, i) == Chunk.Passability.NO) {
				break;
			}
			if (i == yMax - 1) {
				cacheVision(x, y, VISION_VISIBLE);
				return true;
			}
		}
		for (int i = yMin + 1; i < yMax; i++) {
			if (plane.getPassability(this.x, i) == Chunk.Passability.NO) {
				break;
			}
			if (i == yMax - 1) {
				cacheVision(x, y, VISION_VISIBLE);
				return true;
			}
		}
		cacheVision(x, y, VISION_INVISIBLE);
		return false;
	} else if (Math.abs(y - this.y) == 1) {
		int xMin = Math.min(x, this.x);
		int xMax = Math.max(x, this.x);
		for (int i = xMin + 1; i < xMax; i++) {
			if (plane.getPassability(i, y) == Chunk.Passability.NO) {
				break;
			}
			if (i == xMax - 1) {
				cacheVision(x, y, VISION_VISIBLE);
				return true;
			}
		}
		for (int i = xMin + 1; i < xMax; i++) {
			if (plane.getPassability(i, this.y) == Chunk.Passability.NO) {
				break;
			}
			if (i == xMax - 1) {
				cacheVision(x, y, VISION_VISIBLE);
				return true;
			}
		}
		cacheVision(x, y, VISION_INVISIBLE);
		return false;
	} else if (Math.abs(x - this.x) == Math.abs(y - this.y)) {
		int dMax = Math.abs(x - this.x);
		int dx = x > this.x ? 1 : -1;
		int dy = y > this.y ? 1 : -1;
		int cx = this.x;
		int cy = this.y;
		for (int i = 1; i < dMax; i++) {
			cx += dx;
			cy += dy;
			if (plane.getPassability(cx, cy) == Chunk.Passability.NO) {
				cacheVision(x, y, VISION_INVISIBLE);
				return false;
			}
		}
		cacheVision(x, y, VISION_VISIBLE);
		return true;
	} else {
		double[][] start = new double[2][2];
		double[] end = new double[4];
		end[0] = (x > this.x) ? x - 0.5 : x + 0.5;
		end[1] = (y > this.y) ? y - 0.5 : y + 0.5;
		end[2] = x;
		end[3] = y;
		start[0][0] = (x > this.x) ? this.x + 0.5 : this.x - 0.5;
		start[0][1] = (y > this.y) ? this.y + 0.5 : this.y - 0.5;
		start[1][0] = (x > this.x) ? this.x + 0.5 : this.x - 0.5;
		start[1][1] = (y > this.y) ? this.y + 0.5 : this.y - 0.5;
		Coordinate[] rays = rays(this.x, this.y, x, y);
		jump:
		for (int k = 0; k < 3; k++) {
			int endNumX = (k == 0 || k == 1) ? 0 : 2;
			int endNumY = (k == 0 || k == 2) ? 1 : 3;
			for (int j = 0; j < 1; j++) {
				if (start[j][0] == this.x && start[j][1] == this.y) {
					continue;
				}
				double xEnd = end[endNumX];
				double yEnd = end[endNumY];
				double xStart = start[j][0];
				double yStart = start[j][1];
				for (Coordinate c : rays) {
					if (plane.getPassability(c.x, c.y) == Chunk.Passability.NO) {
						if (c.x == x && c.y == y || c.x == x
							&& c.y == y) {
							continue;
						}
						if (Math.abs(((yStart - yEnd) * c.x
							+ (xEnd - xStart) * c.y + (xStart
							* yEnd - yStart * xEnd))
							/ Math.sqrt(Math.abs((xEnd - xStart)
							* (xEnd - xStart)
							+ (yEnd - yStart)
							* (yEnd - yStart)))) <= 0.5) {
							continue jump;
						}
					}
				}
				cacheVision(x, y, VISION_VISIBLE);
				return true;
			}
		}
		cacheVision(x, y, VISION_INVISIBLE);
		return false;
	}
}

public byte getFromCache(int x, int y) {
	return visionCache[x - this.x + VISION_RANGE][y - this.y + VISION_RANGE];
}

public Coordinate getRayEnd(int endX, int endY) {
	Coordinate characterCoord = new Coordinate(this.x, this.y);
	if (characterCoord.isNear(endX, endY) || this.x == endX && this.y == endY) {
		return new Coordinate(endX, endY);
	}
	if (endX == this.x || endY == this.y) {
		if (endX == this.x) {
			int dy = Math.abs(endY - this.y) / (endY - this.y);
			for (int i = this.y + dy; i != endY + dy; i += dy) {
				if (plane.getPassability(endX, i) != Chunk.Passability.FREE) {
					return new Coordinate(endX, i - dy);
				}
			}
		} else {
			int dx = Math.abs(endX - this.x) / (endX - this.x);
			for (int i = this.x + dx; i != endX + dx; i += dx) {
				if (plane.getPassability(i, endY) != Chunk.Passability.FREE) {
					return new Coordinate(i - dx, endY);
				}
			}
		}
		return new Coordinate(endX, endY);
	} else if (Math.abs(endX - this.x) == 1) {
		int dy = Math.abs(endY - this.y) / (endY - this.y);
		int y1 = endY, y2 = endY;
		for (int i = this.y + dy; i != endY + dy; i += dy) {
			if (plane.getPassability(endX, i) != Chunk.Passability.FREE) {
				y1 = i - dy;
				break;
			}
			if (i == endY) {
				return new Coordinate(endX, endY);
			}
		}
		for (int i = this.y + dy; i != endY + dy; i += dy) {
			if (plane.getPassability(this.x, i) != Chunk.Passability.FREE) {
				y2 = i - dy;
				break;
			}
		}
		Coordinate answer;
		if (characterCoord.distance(endX, y1) > characterCoord.distance(this.x, y2)) {
			answer = new Coordinate(endX, y1);
		} else {
			answer = new Coordinate(this.x, y2);
		}
		if (answer.x == this.x
			&& answer.y == y2
			&& plane.getPassability(endX, endY) == Chunk.Passability.FREE) {
			// If answer is the furthest cell on the same line, but
			// {endX:endY} is free
			answer.x = endX;
			answer.y = endY;
		} else if (answer.x == this.x
			&& answer.y == y2
			&& plane.getPassability(endX, endY) == Chunk.Passability.NO) {
			// If answer is the furthest cell on the same line, and
			// {endX:endY} has no passage
			answer.y = endY - dy;
		}
		return answer;
	} else if (Math.abs(endY - this.y) == 1) {
		int dx = Math.abs(endX - this.x) / (endX - this.x);
		int x1 = endX, x2 = endX;
		for (int i = this.x + dx; i != endX + dx; i += dx) {
			if (plane.getPassability(i, endY) != Chunk.Passability.FREE) {
				x1 = i - dx;
				break;
			}
			if (i == endX) {
				return new Coordinate(endX, endY);
			}
		}
		for (int i = this.x + dx; i != endX + dx; i += dx) {
			if (plane.getPassability(i, this.y) != Chunk.Passability.FREE) {
				x2 = i - dx;
				break;
			}
		}
		Coordinate answer;
		if (characterCoord.distance(x1, endY) > characterCoord.distance(x2, this.y)) {
			answer = new Coordinate(x1, endY);
		} else {
			answer = new Coordinate(x2, this.y);
		}
		if (answer.x == x2
			&& answer.y == this.y
			&& plane.getPassability(endX, endY) == Chunk.Passability.FREE) {
			// If answer is the furthest cell on the same line, but
			// {endX:endY} is free
			answer.x = endX;
			answer.y = endY;
		} else if (answer.x == x2
			&& answer.y == this.y
			&& plane.getPassability(endX, endY) == Chunk.Passability.NO) {
			// If answer is the furthest cell on the same line, and
			// {endX:endY} has no passage
			answer.x = endX - dx;
		}

		return answer;
	} else if (Math.abs(endX - this.x) == Math.abs(endY - this.y)) {
		int dMax = Math.abs(endX - this.x);
		int dx = endX > this.x ? 1 : -1;
		int dy = endY > this.y ? 1 : -1;
		int cx = this.x;
		int cy = this.y;
		for (int i = 1; i <= dMax; i++) {
			cx += dx;
			cy += dy;
			if (plane.getPassability(cx, cy) == Chunk.Passability.NO) {
				return new Coordinate(cx - dx, cy - dy);
			}

		}
		return new Coordinate(endX, endY);
	} else {
		double[][] start = new double[2][2];
		double[] end = new double[4];
		end[0] = (endX > this.x) ? endX - 0.5 : endX + 0.5;
		end[1] = (endY > this.y) ? endY - 0.5 : endY + 0.5;
		end[2] = endX;
		end[3] = endY;
		start[0][0] = (endX > this.x) ? this.x + 0.5 : this.x - 0.5;
		start[0][1] = (endY > this.y) ? this.y + 0.5 : this.y - 0.5;
		start[1][0] = (endX > this.x) ? this.x + 0.5 : this.x - 0.5;
		// start[0][1]=this.y;
		// start[1][0]=this.x;
		start[1][1] = (endY > this.y) ? this.y + 0.5 : this.y - 0.5;
		Coordinate[] rays = rays(this.x, this.y, endX, endY);
		int breakX = this.x, breakY = this.y;
		jump:
		for (int k = 0; k < 3; k++) {
			int endNumX = (k == 0 || k == 1) ? 0 : 2;
			int endNumY = (k == 0 || k == 2) ? 1 : 3;
			for (int j = 0; j < 1; j++) {
				if (start[j][0] == this.x && start[j][1] == this.y) {
					continue;
				}
				double xEnd = end[endNumX];
				double yEnd = end[endNumY];
				double xStart = start[j][0];
				double yStart = start[j][1];
				for (Coordinate c : rays) {
					try {
						if (plane.getPassability(c.x, c.y) == Chunk.Passability.NO) {
							if (Math.abs(((yStart - yEnd) * c.x
								+ (xEnd - xStart) * c.y + (xStart
								* yEnd - yStart * xEnd))
								/ Math.sqrt(Math.abs((xEnd - xStart)
								* (xEnd - xStart)
								+ (yEnd - yStart)
								* (yEnd - yStart)))) <= 0.5) {
								continue jump;
							}

						} else {
							breakX = c.x;
							breakY = c.y;
						}
					} catch (Exception e) {
						throw new Error();
					}
				}
				return new Coordinate(endX, endY);
			}
		}
		return new Coordinate(breakX, breakY);
	}
}

/* Getters */

public Coordinate[] rays(int startX, int startY, int endX, int endY) {
	return Utils.concatAll(
		Chunk.vector(startX, startY, endX, endY),
		Chunk.vector(startX, startY + (endY > startY ? 1 : -1), endX + (endX > startX ? -1 : 1), endY),
		Chunk.vector(startX + (endX > startX ? 1 : -1), startY, endX, endY + (endY > startY ? -1 : 1))
	);
}

public int hashCode() {
	return id;
}

public CharacterType getType() {
	return characterType;
}

public boolean isAlive() {
	return isAlive;
}

/**
 * @return the actionPoints
 */
public int getActionPoints() {
	return actionPoints;
}

public int increaseActionPoints(int value) {
	return actionPoints += value;
}

public int getFraction() {
	return fraction;
}

public void setFraction(int fraction) {
	this.fraction = fraction;
}
	/* Setters */

public int getId() {
	return id;
}

public String getName() {
	return name;
}

/**
 * Changes character's position.
 * <p/>
 * Note that this is not a character action, this method is also called when character blinks, being pushed and so on.
 * For action method, use Character.step.
 */
public void move(int x, int y) {
	// TODO: Move full vision cache computing to PlayerCharacter class.
	// Copy cache
	for (int i = 0; i < VISION_CACHE_WIDTH; i++) {
		for (int j = 0; j < VISION_CACHE_WIDTH; j++) {
			visionPrevious[i][j] = visionCache[i][j];
		}
	}
	int xPrev = this.x;
	int yPrev = this.y;
	synchronized (renderLockObject) {
		Tendiwa.getClientEventManager().event(new EventMove(xPrev, yPrev, this));
		plane.removeCharacter(this);
		this.x = x;
		this.y = y;
		plane.addCharacter(this);
	}
	synchronized (renderLockObject) {
		timeStream.notifyNeighborsVisiblilty(this);
		this.computeFullVisionCache();
		Tendiwa.getClientEventManager().event(new EventFovChange(xPrev, yPrev, visionPrevious, visionCache));
	}
}

public void getDamage(int amount, DamageType type) {
	throw new UnsupportedOperationException();
}

protected void changeEnergy(int amount) {
	amount = Math.min(amount, maxEp - ep);
	if (amount != 0) {
		ep += amount;
		throw new UnsupportedOperationException();
	} else {
		if (state == CharacterState.RUNNING) {
			enterState(CharacterState.DEFAULT);
		}
	}
}

protected void removeEffect(CharacterEffect effect) {
	effects.remove(effect);
}

public void getItem(Item item) {
	inventory.add(item);
}

public void loseItem(Item item) {
	Tendiwa.getClient().getEventManager().event(new EventLoseItem(item));
	inventory.removeItem(item);
}

public void addEffect(int effectId, int duration, int modifier) {
	if (effects.containsKey(effectId)) {
		removeEffect(effectId);
	}
	effects.put(effectId, new Character.Effect(effectId, duration, modifier));
	throw new UnsupportedOperationException();
}

public void removeEffect(int effectId) {
	effects.remove(effectId);
	throw new UnsupportedOperationException();
}

protected void moveTime(int amount) {
	for (Character.Effect e : effects.values()) {
		e.duration -= amount;
		if (e.duration < 0) {
			removeEffect(e.effectId);
		}
	}
	changeEnergy(10);
}

/* Checks */
public boolean at(int atX, int atY) {
	return x == atX && y == atY;
}

public boolean isEnemy(Character ch) {
	if (fraction == FRACTION_NEUTRAL) {
		return false;
	}
	return ch.fraction != fraction;
}

public TimeStream getTimeStream() {
	return timeStream;
}

public int getX() {
	return x;
}

public int getY() {
	return y;
}

@Override
public JsonElement serialize(JsonSerializationContext context) {
	JsonArray jArray = new JsonArray();
	jArray.add(new JsonPrimitive(name));
	jArray.add(new JsonPrimitive(x));
	jArray.add(new JsonPrimitive(y));
	jArray.add(new JsonPrimitive(fraction));
	return jArray;
}

@Override
public void place(HorizontalPlane plane, int x, int y) {
	this.x = x;
	this.y = y;
	this.plane.removeCharacter(this);
	plane.addCharacter(this);
}

@Override
public boolean containedIn(HorizontalPlane plane, int x, int y) {
	return this.plane == plane && this.x == x && this.y == y;
}

@Override
public boolean canStepOn(int x, int y) {
	return x >= 0
		&& y >= 0
		&& x < Tendiwa.getWorld().width
		&& y < Tendiwa.getWorld().height
		&& plane.getPassability(x, y) == Chunk.Passability.FREE;
}

public boolean canSee(int x, int y) {
//	return  EnhancedPoint.distance(x, y, this.x, this.y) < 7;
	if (Math.abs(x - this.x) > VISION_RANGE) {
		return false;
	}
	if (Math.abs(y - this.y) > VISION_RANGE) {
		return false;
	}
	byte visionFromCache = getVisionFromCache(x, y);
	if (visionFromCache == VISION_NOT_COMPUTED) {
		return initialCanSee(x, y);
	} else {
		return visionFromCache == VISION_VISIBLE;
	}
}

public boolean isVisionCacheEmpty() {
	return isVisionCacheEmpty;
}

public void computeFullVisionCache() {
	int startX = getStartIndexOfRelativeTable(x, VISION_RANGE);
	int startY = getStartIndexOfRelativeTable(y, VISION_RANGE);
	int endX = getEndIndexOfRelativeTableX(x, VISION_RANGE);
	int endY = getEndIndexOfRelativeTableY(y, VISION_RANGE);
	for (int i = startX; i < endX; i++) {
		for (int j = startY; j < endY; j++) {
			initialCanSee(x - VISION_RANGE + i, y - VISION_RANGE + j);
		}
	}
}

public byte[][] getVisionCache() {
	return visionCache;
}

public HorizontalPlane getPlane() {
	return plane;
}

public void pickUp(Item item) {
	Tendiwa.getClientEventManager().event(new EventItemDisappear(x, y, item));
	plane.getItems(x, y).removeItem(item);
	Tendiwa.getClientEventManager().event(new EventGetItem(item));
	getItem(item);
}

public ItemCollection getInventory() {
	return inventory;
}

public Equipment getEquipment() {
	return equipment;
}

/* Nested classes */
public class Effect {
	// Class that holds description of one current character's effect
	public int duration, modifier, effectId;

	public Effect(int effectId, int duration, int modifier) {
		this.effectId = effectId;
		this.duration = duration;
		this.modifier = modifier;
	}
}
}
