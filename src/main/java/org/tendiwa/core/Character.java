package org.tendiwa.core;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.events.*;
import org.tendiwa.core.meta.CellPosition;
import org.tendiwa.core.observation.Event;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.player.SinglePlayerMode;
import org.tendiwa.core.vision.Seer;
import org.tendiwa.core.vision.SightPassabilityCriteria;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.CellSegment;
import org.tendiwa.pathfinding.dijkstra.PathWalker;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Character implements CellPosition, PlaceableInCell, PathWalker, DamageSource {
public final int id = new UniqueObject().id;
public final ItemCollection inventory = new ItemCollection();
public final Equipment equipment = new Equipment(2, ApparelSlot.values());
public final Seer seer;
protected final String name;
protected final HashMap<Integer, Character.Effect> effects = new HashMap<>();
final CharacterType type;
private final Observable backend;
private final SinglePlayerMode singlePlayerMode;
protected Body body;
protected int actionPoints;
protected int ep;
protected int maxEp;
protected int fraction;
protected HorizontalPlane plane;
protected int x;
protected int y;
protected boolean isAlive;
protected CharacterState state = CharacterState.DEFAULT;
protected TimeStream timeStream;
protected int hp;
protected int maxHp;
private World world;
/**
 * Lazily created by {@link Character#getPathWalkerOverCharacters()}It is not static because it needs the {@link
 * Character#plane} of NonPlayerCharacter.
 */
private PathWalkerOverCharacters pathWalkerOverCharacters;
private Collection<Spell> spells = new HashSet<>();

@Inject
public Character(
	@Named("tendiwa") Observable backend,
	@Assisted("x") int x,
	@Assisted("y") int y,
	@Assisted CharacterType type,
	@Assisted String name,
	SinglePlayerMode singlePlayerMode
) {
	// Common character creation: with all attributes, in location.
	super();
	this.backend = backend;
	this.singlePlayerMode = singlePlayerMode;
	assert type != null;
	this.type = type;
	this.name = name;
	fraction = 0;
	isAlive = true;
	this.x = x;
	this.y = y;
	this.maxHp = type.getMaxHp();
	assert maxHp != 0;
	this.hp = maxHp;
	this.seer = new Seer(this, new CharacterVisionCriteria(), new DefaultObstacleFindingStrategy(this));
}

public PathWalkerOverCharacters getPathWalkerOverCharacters() {
	if (pathWalkerOverCharacters == null) {
		pathWalkerOverCharacters = new PathWalkerOverCharacters();
	}
	return pathWalkerOverCharacters;
}

/* Actions */
public void attack(Character aim) {
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventAttack(this, aim));
	}
	backend.waitForAnimationToStartAndComplete();
	aim.getDamage(7, DamageType.PLAIN, this);
	moveInTime(500);
}

public void emitEvent(Event event) {
	backend.emitEvent(event);
}

public void actAndWait(Runnable runnable) {
	synchronized (backend.getLock()) {
		runnable.run();
	}
	backend.waitForAnimationToStartAndComplete();
}

protected void shootMissile(int toX, int toY, ItemPile missile) {
	loseItem(missile);
	Cell end = seer.getRayEnd(toX, toY);
	plane.addItem(missile, end.getX(), end.getY());
//	Cell aimCell = plane.getCell(toX, toY);
//	if (aimCell.character() != null) {
//		aimCell.character().getDamage(10, DamageType.PLAIN);
//	}
//	throw new UnsupportedOperationException();
}

protected void shootMissile(int toX, int toY, UniqueItem item) {
	loseItem(item);
	Cell end = seer.getRayEnd(toX, toY);
	plane.addItem(item, end.getX(), end.getY());
	Character character = plane.getCharacter(end.getX(), end.getY());
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
	synchronized (backend.getLock()) {
		isAlive = false;
		timeStream.claimCharacterDisappearance(this);
		plane.getChunkWithCell(x, y).removeCharacter(this);
		backend.emitEvent(new EventDie(this));
	}
	backend.waitForAnimationToStartAndComplete();
}

public void putOn(UniqueItem item) {
	synchronized (backend.getLock()) {
		inventory.removeUnique(item);
		equipment.putOn(item);
//		if (isPlayer()) {
		backend.emitEvent(new EventPutOn(this, item));
//		}
	}
	moveInTime(500);
}

public void wield(Item item) {
	synchronized (backend.getLock()) {
		if (item.getType().isStackable()) {
			ItemPile itemPile = (ItemPile) item;
			ItemPile pile = new ItemPile(itemPile.getType(), 1);
			inventory.removePile(pile);
			equipment.wield(pile);
		} else {
			inventory.removeItem(item);
			equipment.wield(item);
		}
		backend.emitEvent(new EventWield(this, item));
	}
}

public void cease(Item item) {
	synchronized (backend.getLock()) {
		inventory.add(item);
		equipment.cease(item);
		backend.emitEvent(new EventUnwield(this, item));
	}
}

public void takeOff(UniqueItem item) {
	synchronized (backend.getLock()) {
		inventory.add(item);
		equipment.takeOff(item);
		backend.emitEvent(new EventTakeOff(this, item));
	}
	moveInTime(500);
}

/**
 * Pick up an item lying on the same cell where the character stands.
 */
public void pickUp(ItemPile pile) {
	backend.emitEvent(new EventItemDisappear(x, y, pile));
	plane.removeItem(pile, x, y);
	backend.emitEvent(new EventGetItem(pile));
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
	backend.emitEvent(new EventItemAppear(item, this.x, this.y));
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
		if (!new Cell(x, y).isNear(nx, ny)) {
			move(bufX, bufY, MovingStyle.STEP);
		}
	}
	moveInTime(500);
}

	/* Special actions */

