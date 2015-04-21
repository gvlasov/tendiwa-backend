package org.tendiwa.pathfinding.astar;

import org.tendiwa.geometry.BasicCell;

@FunctionalInterface
public interface MovementCost {
	/**
	 * Computes cost of moving from one cell to another for {@link org.tendiwa.pathfinding.astar.AStar} algorithm.
	 *
	 * @param current
	 * 	Source cell.
	 * @param neighbor
	 * 	Destination cell.
	 * @return Cost of moving from {@code current} to {@code neighbor}.
	 */
	public double cost(BasicCell current, BasicCell neighbor);
}
