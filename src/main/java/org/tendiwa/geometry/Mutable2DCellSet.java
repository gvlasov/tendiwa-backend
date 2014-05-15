package org.tendiwa.geometry;

/**
 * An editable CellSet that holds a finite number of {@link org.tendiwa.geometry.Cell}s.
 * <p>
 * It is preferable to {@link ScatteredMutableCellSet} when cells in this set cover a significant part of their bounding
 * rectangle. Uses {@code O(bounds.w*bound.h)} memory.
 */
public class Mutable2DCellSet implements MutableCellSet, BoundedCellSet {

    private final Rectangle bounds;
    private final boolean[][] cells;

    /**
     * @param bounds
     *         Bounds where you can write cells.
     */
    public Mutable2DCellSet(Rectangle bounds) {
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
    @Override
    public void add(int x, int y) {
        boolean present = cells[x - bounds.x][y - bounds.y];
        if (!present) {
            throw new IllegalArgumentException(
                    "Can't add cell " + x + " " + y + " because it is already present in this set"
            );
        }
        cells[x - bounds.x][y - bounds.y] = true;
    }

    @Override
    public void add(Cell cell) {
        add(cell.x, cell.y);
    }

    /**
     * Makes a cell absent in this set.
     * <p>
     * You can remove already absent cells.
     *
     * @param cell
     *         A cell to remove from this
     */
    @Override
    public void remove(Cell cell) {
        remove(cell.x, cell.y);
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
    @Override
    public void remove(int x, int y) {
        boolean present = cells[x - bounds.x][y - bounds.y];
        if (present) {
            throw new IllegalArgumentException(
                    "Can't remove cell " + x + " " + y + " because it is not present in this set"
            );
        }
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