/**
 * Player's vision cache gets invalidated in {@link Seer#computeFullVisionCache()}.
 */

public boolean canSee(int x, int y) {
	return seer.canSee(x, y);
}

public Cell[] rays(int startX, int startY, int endX, int endY) {
	return seer.rays(startX, startY, endX, endY);
}

public int hashCode() {
	return id;
}


/* Getters */

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

public int getId() {
	return id;
}

public String getName() {
	return name;
}
	/* Setters */

/**
 * Changes character's position.
 * <p/>
 * Note that this is not a character action, this method is also called when character blinks, being pushed and so on.
 * For action method, use {@link Character#step(int, int)}.
 */
public void move(int x, int y, MovingStyle movingStyle) {
	int xPrev = this.x;
	int yPrev = this.y;
	synchronized (backend.getLock()) {
		plane.removeCharacter(this);
		this.x = x;
		this.y = y;
		plane.addCharacter(this);
		backend.emitEvent(new EventMove(xPrev, yPrev, this, movingStyle));
	}
	backend.waitForAnimationToStartAndComplete();
	timeStream.notifyNeighborsVisiblilty(this);
	if (singlePlayerMode.isPlayer(this)) {
		synchronized (backend.getLock()) {
			seer.storeVisionCacheToPreviousVisionCache();
			seer.invalidateVisionCache();
			seer.computeFullVisionCache();
			VisibilityChange visibilityChange = new VisibilityChange(
				world,
				this,
				xPrev,
				yPrev,
				seer,
				seer.getPreviousVisionCache(),
				seer.getVisionCache(),
				seer.getPreviousBorderVisionCache(),
				seer.getBorderVisionCache()
			);
			backend.emitEvent(visibilityChange.createEvent());
		}
		backend.waitForAnimationToStartAndComplete();
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
	synchronized (backend.getLock()) {
		plane.removeCharacter(this);
		plane = world.getPlane(plane.getLevel() + dz);
		plane.addCharacter(this);
		seer.invalidateVisionCache();
		seer.storeVisionCacheToPreviousVisionCache();
		seer.computeFullVisionCache();
		backend.emitEvent(new EventMoveToPlane(this, world, plane, seer));
	}
	backend.waitForAnimationToStartAndComplete();
	moveInTime(500);
}

public void spendActionPoints(int amount) {
	actionPoints -= amount;
}

public void getDamage(int amount, DamageType type, DamageSource damageSource) {
	this.hp -= amount;
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventGetDamage(this, amount, damageSource, type));
	}
	backend.waitForAnimationToStartAndComplete();
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
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventSay(message, this));
	}
	backend.waitForAnimationToStartAndComplete();
}

public void getItem(Item item) {
	synchronized (backend.getLock()) {
		inventory.add(item);
	}
	backend.waitForAnimationToStartAndComplete();
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
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventLoseItem(item));
		if (item.getType().isStackable()) {
			inventory.removePile((ItemPile) item);
		} else {
			inventory.removeUnique((UniqueItem) item);
		}
	}
	backend.waitForAnimationToStartAndComplete();
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
	if (!singlePlayerMode.isPlayer(nextCharacter)) {
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
	return singlePlayerMode.isPlayer(ch) != singlePlayerMode.isPlayer(this);
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

public void setPlane(HorizontalPlane plane) {
	this.plane = plane;
}

public void pickUp(Item item) {
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventItemDisappear(x, y, item));
		plane.getItems(x, y).removeItem(item);
	}
	backend.waitForAnimationToStartAndComplete();
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventGetItem(item));
		getItem(item);
	}
	backend.waitForAnimationToStartAndComplete();
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
	synchronized (backend.getLock()) {
		loseItem(item);
	}
	backend.waitForAnimationToStartAndComplete();
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventProjectileFly(item, this.x, this.y, x, y, EventProjectileFly.FlightStyle.CAST));
	}
	backend.waitForAnimationToStartAndComplete();
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventItemAppear(item, x, y));
		Chunk chunkWithCell = plane.getChunkWithCell(x, y);
		chunkWithCell.addItem(item, x, y);
	}
	backend.waitForAnimationToStartAndComplete();
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
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventProjectileFly(
			projectile,
			x,
			y,
			flight.endCoordinate.getX(),
			flight.endCoordinate.getY(),
			EventProjectileFly.FlightStyle.PROPELLED
		));
	}
	backend.waitForAnimationToStartAndComplete();

	if (flight.characterHit != null) {
		flight.characterHit.getDamage(10, DamageType.PLAIN, this);
	}
	synchronized (backend.getLock()) {
		backend.emitEvent(new EventItemAppear(projectile, toX, toY));
		Chunk chunkWithCell = plane.getChunkWithCell(toX, toY);
		chunkWithCell.addItem(projectile, toX, toY);
	}
	backend.waitForAnimationToStartAndComplete();
}

private ProjectileFlight computeProjectileFlightEndCoordinate(UniqueItem weapon, Item projectile, int toX, int toY) {
	Cell endCoordinate = new Cell(toX, toY);
	Cell[] vector = CellSegment.cells(x, y, toX, toY);
	Character characterHit = null;
	for (int i = 1; i < vector.length; i++) {
		Cell c = vector[i];
		if (plane.hasCharacter(c.getX(), c.getY()) &&
			testProjectileHit(weapon, projectile, toX, toY)) {
			characterHit = plane.getCharacter(c.getX(), c.getY());
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

public World getWorld() {
	return world;
}

public void setWorld(World world) {
	this.world = world;
	seer.setWorld(world);
}

private class ProjectileFlight {

	private final Cell endCoordinate;
	private final Character characterHit;

	public ProjectileFlight(Cell endCoordinate, Character characterHit) {

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
