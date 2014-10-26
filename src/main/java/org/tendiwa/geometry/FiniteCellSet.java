package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;

import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * An CellSet that holds a finite number of {@link org.tendiwa.geometry.Cell}s.
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

	public ImmutableSet<Cell> toSet();

	/**
	 * Applies a function to all the cells in this {@link org.tendiwa.geometry.FiniteCellSet}. Order of iteration is
	 * undefined.
	 *
	 * @param consumer
	 */
	public default void forEach(CellConsumer consumer) {
		for (Cell cell : this) {
			consumer.consume(cell.x, cell.y);
		}
	}

}
