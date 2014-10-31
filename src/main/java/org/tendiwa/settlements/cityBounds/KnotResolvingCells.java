package org.tendiwa.settlements.cityBounds;

import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellSet;

import java.util.HashSet;
import java.util.Set;

/**
 * For a border and cells inside it, computes a set of prohibited cells that should be excluded from {@code
 * cellsInsideBorder}, so border can be rebuilt around that modified {@code cellsInsideBorder} in such a way that for
 * each cell of the new border there are exactly 2 neighbor cells in that border.
 * <p>
 * A <i>knot</i> is a situation when a cell in {@code border} has more than 2 neighbor cells (i.e. cells for cardinal
 * sides around it that are too contained in {@code border}). More than 2 is either 3 or 4,
 * because there are 4 cardinal sides.
 * <p>
 * Every 4-knot is considered to be also a 3-knot.
 */
final class KnotResolvingCells implements CellSet {
	private final CellSet cellsInsideBorder;
	private Set<Cell> prohibitedCells;

	public KnotResolvingCells(CachedCellSet border, CellSet cellsInsideBorder) {
		this.cellsInsideBorder = cellsInsideBorder;
		this.prohibitedCells = new HashSet<>();
		// Cells with 3 neighbors
		Set<Cell> knots3 = new HashSet<>();
		// Cells with 4 neighbors
		Set<Cell> knots4 = new HashSet<>();
		border.forEach((x, y) -> {
			int neighbors = howManyNeighborsCellHas(x, y, border);
			if (neighbors == 3) {
				knots3.add(new Cell(x, y));
			} else if (neighbors == 4) {
				Cell cell = new Cell(x, y);
				knots4.add(cell);
				// A cell with 4 neighbors may also form a prohibited area
				// in combination with 3-knots.
				knots3.add(cell);
			}
		});
		for (Cell cell : knots3) {
			// Because here we only check two of four cardinal sides (east and north),
			// none of the cells will be operated upon twice
			// (which would be the case if we checked all four sides).
			tryFixingKnot3(knots3, cell, true);
			tryFixingKnot3(knots3, cell, false);
		}
		knots4.forEach(this::fixKnot4);
	}

	/**
	 * Counts how many cells contained in {@code cells} are there from 4 cardinal sides of cell {@code x:y}.
	 *
	 * @param x
	 * 	X-coordinate of a cell.
	 * @param y
	 * 	Y-coordinate of a cell.
	 * @param cells
	 * 	A set of cells.
	 * @return An integer in [0;4]
	 */
	private int howManyNeighborsCellHas(int x, int y, CellSet cells) {
		return (cells.contains(x + 1, y) ? 1 : 0) +
			(cells.contains(x, y + 1) ? 1 : 0) +
			(cells.contains(x - 1, y) ? 1 : 0) +
			(cells.contains(x, y - 1) ? 1 : 0);
	}

	/**
	 * Adds cells to {@link #prohibitedCells} so a 4-knot in {@code cell} is resolved.
	 *
	 * @param cell
	 * 	A cell with a 4-knot.
	 */
	private void fixKnot4(Cell cell) {
		if (
			cellsInsideBorder.contains(cell.x - 1, cell.y - 1)
				&& cellsInsideBorder.contains(cell.x + 1, cell.y + 1)
			) {
			Cell cornerCell = cell.newRelativeCell(-1, -1);
			Cell anotherCornerCell = cell.newRelativeCell(1, 1);
			assert !cellsInsideBorder.contains(cell.x + 1, cell.y - 1);
			assert !cellsInsideBorder.contains(cell.x - 1, cell.y + 1);
			prohibitedCells.add(cornerCell);
			prohibitedCells.add(anotherCornerCell);
		} else if (
			cellsInsideBorder.contains(cell.x + 1, cell.y - 1)
				&& cellsInsideBorder.contains(cell.x - 1, cell.y + 1)
			) {
			assert !cellsInsideBorder.contains(cell.x + 1, cell.y + 1);
			assert !cellsInsideBorder.contains(cell.x - 1, cell.y - 1);
			Cell cornerCell = cell.newRelativeCell(1, -1);
			Cell anotherCornerCell = cell.newRelativeCell(-1, 1);
			prohibitedCells.add(cornerCell);
			prohibitedCells.add(anotherCornerCell);
		}
	}

