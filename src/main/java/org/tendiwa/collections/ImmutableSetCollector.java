package org.tendiwa.collections;

import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

final class ImmutableSetCollector<T> implements Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> {

	@Override
	public Supplier<ImmutableSet.Builder<T>> supplier() {
		return ImmutableSet.Builder::new;
	}

	@Override
	public BiConsumer<ImmutableSet.Builder<T>, T> accumulator() {
		return (builder, element) -> builder.add(element);
	}

	@Override
	public BinaryOperator<ImmutableSet.Builder<T>> combiner() {
		return (b1, b2) -> b1.addAll(b2.build());
	}

	@Override
	public Function<ImmutableSet.Builder<T>, ImmutableSet<T>> finisher() {
		return ImmutableSet.Builder::build;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return ImmutableSet.of();
	}
}
