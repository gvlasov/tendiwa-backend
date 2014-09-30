package org.tendiwa.math;

import java.util.Random;

/**
 * @author Suseika
 */
public class StonesInBasketsSolver {
	/**
	 * Solves a problem of generating a random uniformly distributed partition of N
	 * indistinguishable stones into M distinguishable baskets with given capacity.
	 *
	 * @param basketsCapacity
	 * 	Capacities of baskets, array of length M.
	 * @param numberOfStones
	 * 	Number of stones to put in baskets (N).
	 * @param random
	 * 	A random number generator. Calling this method will always pick only one number from {@code random}.
	 * @see <a href="http://www.reddit.com/r/algorithms/comments/2do7xr/randomly_map_a_list_of_natural_numbers_to_another/">
	 * My question with the explanation of the problem. There I approved a solution totally different from what
	 * I used here</a>
	 */
	public static int[] solve(int[] basketsCapacity, int numberOfStones, Random random) {
		Random rand = new Random(random.nextInt());
		int totalCapacity = computeTotalBasketsCapacity(basketsCapacity);
		if (numberOfStones > totalCapacity) {
			throw new IllegalArgumentException(
				"Number of stones must be no greater than total capacity of baskets " +
					"(" + numberOfStones + " stones, " + totalCapacity + " total capacity of baskets)"
			);
		}
		int[] basketPutChances = computeBasketsChances(basketsCapacity, totalCapacity);
		int numberOfBaskets = basketsCapacity.length;
		int[] stonesPartition = new int[numberOfBaskets];
		for (int i = 0; i < numberOfStones; i++) {
			int generatedPlace = (int) Math.floor(rand.nextDouble() * (totalCapacity + 1));
			assert generatedPlace >= 0 && generatedPlace <= totalCapacity;
			int index = findWhereToPutStone(basketsCapacity, basketPutChances, numberOfBaskets, stonesPartition, generatedPlace);
			stonesPartition[index]++;
		}
		return stonesPartition;
	}

	/**
	 * Decides to which basket to add one stone.
	 *
	 * @param basketsCapacity
	 * @param basketPutChances
	 * @param numberOfBaskets
	 * @param stonesPartition
	 * @param generatedPlace
	 * @return Index of a basket where to put a new stone.
	 */
	private static int findWhereToPutStone(int[] basketsCapacity, int[] basketPutChances, int numberOfBaskets, int[] stonesPartition, int generatedPlace) {
		int index = binarySearchWithClamp(basketPutChances, generatedPlace);
		if (stonesPartition[index] == basketsCapacity[index]) {
			// If a basket is full, select next basket until we find one that is not full.
			int j = 1;
			for (; j < numberOfBaskets; j++) {
				int newIndex = (index + j) % numberOfBaskets;
				if (stonesPartition[newIndex] < basketsCapacity[newIndex]) {
					index = newIndex;
					break;
				}
			}
			assert j < numberOfBaskets : "Nowhere to put stones!";
		}
		return index;
	}

	/**
	 * In O(log(n)), finds an element in array that is {@code >= value} for which the previous element is {@code <
	 * value}.
	 *
	 * @param array
	 * 	A sorted array to search in.
	 * @param value
	 * 	A value.
	 * @return Index of an element in {@code array} that is {@code >= value} for which the previous element is {@code <
	 * value}.
	 */
	private static int binarySearchWithClamp(int[] array, int value) {
		int imax = array.length - 1;
		int imin = 0;
		int lastGreaterImid = -1;
		while (imax >= imin) {
			int imid = imax + imin / 2;
			if (array[imid] == value) {
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
		assert lastGreaterImid != -1;
		return lastGreaterImid;
	}


	/**
	 * @param basketsCapacity
	 * 	Capacities of baskets.
	 * @param summarySize
	 * 	Total capacity of all baskets.
	 * @return Array whose i-th element is a sum of elements of {@code basketsCapacity} from 0-th to i-th.
	 */
	private static int[] computeBasketsChances(int[] basketsCapacity, int summarySize) {
		int[] chances = new int[basketsCapacity.length];
		chances[0] = basketsCapacity[0];
		for (int i = 1; i < basketsCapacity.length; i++) {
			chances[i] = chances[i - 1] + basketsCapacity[i];
		}
		return chances;
	}

	/**
	 * Finds what is the total capacity of all baskets.
	 *
	 * @param basketsCapacity
	 * 	Capacities of baskets.
	 * @return Sum of all capacities of baskets.
	 */
	private static int computeTotalBasketsCapacity(int[] basketsCapacity) {
		int size = 0;
		for (int i = 0; i < basketsCapacity.length; i++) {
			assert basketsCapacity[i] >= 1;
			size += basketsCapacity[i];
		}
		return size;
	}
}
