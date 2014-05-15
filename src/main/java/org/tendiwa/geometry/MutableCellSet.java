package org.tendiwa.geometry;

/**
 * A cell set that you can write to.
 */
public interface MutableCellSet extends FiniteCellSet {
    default public void add(int x, int y) {
        add(new Cell(x, y));
    }

    public void add(Cell cell);
}
