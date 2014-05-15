package org.tendiwa.geometry;

/**
 * A cell set that you can write to.
 */
public interface MutableCellSet extends FiniteCellSet {
    default public void add(int x, int y) {
        add(new Cell(x, y));
    }

    public void add(Cell cell);

    public default void addAll(FiniteCellSet cells) {
        for (Cell cell : cells) {
            add(cell);
        }
    }

    public void remove(Cell cell);

    default public void remove(int x, int y) {
        remove(new Cell(x, y));
    }
}
