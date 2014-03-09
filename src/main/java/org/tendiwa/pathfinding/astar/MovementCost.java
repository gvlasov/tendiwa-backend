package org.tendiwa.pathfinding.astar;

import org.tendiwa.geometry.Cell;

public interface MovementCost {
public int cost(Cell current, Cell neighbor);
}
