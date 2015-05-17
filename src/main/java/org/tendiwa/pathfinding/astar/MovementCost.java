package org.tendiwa.pathfinding.astar;

import org.tendiwa.core.meta.Cell;

@FunctionalInterface
public interface MovementCost {
	/**
	 * Computes cost of moving from one cell to another for {@link Path} algorithm.
	 *
	 * @param current
	 * 	Source cell.
	 * @param neighbor
	 * 	Destination cell.
	 * @return Cost of moving from {@code current} to {@code neighbor}.
	 */
	public double cost(Cell current, Cell neighbor);
}
