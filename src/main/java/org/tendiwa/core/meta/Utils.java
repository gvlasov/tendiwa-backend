package org.tendiwa.core.meta;

import org.tendiwa.geometry.Point2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class Utils {
	public Utils() {

	}

	public static boolean doubleEqualsDelta(double a, double b, double delta) {
		return Math.abs(a - b) < delta;
	}

	public static void printListOfPoints(List<Point2D> points) {
		System.out.println("List:");
		for (Point2D point : points) {
			System.out.println("\tnew Point2D(" + point.x + ", " + point.y + "),");
		}
	}

	public static <T> String join(Collection<T> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<T> iter = s.iterator();
		while (iter.hasNext()) {
			buffer.append(iter.next());
			if (iter.hasNext()) {
				buffer.append(delimiter);
			}
		}
		return buffer.toString();
	}

	public static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	public static <T> Set<T> intersectArrays(List<T> first, List<T> second) {
		// initialize a return set for intersections
		Set<T> instersection = new HashSet<>();

		// load first array to a hash
		HashSet<T> array1ToHash = new HashSet<>();
		for (T fElem : first) {
			array1ToHash.add(fElem);
		}

		// check second array for matches within the hash
		for (T sElem : second) {
			if (array1ToHash.contains(sElem)) {
				// add to the intersect array
				instersection.add(sElem);
			}
		}

		return instersection;

	}

	public static double getLineAngle(Coordinate c1, Coordinate c2) {
		double dx = (double) c2.x - c1.x;
		double dy = (double) c2.y - c1.y;
		double atan;
		if (dx == 0) {
			// Exceptional case of computing atan
			if (dy > 0) {
				atan = Math.PI / 2;
			} else {
				atan = Math.PI / 2 * 3;
			}
		} else {
			// Usual case
			atan = Math.atan(dy / dx);
		}

		// If line end is in II or III parts of space
		if (dx < 0) {
			atan = atan + Math.PI;
		}
		if (atan < 0) {
			atan = Math.PI * 2 + atan;
		}
		return atan;
	}

	public static int integersRangeIntersection(int a1, int a2, int b1, int b2) {
		// Returns amount of integers that is inside of intersection of 2
		// integer ranges
		// example: intersection of (3,9) and (5,11) is (5,9), that is 7
		// numbers: [5,6,7,8,9,10,11]
		return Math.max(a2, b2) - Math.min(a1, b1) - Math.abs(a1 - b1) - Math.abs(a2 - b2) + 1;
	}

	public static <T> T getRandomElement(List<T> list) {
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Can't get random element from an empty list");
		}
		return list.get(new Random().nextInt(list.size()));
	}

	public static <T> T getRandomElement(Collection<T> collection) {
		if (collection.isEmpty()) {
			throw new IllegalArgumentException("Can't get random element from an empty collection");
		}
		List<T> list = new ArrayList<T>(collection);
		return getRandomElement(list);
	}

	public static int previousIndex(int index, int size) {
		return index == 0 ? size - 1 : index - 1;
	}

	public static int nextIndex(int index, int size) {
		return index == size - 1 ? 0 : index + 1;
	}
}
