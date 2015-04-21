package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.core.meta.Cell;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.*;

/**
 * In a wave-like fashion, iterates over cells that can be reached from start cell walking only over passable cells.
 * The
 * closer a cell is in Chebyshev metric to the start cell, the sooner it will be popped out by iterator.
 */
public class Wave implements Iterable<Cell> {
	private final int directions;
	private CellSet passableCells;
	Set<Cell> newFront = new HashSet<>();
	Set<Cell> previousFront;
	private static final int[] dx = new int[]{1, 0, 0, 0 - 1, 1, 1, 0 - 1, 0 - 1};
	private static final int[] dy = new int[]{0, 0 - 1, 1, 0, 1, 0 - 1, 1, 0 - 1};

	Wave(Cell startCell, CellSet passableCells, int directions) {
		assert directions == 4 || directions == 8;
		this.directions = directions;
		requireNonNull(startCell);
		this.passableCells = requireNonNull(passableCells);
		previousFront = ImmutableSet.of();
		newFront.add(startCell);
	}

	public static StepGoingOver from(Cell startCell) {
		Objects.requireNonNull(startCell);
		return new StepGoingOver(startCell);
	}

	/**
	 * Computes this Wave until it fills all allowed space, collecting its cells into a {@link CellSet}.
	 * <p>
	 * Because Wave possibly has an infinite amount of cells (limited only by int size), you should provide {@code
	 * limit} of cells found. After reaching that limit, the Wave is considered to be going into infinity, and this
	 * method throws an exception.
	 * <p>
	 * Unlike
	 *
	 * @param limit
	 * 	The maximum number of cells that is allowed to be collected.
	 * @return A set of all cells of this Wave.
	 * @throws java.lang.IndexOutOfBoundsException
	 * 	If number of cells collected exceeds {@code limit}.
	 * @throws java.lang.IllegalArgumentException
	 * 	if {@code limit} is < 0.
	 * @see #asCellSet(Rectangle)
	 */
	public ScatteredCellSet asCellSet(int limit) {
		if (limit < 0) {
			throw new IllegalArgumentException("Limit must be >= 0");
		}
		ImmutableSet.Builder<Cell> answer = ImmutableSet.builder();
		int i = 0;
		for (Cell cell : this) {
			answer.add(cell);
			if (i++ == limit) {
				throw new IndexOutOfBoundsException("Number of cells fetched from wave exceeds limit (" + limit + ")");
			}
		}
		return new ScatteredCellSet(answer.build());
	}

	/**
	 * Computes this Wave until it fills all allowed space, collecting its cells into a {@link CellSet}.
	 * <p>
	 * Unlike {@link #asCellSet(int)} where memory used grows linearly, this method returns a cell set that uses {@code
	 * bounds.width*bounds.height} memory. This method produces a more efficient CellSet if the Wave fills a
	 * considerable part of {@code bounds}.
	 *
	 * @param bounds
	 * 	A rectangle that contains all cells of this Wave.
	 * @return A set of all cells of this Wave.
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 	If {x:y} is not within bounds.
	 * @see #asCellSet(int)
	 */
	public BoundedCellSet asCellSet(Rectangle bounds) {
		Mutable2DCellSet answer = new Mutable2DCellSet(bounds);
		for (Cell cell : this) {
			answer.add(cell);
		}
		return answer;
	}

	@Override
	public Iterator<Cell> iterator() {
		return new Iterator<Cell>() {
			Iterator<Cell> currentWave = newFront.iterator();
			Cell next = findNext();

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public Cell next() {
				Cell current = next;
				next = findNext();
				return current;
			}

			private Cell findNext() {
				if (!currentWave.hasNext()) {
					currentWave = nextWave().iterator();
				}
				if (currentWave.hasNext()) {
					return currentWave.next();
				} else {
					return null;
				}
			}
		};
	}

	public static class StepGoingOver {
		private final Cell startCell;

		private StepGoingOver(Cell startCell) {

			this.startCell = startCell;
		}

		public StepDirections goingOver(CellSet passableCells) {
			return new StepDirections(startCell, passableCells);
		}

	}

	public static class StepDirections {
		private final Cell startCell;
		private final CellSet passableCells;

		public StepDirections(Cell startCell, CellSet passableCells) {

			this.startCell = startCell;
			this.passableCells = passableCells;
		}

		public Wave in4Directions() {
			return new Wave(startCell, passableCells, 4);
		}

		public Wave in8Directions() {
			return new Wave(startCell, passableCells, 8);
		}
	}

	private Set<Cell> nextWave() {
		Set<Cell> currentFront = newFront;
		newFront = new HashSet<>();
		for (Cell old : currentFront) {
			// Four first elements of dx and dy are cardinal direction shifts.
			for (int j = 0; j < directions; j++) {
				Cell cell = new BasicCell(old.x() + dx[j], old.y() + dy[j]);
				if (!previousFront.contains(cell)
					&& !newFront.contains(cell)
					&& !currentFront.contains(cell)
					&& passableCells.contains(cell)
					) {
					newFront.add(cell);
				}
			}
		}
		previousFront = currentFront;
		return newFront;
	}
}
