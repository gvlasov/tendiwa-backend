package org.tendiwa.geometry;

import org.tendiwa.core.meta.Cell;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * An editable CellSet backed up by a 2d array that contains a finite number of cells.
 * <p>
 * It is preferable to {@link ScatteredMutableCellSet} when cells in this set cover a significant part of their
 * bounding rectangle. Uses {@code Θ(bounds.width*bound.height)} memory.
 */
public class Mutable2DCellSet implements MutableBoundedCellSet, ArrayBackedCellSet {

	private final Rectangle bounds;
	/*
	First index is y coordinate, second index is x coordinate.
	 */
	private final boolean[][] cells;

	/**
	 * @param bounds
	 * 	Bounds where you can write cells.
	 */
	public Mutable2DCellSet(Rectangle bounds) {
		Objects.requireNonNull(bounds);
		this.bounds = bounds;
		this.cells = new boolean[bounds.height()][bounds.width()];
	}

	public Mutable2DCellSet(ArrayBackedCellSet rasterized) {
		this(rasterized.getBounds());
		for (int i=0; i<bounds.height(); i++) {
			for (int j=0; j<bounds.width(); j++) {
				cells[i][j] = rasterized.arrayElement(j, i); // indices not mixed up!
			}
		}
	}

	@Override
	public final Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Makes a cell present in this set.
	 * <p>
	 * You can remove already absent cells.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 	If {x:y} is not within bounds.
	 */
	@Override
	public final void add(int x, int y) {
		boolean present = cells[y - bounds.y()][x - bounds.x()];
		if (present) {
			throw new IllegalArgumentException(
				"Can't add cell " + x + " " + y + " because it is already present in this set"
			);
		}
		cells[y - bounds.y()][x - bounds.x()] = true;
	}

	/**
	 * Adds a new cell to this cell set without checking if that cell is already present.
	 * <p>
	 * If the cell is already present, it remains present.
	 * <p>
	 * This method is much more effective than {@link #add(int, int)}.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 */
	@Override
	public final void addAnyway(int x, int y) {
		cells[y - bounds.y()][x - bounds.x()] = true;
	}

	@Override
	public final void add(Cell cell) {
		add(cell.x(), cell.y());
	}

	/**
	 * Makes a cell absent in this set.
	 * <p>
	 * You can remove already absent cells.
	 *
	 * @param cell
	 * 	A cell to remove from this
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 	If {x:y} is not within bounds.
	 */
	@Override
	public final void remove(Cell cell) {
		remove(cell.x(), cell.y());
	}

	/**
	 * Makes a cell absent in this set.
	 * <p>
	 * You can remove already absent cells.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 */
	@Override
	public final void remove(int x, int y) {
		boolean present = cells[y - bounds.y()][x - bounds.x()];
		if (present) {
			throw new IllegalArgumentException(
				"Can't remove cell " + x + " " + y + " because it is not present in this set"
			);
		}
		cells[y - bounds.y()][x - bounds.x()] = false;
	}

	/**
	 * Makes a cell present in this set if it is not, makes it absent if it is.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 	If cell is not within {@link #getBounds()}.
	 */
	@Override
	public final void toggle(int x, int y) {
		cells[y - bounds.y()][x - bounds.x()] = !cells[y - bounds.y()][x - bounds.x()];
	}

	/**
	 * Checks if this set contains a particular cell.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 * @return true if this set contains a cell {x:y}, false otherwise.
	 */
	@Override
	public final boolean contains(int x, int y) {
		return bounds.contains(x, y) && cells[y - bounds.y()][x - bounds.x()];
	}

	@Override
	public final void forEach(Consumer<? super Cell> action) {
		for (int x = 0; x < bounds.width(); x++) {
			for (int y = 0; y < bounds.height(); y++) {
				if (cells[y][x]) {
					action.accept(new BasicCell(x + bounds.x(), y + bounds.y()));
				}
			}
		}
	}

	/**
	 * Fills a rectangular area with obstacle cells.
	 *
	 * @param r
	 * 	A rectangle to fill.
	 */
	@Override
	public final void excludeRectangle(Rectangle r) {
		int startX = r.x() - bounds.x();
		int endX = r.x() + r.width() - bounds.x();
		int endY = r.y() - bounds.y() + r.height();
		for (int row = r.y() - bounds.y(); row < endY; row++) {
			Arrays.fill(
				cells[row],
				startX,
				endX,
				false
			);
		}
	}

	public final void fillHorizontalSegment(OrthoCellSegment segment) {
		Arrays.fill(
			cells[segment.getY() - bounds.y()],
			segment.getX() - bounds.x(),
			segment.getEndX() - bounds.x()+1,
			true
		);
	}

	@Override
	public final boolean arrayElement(int arrayX, int arrayY) {
		return cells[arrayY][arrayX]; // not a typo
	}
}
