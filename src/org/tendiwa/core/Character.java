package org.tendiwa.core;

import org.tendiwa.core.events.*;
import org.tendiwa.core.meta.CellPosition;
import org.tendiwa.core.meta.Coordinate;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.vision.Seer;
import org.tendiwa.core.vision.SightPassabilityCriteria;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Character implements CellPosition, PlaceableInCell, PathWalker, DamageSource {
public static final Object renderLockObject = Tendiwa.getLock();
public final int id = new UniqueObject().id;
public final ItemCollection inventory = new ItemCollection();
public final Equipment equipment = new Equipment(2, ApparelSlot.values());
public final Seer seer;
protected final String name;
protected final HashMap<Integer, Character.Effect> effects = new HashMap<>();
final CharacterType type;
private final World world;
private final Observable backend;
protected Body body;
protected int actionPoints;
protected int ep;
protected int maxEp;
protected int fraction;
protected HorizontalPlane plane;
protected Chunk chunk;
protected int x;
protected int y;
protected boolean isAlive;
protected CharacterState state = CharacterState.DEFAULT;
protected TimeStream timeStream;
protected int hp;
protected int maxHp;
/**
 * Lazily created by {@link Character#getPathWalkerOverCharacters()}It is not static because it needs the {@link
 * Character#plane} of NonPlayerCharacter.
 */
private PathWalkerOverCharacters pathWalkerOverCharacters;
private Collection<Spell> spells = new HashSet<>();

public Character(World world, Observable backend, HorizontalPlane plane, CharacterType type, int x, int y, String name) {
	// Common character creation: with all attributes, in location.
	super();
	this.world = world;
	this.backend = backend;
	assert type != null;
	this.type = type;
	this.name = name;
	this.plane = plane;
	this.chunk = plane.getChunkWithCell(x, y);
	fraction = 0;
	isAlive = true;
	this.x = x;
	this.y = y;
	this.maxHp = type.getMaxHp();
	assert maxHp != 0;
	this.hp = maxHp;
	this.seer =  new Seer(this, new CharacterVisionCriteria(), new DefaultObstacleFindingStrategy(this));
}

public PathWalkerOverCharacters getPathWalkerOverCharacters() {
	if (pathWalkerOverCharacters == null) {
		pathWalkerOverCharacters = new PathWalkerOverCharacters();
	}
	return pathWalkerOverCharacters;
}

/* Actions */
public void attack(Character aim) {
	synchronized (renderLockObject) {
		Tendiwa.getInstance().emitEvent(new EventAttack(this, aim));
	}
	Tendiwa.waitForAnimationToStartAndComplete();
	aim.getDamage(7, DamageType.PLAIN, this);
	moveInTime(500);
}

protected void shootMissile(int toX, int toY, ItemPile missile) {
	loseItem(missile);
	Coordinate end = seer.getRayEnd(toX, toY);
	plane.addItem(missile, end.x, end.y);
//	Cell aimCell = plane.getCell(toX, toY);
//	if (aimCell.character() != null) {
//		aimCell.character().getDamage(10, DamageType.PLAIN);
//	}
//	throw new UnsupportedOperationException();
}

protected void shootMissile(int toX, int toY, UniqueItem item) {
	loseItem(item);
	Coordinate end = seer.getRayEnd(toX, toY);
	plane.addItem(item, end.x, end.y);
	Character character = plane.getCharacter(end.x, end.y);
	if (character != null) {
		character.getDamage(10, DamageType.PLAIN, this);
	}
	throw new UnsupportedOperationException();
}

protected void castSpell(int spellId, int x, int y) {
	moveInTime(500);
	// TODO Implement spellcasting
	throw new UnsupportedOperationException();
}

protected void die() {
	synchronized (renderLockObject) {
		isAlive = false;
		timeStream.claimCharacterDisappearance(this);
		plane.getChunkWithCell(x, y).removeCharacter(this);
		Tendiwa.getInstance().emitEvent(new EventDie(this));
	}
	Tendiwa.waitForAnimationToStartAndComplete();

}

public void putOn(UniqueItem item) {
	synchronized (renderLockObject) {
		inventory.removeUnique(item);
		equipment.putOn(item);
		if (isPlayer()) {
			Tendiwa.getInstance().emitEvent(new EventPutOn(this, item));
		}
	}
	moveInTime(500);
}

public boolean isPlayer() {
	return this == world.getPlayer();
}

public void wield(Item item) {
	synchronized (renderLockObject) {
		if (item.getType().isStackable()) {
			ItemPile itemPile = (ItemPile) item;
			ItemPile pile = new ItemPile(itemPile.getType(), 1);
			inventory.removePile(pile);
			equipment.wield(pile);
		} else {
			inventory.removeItem(item);
			equipment.wield(item);
		}
		Tendiwa.getInstance().emitEvent(new EventWield(this, item));
	}
}

public void cease(Item item) {
	synchronized (renderLockObject) {
		inventory.add(item);
		equipment.cease(item);
		Tendiwa.getInstance().emitEvent(new EventUnwield(this, item));
	}
}

public void takeOff(UniqueItem item) {
	synchronized (renderLockObject) {
		inventory.add(item);
		equipment.takeOff(item);
		Tendiwa.getInstance().emitEvent(new EventTakeOff(this, item));
	}
	moveInTime(500);
}

/**
 * Pick up an item lying on the same cell where the character stands.
 */
public void pickUp(ItemPile pile) {
	Tendiwa.getInstance().emitEvent(new EventItemDisappear(x, y, pile));
	plane.removeItem(pile, x, y);
	Tendiwa.getInstance().emitEvent(new EventGetItem(pile));
	getItem(pile);
	moveInTime(500);
	throw new UnsupportedOperationException();
}

/**
 * Pick up an item lying on the same cell where the character stands.
 */
public void pickUp(UniqueItem item) {
	getItem(item);
	plane.removeItem(item, x, y);
	moveInTime(500);
	throw new UnsupportedOperationException();
}

public void drop(Item item) {
	loseItem(item);
	Chunk chunk = plane.getChunkWithCell(x, y);
	chunk.addItem(item, x - chunk.getX(), y - chunk.getY());
	Tendiwa.getInstance().emitEvent(new EventItemAppear(item, this.x, this.y));
	moveInTime(500);
}

protected void takeFromContainer(ItemPile pile, org.tendiwa.core.Container container) {
	getItem(pile);
	container.removePile(pile);
	moveInTime(500);
	throw new UnsupportedOperationException();
}

protected void takeFromContainer(UniqueItem item, org.tendiwa.core.Container container) {
	getItem(item);
	container.removeUnique(item);
	moveInTime(500);
	throw new UnsupportedOperationException();
}

protected void putToContainer(ItemPile pile, org.tendiwa.core.Container container) {
	loseItem(pile);
	container.add(pile);
	moveInTime(500);
	throw new UnsupportedOperationException();
}

protected void putToContainer(UniqueItem item, org.tendiwa.core.Container container) {
	loseItem(item);
	container.add(item);
	moveInTime(500);
	throw new UnsupportedOperationException();
}

public void idle() {
	moveInTime(500);
}

protected void step(int x, int y) {
	move(x, y, MovingStyle.STEP);
	if (state == CharacterState.RUNNING) {
		changeEnergy(-30);
		moveInTime(200);
	} else {
		moveInTime(500);
	}

}

protected void makeSound(SoundType type) {
	timeStream.makeSound(x, y, type, this);
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
	if (plane.getPassability(nx, ny) == Passability.FREE) {
		int bufX = character.x;
		int bufY = character.y;
		character.move(nx, ny, MovingStyle.STEP);
		if (!new Coordinate(x, y).isNear(nx, ny)) {
			move(bufX, bufY, MovingStyle.STEP);
		}
	}
	moveInTime(500);
}

/**
 * Player's vision cache gets invalidated in {@link Seer#computeFullVisionCache()}.
 */

public boolean canSee(int x, int y) {
	return seer.canSee(x, y);
}


/* Getters */

public Coordinate[] rays(int startX, int startY, int endX, int endY) {
	return seer.rays(startX, startY, endX, endY);
}

public int hashCode() {
	return id;
}

public boolean isAlive() {
	assert isAlive;
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
 * For action method, use {@link Character#step(int, int)}.
 */
public void move(int x, int y, MovingStyle movingStyle) {
	int xPrev = this.x;
	int yPrev = this.y;
	synchronized (renderLockObject) {
		Tendiwa.getInstance().emitEvent(new EventMove(xPrev, yPrev, this, movingStyle));
		plane.removeCharacter(this);
		this.x = x;
		this.y = y;
		plane.addCharacter(this);
	}
	Tendiwa.waitForAnimationToStartAndComplete();
	timeStream.notifyNeighborsVisiblilty(this);
	if (isPlayer()) {
		synchronized (renderLockObject) {
			seer.storeVisionCacheToPreviousVisionCache();
			seer.invalidateVisionCache();
			seer.computeFullVisionCache();
			VisibilityChange visibilityChange = new VisibilityChange(
				world,
				this,
				xPrev,
				yPrev,
				seer.getPreviousVisionCache(),
				seer.getVisionCache(),
				seer.getPreviousBorderVisionCache(),
				seer.getBorderVisionCache()
			);
			Tendiwa.getInstance().emitEvent(visibilityChange.createEvent());
		}
		Tendiwa.waitForAnimationToStartAndComplete();
	} else {
		seer.invalidateVisionCache();
	}
	moveInTime(500);
}

/**
 * Moves this Character to another vertical plane of a world
 *
 * @param dz
 * 	How many planes to go up. May be negative to go down.
 */
public void moveByPlane(int dz) {
	synchronized (renderLockObject) {
		plane.removeCharacter(this);
		plane = world.getPlane(plane.getLevel() + dz);
		plane.addCharacter(this);
		seer.invalidateVisionCache();
		seer.storeVisionCacheToPreviousVisionCache();
		seer.computeFullVisionCache();
		Tendiwa.getInstance().emitEvent(new EventMoveToPlane());
	}
	Tendiwa.waitForAnimationToStartAndComplete();
	moveInTime(500);
}

public void spendActionPoints(int amount) {
	actionPoints -= amount;
}

public void getDamage(int amount, DamageType type, DamageSource damageSource) {
	this.hp -= amount;
	synchronized (renderLockObject) {
		Tendiwa.getInstance().emitEvent(new EventGetDamage(this, amount, damageSource, type));
	}
	Tendiwa.waitForAnimationToStartAndComplete();
	if (hp <= 0) {
		die();
	}
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

public void say(String message) {
	synchronized (renderLockObject) {
		Tendiwa.getInstance().emitEvent(new EventSay(message, this));
	}
	Tendiwa.waitForAnimationToStartAndComplete();
}

public void getItem(Item item) {
	synchronized (renderLockObject) {
		inventory.add(item);
	}
	Tendiwa.waitForAnimationToStartAndComplete();
}

public void getItem(ItemType type) {
	assert type != null;
	getItem(Items.createItem(type));
}

public void getItem(ItemType type, int amount) {
	if (amount < 1) {
		throw new IllegalArgumentException("Amount must be positive");
	}
	getItem(Items.createItemPile(type, amount));
}

public void loseItem(Item item) {
	synchronized (renderLockObject) {
		Tendiwa.getInstance().emitEvent(new EventLoseItem(item));
		if (item.getType().isStackable()) {
			inventory.removePile((ItemPile) item);
		} else {
			inventory.removeUnique((UniqueItem) item);
		}
	}
	Tendiwa.waitForAnimationToStartAndComplete();
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

protected void moveInTime(int amount) {
	spendActionPoints(amount);
	Character nextCharacter = timeStream.next();
	if (!nextCharacter.isPlayer()) {
		((NonPlayerCharacter) nextCharacter).action();
	}
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
	return ch.isPlayer() != this.isPlayer();
}

public TimeStream getTimeStream() {
	return timeStream;
}

public void setTimeStream(TimeStream timeStream) {
	this.timeStream = timeStream;
}

@Override
public int getX() {
	return x;
}

@Override
public int getY() {
	return y;
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
		&& x < world.width
		&& y < world.height
		&& plane.getPassability(x, y) == Passability.FREE;
}

public HorizontalPlane getPlane() {
	return plane;
}

public void pickUp(Item item) {
	synchronized (renderLockObject) {
		backend.emitEvent(new EventItemDisappear(x, y, item));
		plane.getItems(x, y).removeItem(item);
	}
	Tendiwa.waitForAnimationToStartAndComplete();
	synchronized (renderLockObject) {
		backend.emitEvent(new EventGetItem(item));
		getItem(item);
	}
	Tendiwa.waitForAnimationToStartAndComplete();
}

public ItemCollection getInventory() {
	return inventory;
}

public Equipment getEquipment() {
	return equipment;
}

public CharacterType getType() {
	return type;
}

public void propel(Item item, int x, int y) {
	assert inventory.contains(item);
	synchronized (renderLockObject) {
		loseItem(item);
	}
	Tendiwa.waitForAnimationToStartAndComplete();
	synchronized (renderLockObject) {
		backend.emitEvent(new EventProjectileFly(item, this.x, this.y, x, y, EventProjectileFly.FlightStyle.CAST));
	}
	Tendiwa.waitForAnimationToStartAndComplete();
	synchronized (renderLockObject) {
		backend.emitEvent(new EventItemAppear(item, x, y));
		Chunk chunkWithCell = plane.getChunkWithCell(x, y);
		chunkWithCell.addItem(item, x, y);
	}
	Tendiwa.waitForAnimationToStartAndComplete();
}

/**
 * Shoot an item (projectile) using another item (ranged weapon).
 *
 * @param weapon
 * 	A weapon character is shooting with.
 * @param projectile
 * 	An item being propelled with a ranged weapon. A UniqueItem or the whole ItemPile. You don't need to extract one item
 * 	from ItemPile if you want to shoot a projectile from an ItemPile.
 */
public void shoot(UniqueItem weapon, Item projectile, int toX, int toY) {
	assert getEquipment().isWielded(weapon);
	assert getInventory().contains(projectile);
	loseItem(projectile);

	ProjectileFlight flight = computeProjectileFlightEndCoordinate(weapon, projectile, toX, toY);
	synchronized (renderLockObject) {
		backend.emitEvent(new EventProjectileFly(
			projectile,
			x,
			y,
			flight.endCoordinate.x,
			flight.endCoordinate.y,
			EventProjectileFly.FlightStyle.PROPELLED
		));
	}
	Tendiwa.waitForAnimationToStartAndComplete();

	if (flight.characterHit != null) {
		flight.characterHit.getDamage(10, DamageType.PLAIN, this);
	}
	synchronized (renderLockObject) {
		backend.emitEvent(new EventItemAppear(projectile, toX, toY));
		Chunk chunkWithCell = plane.getChunkWithCell(toX, toY);
		chunkWithCell.addItem(projectile, toX, toY);
	}
	Tendiwa.waitForAnimationToStartAndComplete();
}

private ProjectileFlight computeProjectileFlightEndCoordinate(UniqueItem weapon, Item projectile, int toX, int toY) {
	Coordinate endCoordinate = new Coordinate(toX, toY);
	Coordinate[] vector = Chunk.vector(x, y, toX, toY);
	Character characterHit = null;
	for (int i = 1; i < vector.length; i++) {
		Coordinate c = vector[i];
		if (plane.hasCharacter(c.x, c.y) &&
			testProjectileHit(weapon, projectile, toX, toY)) {
			characterHit = plane.getCharacter(c.x, c.y);
			endCoordinate = c;
		}
	}
	return new ProjectileFlight(
		endCoordinate,
		characterHit
	);
}

private boolean testProjectileHit(UniqueItem weapon, Item projectile, int toX, int toY) {
	return true;
}

public Collection<CharacterAbility> getAvailableActions() {
	return getType().getAvailableActions();
}

public Collection<Spell> getSpells() {
	return spells;
}

public void learnSpell(Spell spell) {
	spells.add(spell);
}

@Override
public String toString() {
	return type.getResourceName();
}

public int getHP() {
	return hp;
}

public int getMaxHP() {
	return maxHp;
}

@Override
public String getLocalizationId() {
	return getType().getLocalizationId();
}

@Override
public DamageSourceType getSourceType() {
	return DamageSourceType.CHARACTER;
}

/**
 * Checks if there are any conditions that make current existing of this character dangerous. This method is supposed to
 * be used to check if a multi-turn task in a client can continue.
 *
 * @return true if something threatens this character, false otherwise.
 */
public boolean isUnderAnyThreat() {
	for (NonPlayerCharacter observer : timeStream.getObservers(this)) {
		if (isEnemy(observer)) {
			return true;
		}
	}
	return false;
}

public Seer getSeer() {
	return seer;
}

public int getHp() {
	return hp;
}

public int getMaxHp() {
	return maxHp;
}

private class ProjectileFlight {

	private final Coordinate endCoordinate;
	private final Character characterHit;

	public ProjectileFlight(Coordinate endCoordinate, Character characterHit) {

		this.endCoordinate = endCoordinate;
		this.characterHit = characterHit;
	}
}

public class Effect {
	// Class that holds description of one current character's effect
	public int duration, modifier, effectId;

	public Effect(int effectId, int duration, int modifier) {
		this.effectId = effectId;
		this.duration = duration;
		this.modifier = modifier;
	}
}

class PathWalkerOverCharacters implements PathWalker {

	@Override
	public boolean canStepOn(int x, int y) {
		return x >= 0
			&& y >= 0
			&& x < world.width
			&& y < world.height
			&& (plane.getPassability(x, y) == Passability.FREE
			|| plane.getCharacter(x, y) != null);

	}
}

private class CharacterVisionCriteria implements SightPassabilityCriteria {
	@Override
	public boolean canSee(int endX, int endY) {
		Direction direction = Directions.shiftToDirection(x - endX, y - endY);
		int[] dCoords = direction.side2d();
		if (direction.isCardinal()) {
			CardinalDirection cardinal = (CardinalDirection) direction;
			if (plane.hasBorderObject(endX, endY, cardinal)) {
				return false;
			}
		} else {

		}
		return Character.this.plane.getPassability(endX + dCoords[0], endY + dCoords[1]) != Passability.NO;
	}
}
}
