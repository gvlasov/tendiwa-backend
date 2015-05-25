package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.stream.Stream;

public interface Polygon extends List<Point2D>, BoundedShape {
	default boolean isClockwise() {
		throw new UnsupportedOperationException();
	}


	ImmutableList<Point2D> toImmutableList();

	default Segment2D edge(Point2D a, Point2D b) {
		return new BasicSegment2D(a, b);
	}

	@Override
	default boolean contains(Object o) {
		return toImmutableList().contains(o);
	}

	@Override
	default int size() {
		return toImmutableList().size();
	}

	@NotNull
	@Override
	default Iterator<Point2D> iterator() {
		return toImmutableList().iterator();
	}


	@Override
	default int indexOf(Object o) {
		return toImmutableList().indexOf(o);
	}

	@Override
	default int lastIndexOf(Object o) {
		return toImmutableList().lastIndexOf(o);
	}

	@Override
	default Point2D get(int index) {
		return toImmutableList().get(index);
	}

	default List<Segment2D> toSegments() {
		List<Segment2D> segments = new ArrayList<>(size());
		new BasicPolyline(this).toSegments().forEach(segments::add);
		segments.add(edge(get(size() - 1), get(0)));
		return segments;
	}

	@Override
	default boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}


	@NotNull
	@Override
	default <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean add(Point2D point2D) {
		throw new UnsupportedOperationException();
	}


	@Deprecated
	@Override
	default boolean addAll(Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean addAll(int index, Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default void clear() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default Point2D set(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default void add(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default Point2D remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default ListIterator<Point2D> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default ListIterator<Point2D> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default List<Point2D> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	default boolean isEmpty() {
		return false;
	}

	@Deprecated
	@Override
	default boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * <a href="http://www.mathopenref.com/coordpolygonarea2.html">Algorithm to find the area of a polygon</a>
	 *
	 * @return Area of this polygon.
	 */
	default double area() {
		double area = 0;
		int j = size() - 1;

		for (int i = 0; i < size(); i++) {
			area = area + (get(j).x() + get(i).x()) * (get(j).y() - get(i).y());
			j = i;
		}
		return Math.abs(area / 2);
	}

	default Polygon translate(Vector2D vector) {
		return new BasicPolygon(
			stream()
				.map(p -> p.add(vector))
				.collect(
					org.tendiwa.collections.Collectors.toImmutableList()
				)
		);
	}

	@Override
	default Stream<Point2D> stream() {
		return toImmutableList().stream();
	}

	default boolean containsPoint(Point2D point) {
		throw new UnsupportedOperationException();
	}


}