package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

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

    /**
     * Creates a set that is an intersection of this set and another set.
     *
     * @param set
     *         Another set.
     * @return A set that is an intersection of this set and another set.
     */
    public default CellSet and(CellSet set) {
        return (x, y) -> contains(x, y) && set.contains(x, y);
    }

    /**
     * Creates a set that is a union of this set and another set.
     *
     * @param set
     *         Another set.
     * @return A set that is a union of this set and another set.
     */
    public default CellSet or(CellSet set) {
        return (x, y) -> contains(x, y) || set.contains(x, y);
    }

    public static Collector<Cell, ?, FiniteCellSet> toCellSet() {
        return new Collector<Cell, ScatteredMutableCellSet, FiniteCellSet>() {
            @Override
            public Supplier<ScatteredMutableCellSet> supplier() {
                return ScatteredMutableCellSet::new;
            }

            @Override
            public BiConsumer<ScatteredMutableCellSet, Cell> accumulator() {
                return ScatteredMutableCellSet::add;
            }

            @Override
            public BinaryOperator<ScatteredMutableCellSet> combiner() {
                return (left, right) -> {
                    left.addAll(right);
                    return left;
                };
            }

            @Override
            public Function<ScatteredMutableCellSet, FiniteCellSet> finisher() {
                return (a) -> a;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return ImmutableSet.of(
                        Characteristics.IDENTITY_FINISH,
                        Characteristics.UNORDERED
                );
            }
        };
    }

    /**
     * Returns a cell set that doesn't contain any cells.
     *
     * @return A cell set that doesn't contain any cells.
     */
    public static CellSet empty() {
        return (x, y) -> false;
    }

    public static Collector<Cell, ?, BoundedCellSet> toBoundedCellSet(Rectangle bounds) {

        return new Collector<Cell, Mutable2DCellSet, BoundedCellSet>() {
            @Override
            public Supplier<Mutable2DCellSet> supplier() {
                return () -> new Mutable2DCellSet(bounds);
            }

            @Override
            public BiConsumer<Mutable2DCellSet, Cell> accumulator() {
                return Mutable2DCellSet::add;
            }

            @Override
            public BinaryOperator<Mutable2DCellSet> combiner() {
                return (left, right) -> {
                    left.addAll(right);
                    return left;
                };
            }

            @Override
            public Function<Mutable2DCellSet, BoundedCellSet> finisher() {
                return (a) -> a;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return ImmutableSet.of(
                        Characteristics.IDENTITY_FINISH,
                        Characteristics.UNORDERED
                );
            }
        };
    }

}
