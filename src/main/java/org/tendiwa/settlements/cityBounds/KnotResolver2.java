package org.tendiwa.settlements.cityBounds;

import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellSet;

import java.util.HashSet;
import java.util.Set;

public class KnotResolver2 implements CellSet {
	private final CellSet cellsInsideBufferBorder;
	private Set<Cell> prohibitedCells;

	public KnotResolver2(CachedCellSet bufferBorder, CellSet cellsInsideBufferBorder) {
		this.cellsInsideBufferBorder = cellsInsideBufferBorder;
		prohibitedCells = new HashSet<>();
		// Cells with 3 neighbors
		Set<Cell> knots3 = new HashSet<>();
		// Cells with 4 neighbors
		Set<Cell> knots4 = new HashSet<>();
		bufferBorder.forEach((x, y) -> {
			int score = (bufferBorder.contains(x + 1, y) ? 1 : 0) +
				(bufferBorder.contains(x, y + 1) ? 1 : 0) +
				(bufferBorder.contains(x - 1, y) ? 1 : 0) +
				(bufferBorder.contains(x, y - 1) ? 1 : 0);
			if (score == 3) {
				knots3.add(new Cell(x, y));
			} else if (score == 4) {
				Cell cell = new Cell(x, y);
				knots4.add(cell);
				// A cell with 4 neighbors may also form a prohibited area
				// in combination with 3-knots.
				knots3.add(cell);
			}
		});
		for (Cell cell : knots3) {
			// Because here we only check two of four cardinal sides (east and north),
			// none of the cells will be operated upon twice (which would be the case if we checked all four sides).
			tryFixingKnot3(knots3, cell, true);
			tryFixingKnot3(knots3, cell, false);
		}
		for (Cell cell : knots4) {
			fixKnot4(cell);
		}
	}

	private void fixKnot4(Cell cell) {
		if (
			cellsInsideBufferBorder.contains(cell.x - 1, cell.y - 1)
				&& cellsInsideBufferBorder.contains(cell.x + 1, cell.y + 1)
			) {
			Cell cornerCell = cell.newRelativeCell(-1, -1);
			Cell anotherCornerCell = cell.newRelativeCell(1, 1);
			assert !cellsInsideBufferBorder.contains(cell.x + 1, cell.y - 1);
			assert !cellsInsideBufferBorder.contains(cell.x - 1, cell.y + 1);
			prohibitedCells.add(cornerCell);
			prohibitedCells.add(anotherCornerCell);
		} else if (
			cellsInsideBufferBorder.contains(cell.x + 1, cell.y - 1)
				&& cellsInsideBufferBorder.contains(cell.x - 1, cell.y + 1)
			) {
			assert !cellsInsideBufferBorder.contains(cell.x + 1, cell.y + 1);
			assert !cellsInsideBufferBorder.contains(cell.x - 1, cell.y - 1);
			Cell cornerCell = cell.newRelativeCell(1, -1);
			Cell anotherCornerCell = cell.newRelativeCell(-1, 1);
			prohibitedCells.add(cornerCell);
			prohibitedCells.add(anotherCornerCell);
		}
	}

	private void tryFixingKnot3(Set<Cell> knots, Cell cell, boolean vertical) {
		if (hasNeighborKnot(knots, cell, vertical)) {
			BoundedCellSet knotSurroundings = findCellsAroundTwoKnotCells(cell, vertical);
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

	private boolean hasNeighborKnot(Set<Cell> knots, Cell knot, boolean vertical) {
		return knots.contains(knot.newRelativeCell(vertical ? 0 : 1, vertical ? 1 : 0));
	}

	private BoundedCellSet findCellsAroundTwoKnotCells(Cell cell, boolean vertical) {
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
		if (cellsInsideBufferBorder.contains(x, y)) {
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
