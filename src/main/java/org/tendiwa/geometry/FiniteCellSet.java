package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;

/**
 * An CellSet that holds a finite number of {@link org.tendiwa.geometry.Cell}s.
 */
public interface FiniteCellSet extends CellSet, Iterable<Cell> {

    public static ScatteredCellSet of(ImmutableSet<Cell> cells) {
        return new ScatteredCellSet(cells);
    }
}
