package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class PolylineCollector implements Collector<Point2D, ImmutableList.Builder<Point2D>, Polyline> {
	@Override
	public Supplier<ImmutableList.Builder<Point2D>> supplier() {
		return ImmutableList::builder;
	}

	@Override
	public BiConsumer<ImmutableList.Builder<Point2D>, Point2D> accumulator() {
		return ImmutableList.Builder::add;
	}

	@Override
	public BinaryOperator<ImmutableList.Builder<Point2D>> combiner() {
		return (a, b) -> a.addAll(b.build());
	}

	@Override
	public Function<ImmutableList.Builder<Point2D>, Polyline> finisher() {
		return (builder) -> new BasicPolyline(builder.build());
	}

	@Override
	public Set<Characteristics> characteristics() {
		return null;
	}
}
