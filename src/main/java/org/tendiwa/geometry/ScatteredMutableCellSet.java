package org.tendiwa.geometry;

import java.util.HashSet;

/**
 * An editable CellSet that holds a finite number of {@link org.tendiwa.geometry.Cell}s.
 */
public class ScatteredMutableCellSet extends ScatteredCellSet implements MutableCellSet {
    public ScatteredMutableCellSet() {
        super();
        cells = new HashSet<>();
    }

    @Override
    public void add(Cell cell) {
        if (!cells.add(cell)) {
            throw new IllegalArgumentException("Cell " + cell + " is already present in this set " + cells);
        }
    }
}
