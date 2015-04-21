package org.tendiwa.pathfinding.dijkstra;

import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.CellSet;

/**
 * The same as {@link org.tendiwa.pathfinding.dijkstra.PathTable} with only one difference:
 * a cell that gets checked for passability by {@link #availableCells} is unconditionally added to path table,
 * but it will not be added to the wave front if the cell does not pass PathWalker's condition.
 * <p>
 * Think of this PathTable as of visibility: cell with a solid wall is visible,
 * but you can't see past that wall.
 */
public class PostConditionPathTable extends PathTable {

	public PostConditionPathTable(BasicCell start, CellSet availableCells, int maxDepth) {
		super(start, availableCells, maxDepth);
	}

	@Override
	protected void computeCell(int thisNumX, int thisNumY, int tableX, int tableY) {
		if (pathTable[tableX][tableY] == NOT_COMPUTED_CELL) {
			pathTable[tableX][tableY] = step + 1;
			if (availableCells.contains(thisNumX, thisNumY)) {
				newFront.add(new BasicCell(thisNumX, thisNumY));
			}
		}
	}
}
