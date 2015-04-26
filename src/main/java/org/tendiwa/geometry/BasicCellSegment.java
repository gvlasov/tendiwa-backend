package org.tendiwa.geometry;

import com.google.common.collect.Iterators;
import org.tendiwa.core.meta.Cell;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A segment defined by a start and end {@link Cells}. Doesn't store anything other than start and end cells.
 */
public final class BasicCellSegment implements CellSegment {

	public final Cell start;
	public final Cell end;

	public BasicCellSegment(Cell start, Cell end) {
		this.start = start;
		this.end = end;
	}

	public BasicCellSegment(Segment2D segment) {
		this.start = segment.start().toCell();
		this.end = segment.end().toCell();
	}

	public static Cell[] cells(Cell start, Cell end) {
		return cells(start.x(), start.y(), end.x(), end.y());
	}

	public static Cell[] cells(int startX, int startY, int endX, int endY) {
		int l = Math.round(Math.max(Math.abs(endX - startX),
			Math.abs(endY - startY)));
		float x[] = new float[l + 2];
		float y[] = new float[l + 2];
		Cell result[] = new Cell[l + 1];

		x[0] = startX;
		y[0] = startY;

		if (startX == endX && startY == endY) {
			result = new Cell[1];
			result[0] = new BasicCell(startX, startY);
			return result;
		}
		float dx = (endX - startX) / (float) l;
		float dy = (endY - startY) / (float) l;
		for (int i = 1; i <= l; i++) {
			x[i] = x[i - 1] + dx;
			y[i] = y[i - 1] + dy;
		}
		x[l + 1] = endX;
		y[l + 1] = endY;

		for (int i = 0; i <= l; i++) {
			result[i] = new BasicCell(Math.round(x[i]), Math.round(y[i]));
		}
		return result;
	}

	public static Cell[] vector(Cell start, Cell end) {
		return cells(start.x(), start.y(), end.x(), end.y());
	}

	/**
	 * Iterates over cells in a line from {@link #start} to {@link #end} inclusive. Computes those cells anew each time
	 * this method is called.
	 *
	 * @return An iterator over an array of cells.
	 */
	@Override
	public Iterator<Cell> iterator() {
		return Iterators.forArray(vector(start, end));
	}

	/**
	 * Computes distance from {@link #start} to {@link #end}.
	 *
	 * @return Distance from {@link #start} to {@link #end}.
	 */
	@Override
	public double length() {
		return Math.sqrt(
			(end.x() - start.x()) * (end.x() - start.x())
				+ (end.y() - start.y()) * (end.y() - start.y()
			)
		);
	}

	@Override
	public List<Cell> asList() {
		return Arrays.asList(vector(start, end));
	}

	@Override
	public Cell start() {
		return start;
	}

	@Override
	public Cell end() {
		return end;
	}
}
