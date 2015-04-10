package org.tendiwa.pathfinding.dijkstra;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.CellSet;

import java.util.LinkedList;

public class Paths {
	public static PathTable getPathTable(Cell start, CellSet availableCells, int maxDepth) {
		return new PathTable(start, availableCells, maxDepth);
	}

	public static LinkedList<Cell> getPath(Cell start, Cell dest, CellSet availableCells, int maxDepth) {
		return new PathTable(start, availableCells, maxDepth).getPath(dest);
	}
}
