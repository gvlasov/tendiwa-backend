package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.*;

public final class SegmentPolyline implements Polyline {
	private final ImmutableList<Segment2D> segments;

	@Override
	public ImmutableList<Segment2D> toSegments() {
		return segments;
	}

	public SegmentPolyline(ImmutableList<Segment2D> segments) {
		if (segments.isEmpty()) {
			throw new IllegalArgumentException(
				"Segment list can't be empty"
			);
		}
		assertSegmentConnectivity(segments);
		this.segments = segments;
	}

	private void assertSegmentConnectivity(ImmutableList<Segment2D> segments) {
		for (int i = 1; i < segments.size(); i++) {
			if (segments.get(i).start() != segments.get(i - 1).end()) {
				throw new IllegalArgumentException(
					"Each segment after the 0-th must start where previous segment ends"
				);
			}
		}
	}

	private List<Point2D> pointsList() {
		List<Point2D> points = new ArrayList<>(segments.size() + 1);
		points.add(segments.get(0).start());
		for (int i = 0; i < segments.size(); i++) {
			points.add(segments.get(i).end());
		}
		return points;
	}

	@Override
	public int size() {
		return segments.size() + 1;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return pointsList().contains(o);
	}

	@Override
	public Iterator<Point2D> iterator() {
		return pointsList().iterator();
	}

	@Override
	public Object[] toArray() {
		return pointsList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return pointsList().toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return pointsList().containsAll(c);
	}

	@Override
	public Point2D get(int index) {
		if (index == 0) {
			return segments.get(0).start();
		} else {
			return segments.get(index - 1).end();
		}
	}

	@Override
	public int indexOf(Object o) {
		return pointsList().indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return pointsList().lastIndexOf(o);
	}

	@Override
	public ListIterator<Point2D> listIterator() {
		return pointsList().listIterator();
	}

	@Override
	public ListIterator<Point2D> listIterator(int index) {
		return pointsList().listIterator(index);
	}

	@Override
	public List<Point2D> subList(int fromIndex, int toIndex) {
		return pointsList().subList(fromIndex, toIndex);
	}
}
