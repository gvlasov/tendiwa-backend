package org.tendiwa.pathfinding.dijkstra;

@FunctionalInterface
public interface PathWalker {
	/**
	 * @param x
	 * 	X coordinate of a cell in world coordinates
	 * @param y
	 * 	Y coordinate of a cell in world coordinates.
	 * @return If a cell can be added to PathTable as visited or not.
	 */
	boolean canStepOn(int x, int y);
}
