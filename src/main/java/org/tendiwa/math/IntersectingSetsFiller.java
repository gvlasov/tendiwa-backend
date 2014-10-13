package org.tendiwa.math;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;

import java.util.*;
import java.util.function.ToIntFunction;

/**
 * This algorithm randomly and uniformly generates solutions for the following problem:
 * <p>
 * There is a set <i>A</i> of <i>n</i> distinguishable objects and <i>1 <= m <= n</i> its subsets
 * <i>B<sub>i</sub></i>. For each <i>B<sub>i</sub></i> there is defined <i>1<= c<sub>i</sub> <=n</i> that is
 * called a cap of <i>B<sub>i</sub></i>. Find a mapping from <i>A</i> to <i>{B<sub>i</sub>}</i> so that each
 * <i>B<sub>i</sub></i> has no more than <i>c<sub>i</sub></i> preimages.
 * <p>
 * This algorithm works in O(n*m)
 */
public class IntersectingSetsFiller<T> {
	/**
	 * @param contents
	 * 	Set <i>A</i> of <i>n</i> distinguishable objects.
	 * @param subsetsToCaps
	 * 	A map from sets <i>B<sub>i</sub></i> to their caps <i>c<sub>i</sub></i>.
	 * 	<p>
	 * 	In order for the result of this
	 * 	algorithm to be deterministic, this argument should contain {@link java.util.Set}s with deterministic
	 * 	iteration order (i.e. {@link java.util.LinkedHashSet}, but not {@link java.util.HashSet}).
	 */
	public IntersectingSetsFiller(
		Set<T> contents,
		ToIntFunction<Set<T>> subsetsToCaps,
		Set<Set<T>> subsets,
		Random random
	) {
		random = new Random(random.nextInt());
		assert areSubsets(subsets, contents);
		TObjectIntMap<Set<T>> positionsLeft = new TObjectIntCustomHashMap<>(
			new IdentityHashingStrategy<>(),
			subsets.size()
		);
		int positionsLeftSum = 0;
		for (Set<T> subset : subsets) {
			int value = subsetsToCaps.applyAsInt(subset);
			positionsLeft.put(subset, value);
			positionsLeftSum += value;
		}

		Map<T, Set<T>[]> containing = prepareContainingMap(contents, subsets);
		int contentsSize = contents.size();
		int[] indices = IntegerPermutationGenerator.generateUsingFisherYates(contentsSize, contentsSize, random);
		T[] arrayContents = (T[]) contents.toArray();
		for (int index : indices) {
			T element = arrayContents[index];
			Set<T>[] whereContained = containing.get(element);
			Set<T> subset = whereContained[getSubsetIndex(whereContained, positionsLeft, positionsLeftSum, random)];
			positionsLeft.adjustValue(subset, -1);
			positionsLeftSum--;
		}
	}

	private int getSubsetIndex(
		Set<T>[] whereContained,
		TObjectIntMap<Set<T>> positionsLeft,
		int positionsLeftSum,
		Random random
	) {
		return random.nextInt(whereContained.length);
	}


	/**
	 * Finds out what element is contained in what subset.
	 *
	 * @param contents
	 * 	Elements.
	 * @param subsets
	 * 	Subsets.
	 * @return A mapping from contents to subsets.
	 */
	private Map<T, Set<T>[]> prepareContainingMap(Set<T> contents, Set<Set<T>> subsets) {
		Map<T, Set<T>[]> containing = new LinkedHashMap<>(contents.size());
		for (T element : contents) {
			LinkedHashSet<Set<T>> elementSubsets = new LinkedHashSet<>();
			for (Set<T> subset : subsets) {
				if (subset.contains(element)) {
					elementSubsets.add(subset);
				}
			}
			containing.put(element, elementSubsets);
		}
		return containing;
	}

	/**
	 * Checks that each set in {@code keys} is contained in {@code contents}.
	 *
	 * @param subsets
	 * 	An array of sets.
	 * @param contents
	 * 	A set of all available objects.
	 * @return true if contents contains all elements of each set in {@code subsets}, false otherwise.
	 */
	private boolean areSubsets(Set<Set<T>> subsets, Set<T> contents) {
		for (Set<T> subset : subsets) {
			if (!contents.containsAll(subset)) {
				return false;
			}
		}
		return true;
	}
}
