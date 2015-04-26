package org.tendiwa.geometry;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class BoundedCellSetTest {
	@Test
	public void shouldIterateOver12BorderCells() {
		BoundedCellSet cellSet = new BoundedCellSet() {

			private Rectangle bounds = rectangle(0, 0, 4, 4);
			private Rectangle hole = rectangle(1, 1, 2, 2);

			@Override
			public Rectangle getBounds() {
				return bounds;
			}

			@Override
			public boolean contains(int x, int y) {
				return bounds.contains(x, y) && !hole.contains(x, y);
			}
		};
		assertEquals(12, Lists.newArrayList(cellSet).size());
	}
}
