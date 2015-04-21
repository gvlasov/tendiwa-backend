package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.collections.IterableToStream;
import org.tendiwa.core.meta.Cell;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * In a {@link FiniteCellSet}, greedily finds a set of cells where there is at least certain Chebyshov distance between
 * each found cell.
 * <p>
 * Example:
 * <pre>{@code
 *     DistantCellsFinder distantCells = new DistantCellsFinder(
 *          collectionOfCells,
 *          20
 *     );
 *     for (Cell cell : distantCells) {
 *         canvas.drawCell(cell, RED);
 *     }
 * }</pre>
 * <p>
 * This class implements {@link java.lang.Iterable} instead of {@link org.tendiwa.geometry.CellSet} because the more
 * remote cells it has found, the longer it takes to find the next remote cell, so it doesn't compute all the remote
 * cells in set {@code cells}, but instead finds as many cells as API user needs with each call to
 * {@link java.util.Iterator#next()}.
 */
public class DistantCellsFinder implements Iterable<Cell> {
	private final ImmutableSet<Cell> cells;
	private final int minDistance;

	public DistantCellsFinder(
		FiniteCellSet cells,
		int minDistance
	)

	{
		this.cells = cells.toSet();
		this.minDistance = minDistance;
	}
	public Stream<Cell> stream() {
		return IterableToStream.stream(iterator());
	}

	@Override
	public Iterator<Cell> iterator() {
		//TODO: Replace this iterator with FiniteCellSet's
		return new Iterator<Cell>() {
			private Collection<Rectangle> occupiedPlaces = new LinkedList<>();
			private Cell next = findNext();

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
				for (Cell nextCell : cells) {
					if (!occupiedPlaces.stream().anyMatch(r -> r.contains(nextCell))) {
						occupyAreaAroundCell(nextCell);
						return nextCell;
					}
				}
				return null;
			}

			private void occupyAreaAroundCell(Cell nextCell) {
				occupiedPlaces.add(new RectangleCenteredAt(
					nextCell,
					minDistance * 2 + 1,
					minDistance * 2 + 1
				));
			}
		};
	}

}
