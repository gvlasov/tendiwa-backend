package org.tendiwa.geometry;

public interface MutableBoundedCellSet extends MutableCellSet, BoundedCellSet {

	void addAnyway(int x, int y);

	void toggle(int x, int y);

	void excludeRectangle(Rectangle r);
}
