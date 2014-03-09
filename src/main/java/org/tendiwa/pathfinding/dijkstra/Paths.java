package org.tendiwa.pathfinding.dijkstra;

import org.tendiwa.geometry.Cell;

import java.util.LinkedList;

public class Paths {
public static PathTable getPathTable(int fromX, int fromY, PathWalker pathWalker, int maxDepth) {
	return new PathTable(fromX, fromY, pathWalker, maxDepth);
}
public static LinkedList<Cell> getPath(int startX, int startY, int destX, int destY, PathWalker walker, int maxDepth) {
	return new PathTable(startX, startY, walker, maxDepth).getPath(destX, destY);
}
}
