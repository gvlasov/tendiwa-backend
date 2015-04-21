package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.Rectangle;

import java.util.Iterator;

public class RectangleCellIterable implements Iterable<BasicCell> {
	private final Rectangle rec;

	public RectangleCellIterable(Rectangle rec) {
		this.rec = rec;
	}

	@Override
	public Iterator<BasicCell> iterator() {
		return new Iterator<BasicCell>() {
			private int n = 0;
			private int maxN = rec.width() * rec.height();

			@Override
			public boolean hasNext() {
				return n < maxN;
			}

			@Override
			public BasicCell next() {
				n++;
				return new BasicCell(
					rec.getX() + (n - 1) % rec.width(),
					rec.getY() + (n - 1) / rec.height()
				);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
