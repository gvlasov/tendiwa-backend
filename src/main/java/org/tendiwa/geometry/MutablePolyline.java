package org.tendiwa.geometry;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface MutablePolyline extends Polyline {
	void addInFront(Point2D point);

	@Override
	void add(int index, Point2D element);

	@Override
	boolean add(Point2D point2D);

	@Override
	boolean addAll(Collection<? extends Point2D> c);

	@Override
	boolean addAll(int index, Collection<? extends Point2D> c);

	@Override
	void clear();

	@Override
	Point2D remove(int index);

	@Override
	boolean remove(Object o);

	@Override
	boolean removeAll(Collection<?> c);

	@Override
	boolean removeIf(Predicate<? super Point2D> filter);

	@Override
	void replaceAll(UnaryOperator<Point2D> operator);

	@Override
	boolean retainAll(Collection<?> c);

	@Override
	Point2D set(int index, Point2D element);
}
