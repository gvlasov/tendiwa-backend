package org.tendiwa.geometry;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import static org.tendiwa.collections.Collectors.*;

public interface Polygon extends List<Point2D> {
	boolean isClockwise();

	default List<Segment2D> toSegments() {
		List<Segment2D> segments = new ArrayList<>(size());
		new BasicPolyline(this).toSegments().forEach(segments::add);
		segments.add(new BasicSegment2D(get(size()-1), get(0)));
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
		return area / 2;
	}

	default Polygon translate(Vector2D vector) {
		return new BasicPolygon(
			stream()
				.map(p -> p.add(vector))
				.collect(toImmutableList())
		);
	}

	default boolean containsPoint(Point2D point) {
		throw new UnsupportedOperationException();
	}
}
