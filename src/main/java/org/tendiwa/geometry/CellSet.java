package org.tendiwa.geometry;

@FunctionalInterface
/**
 * A potentially infinite set of cells.
 */
public interface CellSet {
    /**
     * Checks if a cell is in the set.
     *
     * @param x
     *         X coordinate of a cell.
     * @param y
     *         Y coordinate of a cell.
     * @return true if a cell is in the set, false otherwise.
     */
    public boolean contains(int x, int y);

    /**
     * Checks if a cell is in the set.
     *
     * @param cell
     *         A cell.
     * @return true if a cell is in the set, false otherwise.
     */
    @SuppressWarnings("unused")
    public default boolean contains(Cell cell) {
        return contains(cell.x, cell.y);
    }

}
