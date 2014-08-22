package org.tendiwa.settlements.buildings;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Point2D;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Street implements List<Point2D> {

	final ImmutableList<Point2D> points;
	private String localizationId;

	public Street(ImmutableList<Point2D> points, String localizationId) {
		this.points = points;
		this.localizationId = localizationId;
	}

	public ImmutableList<Point2D> getPoints() {
		return points;
	}

	@Override
	public Iterator<Point2D> iterator() {
		return points.iterator();
	}

	@Override
	public int size() {
		return points.size();
	}

	@Override
	public boolean isEmpty() {
		return points.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return points.contains(o);
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
	public boolean add(Point2D point2D) {
		return points.add(point2D);
	}

	@Override
	public boolean remove(Object o) {
		return points.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return points.containsAll(c);
	}

	@Deprecated
	@Override
	public boolean addAll(Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException("You can't mutate streets");
	}

	@Deprecated
	@Override
	public boolean addAll(int index, Collection<? extends Point2D> c) {
		throw new UnsupportedOperationException("You can't mutate streets");
	}

	@Deprecated
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("You can't mutate streets");
	}

	@Deprecated
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("You can't mutate streets");
	}

	@Deprecated
	@Override
	public void replaceAll(UnaryOperator<Point2D> operator) {
		throw new UnsupportedOperationException("You can't mutate streets");
	}

	@Deprecated
	@Override
	public void sort(Comparator<? super Point2D> c) {
		throw new UnsupportedOperationException("You can't mutate streets");
	}

	@Deprecated
	@Override
	public void clear() {
		throw new UnsupportedOperationException("You can't mutate streets");
	}

	@Override
	public Point2D get(int index) {
		return points.get(index);
	}

	@Deprecated
	@Override
	public Point2D set(int index, Point2D element) {
		throw new UnsupportedOperationException("You can't mutate streets");
	}

	@Deprecated
	@Override
	public void add(int index, Point2D element) {
		throw new UnsupportedOperationException("You can't mutate streets");
	}

	@Deprecated
	@Override
	public Point2D remove(int index) {
		throw new UnsupportedOperationException("You can't mutate streets");
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

	@Override
	public Spliterator<Point2D> spliterator() {
		return points.spliterator();
	}

	@Override
	public boolean removeIf(Predicate<? super Point2D> filter) {
		return points.removeIf(filter);
	}

	@Override
	public Stream<Point2D> stream() {
		return points.stream();
	}

	@Override
	public Stream<Point2D> parallelStream() {
		return points.parallelStream();
	}

	@Override
	public void forEach(Consumer<? super Point2D> action) {
		points.forEach(action);
	}
}
