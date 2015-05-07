package org.tendiwa.geometry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class Polyline_Wr implements Polyline {

	private final Polyline polyline;

	protected Polyline_Wr(Polyline polyline) {
		this.polyline = polyline;
	}

	@Override
	public int size() {
		return polyline.size();
	}

	@Override
	public boolean isEmpty() {
		return polyline.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return polyline.contains(o);
	}

	@Override
	public Iterator<Point2D> iterator() {
		return polyline.iterator();
	}

	@Override
	public Object[] toArray() {
		return polyline.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return polyline.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return polyline.containsAll(c);
	}

	@Override
	public Point2D get(int index) {
		return polyline.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return polyline.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return polyline.lastIndexOf(o);
	}

	@Override
	public ListIterator<Point2D> listIterator() {
		return polyline.listIterator();
	}

	@Override
	public ListIterator<Point2D> listIterator(int index) {
		return polyline.listIterator(index);
	}

	@Override
	public List<Point2D> subList(int fromIndex, int toIndex) {
		return polyline.subList(fromIndex, toIndex);
	}
}
