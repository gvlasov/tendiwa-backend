package org.tendiwa.math;

import java.util.Arrays;
import java.util.Random;

public final class IntegerPermutationGenerator {
	private IntegerPermutationGenerator() {

	}

	/**
	 * Generates a k-permutation from integers in [0..n]. Each permutation has equal chance for
	 * being generated, assuming {@code random} yields uniformly distributed results.
	 * <p>
	 * Works in O(k*log(k))
	 *
	 * @param n
	 * 	Number of elements of a range to pick integers from.
	 * @param k
	 * 	How many integers to pick.
	 * @param random
	 * 	A source of randomness.
	 * @return A k-permutation of [0; n-1].
	 */
	public static int[] generate(int n, int k, Random random) {
		if (n < k) {
			throw new IllegalArgumentException("n must be >= k");
		}
		if (n <= 0) {
			throw new IllegalArgumentException("n must be > 0");
		}

		random = new Random(random.nextInt());
		int[] answer = new int[k];
		int[] usedNumbers = new int[k];
		Arrays.fill(usedNumbers, -1);
		int numberOfNegativeOnes = k;
		for (int i = 0; i < k; i++) {
			int number = random.nextInt(n - i);
			int index = getIndexToInsertAt(usedNumbers, number, numberOfNegativeOnes);
			number += index - numberOfNegativeOnes + 1;
			assert index == 0 || usedNumbers[index - 1] != number;
			assert index == usedNumbers.length || usedNumbers[index] != number;
			assert index >= usedNumbers.length - 1 || usedNumbers[index + 1] != number;
			System.arraycopy(
				usedNumbers,
				numberOfNegativeOnes,
				usedNumbers,
				numberOfNegativeOnes - 1,
				index - numberOfNegativeOnes + 1
			);
			usedNumbers[index] = number;
			answer[i] = number;
			numberOfNegativeOnes--;
		}
		return answer;
	}


	public static int[] generateUsingFisherYates(int n, int k, Random random) {
		int[] array = new int[n];
		for (int i = 0; i < n; i++) {
			array[i] = i;
		}
		fisherYatesShuffleArray(array, random);
		int[] dest = new int[k];
		System.arraycopy(array, 0, dest, 0, k);
		return dest;
	}


	private static void fisherYatesShuffleArray(int[] ar, Random random) {
		for (int i = ar.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	/**
	 * Finds out under which index to insert value {@code base+index} in a sorted array so it remains sorted. That
	 * is, the greater the index, the greater the inserted value will be.
	 *
	 * @param array
	 * 	A sorted array of numbers >= -1, where numbers other than -1 can't occur multiple times.
	 * @param base
	 * 	Any number >= 0.
	 * @param numberOfNegativeOnes
	 * 	How many -1s are there in the beginning of the {@code array}.
	 * @return Index to insert the final value into {@code array} at so {@code array} remains sorted.
	 */
	private static int getIndexToInsertAt(
		int[] array,
		int base,
		int numberOfNegativeOnes
	) {
		assert base >= 0;
		int imax = array.length - 1;
		int imin = numberOfNegativeOnes;
		int lastGreaterImid = -1;
		if (numberOfNegativeOnes < array.length && base < array[numberOfNegativeOnes]) {
			return numberOfNegativeOnes - 1;
		}
		if (base > array[array.length - 1]) {
			return array.length - 1;
		}
		while (imax >= imin) {
			int imid = (imax + imin + 1) / 2;
			if (array[imid] == base + imid - numberOfNegativeOnes) {
				if (imid != array.length - 1 && array[imid + 1] == array[imid] + 1) {
					do {
						imid++;
					} while (imid < array.length && array[imid] == base + imid - numberOfNegativeOnes);
					return imid - 1;
				} else {
					return imid;
				}
			}
			if (array[imid] < base + imid - numberOfNegativeOnes) {
				lastGreaterImid = imid;
				imin = imid + 1;
			} else {
				assert array[imid] > base + imid - numberOfNegativeOnes;
				imax = imid - 1;
			}
		}
		if (lastGreaterImid == -1) {
			throw new RuntimeException();
		}
		return lastGreaterImid;
	}
}
