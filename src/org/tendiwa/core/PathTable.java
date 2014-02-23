package org.tendiwa.core;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Cells;

import java.util.ArrayList;
import java.util.LinkedList;

public class PathTable {

private static final int NOT_COMPUTED_CELL = -1;
private final int startX;
private final int startY;
private final PathWalker walker;
private final int maxDepth;
private final int width;
private int[][] pathTable;
private ArrayList<Cell> newFront;
private ArrayList<Cell> oldFront;
private int step;

public PathTable(int startX, int startY, PathWalker walker, int maxDepth) {
	this.startX = startX;
	this.startY = startY;
	this.walker = walker;
	this.maxDepth = maxDepth;
	this.width = maxDepth * 2 + 1;
	step = 0;

	this.pathTable = new int[maxDepth * 2 + 1][maxDepth * 2 + 1];

	newFront = new ArrayList<>();
	newFront.add(new Cell(startX, startY));

	for (int i = 0; i < width; i++) {
		for (int j = 0; j < width; j++) {
			pathTable[i][j] = NOT_COMPUTED_CELL;
		}
	}
	// Zero-wave consists of a single cell, which is path table's start
	pathTable[maxDepth][maxDepth] = 0;
}

private boolean nextWave() {
	if (step == maxDepth) {
		return false;
	}
	oldFront = newFront;
	newFront = new ArrayList<>();
	for (int i = 0; i < oldFront.size(); i++) {
		int x = oldFront.get(i).getX();
		int y = oldFront.get(i).getY();
		int[] adjactentX = new int[]{x + 1, x, x, x - 1, x + 1, x + 1, x - 1, x - 1};
		int[] adjactentY = new int[]{y, y - 1, y + 1, y, y + 1, y - 1, y + 1, y - 1};
		for (int j = 0; j < 8; j++) {
			int thisNumX = adjactentX[j];
			int thisNumY = adjactentY[j];
			int tableX = thisNumX - startX + maxDepth;
			int tableY = thisNumY - startY + maxDepth;
			if (pathTable[tableX][tableY] == NOT_COMPUTED_CELL && walker.canStepOn(thisNumX, thisNumY)) {
				// Step to cell if character can see it and it is free
				// or character cannot se it and it is not PASSABILITY_NO
				pathTable[tableX][tableY] = step + 1;
				newFront.add(new Cell(thisNumX, thisNumY));
			}
		}
	}
	step++;
	return true;
}

/**
 * Returns steps of path to a destination cell computed on this path table.
 *
 * @param x
 * 	Destination x coordinate.
 * @param y
 * 	Destination y coordinate.
 * @return null if path can't be found.
 */
public LinkedList<Cell> getPath(int x, int y) {
	if (Math.abs(x - startX) > maxDepth || Math.abs(y - startY) > maxDepth) {
		throw new IllegalArgumentException("Trying to get path to " + x + ":" + y + ". That point is too far from start point " + startX + ":" + startY + ", maxDepth is " + maxDepth);
	}
	while (pathTable[maxDepth + x - startX][maxDepth + y - startY] == NOT_COMPUTED_CELL) {
		// There will be 0 iterations if that cell is already computed
		boolean waveAddedNewCells = nextWave();
		if (!waveAddedNewCells) {
			return null;
		}
	}
	if (x == startX && y == startY) {
		throw new RuntimeException("Getting path to itself");
	}
	LinkedList<Cell> path = new LinkedList<>();
	if (Cells.isNear(startX, startY, x, y)) {
		path.add(new Cell(x, y));
		return path;
	}
	int currentNumX = x;
	int currentNumY = y;
	int cX = currentNumX;
	int cY = currentNumY;
	for (
		int j = pathTable[currentNumX - startX + maxDepth][currentNumY - startY + maxDepth];
		j > 0;
		j = pathTable[currentNumX - startX + maxDepth][currentNumY - startY + maxDepth]
		) {
		path.addFirst(new Cell(currentNumX, currentNumY));
		int[] adjactentX = {cX, cX + 1, cX, cX - 1, cX + 1, cX + 1, cX - 1, cX - 1};
		int[] adjactentY = {cY - 1, cY, cY + 1, cY, cY + 1, cY - 1, cY + 1, cY - 1};
		for (int i = 0; i < 8; i++) {
			int thisNumX = adjactentX[i];
			int thisNumY = adjactentY[i];
			int tableX = thisNumX - startX + maxDepth;
			int tableY = thisNumY - startY + maxDepth;
			if (tableX < 0 || tableX >= width) {
				continue;
			}
			if (tableY < 0 || tableY >= width) {
				continue;
			}
			if (pathTable[tableX][tableY] == j - 1) {
				currentNumX = adjactentX[i];
				currentNumY = adjactentY[i];
				cX = currentNumX;
				cY = currentNumY;
				break;
			}
		}
	}
	if (path == null) {
		throw new RuntimeException("Path is null, you wanted to check that");
	}
	return path;
}

public boolean cellComputed(int x, int y) {
	return pathTable[maxDepth + x - startX][maxDepth + y - startY] != NOT_COMPUTED_CELL;
}
}
