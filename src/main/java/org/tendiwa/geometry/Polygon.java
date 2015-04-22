package org.tendiwa.geometry;

import com.sun.istack.internal.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

public interface Polygon extends List<Point2D> {
	boolean isClockwise();


	List<Segment2D> toSegments();


	@NotNull
	@Override
	@Deprecated
	Object[] toArray();

	@NotNull
	@Override
	<T> T[] toArray(T[] a);

	@Deprecated
	@Override
	boolean add(Point2D point2D);

	@Override
	@Deprecated
	boolean remove(Object o);

	@Override
	boolean containsAll(Collection<?> c);

	@Deprecated
	@Override
	boolean addAll(Collection<? extends Point2D> c);

	@Deprecated
	@Override
	boolean addAll(int index, Collection<? extends Point2D> c);

	@Deprecated
	@Override
	boolean removeAll(Collection<?> c);

	@Deprecated
	@Override
	boolean retainAll(Collection<?> c);

	@Deprecated
	@Override
	void clear();

	@Override
	@Deprecated
	Point2D set(int index, Point2D element);

	@Override
	@Deprecated
	void add(int index, Point2D element);

	@Override
	@Deprecated
	Point2D remove(int index);

	@NotNull
	@Override
	@Deprecated
	ListIterator<Point2D> listIterator();

	@NotNull
	@Override
	@Deprecated
	ListIterator<Point2D> listIterator(int index);

	@NotNull
	@Override
	@Deprecated
	List<Point2D> subList(int fromIndex, int toIndex);


}
