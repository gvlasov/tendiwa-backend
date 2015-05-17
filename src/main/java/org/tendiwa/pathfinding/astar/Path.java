package org.tendiwa.pathfinding.astar;

import org.tendiwa.core.Directions;
import org.tendiwa.core.meta.Cell;

import java.util.*;

public final class Path implements List<Cell> {
	private final Cell start;
	private final MovementCost movementCostFunction;
	private final Map<Cell, Double> g = new HashMap<>();
	private final PriorityQueue<Cell> open = new PriorityQueue<>(30, new AStarComparator());
	private final Set<Cell> closed = new HashSet<>();
	/**
	 * Maps cells to their parents
	 */
	private final Map<Cell, Cell> parents = new HashMap<>();
	private final Cell goal;

	public Path(
		Cell start,
		Cell goal,
		MovementCost movementCost
	) {
		this.start = start;
		this.goal = goal;
		this.movementCostFunction = movementCost;
	}

	private List<Cell> path() {
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

	// TODO: Add link to what g means in A*
	private double g(Cell current) {
		return g.get(current);
	}

	// TODO: Add link to what h means in A*
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

	@Override
	public int size() {
		return path().size();
	}

	@Override
	public boolean isEmpty() {
		return path().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return path().contains(o);
	}

	@Override
	public Iterator<Cell> iterator() {
		return path().iterator();
	}

	@Override
	public Object[] toArray() {
		return path().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return path().toArray(a);
	}

	@Deprecated
	@Override
	public boolean add(Cell cell) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return path().containsAll(c);
	}

	@Deprecated
	@Override
	public boolean addAll(Collection<? extends Cell> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean addAll(int index, Collection<? extends Cell> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Cell get(int index) {
		return path().get(index);
	}

	@Deprecated
	@Override
	public Cell set(int index, Cell element) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void add(int index, Cell element) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Cell remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public int indexOf(Object o) {
		return path().indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return path().lastIndexOf(o);
	}

	@Override
	public ListIterator<Cell> listIterator() {
		return path().listIterator();
	}

	@Override
	public ListIterator<Cell> listIterator(int index) {
		return path().listIterator(index);
	}

	@Override
	public List<Cell> subList(int fromIndex, int toIndex) {
		return path().subList(fromIndex, toIndex);
	}

	private class AStarComparator implements Comparator<Cell> {
		@Override
		public int compare(Cell o1, Cell o2) {
			return (int) Math.round(g(o1) + h(o1) - g(o2) - h(o2));
		}
	}
}
