package org.tendiwa.math;

import java.util.Random;

public final class FisherYatesPermutation implements Permutation {
	private final int n;
	private final int k;
	private final Random random;

	/**
	 * A random sequence of {@code k} unique numbers between {@code 0} and {@code n-1}.
	 * <p>
	 * Always picks only one value from {@code random}.
	 *
	 * @param n
	 * 	Greatest number.
	 * @param k
	 * 	Number of numbers to generate.
	 * @param random
	 * 	A source of randomness. Only one value will be used.
	 * @return
	 */
	public FisherYatesPermutation(int n, int k, Random random) {
		this.n = n;
		this.k = k;
		this.random = new Random(random.nextInt());
	}

	@Override
	public int[] generate() {
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
		random = new Random(random.nextInt());
		for (int i = ar.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
}
