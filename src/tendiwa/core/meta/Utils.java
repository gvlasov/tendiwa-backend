package tendiwa.core.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Utils {
	public Utils() {

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
		Set<T> instersection = new HashSet<T>();

		// load first array to a hash
		HashSet<T> array1ToHash = new HashSet<T>();
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
		return list.get(new Random().nextInt(list.size()));
	}
	public static <T> T getRandomElement(Set<T> set) {
		List<T> list = new ArrayList<T>(set);
		return getRandomElement(list);
	}
	/**
	 * Returns an Iterable object that allows iteration over all unique pairs of
	 * elements in a Collection.
	 * 
	 * @param collection
	 * @return
	 */
	public static <T> Iterable<Pair<T, T>> combinationPairsIterable(Collection<T> collection) {
		final ArrayList<T> list = new ArrayList<T>();
		list.addAll(collection);
		return new Iterable<Pair<T, T>>() {

			@Override
			public Iterator<Pair<T, T>> iterator() {
				return new Iterator<Pair<T, T>>() {
					int i = 0;
					int j = 1;
					int l = list.size();

					@Override
					public boolean hasNext() {
						if (i == l-2 && j == l-1) {
							return false;
						}
						return true;
					}

					@Override
					public Pair<T, T> next() {
						if (j == l-1) {
							i++;
						}
						return Pair.of(list.get(i), list.get(j));
					}

					@Override
					public void remove() {
						throw new NotImplementedException();
					}
				};
			}

		};
	}
}
