package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPolygon;

import java.awt.Color;
import java.util.*;
import java.util.function.Consumer;

public final class Polygon implements List<Point2D> {
	private final Point2D[] points;

	public Polygon(Point2D... points) {
		if (points.length < 3) {
			throw new IllegalArgumentException(
				"Polygon must contain at least 3 vertices, but argument's length is " + points.length
			);
		}
		this.points = Arrays.copyOf(points, points.length);
	}

	public Polygon(List<Point2D> points) {
		int size = points.size();
		if (size < 3) {
//			TestCanvas.canvas.draw(points, DrawingPolygon.withColorNonRaster(Color.green));
			throw new IllegalArgumentException(
				"Polygon must contain at least 3 vertices, but argument's length is " + points.size()
			);
		}
		this.points = new Point2D[size];
		for (int i = 0; i < size; i++) {
			this.points[i] = points.get(i);
		}
	}

	/**
	 * Checks if the polygon is clockwise in terms of a y-down coordinate system.
	 * <p>
	 * <a href="http://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in
	 * -clockwise-order">Stackoverflow: how to determine if a list of polygon points are in clockwise order.</a>
	 *
	 * @return true if polygon is clockwise, false if polygon is counter-clockwise.
	 */
	public boolean isClockwise() {
		double sum = 0;
		int preLast = points.length - 1;
		for (int i = 0; i < preLast; i++) {
			sum += (points[i + 1].x - points[i].x) * (points[i + 1].y + points[i].y);
		}
		return sum < 0;
	}

	@Override
	public int size() {
		return points.length;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		for (Point2D point : points) {
			if (point.equals(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<Point2D> iterator() {
		// TODO: Write a custom iterator
		return Iterators.forArray(points);
	}

	@NotNull
	@Override
	@Deprecated
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}

	@Deprecated
	@Override
	public boolean add(Point2D point2D) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Deprecated
	@Override
	public boolean addAll(Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean addAll(int index, Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Point2D get(int index) {
		return points[index];
	}

	@Override
	@Deprecated
	public Point2D set(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void add(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Point2D remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		int size = size();
		for (int i = 0; i < size; i++) {
			if (points[i].equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int size = size();
		for (int i = size - 1; i >= 0; i--) {
			if (points[i].equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@NotNull
	@Override
	@Deprecated
	public ListIterator<Point2D> listIterator() {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	@Deprecated
	public ListIterator<Point2D> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	@Deprecated
	public List<Point2D> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forEach(Consumer<? super Point2D> action) {
		for (Point2D point : points) {
			action.accept(point);
		}
	}

	public Iterable<Segment2D> asSegments() {
		return new Iterable<Segment2D>() {
			@Override
			public Iterator<Segment2D> iterator() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void forEach(Consumer<? super Segment2D> action) {
				int last = size() - 1;
				for (int i = 0; i < last; i++) {
					action.accept(new Segment2D(points[i], points[i + 1]));
				}
			}
		};
	}

	public ImmutableSet<Segment2D> toSegments() {
		ImmutableSet.Builder<Segment2D> builder = ImmutableSet.builder();
		int last = size() - 1;
		for (int i = 0; i < last; i++) {
			builder.add(new Segment2D(points[i], points[i + 1]));
		}
		return builder.build();
	}
}
