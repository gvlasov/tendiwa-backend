package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;
import org.tendiwa.core.meta.Cell;

public final class RectangleByTwoCorners extends Rectangle_Wr {
	public RectangleByTwoCorners(Cell corner1, Cell corner2) {
		super(getRectangleFromTwoCorners(corner1, corner2));
	}

	/**
	 * Returns rectangle defined by two corner points
	 */
	public static Rectangle getRectangleFromTwoCorners(Cell c1, Cell c2) {
		int startX = Math.min(c1.x(), c2.x());
		int startY = Math.min(c1.y(), c2.y());
		int recWidth = Math.max(c1.x(), c2.x()) - startX + 1;
		int recHeight = Math.max(c1.y(), c2.y()) - startY + 1;
		return new BasicRectangle(startX, startY, recWidth, recHeight);
	}
}
