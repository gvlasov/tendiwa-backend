package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.collections.IterableToStream;
import org.tendiwa.core.meta.Cell;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * An CellSet that holds a finite number of {@link Cell}s.
 * <p>
 * Note this is not a functional interface (because it extends both {@link CellSet} and {@link Iterable}, so it can't
 * be used as a lambda expression.
 * <p>
 * When implementing this interface, it may often be useful to override
 * {@link Iterable#forEach(java.util.function.Consumer)}.
 */
public interface FiniteCellSet extends CellSet, Iterable<Cell> {

	public static ScatteredCellSet of(ImmutableSet<Cell> cells) {
		return new ScatteredCellSet(requireNonNull(cells));
	}

	public static ScatteredCellSet of(Cell... cells) {
		return new ScatteredCellSet(ImmutableSet.copyOf(cells));
	}

	public default ImmutableSet<Cell> toSet() {
		return ImmutableSet.copyOf(this);
	}


	public default Stream<Cell> stream() {
		return IterableToStream.stream(this);
	}
}
