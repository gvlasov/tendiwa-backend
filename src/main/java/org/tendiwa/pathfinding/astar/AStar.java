package org.tendiwa.pathfinding.astar;

import org.tendiwa.core.Directions;
import org.tendiwa.geometry.BasicCell;

import java.util.*;

public class AStar {
	private final MovementCost movementCostFunction;
	private final Map<BasicCell, Double> g = new HashMap<>();
	private final PriorityQueue<BasicCell> open = new PriorityQueue<>(30, new AStarComparator());
	private final Set<BasicCell> closed = new HashSet<>();
	/**
	 * Maps cells to their parents
	 */
	private final Map<BasicCell, BasicCell> parents = new HashMap<>();
	private BasicCell goal;

	public AStar(MovementCost movementCost) {
		this.movementCostFunction = movementCost;
	}

	public List<BasicCell> path(BasicCell start, BasicCell goal) {
		this.goal = goal;
		addToOpen(start, null, 0);
		for (BasicCell current = open.poll(); !current.equals(goal); current = open.poll()) {
			closed.add(current);
			for (BasicCell neighbor : neighborsOf(current)) {
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
		List<BasicCell> answer = new ArrayList<>();
		for (BasicCell cell = goal;
			 parents.get(cell) != null;
			 cell = parents.get(cell)
			) {
			answer.add(cell);
		}
		return answer;
	}

	private void addToOpen(BasicCell what, BasicCell parent, double g) {
		this.g.put(what, g);
		open.add(what);
		parents.put(what, parent);
	}

	private double g(BasicCell current) {
		return g.get(current);
	}

	private double h(BasicCell current) {
//	return current.chebyshovDistanceTo(goal);
//	return current.quickDistance(goal);
		return current.distanceDouble(goal);
	}

	private BasicCell[] neighborsOf(BasicCell current) {
		return new BasicCell[]{
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

	private class AStarComparator implements Comparator<BasicCell> {
		@Override
		public int compare(BasicCell o1, BasicCell o2) {
			return (int) Math.round(g(o1) + h(o1) - g(o2) - h(o2));
		}
	}
}
