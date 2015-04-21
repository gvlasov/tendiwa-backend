package org.tendiwa.pathfinding.dijkstra;

import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.CellSet;

import java.util.LinkedList;

public class Paths {
	public static PathTable getPathTable(BasicCell start, CellSet availableCells, int maxDepth) {
		return new PathTable(start, availableCells, maxDepth);
	}

	public static LinkedList<BasicCell> getPath(BasicCell start, BasicCell dest, CellSet availableCells, int maxDepth) {
		return new PathTable(start, availableCells, maxDepth).getPath(dest);
	}
}
