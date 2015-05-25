package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.*;

public final class BasicPolyline implements Polyline {
	private final ImmutableList<Point2D> points;

	public BasicPolyline(List<Point2D> points) {
		if (points.size() < 2) {
			throw new IllegalArgumentException(
				"Polyline must contain at least 2 points"
			);
		}
		this.points = ImmutableList.copyOf(points);
	}

	public BasicPolyline(Point2D first, Point2D second, Point2D... rest) {
		this(constructList(first, second, rest));
	}

	private static ImmutableList<Point2D> constructList(Point2D first, Point2D second, Point2D... rest) {
		Objects.requireNonNull(first);
		Objects.requireNonNull(second);
		for (Point2D point : rest) {
			Objects.requireNonNull(point);
		}
		return ImmutableList.<Point2D>builder()
			.add(first)
			.add(second)
			.add(rest)
			.build();
	}

	@Override
	public int size() {
		return points.size();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return points.contains(o);
	}

	@Override
	public Iterator<Point2D> iterator() {
		return points.iterator();
	}

	@Override
	public Object[] toArray() {
		return points.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return points.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return points.containsAll(c);
	}

	@Override
	public Point2D get(int index) {
		return points.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return points.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return points.lastIndexOf(o);
	}

	@Override
	public ListIterator<Point2D> listIterator() {
		return points.listIterator();
	}

	@Override
	public ListIterator<Point2D> listIterator(int index) {
		return points.listIterator(index);
	}

	@Override
	public List<Point2D> subList(int fromIndex, int toIndex) {
		return points.subList(fromIndex, toIndex);
	}
}
