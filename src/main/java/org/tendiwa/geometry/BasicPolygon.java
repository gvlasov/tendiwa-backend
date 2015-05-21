package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class BasicPolygon implements Polygon {
	private final ImmutableList<Point2D> points;

	public BasicPolygon(Point2D... points) {
		if (points.length < 3) {
			throw new IllegalArgumentException(
				"Polygon must contain at least 3 vertices, but argument's length is " + points.length
			);
		}
		this.points = ImmutableList.copyOf(points);
	}

	public BasicPolygon(List<Point2D> points) {
		int size = points.size();
		if (size < 3) {
			throw new IllegalArgumentException(
				"Polygon must contain at least 3 vertices, but argument's length is " + points.size()
			);
		}
		this.points = ImmutableList.copyOf(points);
	}

	/**
	 * Checks if the polygon is clockwise in terms of a y-down coordinate system.
	 * <p>
	 * <a href="http://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in
	 * -clockwise-order">Stackoverflow: how to determine if a list of polygon points are in clockwise order.</a>
	 *
	 * @return true if polygon is clockwise, false if polygon is counter-clockwise.
	 */
	@Override
	public final boolean isClockwise() {
		double sum = 0;
		int preLast = points.size() - 1;
		for (int i = 0; i < preLast; i++) {
			sum += (points.get(i + 1).x() - points.get(i).x()) * (points.get(i + 1).y() + points.get(i).y());
		}
		return sum < 0;
	}

	@Override
	public final int size() {
		return points.size();
	}

	@Override
	public final boolean isEmpty() {
		return false;
	}

	@Override
	public final boolean contains(Object o) {
		for (Point2D point : points) {
			if (point.equals(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final Iterator<Point2D> iterator() {
		// TODO: Write a custom iterator
		return points.iterator();
	}

	@NotNull
	@Override
	@Deprecated
	public final Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public final <T> T[] toArray(T[] a) {
		return null;
	}

	@Deprecated
	@Override
	public final boolean add(Point2D point2D) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public final boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Deprecated
	@Override
	public final boolean addAll(Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public final boolean addAll(int index, Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public final boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public final boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public final void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Point2D get(int index) {
		return points.get(index);
	}

	@Override
	@Deprecated
	public final Point2D set(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public final void add(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public final Point2D remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final int indexOf(Object o) {
		int size = size();
		for (int i = 0; i < size; i++) {
			if (points.get(i).equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public final int lastIndexOf(Object o) {
		int size = size();
		for (int i = size - 1; i >= 0; i--) {
			if (points.get(i).equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@NotNull
	@Override
	@Deprecated
	public final ListIterator<Point2D> listIterator() {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	@Deprecated
	public final ListIterator<Point2D> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	@Deprecated
	public final List<Point2D> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void forEach(Consumer<? super Point2D> action) {
		points.forEach(action::accept);
	}
}
