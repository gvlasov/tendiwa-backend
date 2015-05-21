package org.tendiwa.geometry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class Polygon_Wr implements Polygon {
	private final Polygon polygon;

	protected Polygon_Wr(Polygon polygon) {
		this.polygon = polygon;
	}

	@Override
	public boolean isClockwise() {
		return polygon.isClockwise();
	}

	@Override
	public boolean isClockwise(Segment2D segment) {
		return polygon.isClockwise(segment);
	}

	@Override
	public List<Segment2D> toSegments() {
		return polygon.toSegments();
	}

	@Override
	public int size() {
		return polygon.size();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return polygon.contains(o);
	}

	@Override
	public Iterator<Point2D> iterator() {
		return polygon.iterator();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(Point2D point2D) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return polygon.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Point2D get(int index) {
		return polygon.get(index);
	}

	@Override
	public Point2D set(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, Point2D element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Point2D remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		return polygon.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return polygon.lastIndexOf(o);
	}

	@Override
	public ListIterator<Point2D> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<Point2D> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Point2D> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
}
