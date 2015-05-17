package org.tendiwa.geometry.graphs2d;

import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class Graph2DUnionCollector implements Collector<Graph2D, MutableGraph2D, Graph2D> {
	@Override
	public Supplier<MutableGraph2D> supplier() {
		return BasicMutableGraph2D::new;
	}

	@Override
	public BiConsumer<MutableGraph2D, Graph2D> accumulator() {
		return (mutable, graph) -> {
			graph.vertexSet().forEach(mutable::addVertex);
			graph.edgeSet().forEach(mutable::addSegmentAsEdge);
		};
	}

	@Override
	public BinaryOperator<MutableGraph2D> combiner() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Function<MutableGraph2D, Graph2D> finisher() {
		return (mutableGraph) -> mutableGraph;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return null;
	}
}
