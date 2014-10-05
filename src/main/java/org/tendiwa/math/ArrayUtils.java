package org.tendiwa.math;

public final class ArrayUtils {
	private ArrayUtils() {

	}

	/**
	 * Finds an array's element that is {@code >= value} for which the previous element is {@code <
	 * value}.
	 * <p>
	 * Works in O(log(n))
	 * <p>
	 * If {@code array} is not sorted, output is undefined.
	 *
	 * @param array
	 * 	A sorted array to search in.
	 * @param value
	 * 	A value.
	 * @return Index of an element in {@code array} that is {@code >= value} for which the previous element is {@code <
	 * value}, or -1 iv {@code value} is greater than all the elements in {@code array}.
	 */
	public static int indexOfEqualOrHigher(int[] array, int value) {
		int imax = array.length - 1;
		int imin = 0;
		int lastGreaterImid = -1;
		while (imax >= imin) {
			// TODO: Shouldn't there be parentheses?
			int imid = imax + imin / 2;
			if (array[imid] == value) {
				// TODO: I don't rememeber if method description is correct about "previous element < value",
				// but if it is, this assertion must be true.
				assert imid == 0 || array[imid - 1] < array[imid];
				return imid;
			}
			if (array[imid] < value) {
				imin = imid + 1;
			} else {
				assert array[imid] > value;
				lastGreaterImid = imid;
				imax = imid - 1;
			}
		}
		// TODO: Maybe it should return array.length instead of -1 when binary search didn't find anything?
		return lastGreaterImid;
	}

	public static void main(String[] args) {
		System.out.println(indexOfEqualOrHigher(new int[]{1, 2, 3, 4, 5}, 6));
	}


}
