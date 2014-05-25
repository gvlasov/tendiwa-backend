package org.tendiwa.pathfinding.astar;

import org.tendiwa.geometry.Cell;

@FunctionalInterface
public interface MovementCost {
public double cost(Cell current, Cell neighbor);
}
