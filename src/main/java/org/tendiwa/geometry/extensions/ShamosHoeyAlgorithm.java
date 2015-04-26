package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.BasicSegment2D;
import org.tendiwa.geometry.Segment2D;

import java.util.*;

/**
 * Checks line segments for intersection.
 * <p>
 * Algorithm needs O(n) space.
 * <p>
 * Unlike the naive O(n^2) approach, this algorithm runs in O(n*log n) time.
 */
public class ShamosHoeyAlgorithm {

	public static boolean areIntersected(Collection<Segment2D> lines) {
		List<AlgEvent> events = new ArrayList<>();
		for (Segment2D l : lines) {
			events.add(new AlgEvent(l, true));
			events.add(new AlgEvent(l, false));
		}
		Collections.sort(events);
		TreeSet<Segment2D> sl = new TreeSet<>(new Comparator<Segment2D>() {
			@Override
			public int compare(Segment2D l1, Segment2D l2) {
				return compareLines(l1, l2);
			}
		});
		for (AlgEvent e : events) {
			if (e.isStart) {
				Segment2D nl = e.line;
				Segment2D above = sl.higher(nl);
				if (above != null) {
					if (linesIntersect(above, nl)) {
						return true;
					}
				}
				Segment2D below = sl.lower(nl);
				if (below != null) {
					if (linesIntersect(below, nl)) {
						return true;
					}
				}
				sl.add(nl);
			} else {
				Segment2D nl = e.line;
				Segment2D above = sl.higher(nl);
				Segment2D below = sl.lower(nl);
				sl.remove(nl);
				if ((above != null) && (below != null)) {
					if (linesIntersect(above, below)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean linesIntersect(Segment2D l1, Segment2D l2) {
		double rx = l1.end().x() - l1.start().x();
		double ry = l1.end().y() - l1.start().y();

		double sx = l2.end().x() - l2.start().x();
		double sy = l2.end().y() - l2.start().y();

		double rXs = rx * sy - ry * sx;
		if (rXs == 0) {
			// lines are parallel
			// they may be colinear, but we also consider that case to be non-intersecting.
			return false;
		} else {
			double qMpx = l2.start().x() - l1.start().x();
			double qMpy = l2.start().y() - l1.start().y();
			double t = (qMpx * sy - qMpy * sx) / rXs;
			double q = (qMpx * ry - qMpy * rx) / rXs;
			// 1e-10 is needed here, because sometimes floating-point errors creep up when moving the points
			return t > 1e-10 && t < (1 - 1e-10) && q > 1e-10 && q < (1 - 1e-10);
		}
	}

	public static int compareLines(Segment2D l1, Segment2D l2) {
		int r;
		// need to handle the case when the line is compared to itself
		// since Segment2D.equals only uses reference equality,
		// we'll use comparison of two points, to be on the safe side.
		if (l1.start().equals(l2.start()) && l1.end().equals(l2.end())) {
			r = 0;
		} else if (l2.start().x() == l1.start().x()) {
			// if both points start on the same X,
			// line with greater start Y value is above the other line
			if (l2.start().y() < l1.start().y()) {
				r = 1;
			} else if (l2.start().y() > l1.start().y()) {
				r = -1;
			} else {
				// but if the lines both start at the same point ((l1.x1,l1.y1) == (l2.x1,l2.y1)),
				// things get a bit more complicated

				if (l1.start().x() == l1.end().x() && l2.start().x() == l2.end().x()) {
					// Both lines are vertical, so we take the one with greater Y2 value to be above the other line
					// This case shouldn't happen in normal practice, but who knows.
					r = l1.end().y() > l2.end().y() ? 1 : -1;
				} else if (l1.start().x() == l1.end().x()) {
					// Only the first line is vertical.
					// Since vertical lines always have their Y2 bigger than Y1 (see AlgEvent constructor),
					// we can safely assume that l1 is above l2.
					r = 1;
				} else if (l2.start().x() == l2.end().x()) {
					// Same as above, but for l2.
					r = -1;
				} else {
					// Both lines start in the same point, both are not vertical -
					// thus we will need to compute which one climbs faster.
					double dx1 = (l1.end().x() - l1.start().x()) / (l1.end().y() - l1.start().y());
					double dx2 = (l2.end().x() - l2.start().x()) / (l2.end().y() - l2.start().y());
					r = Double.compare(dx1, dx2);
				}
			}
		} else if (l2.start().x() < l1.start().x()) {
			// We need to find the Y coordinate for vertical projection of l1.X1 on l2.
			// If that Y coordinate is smaller than l1.Y1, then l1 is considered to be above l2.
			double px = l2.start().x();
			double py = l2.start().y();
			double rx = l2.end().x() - l2.start().x();
			double ry = l2.end().y() - l2.start().y();
			double y = py + (l1.start().x() - px) / rx * ry;
			r = y < l1.start().y() ? 1 : -1;
		} else {
			// Same as above, only reversed.
			double px = l1.start().x();
			double py = l1.start().y();
			double rx = l1.end().x() - l1.start().x();
			double ry = l1.end().y() - l1.start().y();
			double y = py + (l2.start().x() - px) / rx * ry;
			r = y < l2.start().y() ? -1 : 1;
		}
		return r;
	}

	private static class AlgEvent implements Comparable<AlgEvent> {

		public Segment2D line;
		public boolean isStart;

		AlgEvent(Segment2D l, boolean isStart) {
			// ensure proper line direction
			if (l.start().x() < l.end().x() || (l.start().x() == l.end().x() && l.start().y() < l.end().y())) {
				line = new BasicSegment2D(l.start(), l.end());
			} else {
				line = new BasicSegment2D(l.end(), l.start());
			}
			this.isStart = isStart;
		}

		double getX() {
			return (isStart) ? line.start().x() : line.end().x();
		}

		double getY() {
			return (isStart) ? line.start().y() : line.end().y();
		}

		@Override
		public int compareTo(AlgEvent o) {
			if (this.getX() < o.getX()) {
				return -1;
			} else if (this.getX() > o.getX()) {
				return 1;
			} else if (this.getY() < o.getY()) {
				return -1;
			} else if (this.getY() > o.getY()) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}