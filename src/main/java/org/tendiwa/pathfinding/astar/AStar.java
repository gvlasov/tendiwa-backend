package org.tendiwa.pathfinding.astar;

import org.tendiwa.core.Directions;
import org.tendiwa.geometry.Cell;

import java.util.*;

public class AStar {
private final MovementCost movementCostFunction;
private final Map<Cell, Double> g = new HashMap<>();
private final PriorityQueue<Cell> open = new PriorityQueue<>(30, new AStarComparator());
private final Set<Cell> closed = new HashSet<>();
/**
 * Maps cells to their parents
 */
private final Map<Cell, Cell> parents = new HashMap<>();
private Cell goal;

public AStar(MovementCost movementCost) {
	this.movementCostFunction = movementCost;
}

public List<Cell> path(Cell start, Cell goal) {
	this.goal = goal;
	addToOpen(start, null, 0);
	for (Cell current = open.poll(); !current.equals(goal); current = open.poll()) {
		closed.add(current);
		for (Cell neighbor : neighborsOf(current)) {
			double movementCost = movementCostFunction.cost(current, neighbor);
			if (movementCost == Integer.MAX_VALUE) {
				continue;
			}
			double cost = g(current) + movementCost;
			if (open.contains(neighbor) && cost < g(neighbor)) {
				open.remove(neighbor); // because new path is better
			}
			if (closed.contains(neighbor) && cost < g(neighbor)) {
				closed.remove(neighbor);
			}
			if (!open.contains(neighbor) && !closed.contains(neighbor)) {
				addToOpen(neighbor, current, cost);
			}
		}
	}
	List<Cell> answer = new ArrayList<>();
	for (Cell cell = goal;
	     parents.get(cell) != null;
	     cell = parents.get(cell)
		) {
		answer.add(cell);
	}
	return answer;
}

private void addToOpen(Cell what, Cell parent, double g) {
	this.g.put(what, g);
	open.add(what);
	parents.put(what, parent);
}

private double g(Cell current) {
	return g.get(current);
}

private double h(Cell current) {
//	return current.chebyshovDistanceTo(goal);
//	return current.quickDistance(goal);
	return current.distanceDouble(goal);
}

private Cell[] neighborsOf(Cell current) {
	return new Cell[]{
		current.moveToSide(Directions.N),
		current.moveToSide(Directions.NE),
		current.moveToSide(Directions.E),
		current.moveToSide(Directions.SE),
		current.moveToSide(Directions.S),
		current.moveToSide(Directions.SW),
		current.moveToSide(Directions.W),
		current.moveToSide(Directions.NW)
	};
}

private class AStarComparator implements Comparator<Cell> {
	@Override
	public int compare(Cell o1, Cell o2) {
		return (int) Math.round(g(o1) + h(o1) - g(o2) - h(o2));
	}
}
}
