package tendiwa.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is supposed to be used by anyone who creates his own Tendiwa client. It uses the philosophical metaphor of
 * a subjective and objective world, where subjective world (as a player character sees it) is represented by an
 * instance of this class, and the objective world (the world as it is if there's no observer) is represented by {@link
 * World} in backend.
 */
public class RenderWorld {
private final int worldHeight;
private Map<Integer, RenderCell> cells = new HashMap<>();
private Multimap<Integer, RememberedItem> unseenItems = HashMultimap.create();

public RenderWorld(World backendWorld) {
	this.worldHeight = Tendiwa.getWorldHeight();
}

/**
 * Returns an object that represents a subjective view of a particular cell.
 *
 * @param x
 * 	X coordinate of a cell in world coordinates.
 * @param y
 * 	Y coordinate of a cell in world coordinates.
 * @return The cell object, or null if that cell has not yet been seen or unseen.
 */
public RenderCell getCell(int x, int y) {
	return cells.get(cellHash(x, y));
}

/**
 * Returns a cell corresponding to a hash. Hash is f(x,y) = x*worldHeight+y. For example, for cell 5:12 in 400x300 world
 * hash is 5*300+12 = 1512.
 *
 * @param hash
 * 	f(x,y) = x*worldHeight+y
 * @return The cell object, or null if that cell has not yet been seen or unseen.
 */
public RenderCell getCell(int hash) {
	return cells.get(hash);
}

/**
 * Checks if a player sees or has ever seen a cell. That is, if there is an object created to hold a subjective state of
 * a cell.
 *
 * @param x
 * 	X coordinate of cell in world coordinates.
 * @param y
 * 	Y coordinate of cell in world coordinates.
 * @return True if there is a cell with given coordinates, false otherwise.
 */
public boolean hasCell(int x, int y) {
	assert x >= 0 && x < Tendiwa.getWorldWidth() && y >= 0 && y <= Tendiwa.getWorldHeight();
	return cells.containsKey(cellHash(x, y));
}

/**
 * Checks if player can see a cell. Objective analog is {@link Character#canSee(int, int)}. Note that if this method
 * returns false, then cell can be either unseen or not yet seen.
 *
 * @param x
 * 	X coordinate of cell in world coordinates.
 * @param y
 * 	Y coordinate of cell in world coordinates.
 * @return True if cell is visible, false otherwise.
 */
public boolean isCellVisible(int x, int y) {
	return hasCell(x, y) && getCell(x, y).isVisible();
}

/**
 * Checks if player seen the cell earlier. There is no objective analog of this method. Note that f this method returns
 * false, then cell can be either seen or not yet seen at all.
 *
 * @param x
 * 	X coordinate of cell in world coordinates.
 * @param y
 * 	Y coordinate of cell in world coordinates.
 * @return True if cell is visible, false otherwise.
 */
public boolean isCellUnseen(int x, int y) {
	return hasCell(x, y) && !getCell(x, y).isVisible();
}

/**
 * Adds a new cell to subjective world (if a cell with that coordinates has not been seen yet), or sees an unseen cell.
 *
 * @param cell
 * 	A new cell.
 */
public void seeCell(RenderCell cell) {
	assert cell != null;
	int key = cellHash(cell.x, cell.y);
	assert !cells.containsKey(key) || !cells.get(key).isVisible() : "Already is visible";
	cells.put(key, cell);
}

public void addUnseenItem(int x, int y, Item item) {
	unseenItems.put(cellHash(x, y), new RememberedItem(x, y, item.getType()));
}

/**
 * Returns all items that player has unseen.
 *
 * @return All remembered items.
 */
public Collection<RememberedItem> getRememberedItems() {
	return Collections.unmodifiableCollection(unseenItems.values());
}

/**
 * Checks if there are any unseen items in a cell with a given hash.
 *
 * @param hash
 * 	Hash of cell coordinates f(x,y) = x*worldHeight + y;
 * @return True if there are any items, false otherwise.
 */
public boolean hasAnyUnseenItems(int hash) {
	return unseenItems.containsKey(hash);
}

public boolean hasAnyUnseenItems(int x, int y) {
	return unseenItems.containsKey(cellHash(x, y));
}

private int cellHash(int x, int y) {
	return x * worldHeight + y;
}

public Collection<RememberedItem> getUnseenItems(int x, int y) {
	return unseenItems.get(cellHash(x, y));
}

/**
 * Forgets about unseen items in a particular cell.
 *
 * @param x
 * 	X coordinate of cell in world coordinates.
 * @param y
 * 	Y coordinate of cell in world coordinates.
 */
public void removeUnseenItems(int x, int y) {
	unseenItems.removeAll(cellHash(x, y));
}
}
