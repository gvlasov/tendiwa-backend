package org.tendiwa.geometry;

/**
 * A cell set that you can write to. You can't write cells outside this set's bounds.
 */
public class MutableCellSet implements BoundedCellSet {

    private final Rectangle bounds;
    private final boolean[][] cells;

    /**
     * @param bounds
     *         Bounds where you can write cells.
     */
    public MutableCellSet(Rectangle bounds) {
        this.bounds = bounds;
        cells = new boolean[bounds.width][bounds.height];
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Makes a cell present in this set.
     * <p>
     * You can remove already absent cells.
     *
     * @param x
     *         X coordinate of a cell.
     * @param y
     *         Y coordinate of a cell.
     */
    public void add(int x, int y) {
        cells[x - bounds.x][y - bounds.y] = true;
    }

    /**
     * Makes a cell absent in this set.
     * <p>
     * You can remove already absent cells.
     *
     * @param x
     *         X coordinate of a cell.
     * @param y
     *         Y coordinate of a cell.
     */
    public void remove(int x, int y) {
        cells[x - bounds.x][y - bounds.y] = false;
    }

    /**
     * Makes a cell present in this set if it is not, makes it absent if it is.
     *
     * @param x
     *         X coordinate of a cell.
     * @param y
     *         Y coordinate of a cell.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *         If cell is not within {@link #getBounds()}.
     */
    public void toggle(int x, int y) {
        cells[x - bounds.x][y - bounds.y] = !cells[x - bounds.x][y - bounds.y];
    }

    /**
     * Checks if this set contains a particular cell.
     *
     * @param x
     *         X coordinate of a cell.
     * @param y
     *         Y coordinate of a cell.
     * @return true if this set contains a cell {x:y}, false otherwise.
     */
    @Override
    public boolean contains(int x, int y) {
        return bounds.contains(x, y) && cells[x - bounds.x][y - bounds.y];
    }
}
