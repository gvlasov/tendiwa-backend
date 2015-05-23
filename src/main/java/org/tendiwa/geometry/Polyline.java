package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface Polyline extends List<Point2D>, BoundedShape {
	default ImmutableList<Segment2D> toSegments() {
		List<Segment2D> segments = new ArrayList<>(size());
		int last = size() - 1;
		for (int i = 0; i < last; i++) {
			segments.add(new BasicSegment2D(get(i), get(i + 1)));
		}
		return ImmutableList.copyOf(segments);
	}

	default Point2D start() {
		return get(0);
	}

	default Point2D end() {
		return get(size() - 1);
	}

	@Override
	default void add(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean add(Point2D point2D) {
		throw new UnsupportedOperationException();
	}

	@Override
	default boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	default boolean addAll(Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	default boolean addAll(int index, Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	default boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	default boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Point2D set(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Point2D remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void replaceAll(UnaryOperator<Point2D> operator) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void sort(Comparator<? super Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	default boolean removeIf(Predicate<? super Point2D> filter) {
		throw new UnsupportedOperationException();
	}

	static PolylineCollector toPolyline() {
		return new PolylineCollector();
	}

	default ImmutableSet<Polyline> splitAtPoints(Set<Point2D> points) {
		ImmutableSet.Builder<Polyline> polylines = ImmutableSet.builder();
		PolylineBuilder polyline = new PolylineBuilder();
		polyline.add(get(0));
		for (int i = 1; i < size(); i++) {
			Point2D point = get(i);
			polyline.add(point);
			if (points.contains(point)) {
				polylines.add(polyline.build());
				polyline = new PolylineBuilder();
				polyline.add(point);
			}
		}
		return polylines.build();
	}
}
