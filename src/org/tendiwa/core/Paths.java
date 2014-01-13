package org.tendiwa.core;

import java.util.LinkedList;

public class Paths {
public static PathTable getPathTable(int fromX, int fromY, PathWalker pathWalker, int maxDepth) {
	return new PathTable(fromX, fromY, pathWalker, maxDepth);
}
public static LinkedList<EnhancedPoint> getPath(int startX, int startY, int destX, int destY, PathWalker walker, int maxDepth) {
	return new PathTable(startX, startY, walker, maxDepth).getPath(destX, destY);
}
}
