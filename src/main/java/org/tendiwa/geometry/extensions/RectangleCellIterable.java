package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Rectangle;

import java.util.Iterator;

public class RectangleCellIterable implements Iterable<Cell> {
	private final Rectangle rec;

	public RectangleCellIterable(Rectangle rec) {
		this.rec = rec;
	}

	@Override
	public Iterator<Cell> iterator() {
		return new Iterator<Cell>() {
			private int n = 0;
			private int maxN = rec.getWidth() * rec.getHeight();

			@Override
			public boolean hasNext() {
				return n < maxN;
			}

			@Override
			public Cell next() {
				n++;
				return new Cell(
					rec.getX() + (n - 1) % rec.getWidth(),
					rec.getY() + (n - 1) / rec.getHeight()
				);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
