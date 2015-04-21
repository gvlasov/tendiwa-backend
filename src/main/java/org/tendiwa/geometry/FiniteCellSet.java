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

	/**
	 * Applies a function to all the cells in this {@link org.tendiwa.geometry.FiniteCellSet}. Order of iteration is
	 * undefined.
	 *
	 * @param consumer
	 */
	public default void forEach(CellConsumer consumer) {
		for (Cell cell : this) {
			consumer.consume(cell.x(), cell.y());
		}
	}

	public default Stream<Cell> stream() {
		return IterableToStream.stream(this);
	}
}
