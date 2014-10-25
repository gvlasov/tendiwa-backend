package org.tendiwa.geometry;

import java.util.Iterator;

public class TableCellIterator implements Iterator<Cell> {
	private final int[][] table;
	private final Condition condition;
	private final int width;
	private final int height;
	private int n = -1;
	private final int maxN;
	private Cell next = findNext();

	public TableCellIterator(int[][] table, Condition condition) {
		this.table = table;
		this.condition = condition;
		this.width = table.length;
		this.height = table[0].length;
		maxN = width * height - 1;
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public Cell next() {
		Cell answer = next;
		findNext();
		return answer;
	}

	private Cell findNext() {
		int x, y;
		do {
			n++;
			x = n % width;
			y = n / width;
		} while (!condition.isCellIterable(x, y, table) && n < maxN);
		if (n < maxN) {
			next = new Cell(x, y);
		} else {
			next = null;
		}
		return next;
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@FunctionalInterface
	public interface Condition {
		public boolean isCellIterable(int x, int y, int[][] table);
	}
}
