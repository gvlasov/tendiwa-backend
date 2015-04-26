package org.tendiwa.geometry.extensions;

import org.junit.Test;
import org.tendiwa.geometry.Rectangle;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class MutableChunkedCellSetTest {
	/**
	 * <ol>
	 * <li>Creates a {@link org.tendiwa.geometry.extensions.MutableChunkedCellSet};</li>
	 * <li>Creates two non intersecting {@link org.tendiwa.geometry.Rectangle}s that are subsets of that
	 * {@link org.tendiwa.geometry.extensions.MutableChunkedCellSet};</li>
	 * <li>Adds all the cells of rectangles to the cell set;</li>
	 * <li>Checks that cell set translated to a {@link java.util.Set} contains the same number of cell that are
	 * contained by both rectangles added together</li>
	 * </ol>
	 */
	@Test
	public void toSet() {
		MutableChunkedCellSet cells = new MutableChunkedCellSet(
			rectangle(20, 48, 590, 370),
			17
		);
		Rectangle r1 = rectangle(100, 100, 30, 30);
		Rectangle r2 = rectangle(200, 200, 67, 79);
		assert !r1.intersection(r2).isPresent();
		r1.forEach(cells::add);
		r2.forEach(cells::add);
		assertEquals(
			r1.area() + r2.area(),
			cells.toSet().size()
		);
	}

	public static void main(String[] args) {
		new MutableChunkedCellSetTest().toSet();
	}

}