	/**
	 * Resolves a 3-knot in {@code cell} by prohibiting inner cells around it if there is another 3-knot near
	 * {@code cell}.
	 *
	 * @param knots
	 * 	A set of existing knots.
	 * @param cell
	 * 	A cell with a 3-knot.
	 * @param vertical
	 * 	See {@link #hasNeighborKnot(java.util.Set, org.tendiwa.geometry.Cell, boolean)}.
	 */
	private void tryFixingKnot3(Set<Cell> knots, Cell cell, boolean vertical) {
		if (hasNeighborKnot(knots, cell, vertical)) {
			BoundedCellSet knotSurroundings = findInnerCellsAround2BorderNeighbors(cell, vertical);
			Cell anySurroundingCell = knotSurroundings.iterator().next();
			if (anySurroundingCell == null) {
				return;
			}
			BoundedCellSet continuousKnotSurroundings = Wave
				.from(anySurroundingCell)
				.goingOver(knotSurroundings)
				.in4Directions()
				.asCellSet(knotSurroundings.getBounds());
			assert knotSurroundings.getBounds().area() == 12;
			assert continuousKnotSurroundings.getBounds().area() == 12;
			boolean surroundingsAreUnite = knotSurroundings
				.toSet()
				.stream()
				.allMatch(continuousKnotSurroundings::contains);
			if (!surroundingsAreUnite) {
				knotSurroundings.forEach(prohibitedCells::add);
			}
		}
	}

	/**
	 * Checks if there is a knot to the east or to the south from a {@code cell}.
	 *
	 * @param knots
	 * 	A set of existing knots.
	 * @param knot
	 * 	A cell where there is a knot.
	 * @param vertical
	 * 	Whether we're checking a cell from south (true) or east (false).
	 * @return true if there is a knot in the specified neighbor cell, false othewise.
	 */
	private boolean hasNeighborKnot(Set<Cell> knots, Cell knot, boolean vertical) {
		return knots.contains(knot.newRelativeCell(vertical ? 0 : 1, vertical ? 1 : 0));
	}

	/**
	 * Finds cells that are contained in {@link #cellsInsideBorder} and surround two members of border: {@code cell}
	 * and its cardinal neighbor.
	 * <p>
	 * A maximum of 10 cells can be found. Cells are situated in the following manner:
	 * <p>
	 * <pre>
	 * ####
	 * #CN#
	 * ####
	 * </pre>
	 * or
	 * <pre>
	 * ###
	 * #C#
	 * #N#
	 * ###
	 * </pre>
	 * <p>
	 * Where {@code C} is {@code cell}, {@code N} is its neighbor and {@code #} are the found cells.
	 *
	 * @param cell
	 * 	A cell
	 * @param vertical
	 * 	if true, a neighbor from south will be picked; else a neighbor from east will be picked.
	 * @return A set of all cells that are arount
	 */
	private BoundedCellSet findInnerCellsAround2BorderNeighbors(Cell cell, boolean vertical) {
		Mutable2DCellSet markedCells;
		if (vertical) {
			markedCells = new Mutable2DCellSet(new Rectangle(cell.x - 1, cell.y - 1, 3, 4));
			markCellIfItIsInside(markedCells, cell.x - 1, cell.y - 1);
			markCellIfItIsInside(markedCells, cell.x, cell.y - 1);
			markCellIfItIsInside(markedCells, cell.x + 1, cell.y - 1);
			markCellIfItIsInside(markedCells, cell.x - 1, cell.y);
			markCellIfItIsInside(markedCells, cell.x + 1, cell.y);
			markCellIfItIsInside(markedCells, cell.x - 1, cell.y + 1);
			markCellIfItIsInside(markedCells, cell.x + 1, cell.y + 1);
			markCellIfItIsInside(markedCells, cell.x - 1, cell.y + 2);
			markCellIfItIsInside(markedCells, cell.x, cell.y + 2);
			markCellIfItIsInside(markedCells, cell.x + 1, cell.y + 2);
		} else {
			markedCells = new Mutable2DCellSet(new Rectangle(cell.x - 1, cell.y - 1, 4, 3));
			markCellIfItIsInside(markedCells, cell.x - 1, cell.y - 1);
			markCellIfItIsInside(markedCells, cell.x, cell.y - 1);
			markCellIfItIsInside(markedCells, cell.x + 1, cell.y - 1);
			markCellIfItIsInside(markedCells, cell.x + 2, cell.y - 1);
			markCellIfItIsInside(markedCells, cell.x - 1, cell.y);
			markCellIfItIsInside(markedCells, cell.x + 2, cell.y);
			markCellIfItIsInside(markedCells, cell.x - 1, cell.y + 1);
			markCellIfItIsInside(markedCells, cell.x, cell.y + 1);
			markCellIfItIsInside(markedCells, cell.x + 1, cell.y + 1);
			markCellIfItIsInside(markedCells, cell.x + 2, cell.y + 1);
		}
		return markedCells;
	}

	private void markCellIfItIsInside(MutableCellSet markedCells, int x, int y) {
		if (cellsInsideBorder.contains(x, y)) {
			markedCells.add(x, y);
		}
	}

	@Override
	public boolean contains(int x, int y) {
		return prohibitedCells.contains(new Cell(x, y));
	}

	@Override
	public boolean contains(Cell cell) {
		return prohibitedCells.contains(cell);
	}
}
