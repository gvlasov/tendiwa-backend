package tendiwa.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;

/**
 * This class is supposed to be used by anyone who creates his own Tendiwa client. It uses the philosophical metaphor of
 * a subjective and objective world, where subjective world (as a player character sees it) is represented by an
 * instance of this class, and the objective world (the world as it is if there's no observer) is represented by {@link
 * World} in backend.
 */
public class RenderPlane {
private final int worldHeight;
private final HorizontalPlane backendPlane;
private Map<Integer, RenderCell> cells = new HashMap<>();
private Multimap<Integer, RememberedItem> unseenItems = HashMultimap.create();
private Map<Integer, BorderObject> visibleBorderObjects = new HashMap<>();
private Map<Integer, BorderObject> unseenBorderObjects = new HashMap<>();

public RenderPlane(HorizontalPlane backendPlane) {
	this.worldHeight = Tendiwa.getWorldHeight();
	this.backendPlane = backendPlane;
}

public void updateBorderObjectsVisibility() {
	SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
	System.out.println("cells size " + cells.size());
	// Find all visible pairs of cells that are neighbors by a cardinal side.
	for (Map.Entry<Integer, RenderCell> e : cells.entrySet()) {
		Integer hash = e.getKey();

		graph.addVertex(hash);
		int[] coords = cellHashToCoords(hash);
		Set<Integer> vertices = graph.vertexSet();
		for (CardinalDirection dir : CardinalDirection.values()) {
			int[] dCoords = dir.side2d();
			Integer vertex = cellHash(coords[0] + dCoords[0], coords[1] + dCoords[1]);
			if (vertices.contains(vertex)) {
				graph.addEdge(hash, vertex);
			}
		}
	}
	Map<Integer, BorderObject> currentlyVisibleBorderObjects = new HashMap<>();
	for (DefaultEdge e : graph.edgeSet()) {
		int[] coordsSource = cellHashToCoords(graph.getEdgeSource(e));
		int[] coordsTarget = cellHashToCoords(graph.getEdgeTarget(e));
		CardinalDirection direction = (CardinalDirection) Directions.shiftToDirection(coordsSource[0] - coordsTarget[0], coordsSource[1] - coordsTarget[1]);
		BorderObject borderObject = backendPlane.getBorderObject(coordsSource[0], coordsSource[1], direction);
		if (borderObject != null) {
			currentlyVisibleBorderObjects.put(
				borderHash(coordsSource[0], coordsSource[1], direction),
				borderObject
			);
		}
	}
	System.out.println("Currently vis obj size: " + currentlyVisibleBorderObjects.size());
	for (Map.Entry<Integer, BorderObject> e : currentlyVisibleBorderObjects.entrySet()) {
		if (visibleBorderObjects.containsValue(e.getValue())) {
			// See currently visible border object
			visibleBorderObjects.put(e.getKey(), e.getValue());
		}
		if (unseenBorderObjects.containsKey(e.getKey())) {
			// Forget unseeing currently visible border object
			unseenBorderObjects.remove(e.getKey());
		}
	}
	// Unsee previously visible border objects
	for (Map.Entry<Integer, BorderObject> e : visibleBorderObjects.entrySet()) {
		if (!currentlyVisibleBorderObjects.containsKey(e.getKey())) {
			visibleBorderObjects.remove(e.getKey());
			unseenBorderObjects.put(e.getKey(), e.getValue());
		}
	}
}

private int borderHash(int x, int y, CardinalDirection side) {
	assert side != null;
	if (side != Directions.N && side != Directions.W) {
		if (side == Directions.E) {
			side = Directions.W;
			x += 1;
		} else {
			assert side == Directions.S;
			side = Directions.N;
			y += 1;
		}
	}
	return (side == Directions.N ? 1 : 0) + y * 2 + x * worldHeight * 2;
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
	if (x >= 0 && x < Tendiwa.getWorldWidth() && y >= 0 && y <= Tendiwa.getWorldHeight()) {
		return cells.containsKey(cellHash(x, y));
	} else {
		return false;
	}
}

/**
 * Checks if player can see a cell. Objective analog is {@link Character#isCellVisible(int, int)}. Note that if this
 * method returns false, then cell can be either unseen or not yet seen.
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
	assert !cells.containsKey(key) || !cells.get(key).isVisible() : "Cell " + cell.getX() + ":" + cell.getY() + " is already visible";
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

private int[] cellHashToCoords(int hash) {
	return new int[]{hash / worldHeight, hash % worldHeight};
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

public void unseeAllCells() {
	for (RenderCell cell : cells.values()) {
		cell.setVisible(false);
	}
}
}
