package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * An CellSet that holds a finite number of {@link org.tendiwa.geometry.Cell}s.
 * <p>
 * This implementation of {@link CellSet} is better memory-wise than {@link org.tendiwa.geometry.extensions
 * .CachedCellSet} when a bounding rectangle of all the cells would cover much more cells that are in the set. That is,
 * this implementation uses {@code O(n)} where n is the number of cells in a set, when CachedCellSet uses {@code O(w*h)}
 * memory where {@code w} and {@code h} are width and height of the bounding rectangle.
 *
 * @see org.tendiwa.geometry.ScatteredMutableCellSet
 */
public class ScatteredCellSet implements FiniteCellSet {
    // cells is not of type ImmutableSet to be mutated in ScatteredMutableCellSet subclass.
    Set<Cell> cells;

    public ScatteredCellSet(ImmutableSet<Cell> cells) {
        this.cells = Objects.requireNonNull(cells);

    }

    ScatteredCellSet() {
    }


    @Override
    public Iterator<Cell> iterator() {
        return cells.iterator();
    }

    @Override
    public boolean contains(Cell cell) {
        return cells.contains(cell);
    }

    @Override
    public boolean contains(int x, int y) {
        return contains(new Cell(x, y));
    }

    @Override
    public ImmutableSet<Cell> toSet() {
        return (ImmutableSet<Cell>) cells;
    }
}
