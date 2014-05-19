package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;

import static java.util.Objects.requireNonNull;

/**
 * An CellSet that holds a finite number of {@link org.tendiwa.geometry.Cell}s.
 * <p>
 * Note this is not a functional interface (because it extends both {@link CellSet} and {@link Iterable}, so it can't be
 * used as a lambda expression.
 */
public interface FiniteCellSet extends CellSet, Iterable<Cell> {

    public static ScatteredCellSet of(ImmutableSet<Cell> cells) {
        return new ScatteredCellSet(requireNonNull(cells));
    }

    public static ScatteredCellSet of(Cell... cells) {
        return new ScatteredCellSet(ImmutableSet.copyOf(cells));
    }
    public ImmutableSet<Cell> toSet();
}
